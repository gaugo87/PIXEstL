package ggo.pixestl.csg;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import eu.mihosoft.jcsg.CSG;
import eu.mihosoft.jcsg.Polygon;

public abstract class CSGThread implements Runnable
{
    final private ExecutorService executorService;
    protected final CSGWorkData csgWorkData;
    final private List<CSGThreadRow> threadByRowList;
    protected CSG csg = null;
    private boolean allComplete;

    final private Class threadRowClass;

    public CSGThread(Class threadRowClass,CSGWorkData instruction)
    {
        this.threadRowClass=threadRowClass;
        this.csgWorkData=instruction;
        this.executorService = Executors.newFixedThreadPool(csgWorkData.getGenInstruction().getRowThreadNumber());
        this.threadByRowList = new ArrayList<>();
        this.allComplete=false;
    }

    public void run()
    {
        int height = getImageToProcess().getHeight();
        for (int y = 0; y < height; y++)
        {
            CSGThreadRow threadRow= CSGThreadRowFactory.newInstance(threadRowClass);
            if (threadRow == null ) continue;
            threadRow.init(csgWorkData,y);
            threadByRowList.add(threadRow);
            executorService.execute(threadRow);
        }

        executorService.shutdown();
        try
        {
            allComplete = executorService.awaitTermination(csgWorkData.getGenInstruction().getRowThreadTimeout(), TimeUnit.SECONDS);
        }
        catch (InterruptedException e)
        {
            allComplete=false;
        }

        if (!allComplete) return;

        List<Polygon> polys=new ArrayList<>();
        for(CSGThreadRow threadByRow : threadByRowList)
        {
            polys.addAll(threadByRow.getPolygonList());
        }
        if (polys.size()>0) {
            csg = CSG.fromPolygons(polys);
        }

        postProcessing();
    }

    abstract public void postProcessing();
    abstract public BufferedImage getImageToProcess();

    public String writeSTLString()
    {
        return csg!=null?csg.toStlString():null;
    }

    public String getThreadName()
    {
        return csgWorkData.getThreadName();
    }

    public boolean isAllComplete() {
        return allComplete;
    }
}
