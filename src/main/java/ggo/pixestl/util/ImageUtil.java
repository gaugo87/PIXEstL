package ggo.pixestl.util;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

public class ImageUtil
{
	
	
	public static BufferedImage resizeImage(BufferedImage image, double imageWidthMm, double pixelMm) {
		int height = image.getHeight();
		int width = image.getWidth();
		
		int nbPixelWidth = (int)(imageWidthMm / pixelMm);
		int heightMm = (int)(height*imageWidthMm/width);
		int nbPixelHeight = (int)(heightMm / pixelMm);
        
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
