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
	final private String threadName;
	final GenInstruction genInstruction;

	final double curve;
	final int offset;
	final int layerMax;


	public CSGWorkData(BufferedImage colorImage,BufferedImage texturedImage,Palette palette,
					   String threadName,String hexCode,GenInstruction genInstruction)
	{
		this(colorImage,texturedImage,palette,threadName,hexCode,0,-1,-1,genInstruction);
	}

	public CSGWorkData(BufferedImage colorImage,BufferedImage texturedImage,Palette palette,
			String threadName,String hexCode,double curve,int offset, int layerMax, GenInstruction genInstruction)
	{
		this.colorImage=colorImage;
		this.texturedImage=texturedImage;
		this.palette=palette;
		this.threadName=threadName;
		this.hexCode=hexCode;	
		this.genInstruction=genInstruction;
		this.curve=curve;
		this.layerMax=layerMax;
		this.offset=offset;

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

	public String getThreadName() {
		return threadName;
	}

	public int getOffset() {return offset; }

	public int getLayerMax() {return layerMax; }

	public double getCurve() {return curve; }

	public GenInstruction getGenInstruction() {
		return genInstruction;
	}

}
