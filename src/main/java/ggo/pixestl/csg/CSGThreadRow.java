package ggo.pixestl.csg;

import java.util.ArrayList;
import java.util.List;
import eu.mihosoft.jcsg.Polygon;


public abstract class CSGThreadRow implements Runnable
{
    protected CSGWorkData csgWorkData=null;
    protected int y=0;
    final protected List<Polygon> polygonList;

    public CSGThreadRow()
    {
        polygonList= new ArrayList<>();
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
