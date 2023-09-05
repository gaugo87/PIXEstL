package ggo.pixestl.csg.color;

import java.awt.Color;
import java.awt.image.BufferedImage;
import eu.mihosoft.jcsg.CSG;
import eu.mihosoft.jcsg.Cube;
import eu.mihosoft.vvecmath.Transform;
import ggo.pixestl.csg.CSGThreadRow;
import ggo.pixestl.palette.ColorCombi;
import ggo.pixestl.util.ColorUtil;
import ggo.pixestl.util.ImageUtil;

public class CSGThreadColorRow extends CSGThreadRow
{
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

            int pixel = img.getRGB(x,y);

            Color pixelColor = new Color(pixel);
            
            ColorCombi colorCombi = csgWorkData.getPalette().getColorCombi(pixelColor);
            int layerHeight = colorCombi.getLayerHeight(csgWorkData.getHexCode());
            
            if (layerHeight == 0) continue;
            double onePixelHeightSize=csgWorkData.getGenInstruction().getColorPixelLayerThickness();

            int layerBefore=colorCombi.getLayerPosition(csgWorkData.getPalette(),csgWorkData.getHexCode());

            if (csgWorkData.getOffset() != -1 && csgWorkData.getLayerMax() != -1)
            {
                if (layerBefore >= csgWorkData.getOffset()+csgWorkData.getLayerMax()) continue;

                if (layerBefore < csgWorkData.getOffset())
                {
                    if (layerBefore+layerHeight<csgWorkData.getOffset()) continue;
                    int delta = csgWorkData.getOffset()-layerBefore;
                    layerHeight-=delta;
                    layerBefore=0;
                    if (layerHeight > csgWorkData.getLayerMax()) {
                        layerHeight=csgWorkData.getLayerMax();
                    }
                }
                else {
                    if (layerBefore <= csgWorkData.getOffset()) layerBefore=0;
                    if (layerBefore > csgWorkData.getOffset())
                    {
                        layerBefore-=csgWorkData.getOffset();
                    }

                    if (layerHeight + layerBefore > csgWorkData.getLayerMax()) {
                        int delta = layerHeight + layerBefore - csgWorkData.getLayerMax();
                        layerHeight -= delta;
                    }
                }
                if (layerHeight==0) continue;
            }
            double curPixelHeight=onePixelHeightSize*layerHeight;
            
            double curPixelHeightAdjust=(curPixelHeight/2);

            curPixelHeightAdjust+=layerBefore*onePixelHeightSize;
            
            double pixelWidth=csgWorkData.getGenInstruction().getColorPixelWidth();
            
            CSG square = new Cube(pixelWidth,pixelWidth,curPixelHeight).toCSG();
            Transform transform = Transform.unity()
            		.translateX(x*pixelWidth)
            		.translateY(y*pixelWidth)
            		.translateZ(curPixelHeightAdjust);
            square = square.transformed(transform);
            polygonList.addAll(square.getPolygons());
        }
        
	}


}
