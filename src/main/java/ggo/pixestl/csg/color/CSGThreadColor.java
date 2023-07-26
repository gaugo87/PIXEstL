package ggo.pixestl.csg.color;

import java.awt.image.BufferedImage;
import ggo.pixestl.csg.CSGThread;
import ggo.pixestl.csg.CSGWorkData;

public class CSGThreadColor extends CSGThread
{
	public CSGThreadColor(Class c,CSGWorkData csgWorkData)
	{
		super(c,csgWorkData);
	}
	public void postProcessing()
	{
        //nothing
	}
	public BufferedImage getImageToProcess() {
		return csgWorkData.getColorImage();
	}

}
