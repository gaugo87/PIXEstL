package ggo.pixestl.palette;

import ggo.pixestl.generator.GenInstruction;
import ggo.pixestl.generator.GenInstruction.PixelCreationMethod;
import ggo.pixestl.util.ColorUtil;
import ggo.pixestl.util.ImageUtil;
import org.json.JSONObject;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Palette 
{
	private int nbLayers;
	
	GenInstruction genInstruction;
	
	private Map<Color,ColorCombi> quantizedColors;
	
	private Map<String,String> hexCodesMap;
	List<String> sortedHexcodeList = new ArrayList<>();
	
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
            }
            else if (genInstruction.getPixelCreationMethod() == PixelCreationMethod.FULL )
            {
            	double[] hsl= ColorUtil.colorToHSL(Color.decode(hexColor));
            	colorLayerList.add(new ColorLayer(hexColor, genInstruction.getColorPixelLayerNumber() ,hsl[0],hsl[1],hsl[2]));
            }
        }
        colorLayerList.sort(new ColorLayer.LayerComparator());
                
        List<ColorCombi> colorCombiList = new ArrayList<>();
        for (int i=0; i<colorLayerList.size();i++)
        {
        	ColorLayer colorLayer = colorLayerList.get(i);
        	ColorCombi cC = new ColorCombi(colorLayer);
        	colorCombiList.add(cC);
        	if (i+1<colorLayerList.size())
        	{
        		colorCombiList.addAll(computeCombination(cC,colorLayerList.subList(i+1,colorLayerList.size())));        		
        	}
        }
        for (ColorCombi c : colorCombiList)
        {
        	if (c.getTotalLayers() != genInstruction.getColorPixelLayerNumber()) continue;
        	quantizedColors.put(c.getColor(genInstruction),c);
        }
        
        sortedHexcodeList.addAll(hexCodesMap.keySet());
        sortedHexcodeList.sort(null);
        
	}
		
	private List<ColorCombi> computeCombination(ColorCombi cC,List<ColorLayer> colorLayerList) {
		List<ColorCombi> colorCombiList = new ArrayList<>();
		for (int i=0; i<colorLayerList.size();i++)
        {
        	ColorLayer colorLayer = colorLayerList.get(i);
        	int layer = colorLayer.getLayer();
        	
        	if (cC.getTotalLayers()+layer > nbLayers) break;
        	if (cC.getTotalColors()>= hexCodesMap.entrySet().size()) break;
        	
        	ColorCombi cC2 = cC.combineLithoColorLayer(colorLayer,nbLayers);
        	if (cC2 == null) continue;
        	if (cC2.getTotalLayers() == genInstruction.getColorPixelLayerNumber()) colorCombiList.add(cC2);       	
        	if (i+1<colorLayerList.size()) colorCombiList.addAll(computeCombination(cC2,colorLayerList.subList(i+1,colorLayerList.size())));
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
	
	private List<Color> getColors() {
		return new ArrayList<>(quantizedColors.keySet());
	}
		
	
	public ColorCombi getColorCombi(Color c)
	{
		return quantizedColors.get(c);
	}
	
	public int getPriorityColor(String hexCode)
	{
		for (int i=0;i<sortedHexcodeList.size();i++)
			if (hexCode.equals(sortedHexcodeList.get(i))) return i;
		return -1;
	}

	
	public BufferedImage quantizeColors(BufferedImage image)
	{
		int width = image.getWidth();
		int height = image.getHeight();
		
		List<Color> colors=getColors();

		BufferedImage quantizedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

		for (int y = 0; y < height; y++)
		{
			for (int x = 0; x < width; x++)
			{
				Color pixelColor = new Color(image.getRGB(x, y));
				Color closestColor = ColorUtil.findClosestColor(pixelColor, colors);
				quantizedImage.setRGB(x, y, closestColor.getRGB());
			}
		}
		return quantizedImage;
	}
	
	
	
	
	public void restrictColors(BufferedImage image,int colorNumber) {
		
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
		        for (ColorLayer cL : cc.getLayers().values())
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
    		for (ColorLayer cL : cC.getLayers().values())
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
        		for (String hexCode : cC.getLayers().keySet())
        		{
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
        sortedHexcodeList.addAll(hexCodesMap.keySet());
        sortedHexcodeList.sort(null);
        
        
	}
	
	

}
