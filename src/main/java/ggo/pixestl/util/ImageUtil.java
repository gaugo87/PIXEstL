package ggo.pixestl.util;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.text.DecimalFormat;

public class ImageUtil
{

    public static void checkRatio(BufferedImage image, double imageWidthMm, double imageHeightMm)
    {
        if(imageWidthMm == 0 || imageHeightMm== 0) return;
        int height = image.getHeight();
        int width = image.getWidth();

        double ratioSrc = (double)width/(double)height;

        DecimalFormat decimalFormat = new DecimalFormat("#.##");
        String ratioSrcString = decimalFormat.format(ratioSrc);

        double ratioDest = imageWidthMm / imageHeightMm;
        String ratioDestString = decimalFormat.format(ratioDest);

        if (!ratioSrcString.equals(ratioDestString))
        {
            System.out.println("Warning : The image ratio is not preserved. (Source ratio:"+ratioSrcString+"; Destination ratio:"+ratioDestString+")");
        }
    }

	
	public static BufferedImage resizeImage(BufferedImage image, double imageWidthMm, double imageHeightMm, double pixelMm) {
		int height = image.getHeight();
		int width = image.getWidth();

        int nbPixelWidth;
        int nbPixelHeight;

        if (imageWidthMm != 0 && imageHeightMm == 0)
        {
            nbPixelWidth = (int)(imageWidthMm / pixelMm);
            int heightMm = (int)(height*imageWidthMm/width);
            nbPixelHeight = (int)(heightMm / pixelMm);
        }
		else if (imageWidthMm == 0 && imageHeightMm != 0)
        {
            nbPixelHeight = (int)(imageHeightMm / pixelMm);
            int widthMm = (int)(width*imageHeightMm/height);
            nbPixelWidth = (int)(widthMm / pixelMm);
        }
        else
        {
            nbPixelWidth = (int)(imageWidthMm / pixelMm);
            nbPixelHeight = (int)(imageHeightMm / pixelMm);
        }
        
        BufferedImage resizedImage = new BufferedImage(nbPixelWidth, nbPixelHeight, image.getType());
        Graphics2D g2d = resizedImage.createGraphics();
        g2d.drawImage(image, 0, 0, nbPixelWidth, nbPixelHeight, null);
        g2d.dispose();
		return resizedImage;
	}

    public static boolean hasATransparentPixel(BufferedImage image)
    {
        if (image.getType() != BufferedImage.TYPE_INT_ARGB) return false;
        int height = image.getHeight();
        int width = image.getWidth();

        for (int y = 0; y < height; y++)
        {
            for (int x = 0; x < width; x++)
            {
               if (ColorUtil.transparentPixel(image,x,y)) return true;
            }
        }
        return false;
    }

    public static BufferedImage convertToBlackAndWhite(BufferedImage image) {
        int width = image.getWidth();
        int height = image.getHeight();
        
        BufferedImage resultImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (ColorUtil.transparentPixel(image,x,y))
                {
                    resultImage.setRGB(x, y,0x00000000 );
                    continue;
                }
                Color color = new Color(image.getRGB(x, y));
                int luminance = (int) (0.2126 * color.getRed() + 0.7152 * color.getGreen() + 0.0722 * color.getBlue());
                Color grayscaleColor = new Color(luminance, luminance, luminance);
                resultImage.setRGB(x, y, grayscaleColor.getRGB());
            }
        }
        
        return resultImage;
    }
	
	
	public static BufferedImage flipImage(BufferedImage image)
	{
        BufferedImage flippedImage = new BufferedImage(image.getWidth(), image.getHeight(), image.getType());
        AffineTransform transformV = AffineTransform.getScaleInstance(1, -1);
        transformV.translate(0, image.getHeight()*-1);
        Graphics2D g2d = flippedImage.createGraphics();
        g2d.drawImage(image, transformV, null);        
        g2d.dispose();
        return flippedImage;
    }
}
