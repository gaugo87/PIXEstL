package ggo.pixestl.csg;

import eu.mihosoft.jcsg.Polygon;

import java.util.ArrayList;
import java.util.List;


public abstract class CSGThreadRow implements Runnable
{
    private final CSGThread csgThread;
    protected CSGWorkData csgWorkData=null;
    protected int y=0;
    final protected List<Polygon> polygonList;

    public CSGThreadRow(CSGThread csgThread)
    {
        polygonList= new ArrayList<>();
        this.csgThread=csgThread;
    }

    private void savePolygonListInTempFile(List<Polygon> polygonList)
    {
        csgThread.savePolygonListInTempFile(polygonList);
    }

    protected void savePolygonList(List<Polygon> polygonList)
    {
        if (csgWorkData.getGenInstruction().isLowMemory())
        {
            savePolygonListInTempFile(polygonList);
        }
        else {
            this.polygonList.addAll(polygonList);
        }
    }

    public void init(CSGWorkData csgWorkData,int y)
    {
        this.csgWorkData=csgWorkData;
        this.y=y;
    }

    abstract public void run();

    protected List<Polygon> getPolygonList() {
        return polygonList;
    }
}
