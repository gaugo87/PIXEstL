package ggo.pixestl.stl;

import java.awt.image.BufferedImage;
import java.io.IOException;
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
import ggo.pixestl.csg.plate.CSGSupportPlate;
import ggo.pixestl.csg.texture.CSGThreadTexture;
import ggo.pixestl.generator.GenInstruction;
import ggo.pixestl.palette.Palette;
public class StlMaker {

	private final Palette palette;
	private final BufferedImage colorImage;
	private final BufferedImage texturedImage;
	GenInstruction genInstruction;
		
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
    	
    	if (this.colorImage !=null)
    	{
    		csgPlate = new CSGSupportPlate();
    		csgPlate.generate(colorImage, genInstruction);
    		
    		for (String hexCode : palette.getColorHexList())
    		{
    			String colorName = palette.getColorName(hexCode);
    			CSGWorkData csgWorkData =new CSGWorkData(colorImage,texturedImage,palette,colorName,hexCode,genInstruction);
    			CSGThreadColor csgThreadColor = new CSGThreadColor(csgWorkData);
    			csgThreadColors.add(csgThreadColor);
    			executorService.execute(csgThreadColor);
    		}
    	}

    	if (this.texturedImage != null)
    	{
			List<String> colorList=palette.getColorHexList();
			Collections.sort(colorList);
			String whiteColor = colorList.get(colorList.size()-1);
			
			CSGWorkData csgWorkData =new CSGWorkData(colorImage,texturedImage,palette,palette.getColorName(whiteColor),whiteColor,genInstruction);
			csgThreadTexture = new CSGThreadTexture(csgWorkData); 
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
				for (CSGThreadColor stlGeneration : csgThreadColors)
		    	{
					if (!stlGeneration.isAllComplete())
					{
						allComplete=false;
						break;
					}
		    	}
			}
			if (csgThreadTexture!=null && !csgThreadTexture.isAllComplete())
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
			

    	for (CSGThreadColor stlGeneration : csgThreadColors)
    	{
    		String stlString=stlGeneration.writeSTLString();
    		if (stlString == null) continue;
    		String colorName=stlGeneration.getColorName();
    		ZipEntry zipEntry = new ZipEntry("layer-"+colorName+".stl");
    		zipOut.putNextEntry(zipEntry);
    		byte[] stl = stlString.getBytes();    		
    		zipOut.write(stl,0,stl.length);
    		zipOut.closeEntry();    		
    	}
    	
    	if (csgThreadTexture != null)
    	{
    		String stlString=csgThreadTexture.writeSTLString();
        	ZipEntry  zipEntry = new ZipEntry("layer-texture-"+csgThreadTexture.getColorName()+".stl");
    		zipOut.putNextEntry(zipEntry);		
    		byte[] stl = stlString.getBytes();		
    		zipOut.write(stl,0,stl.length);
    		zipOut.closeEntry(); 
    	}
    	
    	System.out.println("GENERATION COMPLETE !");

    }
    

	
	

}
