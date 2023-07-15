package ggo.pixestl.csg.texture;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import eu.mihosoft.jcsg.CSG;
import eu.mihosoft.jcsg.Polygon;
import eu.mihosoft.vvecmath.Transform;
import ggo.pixestl.csg.CSGWorkData;
import ggo.pixestl.generator.GenInstruction;

public class CSGThreadTexture implements Runnable
{
	final private ExecutorService executorService;
	final private CSGWorkData csgWorkData;
	final private List<CSGThreadTextureRow> csgGenerationByLines;
	private CSG csg = null;	
	private boolean allComplete;
	
    public CSGThreadTexture(CSGWorkData data)
	{
		this.csgWorkData=data;
		this.executorService = Executors.newFixedThreadPool(csgWorkData.getGenInstruction().getRowThreadNumber());
		this.csgGenerationByLines = new ArrayList<>();	
		this.allComplete=false;
	}

	public void run()
	{		
        int height = csgWorkData.getTexturedImage().getHeight();
        for (int y = 0; y < height-1; y++)
        { 
        	CSGThreadTextureRow generationByLine = new CSGThreadTextureRow(csgWorkData,y);
        	csgGenerationByLines.add(generationByLine);
        	executorService.execute(generationByLine);
        }
        executorService.shutdown();
        
        try
        {
            allComplete = executorService.awaitTermination(csgWorkData.getGenInstruction().getRowThreadTimeout(),TimeUnit.SECONDS);
        }
        catch (InterruptedException e)
        {
        	allComplete=false;
        }
        if (!allComplete) return;
        
        List<Polygon> polys=new ArrayList<>();         
        for(CSGThreadTextureRow csgGenerationByLine : csgGenerationByLines)
        {
        	polys.addAll(csgGenerationByLine.getPolys());
        }        
        csg = CSG.fromPolygons(polys);
       
        if (csgWorkData.getGenInstruction().isNoColorLayer())
        {
        	GenInstruction g = csgWorkData.getGenInstruction();
        	
        	double tW=csgWorkData.getTexturedImage().getWidth()*g.getTexturePixelWidth();
        	double tH=csgWorkData.getTexturedImage().getHeight()*g.getTexturePixelWidth();
        	
        	double cW=csgWorkData.getColorImage().getWidth()*g.getColorPixelWidth();
        	double cH=csgWorkData.getColorImage().getHeight()*g.getColorPixelWidth();
        	
        	double diffW=tW-cW;
        	double diffH=tH-cH;
        	
	        Transform transform = Transform.unity()
	        		.translateX(-diffW/2-(g.getColorPixelWidth()-g.getTexturePixelWidth())/2)
	        		.translateY(-diffH/2-(g.getColorPixelWidth()-g.getTexturePixelWidth())/2)
	        		.translateZ(csgWorkData.getGenInstruction().getColorPixelLayerThickness()*csgWorkData.getGenInstruction().getColorPixelLayerNumber());
	        csg = csg.transformed(transform);
        }
	}
			
	public String writeSTLString()
	{
		return csg!=null?csg.toStlString():null;
	}
	
	public String getColorName()
	{
		return csgWorkData.getColorName();
	}

	public boolean isAllComplete() {
		return allComplete;
	}
}
