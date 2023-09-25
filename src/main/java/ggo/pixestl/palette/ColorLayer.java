package ggo.pixestl.palette;

import java.awt.*;
import java.util.Comparator;

import ggo.pixestl.util.ColorUtil;

public class ColorLayer 
{
	
	final private String hexCode;
	final private int layer;
	final private double c;
	final private double m;
	final private double y;
	final private double k;


	public ColorLayer(String hexCode,int layer, double h, double s, double l)
	{
        this.hexCode = hexCode;
        this.layer = layer;

        double[] cmyk = ColorUtil.hslToCmyk(h, s, l);

        c = cmyk[0];
        m = cmyk[1];
        y = cmyk[2];
        k = cmyk[3];
    }

    public String getHexCode() {
        return hexCode;
    }
    public int getLayer() {
        return layer;
    }

    public double getC() {
        return c;
    }
    public double getM() {
        return m;
    }
    public double getY() {
        return y;
    }
    public double getK() {
        return k;
    }
	public static class LayerComparator implements Comparator<ColorLayer> {
        public int compare(ColorLayer lCL1, ColorLayer lCL2)
        {
            Color c1 = Color.decode(lCL1.getHexCode());
            Color c2 = Color.decode(lCL2.getHexCode());

            // Calculez la luminance en utilisant la formule Y = 0.299*R + 0.587*G + 0.114*B
            double k1 = ColorUtil.colorToCMYK(c1)[3];
            double k2 = ColorUtil.colorToCMYK(c2)[3];

            // Triez de la plus foncée à la plus claire
            return Double.compare(k2, k1);
        }
    }
	

}
