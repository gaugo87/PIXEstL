package ggo.pixestl.csg.color;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import eu.mihosoft.jcsg.CSG;
import ggo.pixestl.csg.CSGWorkData;

public class CSGThreadColor implements Runnable
{
	final private ExecutorService executorService;
	final private CSGWorkData csgInstruction;
	final private List<CSGThreadColorRow> csgGenerationByLines;
	private CSG csg = null;	
	private boolean allComplete;
	
    public CSGThreadColor(CSGWorkData instruction)
	{
		this.csgInstruction=instruction;
		this.executorService = Executors.newFixedThreadPool(csgInstruction.getGenInstruction().getRowThreadNumber());
		this.csgGenerationByLines = new ArrayList<>();		
		this.allComplete=false;
	}

	public void run()
	{
        int height = csgInstruction.getColorImage().getHeight();
        for (int y = 0; y < height; y++)
        { 
        	CSGThreadColorRow generationByLine = new CSGThreadColorRow(csgInstruction,y);
        	csgGenerationByLines.add(generationByLine);
        	executorService.execute(generationByLine);
        }
        
        executorService.shutdown();
        try
        {
            allComplete = executorService.awaitTermination(csgInstruction.getGenInstruction().getRowThreadTimeout(), TimeUnit.SECONDS);
        }
        catch (InterruptedException e)
        {
        	allComplete=false;
        }
        
        if (!allComplete) return;
        
        for(CSGThreadColorRow csgGenerationByLine : csgGenerationByLines)
        {
        	CSG currentCsg = csgGenerationByLine.getCsg();
        	if (currentCsg == null) continue;
        	csg=csg==null?currentCsg:csg.dumbUnion(currentCsg);
        }             	
	}
		
	public String writeSTLString()
	{
		return csg!=null?csg.toStlString():null;
	}
	
	public String getColorName()
	{
		return csgInstruction.getColorName();
	}

	public boolean isAllComplete() {
		return allComplete;
	}
}
