package ggo.pixestl.stl;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import ggo.pixestl.csg.CSGWorkData;
import ggo.pixestl.csg.color.CSGThreadColor;
import ggo.pixestl.csg.color.CSGThreadColorRow;
import ggo.pixestl.csg.plate.CSGSupportPlate;
import ggo.pixestl.csg.plate.CSGThreadSupport;
import ggo.pixestl.csg.plate.CSGThreadSupportRow;
import ggo.pixestl.csg.texture.CSGThreadTexture;
import ggo.pixestl.csg.texture.CSGThreadTextureRow;
import ggo.pixestl.csg.texture.CSGThreadTextureRowWithTransparency;
import ggo.pixestl.generator.GenInstruction;
import ggo.pixestl.palette.Palette;
import ggo.pixestl.util.ImageUtil;

public class StlMaker {

	private final Palette palette;
	private final BufferedImage colorImage;
	private final BufferedImage texturedImage;
	final GenInstruction genInstruction;

	final int FLEXIBLE_COLOR_PLATE_NB=3;
		
	public StlMaker(BufferedImage colorImage,BufferedImage texturedImage,Palette palette,GenInstruction genInstruction)
	{
		this.colorImage=colorImage;
		this.texturedImage=texturedImage;
		this.palette=palette;
		this.genInstruction=genInstruction;

	}
	
