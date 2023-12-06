package ggo.pixestl.generator;

public class GenInstruction
{
	
	
	public enum PixelCreationMethod {ADDITIVE,FULL}
	public static final String ADD = "ADDITIVE";
	public static final String FULL = "FULL";


	public enum ColorDistanceComputation {RGB,CIELab}
	public static final String RGB = "RGB";
	public static final String CIELab = "CIELab";
	public static final double DEFAULT_VALUE_PLATE_THICKNESS=0.2;
	public static final int DEFAULT_VALUE_COLOR_LAYER_NUMBER=5;
	public static final double DEFAULT_VALUE_COLOR_PIXEL_LAYER_THICKNESS=0.1;
	public static final double DEFAULT_VALUE_COLOR_PIXEL_WIDTH=0.8;
	public static final double DEFAULT_VALUE_TEXTURE_MAX_THICKNESS=1.8;
	public static final double DEFAULT_VALUE_TEXTURE_MIN_THICKNESS=0.3;
	public static final double DEFAULT_VALUE_TEXTURE_PIXEL_WIDTH=0.25;
	public static final int DEFAULT_VALUE_LAYER_THREAD_TIMEOUT=120;
	public static final int DEFAULT_VALUE_ROW_THREAD_MAX_NUMBER=Runtime.getRuntime().availableProcessors();
	public static final int DEFAULT_VALUE_LAYER_THREAD_MAX_NUMBER=0;
	public static final int DEFAULT_VALUE_ROW_THREAD_TIMEOUT=60;
	public static final boolean DEFAULT_VALUE_COLOR_LAYER=true;
	public static final boolean DEFAULT_VALUE_TEXTURE_LAYER=true;

	public static final double DEFAULT_VALUE_CURVE=0.0;
	
	
	protected String srcImagePath;
	protected String destZipPath;
	
	protected String palettePath;
	protected int colorNumber;
	
	protected PixelCreationMethod pixelCreationMethod=PixelCreationMethod.ADDITIVE;

	protected ColorDistanceComputation colorDistanceComputation = ColorDistanceComputation.CIELab;
	
	protected double destImageWidth=0; //mm

	protected double destImageHeight=0; //mm
	
	protected double plateThickness=DEFAULT_VALUE_PLATE_THICKNESS; //mm
	
	protected double colorPixelWidth=DEFAULT_VALUE_COLOR_PIXEL_WIDTH; //mm
	protected double colorPixelLayerThickness=DEFAULT_VALUE_COLOR_PIXEL_LAYER_THICKNESS; //mm
	protected int colorPixelLayerNumber=DEFAULT_VALUE_COLOR_LAYER_NUMBER; //mm
	
	protected double texturePixelWidth=DEFAULT_VALUE_TEXTURE_PIXEL_WIDTH; //mm
	protected double textureMinThickness=DEFAULT_VALUE_TEXTURE_MIN_THICKNESS; //mm
	protected double textureMaxThickness=DEFAULT_VALUE_TEXTURE_MAX_THICKNESS; //mm
	
	
	protected int layerThreadMaxNumber=DEFAULT_VALUE_LAYER_THREAD_MAX_NUMBER;
	protected int rowThreadNumber=DEFAULT_VALUE_ROW_THREAD_MAX_NUMBER;
	protected int layerThreadTimeout=DEFAULT_VALUE_LAYER_THREAD_TIMEOUT; //s
	protected int rowThreadTimeout=DEFAULT_VALUE_ROW_THREAD_TIMEOUT; //s

	protected double curve=DEFAULT_VALUE_CURVE;
	
	protected boolean colorLayer=DEFAULT_VALUE_COLOR_LAYER;
	protected boolean textureLayer=DEFAULT_VALUE_TEXTURE_LAYER;

	protected boolean debug = false;
		
	public GenInstruction()
	{
	}
	
	
	public String getSrcImagePath() {
		return srcImagePath;
	}
	public String getDestZipPath() {
		return destZipPath;
	}
	public void setDestZipPath(String destZipPath) {
		this.destZipPath = destZipPath;
	}
	public String getPalettePath() {
		return palettePath;
	}
	public double getDestImageWidth() {
		return destImageWidth;
	}
	public double getDestImageHeight() {
		return destImageHeight;
	}
	public double getColorPixelWidth() {
		return colorPixelWidth;
	}
	public double getTexturePixelWidth() {
		return texturePixelWidth;
	}
	public PixelCreationMethod getPixelCreationMethod() {
		return pixelCreationMethod;
	}
	public ColorDistanceComputation getColorDistanceComputation() {
		return colorDistanceComputation;
	}
	public int getColorPixelLayerNumber() {
		return colorPixelLayerNumber;
	}
	public int getColorNumber() {
		return colorNumber;
	}
	public double getPlateThickness() {
		return plateThickness;
	}
	public double getColorPixelLayerThickness() {
		return colorPixelLayerThickness;
	}
	public double getTextureMinThickness() {
		return textureMinThickness;
	}
	public double getTextureMaxThickness() {
		return textureMaxThickness;
	}
	public int getLayerThreadMaxNumber() {
		return layerThreadMaxNumber;
	}
	public int getRowThreadNumber() {
		return rowThreadNumber;
	}
	public int getLayerThreadTimeout() {
		return layerThreadTimeout;
	}
	public int getRowThreadTimeout() {
		return rowThreadTimeout;
	}
	public boolean isColorLayer() {
		return colorLayer;
	}
	public boolean isTextureLayer() {
		return textureLayer;
	}
	public void setDebug(boolean debug) { this.debug=debug; }
	public boolean isDebug() { return debug; }

	public double getCurve() {
		return curve;
	}

	
}
