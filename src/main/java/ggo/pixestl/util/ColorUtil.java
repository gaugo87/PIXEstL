package ggo.pixestl.util;

import ggo.pixestl.generator.GenInstruction;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class ColorUtil {

    public static Color findClosestColor(Color targetColor, List<Color> colors, GenInstruction.ColorDistanceComputation colorDistanceComputation)
    {
        return findClosestColorInternal(targetColor,colors,colorDistanceComputation);
    }

    private static Color findClosestColorInternal(Color targetColor, List<Color> colors, GenInstruction.ColorDistanceComputation colorDistanceComputation)
    {
        double minDistance = Double.MAX_VALUE;
        Color closestColor = colors.get(0);

        for (Color color : colors)
        {
            double distance;
            switch (colorDistanceComputation)
            {
                case RGB:
                    distance = colorDistanceRGB(targetColor, color);
                    break;
                case CIELab:
                default :
                    distance = colorDistanceCIELab(targetColor, color);
            }
            if (distance < minDistance)
            {
                minDistance = distance;
                closestColor = color;
            }
        }
        return closestColor;
    }

    private static int colorDistanceRGB(Color color1, Color color2)
    {
        int r1 = color1.getRed();
        int g1 = color1.getGreen();
        int b1 = color1.getBlue();

        int r2 = color2.getRed();
        int g2 = color2.getGreen();
        int b2 = color2.getBlue();

        int dr = r1 - r2;
        int dg = g1 - g2;
        int db = b1 - b2;

        return dr * dr + dg * dg + db * db;
    }

	private static double colorDistanceCIELab(Color color1, Color color2)
	{
        double[] lab1 = rgbToLab(color1.getRed(), color1.getGreen(), color1.getBlue());
        double[] lab2 = rgbToLab(color2.getRed(), color2.getGreen(), color2.getBlue());

        return deltaE(lab1[0], lab1[1], lab1[2], lab2[0], lab2[1], lab2[2]);
	}
	
	
	public static Color cmykToColor(double cyan, double magenta, double yellow, double black) {

        int red = (int) ((1 - cyan) * (1 - black) * 255);
        int green = (int) ((1 - magenta) * (1 - black) * 255);
        int blue = (int) ((1 - yellow) * (1 - black) * 255);

        return new Color(red, green, blue);
    }

	
	
	public static double[] hslToCmyk(double h, double s, double l) {
        double c, m, y, k;
        double r, g, b;
        
        s/=100;
        l/=100;

        if (s == 0)
        {
            c = 0;
            m = 0;
            y = 0;
            k = 1 - l;
        } else {
            double q = l < 0.5f ? l * (1 + s) : l + s - l * s;
            double p = 2 * l - q;
            double hk = h / 360f;

            r = hueToRgb(p, q, hk + 1 / 3f);
            g = hueToRgb(p, q, hk);
            b = hueToRgb(p, q, hk - 1 / 3f);

            c = (1 - r);
            m = (1 - g);
            y = (1 - b);
            k = Math.min(c, Math.min(m, y));
            c = (c - k) / (1 - k);
            m = (m - k) / (1 - k);
            y = (y - k) / (1 - k);
        }

        return new double[]{c, m, y, k};
    }

    public static double hueToRgb(double p, double q, double t) {
        if (t < 0) t += 1;
        if (t > 1) t -= 1;
        if (t < 1 / 6f) return p + (q - p) * 6 * t;
        if (t < 1 / 2f) return q;
        if (t < 2 / 3f) return p + (q - p) * (2 / 3f - t) * 6;
        return p;
    }
    
    public static double[] colorToHSL(Color color) {
        double r = color.getRed() / 255.0;
        double g = color.getGreen() / 255.0;
        double b = color.getBlue() / 255.0;

        double max = Math.max(r, Math.max(g, b));
        double min = Math.min(r, Math.min(g, b));

        double luminosity = (max + min) / 2.0;

        double saturation;
        if (max == min) {
            saturation = 0;
        } else {
            double delta = max - min;
            saturation = delta / (1 - Math.abs(2 * luminosity - 1));
        }

        double hue;
        if (max == min) {
            hue = 0;
        } else if (max == r) {
            hue = (60 * ((g - b) / (max - min)) + 360) % 360;
        } else if (max == g) {
            hue = (60 * ((b - r) / (max - min)) + 120) % 360;
        } else {
            hue = (60 * ((r - g) / (max - min)) + 240) % 360;
        }
        return new double[]{hue, saturation * 100, luminosity * 100};
    }

    public static boolean transparentPixel(BufferedImage image, int x, int y)
    {
        if (x<0 || x >= image.getWidth()) return true;
        if (y<0 || y >= image.getHeight()) return true;

        int pixel = image.getRGB(x, y);
        return (pixel & 0xFF000000) == 0;
    }

    public static boolean hasATransparentPixelAsNeighbor(BufferedImage image, int x, int y) {
        List<int[]> neighborList = new ArrayList<>();
        neighborList.add(new int [] {x,y+1});
        neighborList.add(new int [] {x+1,y});
        neighborList.add(new int [] {x,y-1});
        neighborList.add(new int [] {x-1,y});

        for (int[] neighborPixel : neighborList)
        {
            int xN = neighborPixel[0];
            int yN = neighborPixel[1];
            if (xN<0 || xN> image.getWidth()-1
                    || yN<0 || yN > image.getHeight()-1) return true;
            if (ColorUtil.transparentPixel(image,xN,yN)) return true;
        }
        return false;
    }

    public static double[] colorToCMYK(Color color) {
    	double r = color.getRed() / 255f;
    	double g = color.getGreen() / 255f;
    	double b = color.getBlue() / 255f;

    	double k = 1 - Math.max(Math.max(r, g), b);
        double c = 0;
        double m = 0;
        double y = 0;
        if (k < 1.0) {
            c = (1 - r - k) / (1 - k);
            m = (1 - g - k) / (1 - k);
            y = (1 - b - k) / (1 - k);
        }

        return new double[]{c, m, y, k};
    }

    public static String colorToHexCode(Color color) {
        int red = color.getRed();
        int green = color.getGreen();
        int blue = color.getBlue();

        return String.format("#%02X%02X%02X", red, green, blue);
    }

    public static double[] rgbToLab(int r, int g, int b) {
        double[] xyz = rgbToXyz(r, g, b);
        return xyzToLab(xyz[0], xyz[1], xyz[2]);
    }

    public static double[] rgbToXyz(int r, int g, int b) {
        double rr = pivotRgbToXyz((double) r / 255);
        double gg = pivotRgbToXyz((double) g / 255);
        double bb = pivotRgbToXyz((double) b / 255);

        // Convert using D65 illuminant
        double x = rr * 0.4124564 + gg * 0.3575761 + bb * 0.1804375;
        double y = rr * 0.2126729 + gg * 0.7151522 + bb * 0.0721750;
        double z = rr * 0.0193339 + gg * 0.1191920 + bb * 0.9503041;

        return new double[]{x * 100, y * 100, z * 100};
    }

    public static Color hexToColor(String hexColor)
    {
        if (hexColor.startsWith("#") && hexColor.length() == 7) {
            try {
                int r = Integer.parseInt(hexColor.substring(1, 3), 16);
                int g = Integer.parseInt(hexColor.substring(3, 5), 16);
                int b = Integer.parseInt(hexColor.substring(5, 7), 16);
                return new Color(r, g, b);
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Incorrect color format : " + hexColor);
            }
        } else {
            throw new IllegalArgumentException("Incorrect color format : " + hexColor);
        }
    }

    public static double[] xyzToLab(double x, double y, double z) {
        x /=  95.047;
        y /= 100.000;
        z /= 108.883;

        if (x > 0) x = pivotXyzToLab(x);
        if (y > 0) y = pivotXyzToLab(y);
        if (z > 0) z = pivotXyzToLab(z);

        double l = Math.max(0, 116 * y - 16);
        double a = (x - y) * 500;
        double b = (y - z) * 200;

        return new double[]{l, a, b};
    }

    private static double pivotRgbToXyz(double n) {
        return (n > 0.04045) ? Math.pow((n + 0.055) / 1.055, 2.4) : n / 12.92;
    }

    private static double pivotXyzToLab(double n) {
        return (n > Math.pow(6.0/29.0, 3)) ? Math.pow(n, 1.0/3.0) : (n / (3 * Math.pow(6.0/29.0, 2))) + 4.0/29.0;
    }

    public static double deltaE(double l1, double a1, double b1, double l2, double a2, double b2) {
        double dL = l2 - l1;
        double da = a2 - a1;
        double db = b2 - b1;

        return Math.sqrt(dL * dL + da * da + db * db);
    }


    public static class HexCodeComparator implements Comparator<String> {
        public int compare(String s1, String s2)
        {
            Color c1 = Color.decode(s1);
            Color c2 = Color.decode(s2);

            double e1 = c1.getBlue()+ c1.getGreen()+c1.getRed();
            double e2 = c2.getBlue()+c2.getGreen()+c2.getRed();

            return Double.compare(e1, e2);
        }
    }

}
