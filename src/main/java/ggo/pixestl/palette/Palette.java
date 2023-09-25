package ggo.pixestl.palette;

import ggo.pixestl.generator.GenInstruction;
import ggo.pixestl.generator.GenInstruction.PixelCreationMethod;
import ggo.pixestl.util.ColorUtil;
import ggo.pixestl.util.FindClosestColorThread;
import ggo.pixestl.util.ImageUtil;
import org.json.JSONObject;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Palette 
{
	private int nbLayers;
	
	final GenInstruction genInstruction;
	
	private Map<Color,ColorCombi> quantizedColors;
	
	private Map<String,String> hexCodesMap;

	private final List<String> hexColorList = new ArrayList<>();
	private final List<List<String> > hexColorGroupList = new ArrayList<>();

	private int nbGroup=0;

	private int layerCount = 0;

	public Palette(String path,GenInstruction genInstruction) throws IOException {
		this.genInstruction=genInstruction;
		
		quantizedColors = new HashMap<>();
		hexCodesMap = new HashMap<>();
		nbLayers=0;
		
		List<ColorLayer> colorLayerList =new ArrayList<>();		
		String jsonContent = new String(Files.readAllBytes(new File(path).toPath()));
	    JSONObject jsonObject = new JSONObject(jsonContent);

        for (String hexColor : jsonObject.keySet())
        {
            JSONObject colorObject = jsonObject.getJSONObject(hexColor);
            String colorName =(String) colorObject.get("name");
            
            hexCodesMap.put(hexColor,colorName);
            if (colorObject.has("active") && !colorObject.getBoolean("active")) continue;
            if (colorObject.has("layers") && genInstruction.getPixelCreationMethod() == PixelCreationMethod.ADDITIVE)
            {
            	JSONObject layersObject = (JSONObject)colorObject.get("layers");
                int nbL=0;
	            for (String layerKey : layersObject.keySet())
	            {
	            	nbL++;
	            	nbLayers= Math.max(nbLayers, nbL);
	            	int layer = Integer.parseInt(layerKey);
	                JSONObject subObject = layersObject.getJSONObject(layerKey);
	                double h = subObject.getInt("H");
	                double s = subObject.getInt("S");
	                double l = subObject.getDouble("L");
	                colorLayerList.add(new ColorLayer(hexColor, layer ,h,s,l));
	            }
				if (!hexColorList.contains(hexColor)) hexColorList.add(hexColor);
            }
            else if (genInstruction.getPixelCreationMethod() == PixelCreationMethod.FULL )
            {
            	double[] hsl= ColorUtil.colorToHSL(Color.decode(hexColor));
            	colorLayerList.add(new ColorLayer(hexColor, genInstruction.getColorPixelLayerNumber() ,hsl[0],hsl[1],hsl[2]));
				if (!hexColorList.contains(hexColor)) hexColorList.add(hexColor);
            }
        }

		nbLayers=genInstruction.getColorPixelLayerNumber();
        colorLayerList.sort(new ColorLayer.LayerComparator());

		hexColorList.sort(new ColorUtil.HexCodeComparator());


		computeColorsByGroup(colorLayerList);
	}

	List<ColorCombi> createMultiCombi(List<String> restrictColorList,List<ColorLayer> colorLayerList)
	{
		List<ColorCombi> colorCombiList = new ArrayList<>();
		for (int i=0; i<colorLayerList.size();i++)
		{
			ColorLayer colorLayer = colorLayerList.get(i);
			if (restrictColorList != null) {
				if (!restrictColorList.contains(colorLayer.getHexCode())) continue;
			}

			ColorCombi cC = new ColorCombi(colorLayer);
			colorCombiList.add(cC);
			if (i+1<colorLayerList.size())
			{
				colorCombiList.addAll(computeCombination(restrictColorList,cC,colorLayerList));
			}
		}
		List<ColorCombi> finalColorCombiList = new ArrayList<>();
		for (ColorCombi c : colorCombiList)
		{
			if (c.getTotalLayers() != genInstruction.getColorPixelLayerNumber()) continue;
			finalColorCombiList.add(c);
		}

		return finalColorCombiList;
	}
		
	private List<ColorCombi> computeCombination(List<String> restrictColorList,ColorCombi cC,List<ColorLayer> colorLayerList) {
		List<ColorCombi> colorCombiList = new ArrayList<>();
		for (int i=0; i<colorLayerList.size();i++)
        {

        	ColorLayer colorLayer = colorLayerList.get(i);
			if (restrictColorList!=null) {
				if (!restrictColorList.contains(colorLayer.getHexCode())) continue;
			}
        	int layer = colorLayer.getLayer();
        	
        	if (cC.getTotalLayers()+layer > nbLayers) continue;
        	if (cC.getTotalColors()>= hexCodesMap.entrySet().size()) break;
        	
        	ColorCombi cC2 = cC.combineLithoColorLayer(colorLayer,nbLayers);
        	if (cC2 == null) continue;
        	if (cC2.getTotalLayers() == genInstruction.getColorPixelLayerNumber()) colorCombiList.add(cC2);       	
        	if (i+1<colorLayerList.size()) colorCombiList.addAll(computeCombination(restrictColorList,cC2,colorLayerList));
        }
		return colorCombiList;
	}
	
	public List<String> getColorHexList()
	{
		return new ArrayList<>(hexCodesMap.keySet());
	}
	
	public String getColorName(String hexCode)
	{
		return hexCodesMap.get(hexCode);
	}
	
	public List<Color> getColors() {
		return new ArrayList<>(quantizedColors.keySet());
	}
		
	
	public ColorCombi getColorCombi(Color c)
	{
		return quantizedColors.get(c);
	}

	
	public BufferedImage quantizeColors(BufferedImage image)
	{
		int width = image.getWidth();
		int height = image.getHeight();
		List<Color> colors=getColors();
		BufferedImage quantizedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

		boolean allComplete = false;
		List<FindClosestColorThread> findClosestColorThreadList =new ArrayList<>();
		ExecutorService executorService = Executors.newFixedThreadPool(genInstruction.getRowThreadNumber());
		for (int y = 0; y < height; y++)
		{
			for (int x = 0; x < width; x++)
			{
				if (ColorUtil.transparentPixel(image,x,y))
				{
					quantizedImage.setRGB(x, y,0x00000000 );
					continue;
				}
				Color pixelColor = new Color(image.getRGB(x, y));
				FindClosestColorThread findClosestColorThread = new FindClosestColorThread(x,y,pixelColor, colors,genInstruction.getColorDistanceComputation());
				executorService.execute(findClosestColorThread);
				findClosestColorThreadList.add(findClosestColorThread);
			}
		}

		executorService.shutdown();

		try
		{
			allComplete = executorService.awaitTermination(genInstruction.getLayerThreadTimeout(), TimeUnit.SECONDS);
			if (!allComplete)
			{
				System.err.println("Some color distance computation threads could not complete within the timeout period");
			}
		}
		catch (InterruptedException e)
		{
			System.err.println("The main thread was interrupted while waiting.");
		}
		finally
		{
			if (!allComplete)
			{
				System.err.println("GENERATION ABORTED");
				System.exit(-1);
			}
		}

		List<Color> usedColorList = new ArrayList<>();

		for (FindClosestColorThread findClosestColorThread : findClosestColorThreadList)
		{
			if (!usedColorList.contains(findClosestColorThread.getClosestColor()))usedColorList.add(findClosestColorThread.getClosestColor());
			quantizedImage.setRGB(findClosestColorThread.getX(), findClosestColorThread.getY(), findClosestColorThread.getClosestColor().getRGB());
		}



		Map<Color,ColorCombi> quantizedColorsTemp = new HashMap<>();
		for (Color c: usedColorList)
		{
			quantizedColorsTemp.put(c,quantizedColors.get(c));
		}
		quantizedColors=quantizedColorsTemp;
		System.out.println("Nb color used="+quantizedColors.size());

		return quantizedImage;
	}

	public void computeColorsByGroup(List<ColorLayer> colorLayerList)
	{
		hexColorList.remove("#FFFFFF");

		int nbColorPool = hexColorList.size(); //by default 1 group by Color
		if (genInstruction.getPixelCreationMethod() == PixelCreationMethod.ADDITIVE && genInstruction.getColorNumber() != 0)
		{
			nbColorPool = genInstruction.getColorNumber() - 1;
		}

		nbGroup = hexColorList.size()/nbColorPool;
		nbGroup+=hexColorList.size()%nbColorPool==0?0:1;

		List<List<String> > hexColorGroup = new ArrayList<>();

		for (int i=0;i<nbColorPool;i++) hexColorGroup.add(new ArrayList<>());

		for (int i=0;i<nbGroup;i++)
		{
			for (int j=0;j<nbColorPool;j++)
			{
				if (nbColorPool*i+j>=hexColorList.size()) break;
				hexColorGroup.get(i).add(hexColorList.get(nbColorPool*i+j));
			}
		}
		List<List<ColorCombi>> colorCombiListList = new ArrayList<>();
		for (int i=0;i<nbGroup;i++)
		{
			hexColorGroup.get(i).add("#FFFFFF");
			List<ColorCombi> curColorCombiList = createMultiCombi(hexColorGroup.get(i),colorLayerList);
			colorCombiListList.add(curColorCombiList);
		}

		Collections.reverse(colorCombiListList);

		List<List<ColorCombi>> tempColorCombiListList = new ArrayList<>();
		tempColorCombiListList.add(colorCombiListList.get(0));
		for(int i=0;i<nbGroup-1;i++)
		{
			List<ColorCombi> tempColorCombiList = new ArrayList<>();
			for (ColorCombi cI : tempColorCombiListList.get(i))
			{
				if (nbGroup>1) cI.addLayer(new ColorLayer("#FFFFFF",1,0,0,100));
				for (ColorCombi cI1 : colorCombiListList.get(i+1)) {
					tempColorCombiList.add(cI.combineLithoColorCombi(cI1));
				}
			}
			tempColorCombiListList.add(tempColorCombiList);
		}
		List<ColorCombi> finalCombiList =tempColorCombiListList.get(tempColorCombiListList.size()-1);


		layerCount=nbLayers * nbGroup+((nbGroup>1)?1:0);

		for (ColorCombi c : finalCombiList)
		{
			quantizedColors.put(c.getColor(genInstruction),c);
		}

		initHexColorGroupList(hexColorGroup,nbColorPool);

	}

	private void initHexColorGroupList(List<List<String> > hexColorGroup,int nbColorPool)
	{
		for (int i=0;i<nbColorPool;i++)
		{
			hexColorGroupList.add(new ArrayList<>());
		}

		for (List<String> groupLayer : hexColorGroup)
		{
			groupLayer.remove("#FFFFFF");
			for (int i=0;i<nbColorPool;i++)
			{
				if (i>=groupLayer.size()) continue;
				hexColorGroupList.get(i).add(groupLayer.get(i));
				Collections.reverse(hexColorGroupList.get(i));

			}
		}

		List<String> whiteGroup = new ArrayList<>();
		whiteGroup.add("#FFFFFF");
		hexColorGroupList.add(whiteGroup);
	}


	
	
	
	public void restrictFullColors(BufferedImage image,int colorNumber) {
		
		BufferedImage pixelatedImage = ImageUtil.resizeImage(image,genInstruction.getDestImageWidth(), genInstruction.getColorPixelWidth());
		BufferedImage quantizedImage = quantizeColors(pixelatedImage);
		
		Map<String, Integer> colorCounts = new HashMap<>();
		for (int y = 0; y < quantizedImage.getHeight(); y++)
		{
		    for (int x = 0; x < quantizedImage.getWidth(); x++)
		    {
		        int rgb = quantizedImage.getRGB(x, y);
		        Color c = new Color(rgb);
		        ColorCombi cc = quantizedColors.get(c);
		        for (ColorLayer cL : cc.getLayers())
		        {
		        	int count = colorCounts.getOrDefault(cL.getHexCode(), 0);
		        	int nbLayer=cL.getLayer();
		        	if ("#000000".equals(cL.getHexCode()) &&  nbLayer == 5) nbLayer=1;
		        	colorCounts.put(cL.getHexCode(), count + nbLayer);
		        	
		        }
		        		
	        }
		}
		
		List<Map.Entry<String, Integer>> sortedColors = new ArrayList<>(colorCounts.entrySet());
		sortedColors.sort((entry1, entry2) -> entry2.getValue().compareTo(entry1.getValue()));
		
		List<String> mostFrequentColors = new ArrayList<>();

		for(Map.Entry<String, Integer> map :  sortedColors)
		{
			if ("#FFFFFF".equals(map.getKey()))
			{
				mostFrequentColors.add("#FFFFFF");
				colorNumber--;
			}
		}
			
		for (int i = 0; i < Math.min(sortedColors.size(), colorNumber); i++)
		{
			mostFrequentColors.add(sortedColors.get(i).getKey());
		}
		
		Map<Color,ColorCombi> newQuantizedColors = new HashMap<>();
        for (Color c : quantizedColors.keySet())
        {
        	ColorCombi cC = quantizedColors.get(c);
        	boolean excluded = false;
    		for (ColorLayer cL : cC.getLayers())
	        {
	        	if (!mostFrequentColors.contains(cL.getHexCode()))
	        	{
	        		excluded=true;
	        		break;
	        	}
	        }
    		if (!excluded) newQuantizedColors.put(c,quantizedColors.get(c));
        }
        quantizedColors=newQuantizedColors;
        
        Map<String,String> newHexCodesMap = new HashMap<>();
        {
        	for (Color c : quantizedColors.keySet())
        	{
        		ColorCombi cC=quantizedColors.get(c);
        		for (ColorLayer l : cC.getLayers())
        		{
					String hexCode=l.getHexCode();
	        		for (String hex : hexCodesMap.keySet())
	            	{
	            		if (hexCode.equals(hex))
	            		{
	            			newHexCodesMap.put(hex, hexCodesMap.get(hex));
	            			break;
	            		}
	            	}
        		}
        	}
        }
        hexCodesMap=newHexCodesMap;
	}

	public String generateSwapFilamentsInstruction()
	{
		double layerIdx=0;
		StringBuilder sB = new StringBuilder();
		for (int i=0;i<getNbGroup();i++)
		{
			sB.append("Layer[").append(layerIdx).append("] :");
			int j=0;
			for (List<String> hexColorGroup: hexColorGroupList())
			{
				if (i>=hexColorGroup.size())continue;
				if (j!=0) sB.append(", ");
				j++;
				if (i!=0)
				{
					sB.append(getColorName(hexColorGroup.get(i - 1))).append("-->");
				}
				sB.append(getColorName(hexColorGroup.get(i)));
			}
			sB.append("\n");
			if (i==0) layerIdx+=genInstruction.getPlateThickness();
			layerIdx+=(genInstruction.getColorPixelLayerThickness()*(genInstruction.getColorPixelLayerNumber()+1));
		}
		return sB.toString();
	}

	public int getNbGroup() { return nbGroup;}

	public List<List<String>> hexColorGroupList() {
		return hexColorGroupList;
	}

	public int getLayerCount() {
		return layerCount;
	}


}
