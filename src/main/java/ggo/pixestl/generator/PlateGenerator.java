package ggo.pixestl.generator;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.imageio.ImageIO;

import ggo.pixestl.palette.Palette;
import ggo.pixestl.stl.StlMaker;
import ggo.pixestl.util.ImageUtil;


public class PlateGenerator {
	

	public PlateGenerator()
	{
		
	}
	
	
	public void process(GenInstruction genInstruction) throws Exception
	{		
		File srcImageFile= new File(genInstruction.getSrcImagePath());		
		Palette palette =new Palette(genInstruction.getPalettePath(),genInstruction);
		
		
		BufferedImage image = ImageIO.read(srcImageFile);
		
		if (genInstruction.getColorNumber()!=0)
		{
			palette.restrictColors(image,genInstruction.getColorNumber());
		}

		
		BufferedImage quantizedColorImage = null;
		BufferedImage textureImage = null;
		
		if (genInstruction.isColorLayer())
		{
			BufferedImage colorImage=ImageUtil.resizeImage(image, genInstruction.getDestImageWidth(),genInstruction.getColorPixelWidth());			
			quantizedColorImage = palette.quantizeColors(colorImage);
		}
		if (genInstruction.isTextureLayer()) textureImage=ImageUtil.convertToBlackAndWhite(ImageUtil.resizeImage(image,genInstruction.getDestImageWidth(),genInstruction.getTexturePixelWidth()));
		
		
		BufferedImage flipColorImage =quantizedColorImage!=null?ImageUtil.flipImage(quantizedColorImage):null;
		BufferedImage flipTextureImage = textureImage!=null?ImageUtil.flipImage(textureImage):null;
		StlMaker maker = new StlMaker(flipColorImage,flipTextureImage,palette,genInstruction);
		
		try (ZipOutputStream zipOut = new ZipOutputStream(Files.newOutputStream(Paths.get(genInstruction.getDestZipPath()))))
		{
			
			if (quantizedColorImage != null)
			{
				ZipEntry zipEntry = new ZipEntry("image-color-preview.png");
				zipOut.putNextEntry(zipEntry);
				ImageIO.write(quantizedColorImage, "png", zipOut);
				zipOut.closeEntry();
			}
			if (textureImage != null)
			{
				ZipEntry zipEntry = new ZipEntry("image-texture-preview.png");
				zipOut.putNextEntry(zipEntry);
				ImageIO.write(textureImage, "png", zipOut);
				zipOut.closeEntry();				
			}			
			maker.process(zipOut);
			
        }
		catch (IOException e) {
            e.printStackTrace();
        }
	}
	
	

}
