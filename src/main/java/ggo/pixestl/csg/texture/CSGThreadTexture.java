package ggo.pixestl.csg.texture;

import java.awt.image.BufferedImage;
import eu.mihosoft.vvecmath.Transform;
import ggo.pixestl.csg.CSGThread;
import ggo.pixestl.csg.CSGThreadRow;
import ggo.pixestl.csg.CSGWorkData;
import ggo.pixestl.generator.GenInstruction;

public class CSGThreadTexture<T extends CSGThreadRow> extends CSGThread<T>
{
	public CSGThreadTexture(Class c,CSGWorkData csgWorkData)
	{
		super(c,csgWorkData);
	}
	public void postProcessing()
	{
		if (csg == null) return;
        if (csgWorkData.getGenInstruction().isColorLayer())
        {
        	GenInstruction g = csgWorkData.getGenInstruction();
        	
        	double tW=csgWorkData.getTexturedImage().getWidth()*g.getTexturePixelWidth();
        	double tH=csgWorkData.getTexturedImage().getHeight()*g.getTexturePixelWidth();
        	
        	double cW=csgWorkData.getColorImage().getWidth()*g.getColorPixelWidth();
        	double cH=csgWorkData.getColorImage().getHeight()*g.getColorPixelWidth();
        	
        	double diffW=tW-cW;
        	double diffH=tH-cH;
        	
	        Transform transform = Transform.unity()
	        		.translateX(-diffW/2-(g.getColorPixelWidth()-g.getTexturePixelWidth())/2)
	        		.translateY(-diffH/2-(g.getColorPixelWidth()-g.getTexturePixelWidth())/2)
	        		.translateZ(csgWorkData.getGenInstruction().getColorPixelLayerThickness()*csgWorkData.getGenInstruction().getColorPixelLayerNumber());
	        csg = csg.transformed(transform);
        }
	}

	@Override
	public BufferedImage getImageToProcess() {
		return csgWorkData.getTexturedImage();
	}
}
