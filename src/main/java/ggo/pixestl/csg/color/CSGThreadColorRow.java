package ggo.pixestl.csg.color;

import eu.mihosoft.jcsg.CSG;
import eu.mihosoft.jcsg.Cube;
import eu.mihosoft.vvecmath.Transform;
import ggo.pixestl.csg.CSGThread;
import ggo.pixestl.csg.CSGThreadRow;
import ggo.pixestl.palette.ColorCombi;
import ggo.pixestl.palette.ColorLayer;
import ggo.pixestl.util.ColorUtil;
import ggo.pixestl.util.ImageUtil;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.List;

public class CSGThreadColorRow extends CSGThreadRow
{

    public CSGThreadColorRow(CSGThread csgThread)
    {
        super(csgThread);
    }
	public void run()
	{
		int width = csgWorkData.getColorImage().getWidth();

        BufferedImage img= csgWorkData.getColorImage();
        boolean transparentMode = ImageUtil.hasATransparentPixel(img);


        for (String colorName : csgWorkData.getHexCode())
        {
            for (int x = 0; x < width; x++) {
                if (ColorUtil.transparentPixel(img, x, y)) continue;
                if (transparentMode) {
                    if (ColorUtil.hasATransparentPixelAsNeighbor(img, x, y)) continue;
                }

                int pixel = img.getRGB(x, y);
                int k=1;
                int pixelNext;
                for (;x+k<width;k++)
                {
                    pixelNext = img.getRGB(x+k, y);
                    if (pixelNext != pixel) break;
                    else if (ColorUtil.hasATransparentPixelAsNeighbor(img, x+k, y)) break;
                }
                k--;

                Color pixelColor = new Color(pixel);

                ColorCombi colorCombi = csgWorkData.getPalette().getColorCombi(pixelColor);

                List<ColorLayer> colorLayerList = colorCombi.getLayerList(colorName);

                for (ColorLayer layer : colorLayerList) {

                    int layerHeight = layer.getLayer();

                    if (layerHeight == 0) continue;
                    double onePixelHeightSize = csgWorkData.getGenInstruction().getColorPixelLayerThickness();

                    int layerBefore = colorCombi.getLayerPosition(layer);

                    if (csgWorkData.getOffset() != -1 && csgWorkData.getLayerMax() != -1) {
                        if (layerBefore >= csgWorkData.getOffset() + csgWorkData.getLayerMax()) continue;

                        if (layerBefore < csgWorkData.getOffset()) {
                            if (layerBefore + layerHeight < csgWorkData.getOffset()) continue;
                            int delta = csgWorkData.getOffset() - layerBefore;
                            layerHeight -= delta;
                            layerBefore = 0;
                            if (layerHeight > csgWorkData.getLayerMax()) {
                                layerHeight = csgWorkData.getLayerMax();
                            }
                        } else {
                            if (layerBefore <= csgWorkData.getOffset()) layerBefore = 0;
                            if (layerBefore > csgWorkData.getOffset()) {
                                layerBefore -= csgWorkData.getOffset();
                            }

                            if (layerHeight + layerBefore > csgWorkData.getLayerMax()) {
                                int delta = layerHeight + layerBefore - csgWorkData.getLayerMax();
                                layerHeight -= delta;
                            }
                        }
                        if (layerHeight == 0) continue;
                    }
                    double curPixelHeight = onePixelHeightSize * layerHeight;

                    double curPixelHeightAdjust = (curPixelHeight / 2);

                    curPixelHeightAdjust += layerBefore * onePixelHeightSize;

                    double pixelWidth = csgWorkData.getGenInstruction().getColorPixelWidth();

                    CSG square = new Cube(pixelWidth+k*pixelWidth, pixelWidth, curPixelHeight).toCSG();
                    Transform transform = Transform.unity()
                            .translateX(x*pixelWidth+(pixelWidth*k)/2)
                            .translateY(y * pixelWidth)
                            .translateZ(curPixelHeightAdjust);
                    square = square.transformed(transform);

                    savePolygonList(square.getPolygons());
                }
                x+=k;
            }
        }
	}


}
