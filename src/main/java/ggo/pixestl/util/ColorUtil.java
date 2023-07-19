package ggo.pixestl.util;

import java.awt.Color;
import java.util.List;

public class ColorUtil {
	
	public static Color findClosestColor(Color targetColor, List<Color> colors)
	{
		int minDistance = Integer.MAX_VALUE;
		Color closestColor = colors.get(0);

		for (Color color : colors)
		{
			int distance = colorDistance(targetColor, color);
			if (distance < minDistance)
			{
				minDistance = distance;
				closestColor = color;
			}
		}
		return closestColor;
	}

	private static int colorDistance(Color color1, Color color2)
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

    public static double[] colorToCMYK(Color color) {
    	double r = color.getRed() / 255f;
    	double g = color.getGreen() / 255f;
    	double b = color.getBlue() / 255f;

    	double k = 1 - Math.max(Math.max(r, g), b);
    	double c = (1 - r - k) / (1 - k);
    	double m = (1 - g - k) / (1 - k);
    	double y = (1 - b - k) / (1 - k);

        return new double[]{c, m, y, k};
    }

    public static String colorToHexCode(Color color) {
        int red = color.getRed();
        int green = color.getGreen();
        int blue = color.getBlue();

        return String.format("#%02X%02X%02X", red, green, blue);
    }
    
    

}
