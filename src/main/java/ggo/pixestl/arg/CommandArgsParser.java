package ggo.pixestl.arg;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import ggo.pixestl.generator.GenInstruction;

public class CommandArgsParser extends GenInstruction {
	
	
	static final List<Option> reqArgList = new ArrayList<>();
	static final List<Option> optArgList = new ArrayList<>();
	
	static private Options generateOptions()
	{
		final Options options = new Options();	

		
	
		reqArgList.add(new Option("i", "srcImagePath", true, "Path to the source image."));
		reqArgList.add(new Option("p", "palettePath", true, "Path to the palette file."));
		reqArgList.add(new Option("w", "destImageWidth", true, "Width of the destination image (mm)."));

		optArgList.add(new Option("o", "destZipPath", true, "Destination ZIP file path.\nDefault: <-image>.zip"));
		optArgList.add(new Option("c", "colorNumber", true, "Maximum number of color number.\nDefault: no limits"));
		optArgList.add(new Option("F", "pixelCreationMethod", true, "Method for pixel creation [ADDITIVE,FULL].\nDefault: "+GenInstruction.ADD));
		optArgList.add(new Option("d", "colorDistanceComputation", true, "Method for pixel color distance computation [RGB,CIELab].\nDefault: "+GenInstruction.CIELab));
		optArgList.add(new Option("f", "plateThickness", true, "Thickness of the plate (mm).\nDefault: "+GenInstruction.DEFAULT_VALUE_PLATE_THICKNESS));
		optArgList.add(new Option("l", "colorLayerNumber", true, "Number of color pixel layers.\nDefault: "+GenInstruction.DEFAULT_VALUE_COLOR_LAYER_NUMBER));
		optArgList.add(new Option("b", "colorPixelLayerThickness", true, "Thickness of each color pixel layer (mm).\nDefault: "+GenInstruction.DEFAULT_VALUE_COLOR_PIXEL_LAYER_THICKNESS));
		optArgList.add(new Option("cW", "colorPixelWidth", true, "Width of color pixels (mm).\nDefault: "+GenInstruction.DEFAULT_VALUE_COLOR_PIXEL_WIDTH));
		optArgList.add(new Option("M", "textureMaxThickness", true, "Maximum thickness of the texture (mm).\nDefault: "+GenInstruction.DEFAULT_VALUE_TEXTURE_MAX_THICKNESS));
		optArgList.add(new Option("m", "textureMinThickness", true, "Minimum  thickness of the texture (mm).\nDefault: "+GenInstruction.DEFAULT_VALUE_TEXTURE_MIN_THICKNESS));
		optArgList.add(new Option("tW", "texturePixelWidth", true, "Width of texture pixels (mm).\nDefault: "+GenInstruction.DEFAULT_VALUE_TEXTURE_PIXEL_WIDTH));
		optArgList.add(new Option("n", "layerThreadMaxNumber", true, "Maximum number of threads for layers generation.\nDefault: 1 by STL layer"));
		optArgList.add(new Option("t", "layerThreadTimeout", true, "Timeout for layer threads (second).\nDefault: "+GenInstruction.DEFAULT_VALUE_LAYER_THREAD_TIMEOUT));
		optArgList.add(new Option("N", "rowThreadMaxNumber", true, "Number of threads for rows generation.\nDefault: "+GenInstruction.DEFAULT_VALUE_ROW_THREAD_MAX_NUMBER));
		optArgList.add(new Option("T", "rowThreadTimeout", true, "Timeout for row threads (second).\nDefault : "+GenInstruction.DEFAULT_VALUE_ROW_THREAD_TIMEOUT));
		optArgList.add(new Option("X", "debug", false, "Debug mode"));
		
		optArgList.add(new Option("z", "colorLayer", true, "Color layers will generate or not. Default : "+DEFAULT_VALUE_COLOR_LAYER));
		optArgList.add(new Option("Z", "textureLayer", true, "Texture layers will generate or not. Default : "+DEFAULT_VALUE_TEXTURE_LAYER));
		
		
		for(Option o : reqArgList)
		{
			o.setRequired(true);
			options.addOption(o);
		}
		for(Option o : optArgList) options.addOption(o);
		
		return options;		
	}
	
	static public GenInstruction argsToGenInstruction(String[] args) throws Exception
	{
		CommandLine line = null;
		Options options=generateOptions();
		try
		{
		    CommandLineParser parser = new DefaultParser();
		    line = parser.parse(options, args);
		}
		catch (ParseException exp) {
	        System.err.println("Parsing failed.  Reason: " + exp.getMessage());
	        HelpFormatter formatter = new HelpFormatter();
	        formatter.printHelp("PIXEstL", options);
	        System.exit(-1);
	    }
		return computeInstruction(line);
	}
	
