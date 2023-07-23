package ggo.pixestl.csg.plate;

import ggo.pixestl.csg.CSGThread;
import ggo.pixestl.csg.CSGWorkData;
import java.awt.image.BufferedImage;


public class CSGThreadSupport extends CSGThread<CSGThreadSupportRow>
{
	public CSGThreadSupport(Class c, CSGWorkData csgWorkData)
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
