package ggo.pixestl.csg;

import java.awt.image.BufferedImage;

import ggo.pixestl.generator.GenInstruction;
import ggo.pixestl.palette.Palette;

public class CSGWorkData
{
	final private BufferedImage colorImage;
	final private BufferedImage texturedImage;
	final private Palette palette;
	final private String hexCode;
	final private String colorName;

	
	GenInstruction genInstruction;
	

	public CSGWorkData(BufferedImage colorImage,BufferedImage texturedImage,Palette palette,
			String colorName,String hexCode,GenInstruction genInstruction)
	{
		this.colorImage=colorImage;
		this.texturedImage=texturedImage;
		this.palette=palette;
		this.colorName=colorName;
		this.hexCode=hexCode;	
		this.genInstruction=genInstruction;
	}

	public BufferedImage getColorImage() {
		return colorImage;
	}

	public BufferedImage getTexturedImage() {
		return texturedImage;
	}

	public Palette getPalette() {
		return palette;
	}

	public String getHexCode() {
		return hexCode;
	}

	public String getColorName() {
		return colorName;
	}

	public GenInstruction getGenInstruction() {
		return genInstruction;
	}

}
