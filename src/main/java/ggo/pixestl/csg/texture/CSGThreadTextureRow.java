package ggo.pixestl.csg.texture;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import eu.mihosoft.jcsg.Polygon;
import eu.mihosoft.vvecmath.Vector3d;
import ggo.pixestl.csg.CSGWorkData;
import ggo.pixestl.util.ColorUtil;

public class CSGThreadTextureRow implements Runnable
{	
	final private CSGWorkData csgInstruction;
    final private int y;
    final private List<Polygon> polygonList;
    
	
   

	public CSGThreadTextureRow(CSGWorkData csgInstruction,int y)
	{
		this.csgInstruction=csgInstruction;
		this.y=y;
		polygonList= new ArrayList<>();	
	}
    
	public void run()
	{
		int width = csgInstruction.getTexturedImage().getWidth();
		int height = csgInstruction.getTexturedImage().getHeight();
		
		double pixelWidth=csgInstruction.getGenInstruction().getTexturePixelWidth();
		
		double diffW=0;
		double diffH=0;
		
		if (csgInstruction.getGenInstruction().isColorLayer())
		{
			diffW= pixelWidth*width - csgInstruction.getColorImage().getWidth()*csgInstruction.getGenInstruction().getColorPixelWidth();
			diffH= pixelWidth*height - csgInstruction.getColorImage().getHeight()*csgInstruction.getGenInstruction().getColorPixelWidth();
		}		
		
		double iMid=((double) width /2)*pixelWidth;
		double jMid=((double) height /2)*pixelWidth;
		
        for (int x = 0; x < width-1; x++)
        {
        	double i=x*pixelWidth;
        	if (x==0)
        	{
        		i=diffW/2-pixelWidth/2;
        	}
        	
        	double i1=x*pixelWidth+pixelWidth;
        	if (x==width-2) i1+=-diffW/2+pixelWidth/2;
        	
    		double j=y*pixelWidth;	
    		if (y==0)
    		{
    			j=diffH/2-pixelWidth/2;
    		}

    		double j1=y*pixelWidth+pixelWidth;
    		if (y==height-2) j1+=-diffH/2+pixelWidth/2;
    		
        	if (x == 0)
        	{
                List<Vector3d> triangle1 = new ArrayList<>();
                triangle1.add(Vector3d.xyz(i,j, getPixelHeight(x, y)));
                triangle1.add(Vector3d.xyz(i,j1, getPixelHeight(x, y+1)));
                triangle1.add(Vector3d.xyz(i,j1, 0));
                polygonList.add(Polygon.fromPoints(triangle1));

                List<Vector3d> triangle2 = new ArrayList<>();
                triangle2.add(Vector3d.xyz(i,j, getPixelHeight(x, y)));
                triangle2.add(Vector3d.xyz(i,j, 0));
                triangle2.add(Vector3d.xyz(i,j1, 0));
                polygonList.add(Polygon.fromPoints(triangle2));

                List<Vector3d> triangle3 = new ArrayList<>();
                triangle3.add(Vector3d.xyz(i,j, 0));
                triangle3.add(Vector3d.xyz(i,j1, 0));
                triangle3.add(Vector3d.xyz(iMid,jMid, 0));
                polygonList.add(Polygon.fromPoints(triangle3));      
        	}
        	
        	if (y == 0)
        	{
                List<Vector3d> triangle1 = new ArrayList<>();
                triangle1.add(Vector3d.xyz(i,j, getPixelHeight(x, y)));
                triangle1.add(Vector3d.xyz(i1,j, getPixelHeight(x+1, y)));
                triangle1.add(Vector3d.xyz(i1,j, 0));
                polygonList.add(Polygon.fromPoints(triangle1));

                List<Vector3d> triangle2 = new ArrayList<>();
                triangle2.add(Vector3d.xyz(i,j, getPixelHeight(x, y)));
                triangle2.add(Vector3d.xyz(i,j, 0));
                triangle2.add(Vector3d.xyz(i1,j, 0));
                polygonList.add(Polygon.fromPoints(triangle2));
                
                List<Vector3d> triangle3 = new ArrayList<>();
                triangle3.add(Vector3d.xyz(i,j, 0));
                triangle3.add(Vector3d.xyz(i1,j, 0));
                triangle3.add(Vector3d.xyz(iMid,jMid, 0));
                polygonList.add(Polygon.fromPoints(triangle3));
        	}
        	
        	if (x == width-2)
        	{
                List<Vector3d> triangle1 = new ArrayList<>();
                triangle1.add(Vector3d.xyz(i1,j, getPixelHeight(x+1, y)));
                triangle1.add(Vector3d.xyz(i1,j1, getPixelHeight(x+1, y+1)));
                triangle1.add(Vector3d.xyz(i1,j1, 0));
                polygonList.add(Polygon.fromPoints(triangle1));

                List<Vector3d> triangle2 = new ArrayList<>();
                triangle2.add(Vector3d.xyz(i1,j, getPixelHeight(x+1, y)));
                triangle2.add(Vector3d.xyz(i1,j, 0));
                triangle2.add(Vector3d.xyz(i1,j1, 0));
                polygonList.add(Polygon.fromPoints(triangle2));
                
                List<Vector3d> triangle3 = new ArrayList<>();
                triangle3.add(Vector3d.xyz(i1,j, 0));
                triangle3.add(Vector3d.xyz(i1,j1, 0));
                triangle3.add(Vector3d.xyz(iMid,jMid, 0));
                polygonList.add(Polygon.fromPoints(triangle3));
        	}
        	
        	if (y == height-2)
        	{
                List<Vector3d> triangle1 = new ArrayList<>();
                triangle1.add(Vector3d.xyz(i,j1, getPixelHeight(x, y+1)));
                triangle1.add(Vector3d.xyz(i1,j1, getPixelHeight(x+1, y+1)));
                triangle1.add(Vector3d.xyz(i1,j1, 0));
                polygonList.add(Polygon.fromPoints(triangle1));

                List<Vector3d> triangle2 = new ArrayList<>();
                triangle2.add(Vector3d.xyz(i,j1, getPixelHeight(x, y+1)));
                triangle2.add(Vector3d.xyz(i,j1, 0));
                triangle2.add(Vector3d.xyz(i1,j1, 0));
                polygonList.add(Polygon.fromPoints(triangle2));
                
                List<Vector3d> triangle3 = new ArrayList<>();
                triangle3.add(Vector3d.xyz(i,j1, 0));
                triangle3.add(Vector3d.xyz(i1,j1, 0));
                triangle3.add(Vector3d.xyz(iMid,jMid, 0));
                polygonList.add(Polygon.fromPoints(triangle3));
        	}
        	       	
            List<Vector3d> triangle1 = new ArrayList<>();
            triangle1.add(Vector3d.xyz(i,j, getPixelHeight(x, y)));
            triangle1.add(Vector3d.xyz(i,j1, getPixelHeight(x, y+1)));
            triangle1.add(Vector3d.xyz(i1,j, getPixelHeight(x+1,y)));
            polygonList.add(Polygon.fromPoints(triangle1));

            List<Vector3d> triangle2 = new ArrayList<>();
            triangle2.add(Vector3d.xyz(i1, j1,getPixelHeight(x+1,y+1)));
            triangle2.add(Vector3d.xyz(i, j1,getPixelHeight(x,y+1)));
            triangle2.add(Vector3d.xyz(i1,j,getPixelHeight(x+1,y)));
            polygonList.add(Polygon.fromPoints(triangle2));
        }
	}
	
	
	public double getPixelHeight(int x, int y)
	{		
		double layerHeight;
		double maxThickness=csgInstruction.getGenInstruction().getTextureMaxThickness();
		double minThickness=csgInstruction.getGenInstruction().getTextureMinThickness();
		
	
		int pixelColorInt = csgInstruction.getTexturedImage().getRGB(x, y);
        Color pixelColor = new Color(pixelColorInt);
        
        layerHeight =ColorUtil.colorToCMYK(pixelColor)[3];
        layerHeight*=(maxThickness-minThickness);
        layerHeight+=minThickness;
        return layerHeight;
	}
	
	 protected List<Polygon> getPolys() {
		return polygonList;
	}
}