    public void process(ZipOutputStream zipOut) throws IOException
    {
    	int layerThreadNumber=palette.getColorHexList().size()+1;
    	
    	if (genInstruction.getLayerThreadMaxNumber() != 0 && layerThreadNumber>genInstruction.getLayerThreadMaxNumber())layerThreadNumber=genInstruction.getLayerThreadMaxNumber();

		ExecutorService executorService = Executors.newFixedThreadPool(layerThreadNumber);
    	boolean allComplete = false;
    	CSGSupportPlate csgPlate = null;
    	List<CSGThreadColor> csgThreadColors =new ArrayList<>();
    	CSGThreadTexture csgThreadTexture = null;
		CSGThreadSupport csgSupportPlate = null;

    	
    	if (this.colorImage !=null)
    	{
			if (ImageUtil.hasATransparentPixel(this.colorImage) && genInstruction.getCurve()!=0.0)
			{
				throw new IllegalArgumentException("Curve mode not compatible with image with transparency");
			}

			if (!ImageUtil.hasATransparentPixel(this.colorImage)) {
				csgPlate = new CSGSupportPlate();
				csgPlate.generate(colorImage, genInstruction);
			}
			else {
				CSGWorkData csgWorkData =new CSGWorkData(colorImage,texturedImage,palette,"plate",null,genInstruction);
				csgSupportPlate = new CSGThreadSupport(CSGThreadSupportRow.class,csgWorkData);
				executorService.execute(csgSupportPlate);
			}
			int nbColorPlate = 1;

			int colorPlateLayerNb=-1;
			if (genInstruction.getCurve() != 0.0)
			{
				colorPlateLayerNb=FLEXIBLE_COLOR_PLATE_NB;
				nbColorPlate = (genInstruction.getColorPixelLayerNumber()/FLEXIBLE_COLOR_PLATE_NB);
				nbColorPlate+=(genInstruction.getColorPixelLayerNumber()%FLEXIBLE_COLOR_PLATE_NB!=0)?1:0;
			}
    		
    		for (List<String> hexCodeList : palette.hexColorGroupList())
    		{
				StringBuilder colorName = new StringBuilder();
				for (String hexColor : hexCodeList)
				{
					if (colorName.length() > 0) colorName.append("+");
					colorName.append(palette.getColorName(hexColor));
				}

				for (int i=0;i<nbColorPlate;i++)
				{

					String threadName="layer-";
					threadName+=nbColorPlate==1?"":(i+1)+"-";
					CSGWorkData csgWorkData =new CSGWorkData(colorImage,texturedImage,palette,threadName+colorName,hexCodeList,
							i*colorPlateLayerNb,colorPlateLayerNb,genInstruction);
					CSGThreadColor csgThreadColor = new CSGThreadColor(CSGThreadColorRow.class,csgWorkData);
					csgThreadColors.add(csgThreadColor);
					executorService.execute(csgThreadColor);
				}
			}
    	}

    	if (this.texturedImage != null)
    	{
			List<String> colorList=palette.getColorHexList();
			Collections.sort(colorList);
			List<String> colorNameList = new ArrayList<>();
			String whiteColor = colorList.get(colorList.size()-1);
			colorNameList.add(whiteColor);
			
			CSGWorkData csgWorkData =new CSGWorkData(colorImage,texturedImage,palette,"layer-texture-"+palette.getColorName(whiteColor),colorNameList,genInstruction);
			if (ImageUtil.hasATransparentPixel(texturedImage)) {
				csgThreadTexture = new CSGThreadTexture(CSGThreadTextureRowWithTransparency.class, csgWorkData);
			}
			else {
				csgThreadTexture = new CSGThreadTexture(CSGThreadTextureRow.class,csgWorkData);
			}
			executorService.execute(csgThreadTexture);
			
    	}		
    	executorService.shutdown();
    	 	
		try
		{
		    allComplete = executorService.awaitTermination(genInstruction.getLayerThreadTimeout(), TimeUnit.SECONDS);
		    if (!allComplete)
		    {
		    	System.err.println("Some layer threads could not complete within the timeout period");
		    }
		}
		catch (InterruptedException e)
		{
			System.err.println("The main thread was interrupted while waiting.");
		}
		finally
		{
			if (allComplete)
			{
				for (CSGThreadColor stlGeneration : csgThreadColors) {
					if (!stlGeneration.isAllComplete()) {
						allComplete = false;
						break;
					}
				}
			}
			if (csgThreadTexture!=null && !csgThreadTexture.isAllComplete())
			{
				allComplete=false;
			}
			if (csgSupportPlate!=null && !csgSupportPlate.isAllComplete())
			{
				allComplete=false;
			}
			if (!allComplete)
			{
				System.err.println("GENERATION ABORTED");
				System.exit(-1);
			}
		}
		
		if (csgPlate != null)
		{
			ZipEntry zipEntry = new ZipEntry("layer-plate.stl");
		    zipOut.putNextEntry(zipEntry);
		    byte[] stlPlate = csgPlate.writeSTLString().getBytes();
		    zipOut.write(stlPlate,0,stlPlate.length);
			zipOut.closeEntry();   		
		}

		if (csgSupportPlate != null)
		{
			ZipEntry zipEntry = new ZipEntry("layer-plate.stl");
			zipOut.putNextEntry(zipEntry);
			csgSupportPlate.writeSTLString(zipOut);
			zipOut.closeEntry();
		}

    	for (CSGThreadColor stlGeneration : csgThreadColors)
    	{
    		if (!stlGeneration.hasSTL()) continue;
    		ZipEntry zipEntry = new ZipEntry(stlGeneration.getThreadName()+".stl");
    		zipOut.putNextEntry(zipEntry);
			stlGeneration.writeSTLString(zipOut);
    		zipOut.closeEntry();    		
    	}

		if (genInstruction.isColorLayer() && genInstruction.getPixelCreationMethod()== GenInstruction.PixelCreationMethod.ADDITIVE)
		{
			String instructions = palette.generateSwapFilamentsInstruction();
			System.out.println(instructions);
			ZipEntry  zipEntry = new ZipEntry("instructions.txt");
			zipOut.putNextEntry(zipEntry);
			byte[] b = instructions.getBytes(Charset.defaultCharset());
			zipOut.write(b,0,b.length);
			zipOut.closeEntry();

		}
    	
    	if (csgThreadTexture != null)
    	{
        	ZipEntry  zipEntry = new ZipEntry(csgThreadTexture.getThreadName()+".stl");
    		zipOut.putNextEntry(zipEntry);
			csgThreadTexture.writeSTLString(zipOut);
    		zipOut.closeEntry(); 
    	}

    }
    

	
	

}
