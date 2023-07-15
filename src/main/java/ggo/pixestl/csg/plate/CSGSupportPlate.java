package ggo.pixestl.csg.plate;

import java.awt.image.BufferedImage;

import eu.mihosoft.jcsg.CSG;
import eu.mihosoft.jcsg.Cube;
import eu.mihosoft.vvecmath.Transform;
import ggo.pixestl.generator.GenInstruction;

public class CSGSupportPlate {
	
	private CSG csg;
	
	public CSGSupportPlate() {
		csg=null;
	}
	
	public void generate(BufferedImage colorImage,GenInstruction genInstruction)
	{
		double colorPixelWidth=genInstruction.getColorPixelWidth();
		double plateThickness=genInstruction.getPlateThickness();
		double width=colorImage.getWidth()*colorPixelWidth;
		double height=colorImage.getHeight()*colorPixelWidth;
		               
	    csg = new Cube(width, height,plateThickness).toCSG();
	    Transform transform = Transform.unity().translateX((width-colorPixelWidth)/2).translateY((height-colorPixelWidth)/2)
	    		.translateZ(((plateThickness/2)-plateThickness));
	    csg = csg.transformed(transform);
	    
	}
	
	public String writeSTLString()
	{
		return csg!=null?csg.toStlString():null;
	}
	
	

	
	
	
	
	
}
