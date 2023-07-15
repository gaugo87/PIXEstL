package ggo.pixestl.csg.color;

import java.awt.Color;

import eu.mihosoft.jcsg.CSG;
import eu.mihosoft.jcsg.Cube;
import eu.mihosoft.vvecmath.Transform;
import ggo.pixestl.csg.CSGWorkData;
import ggo.pixestl.palette.ColorCombi;

public class CSGThreadColorRow implements Runnable
{	
	final private CSGWorkData csgWorkData;
    final private int y;
    private CSG csg;
    
	
    public CSGThreadColorRow(CSGWorkData data,int y)
	{
		this.csgWorkData=data;
		this.y=y;
		
		csg=null;		
	}
    
	public void run()
	{
		int width = csgWorkData.getColorImage().getWidth();
  	        	
        for (int x = 0; x < width; x++)
        {
            int pixel = csgWorkData.getColorImage().getRGB(x,y);
            Color pixelColor = new Color(pixel);
            
            ColorCombi colorCombi = csgWorkData.getPalette().getColorCombi(pixelColor);
            int layerHeight = colorCombi.getLayerHeight(csgWorkData.getHexCode());
            
            if (layerHeight == 0) continue;
            double onePixelHeightSize=csgWorkData.getGenInstruction().getColorPixelLayerThickness();
            double curPixelHeight=onePixelHeightSize*layerHeight;
            
            double curPixelHeightAdjust=(curPixelHeight/2);
            int layerBefore=colorCombi.getLayerPosition(csgWorkData.getPalette(),csgWorkData.getHexCode());
            curPixelHeightAdjust+=layerBefore*onePixelHeightSize;
            
            double pixelWidth=csgWorkData.getGenInstruction().getColorPixelWidth();
            
            CSG square = new Cube(pixelWidth,pixelWidth,curPixelHeight).toCSG();
            Transform transform = Transform.unity()
            		.translateX(x*pixelWidth)
            		.translateY(y*pixelWidth)
            		.translateZ(curPixelHeightAdjust);
            square = square.transformed(transform);
            csg=csg==null?square:csg.dumbUnion(square);
        }
        
	}

	public CSG getCsg() {
		return csg;
	}
}
