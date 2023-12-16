package ggo.pixestl.csg.texture;

import ggo.pixestl.csg.CSGThread;
import ggo.pixestl.csg.CSGWorkData;

import java.awt.image.BufferedImage;

public class CSGThreadTexture extends CSGThread
{
	public CSGThreadTexture(Class c,CSGWorkData csgWorkData)
	{
		super(c,csgWorkData);
	}
	public void postProcessing()
	{
		//Nothing
	}

	@Override
	public BufferedImage getImageToProcess() {
		return csgWorkData.getTexturedImage();
	}
}
