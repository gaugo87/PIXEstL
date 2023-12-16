package ggo.pixestl.csg.texture;

import eu.mihosoft.jcsg.Polygon;
import eu.mihosoft.vvecmath.Vector3d;
import ggo.pixestl.csg.CSGThread;
import ggo.pixestl.util.ColorUtil;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

public class CSGThreadTextureRowWithTransparency extends CSGThreadTextureRow
{
    public CSGThreadTextureRowWithTransparency(CSGThread csgThread)
    {
        super(csgThread);
    }

	public void run()
	{
		int width = csgWorkData.getTexturedImage().getWidth();

        BufferedImage img = csgWorkData.getTexturedImage();
		double pixelWidth=csgWorkData.getGenInstruction().getTexturePixelWidth();

        for (int x = 0; x < width-1; x++)
        {
            List<Polygon> localPolygonList = new ArrayList<>();

            if (ColorUtil.transparentPixel(img,x,y)) continue;

        	double i=x*pixelWidth;
            double j=y*pixelWidth;
            double i1=x*pixelWidth+pixelWidth;
            double j1=y*pixelWidth+pixelWidth;
            double jm1=y*pixelWidth-pixelWidth;

            boolean toBuildA = true;
            boolean toBuildB = true;
            boolean toBuildC = true;
            boolean toBuildD = true;
            if (ColorUtil.transparentPixel(img,x+1,y))
            {
                toBuildB = false;
                toBuildC = false;
                toBuildD = false;
            }
            if (ColorUtil.transparentPixel(img,x+1,y+1)){
                toBuildA = false;
                toBuildB = false;
            }
            else {
                toBuildC = false;
            }
            if (ColorUtil.transparentPixel(img,x,y+1))
            {
                toBuildA = false;
                toBuildC = false;
            }
            if (ColorUtil.transparentPixel(img,x+1,y-1))
            {
                toBuildD = false;

            }
            if (!ColorUtil.transparentPixel(img,x,y-1))
            {
                toBuildD = false;
            }

            //top && bottom
            if (toBuildA) {
                List<Vector3d> triangleA1 = new ArrayList<>();
                triangleA1.add(Vector3d.xyz(i, j, getPixelHeight(x, y)));
                triangleA1.add(Vector3d.xyz(i1, j1, getPixelHeight(x + 1, y + 1)));
                triangleA1.add(Vector3d.xyz(i, j1, getPixelHeight(x, y + 1)));
                localPolygonList.add(Polygon.fromPoints(triangleA1));

                List<Vector3d> triangleA2 = new ArrayList<>();
                triangleA2.add(Vector3d.xyz(i, j, 0));
                triangleA2.add(Vector3d.xyz(i1, j1, 0));
                triangleA2.add(Vector3d.xyz(i, j1, 0));
                localPolygonList.add(Polygon.fromPoints(triangleA2));
            }
            if (toBuildB) {

                List<Vector3d> triangleB1 = new ArrayList<>();
                triangleB1.add(Vector3d.xyz(i, j, getPixelHeight(x, y)));
                triangleB1.add(Vector3d.xyz(i1, j, getPixelHeight(x+1, y)));
                triangleB1.add(Vector3d.xyz(i1, j1, getPixelHeight(x + 1, y+1)));
                localPolygonList.add(Polygon.fromPoints(triangleB1));

                List<Vector3d> triangleB2 = new ArrayList<>();
                triangleB2.add(Vector3d.xyz(i, j, 0));
                triangleB2.add(Vector3d.xyz(i1, j, 0));
                triangleB2.add(Vector3d.xyz(i1, j1, 0));
                localPolygonList.add(Polygon.fromPoints(triangleB2));
            }

            if (toBuildC) {

                List<Vector3d> triangleC1 = new ArrayList<>();
                triangleC1.add(Vector3d.xyz(i, j, getPixelHeight(x, y)));
                triangleC1.add(Vector3d.xyz(i1, j, getPixelHeight(x+1, y)));
                triangleC1.add(Vector3d.xyz(i, j1, getPixelHeight(x, y+1)));
                localPolygonList.add(Polygon.fromPoints(triangleC1));

                List<Vector3d> triangleC2 = new ArrayList<>();
                triangleC2.add(Vector3d.xyz(i, j, 0));
                triangleC2.add(Vector3d.xyz(i1, j, 0));
                triangleC2.add(Vector3d.xyz(i, j1, 0));
                localPolygonList.add(Polygon.fromPoints(triangleC2));
            }

            if (toBuildD) {

                List<Vector3d> triangleD1 = new ArrayList<>();
                triangleD1.add(Vector3d.xyz(i, j, getPixelHeight(x, y)));
                triangleD1.add(Vector3d.xyz(i1, j, getPixelHeight(x+1, y)));
                triangleD1.add(Vector3d.xyz(i1, jm1, getPixelHeight(x+1, y-1)));
                localPolygonList.add(Polygon.fromPoints(triangleD1));

                List<Vector3d> triangleD2 = new ArrayList<>();
                triangleD2.add(Vector3d.xyz(i, j, 0));
                triangleD2.add(Vector3d.xyz(i1, j, 0));
                triangleD2.add(Vector3d.xyz(i1, jm1, 0));
                localPolygonList.add(Polygon.fromPoints(triangleD2));
            }

            //wall / and \
            if ((toBuildA && !toBuildB) || (toBuildB && !toBuildA)) {

                List<Vector3d> triangleA1 = new ArrayList<>();
                triangleA1.add(Vector3d.xyz(i, j, getPixelHeight(x, y)));
                triangleA1.add(Vector3d.xyz(i, j, 0));
                triangleA1.add(Vector3d.xyz(i1, j1, 0));
                localPolygonList.add(Polygon.fromPoints(triangleA1));

                List<Vector3d> triangleA2 = new ArrayList<>();
                triangleA2.add(Vector3d.xyz(i, j, getPixelHeight(x, y)));
                triangleA2.add(Vector3d.xyz(i1, j1, getPixelHeight(x+1, y+1)));
                triangleA2.add(Vector3d.xyz(i1, j1, 0));
                localPolygonList.add(Polygon.fromPoints(triangleA2));
            }
            if (toBuildC) {

                List<Vector3d> triangleC1 = new ArrayList<>();
                triangleC1.add(Vector3d.xyz(i, j1, getPixelHeight(x, y+1)));
                triangleC1.add(Vector3d.xyz(i, j1, 0));
                triangleC1.add(Vector3d.xyz(i1, j, 0));
                localPolygonList.add(Polygon.fromPoints(triangleC1));

                List<Vector3d> triangleC2 = new ArrayList<>();
                triangleC2.add(Vector3d.xyz(i, j1, getPixelHeight(x, y+1)));
                triangleC2.add(Vector3d.xyz(i1, j, 0));
                triangleC2.add(Vector3d.xyz(i1, j, getPixelHeight(x+1, y)));
                localPolygonList.add(Polygon.fromPoints(triangleC2));
            }
            if (toBuildD) {

                List<Vector3d> triangleD1 = new ArrayList<>();
                triangleD1.add(Vector3d.xyz(i, j, getPixelHeight(x, y)));
                triangleD1.add(Vector3d.xyz(i, j, 0));
                triangleD1.add(Vector3d.xyz(i1, jm1, 0));
                localPolygonList.add(Polygon.fromPoints(triangleD1));

                List<Vector3d> triangleD2 = new ArrayList<>();
                triangleD2.add(Vector3d.xyz(i, j, getPixelHeight(x, y)));
                triangleD2.add(Vector3d.xyz(i1, jm1, getPixelHeight(x+1, y-1)));
                triangleD2.add(Vector3d.xyz(i1, jm1, 0));
                localPolygonList.add(Polygon.fromPoints(triangleD2));
            }
            //wall --
            if (toBuildB && (ColorUtil.transparentPixel(img,x,y-1) && ColorUtil.transparentPixel(img,x+1,y-1)))                {
                List<Vector3d> triangleD1 = new ArrayList<>();
                triangleD1.add(Vector3d.xyz(i, j, getPixelHeight(x, y)));
                triangleD1.add(Vector3d.xyz(i, j, 0));
                triangleD1.add(Vector3d.xyz(i1, j, getPixelHeight(x+1, y)));
                localPolygonList.add(Polygon.fromPoints(triangleD1));

                List<Vector3d> triangleD2 = new ArrayList<>();
                triangleD2.add(Vector3d.xyz(i, j, 0));
                triangleD2.add(Vector3d.xyz(i1, j, getPixelHeight(x+1, y)));
                triangleD2.add(Vector3d.xyz(i1, j, 0));
                localPolygonList.add(Polygon.fromPoints(triangleD2));
            }

            if (toBuildA && ColorUtil.transparentPixel(img,x,y+2) && ColorUtil.transparentPixel(img,x+1,y+2))
            {
                List<Vector3d> triangleD1 = new ArrayList<>();
                triangleD1.add(Vector3d.xyz(i, j1, getPixelHeight(x, y)));
                triangleD1.add(Vector3d.xyz(i, j1, 0));
                triangleD1.add(Vector3d.xyz(i1, j1, getPixelHeight(x+1, y+1)));
                localPolygonList.add(Polygon.fromPoints(triangleD1));

                List<Vector3d> triangleD2 = new ArrayList<>();
                triangleD2.add(Vector3d.xyz(i, j1, 0));
                triangleD2.add(Vector3d.xyz(i1, j1, getPixelHeight(x+1, y+1)));
                triangleD2.add(Vector3d.xyz(i1, j1, 0));
                localPolygonList.add(Polygon.fromPoints(triangleD2));
            }

            //wall |
            if (toBuildA && ColorUtil.transparentPixel(img,x-1,y) && ColorUtil.transparentPixel(img,x-1,y+1))
            {
                List<Vector3d> triangleD1 = new ArrayList<>();
                triangleD1.add(Vector3d.xyz(i, j, getPixelHeight(x, y)));
                triangleD1.add(Vector3d.xyz(i, j, 0));
                triangleD1.add(Vector3d.xyz(i, j1, getPixelHeight(x, y+1)));
                localPolygonList.add(Polygon.fromPoints(triangleD1));

                List<Vector3d> triangleD2 = new ArrayList<>();
                triangleD2.add(Vector3d.xyz(i, j, 0));
                triangleD2.add(Vector3d.xyz(i, j1, getPixelHeight(x, y+1)));
                triangleD2.add(Vector3d.xyz(i, j1, 0));
                localPolygonList.add(Polygon.fromPoints(triangleD2));
            }

            if (toBuildB && ColorUtil.transparentPixel(img,x+2,y) && ColorUtil.transparentPixel(img,x+2,y+1))
            {
                List<Vector3d> triangleD1 = new ArrayList<>();
                triangleD1.add(Vector3d.xyz(i1, j, getPixelHeight(x+1, y)));
                triangleD1.add(Vector3d.xyz(i1, j, 0));
                triangleD1.add(Vector3d.xyz(i1, j1, getPixelHeight(x+1, y+1)));
                localPolygonList.add(Polygon.fromPoints(triangleD1));

                List<Vector3d> triangleD2 = new ArrayList<>();
                triangleD2.add(Vector3d.xyz(i1, j, 0));
                triangleD2.add(Vector3d.xyz(i1, j1, getPixelHeight(x+1, y+1)));
                triangleD2.add(Vector3d.xyz(i1, j1, 0));
                localPolygonList.add(Polygon.fromPoints(triangleD2));
            }
            savePolygonList(localPolygonList);
        }
	}
    public double getPixelHeight(int x, int y)
    {
        if (ColorUtil.transparentPixel(csgWorkData.getTexturedImage(),x,y))
        {
            return 0;
        }
        return super.getPixelHeight(x,y);
    }

}
