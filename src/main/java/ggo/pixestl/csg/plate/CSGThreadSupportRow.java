package ggo.pixestl.csg.plate;

import eu.mihosoft.jcsg.CSG;
import eu.mihosoft.jcsg.Cube;
import eu.mihosoft.vvecmath.Transform;
import ggo.pixestl.csg.CSGThread;
import ggo.pixestl.csg.CSGThreadRow;
import ggo.pixestl.util.ColorUtil;
import ggo.pixestl.util.ImageUtil;

import java.awt.image.BufferedImage;

public class CSGThreadSupportRow extends CSGThreadRow
{
    public CSGThreadSupportRow(CSGThread csgThread)
    {
        super(csgThread);
    }

	public void run()
	{
		int width = csgWorkData.getColorImage().getWidth();

        BufferedImage img= csgWorkData.getColorImage();
        boolean transparentMode = ImageUtil.hasATransparentPixel(img);
  	        	
        for (int x = 0; x < width; x++)
        {
            if (ColorUtil.transparentPixel(img,x,y)) continue;
            if (transparentMode) {
                if (ColorUtil.hasATransparentPixelAsNeighbor(img, x, y)) continue;
            }

            int k=1;
            for (;x+k<width;k++)
            {
                if (ColorUtil.transparentPixel(img,x+k,y)) break;
                if (transparentMode) {
                    if (ColorUtil.hasATransparentPixelAsNeighbor(img, x+k, y)) break;
                }
            }
            k--;

            double pixelWidth=csgWorkData.getGenInstruction().getColorPixelWidth();
            double plateThickness=csgWorkData.getGenInstruction().getPlateThickness();

            CSG square = new Cube(pixelWidth+pixelWidth*k, pixelWidth,plateThickness).toCSG();
            Transform transform = Transform.unity().translateX(x*pixelWidth+(pixelWidth*k)/2)
                    .translateY(y*pixelWidth)
                    .translateZ(((plateThickness/2)-plateThickness));
            square = square.transformed(transform);

            savePolygonList(square.getPolygons());
            x+=k;
        }
        
	}
}
