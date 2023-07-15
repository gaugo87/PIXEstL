package ggo.pixestl.palette;

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
        public int compare(ColorLayer lCL1, ColorLayer lCL2) {
            return Integer.compare(lCL1.getLayer(), lCL2.getLayer());
        }
    }
	

}