	static private GenInstruction computeInstruction(CommandLine line) throws Exception
	{
		CommandArgsParser genInstruction = new CommandArgsParser();
		
		Method[] methods = CommandArgsParser.class.getDeclaredMethods();
        for (Method method : methods)
        {
        	if (!method.getName().startsWith("set") || !method.getName().endsWith("String")) continue;
        	String methodName=method.getName();
        	String optionName = methodName.substring("set".length(),"set".length()+1).toLowerCase()
        			+methodName.substring("set".length()+1);
        	optionName=optionName.replaceAll("String","");
        	
        	if (line.hasOption(optionName))
        	{
        		method.setAccessible(true);
        		method.invoke(genInstruction,line.getOptionValues(optionName));
        	}
        }
		genInstruction.setDebug(line.hasOption("X"));
        if (genInstruction.getDestZipPath() == null)
        {
	        String outName=genInstruction.getSrcImagePath();
			int idx = genInstruction.getSrcImagePath().lastIndexOf('.');	        
	        if (idx > 0) {
	        	outName = genInstruction.getSrcImagePath().substring(0, idx);
	        }
	        outName+=".zip";
	        genInstruction.setDestZipPath(outName);
        }        
        return genInstruction;
	}

	public void setSrcImagePathString(String srcImagePath) {
		this.srcImagePath = srcImagePath;
	}

	public void setDestZipPathString(String destZipPath) {
		this.destZipPath = destZipPath;
	}
	
	public void setPalettePathString(String palettePath) {
		this.palettePath = palettePath;
	}	
	
	public void setDestImageWidthString(String destImageWidth) {
		this.destImageWidth = Double.parseDouble(destImageWidth);
	}
	
	public void setPixelCreationMethodString(String pixelCreationMethodString)
	{	
		pixelCreationMethod=null;
		if (ADD.equals(pixelCreationMethodString)) pixelCreationMethod=PixelCreationMethod.ADDITIVE;
		else if (FULL.equals(pixelCreationMethodString)) pixelCreationMethod=PixelCreationMethod.FULL;
		if (pixelCreationMethod == null) throw new IllegalArgumentException(pixelCreationMethodString+" don't match with "+ADD+" or "+FULL);
	}

	public void setColorDistanceComputationString(String colorDistanceComputationString)
	{
		colorDistanceComputation=null;
		if (RGB.equals(colorDistanceComputationString)) colorDistanceComputation=ColorDistanceComputation.RGB;
		else if (CIELab.equals(colorDistanceComputationString)) colorDistanceComputation=ColorDistanceComputation.CIELab;
		if (colorDistanceComputation == null) throw new IllegalArgumentException(colorDistanceComputationString+" don't match with "+RGB+" or "+CIELab);
	}
	
	public void setColorPixelWidthString(String colorPixelWidthString)
	{	
		this.colorPixelWidth = Double.parseDouble(colorPixelWidthString);
	}
	
	public void setTexturePixelWidthString(String texturePixelWidthString)
	{	
		this.texturePixelWidth = Double.parseDouble(texturePixelWidthString);
	}
	
	public void setColorLayerNumberString(String colorPixelLayerNumberString) {
		this.colorPixelLayerNumber = Integer.parseInt(colorPixelLayerNumberString);
	}
	
	public void setColorNumberString(String colorNumber) {
		this.colorNumber = Integer.parseInt(colorNumber);
	}

	public void setPlateThicknessString(String plateThicknessString) {
		this.plateThickness = Double.parseDouble(plateThicknessString);
	}

	public void setColorPixelLayerThicknessString(String colorPixelLayerThicknessString) {
		this.colorPixelLayerThickness = Double.parseDouble(colorPixelLayerThicknessString);
	}

	public void setTextureMinThicknessString(String textureMinThicknessString) {
		this.textureMinThickness = Double.parseDouble(textureMinThicknessString);
	}

	public void setTextureMaxThicknessString(String textureMaxThicknessString) {
		this.textureMaxThickness = Double.parseDouble(textureMaxThicknessString);
	}
	
	public void setLayerThreadMaxNumberString(String layerThreadMaxNumberString) {
		this.layerThreadMaxNumber = Integer.parseInt(layerThreadMaxNumberString);
	}

	public void setRowThreadNumberString(String rowThreadNumberString) {
		this.rowThreadNumber = Integer.parseInt(rowThreadNumberString);
	}

	public void setLayerThreadTimeoutString(String layerThreadTimeoutString) {
		this.layerThreadTimeout = Integer.parseInt(layerThreadTimeoutString);
	}

	public void setRowThreadTimeoutString(String rowThreadTimeoutString) {
		this.rowThreadTimeout = Integer.parseInt(rowThreadTimeoutString);
	}

	public void setColorLayerString(String colorLayerString) {
		this.colorLayer = Boolean.parseBoolean(colorLayerString);
	}

	public void setTextureLayerString(String textureLayerString) {
		this.textureLayer = Boolean.parseBoolean(textureLayerString);
	}
	
}
