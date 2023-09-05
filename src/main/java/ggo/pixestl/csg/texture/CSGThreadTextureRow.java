package ggo.pixestl.csg.texture;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import eu.mihosoft.jcsg.Polygon;
import eu.mihosoft.vvecmath.Vector3d;
import ggo.pixestl.csg.CSGThreadRow;
import ggo.pixestl.util.ColorUtil;

public class CSGThreadTextureRow extends CSGThreadRow
{
	public void run() {
        int width = csgWorkData.getTexturedImage().getWidth();
        int height = csgWorkData.getTexturedImage().getHeight();

        double diffW = 0;
        double diffH = 0;

        double pixelWidth = csgWorkData.getGenInstruction().getTexturePixelWidth();

        if (csgWorkData.getGenInstruction().isColorLayer()) {
            diffW = pixelWidth * width - csgWorkData.getColorImage().getWidth() * csgWorkData.getGenInstruction().getColorPixelWidth();
            diffH = pixelWidth * height - csgWorkData.getColorImage().getHeight() * csgWorkData.getGenInstruction().getColorPixelWidth();
        }
        double iMid = ((double) width / 2) * pixelWidth;
        double jMid = ((double) height / 2) * pixelWidth;

        double widthPixel = width * pixelWidth;

        double curve = csgWorkData.getCurve();

        if (y == height - 1) return; //no need to manage the last row

        for (int x = 0; x < width; x++) {
            if (x == width - 1) continue; //no need to manage the last column

            double i = x * pixelWidth;
            if (x == 0) i = diffW / 2 - pixelWidth / 2;

            double j = y * pixelWidth;
            if (y == 0) j = diffH / 2 - pixelWidth / 2;

            double i1 = x * pixelWidth + pixelWidth;
            if (x == width - 2) i1 += -diffW / 2 + pixelWidth / 2;

            double j1 = y * pixelWidth + pixelWidth;
            if (y == height - 2) j1 += -diffH / 2 + pixelWidth / 2;

            if (x == 0) {
                List<Vector3d> triangle1 = new ArrayList<>();
                triangle1.add(Vector3d.xyz(i, j, getPixelHeight(x, y)));
                triangle1.add(Vector3d.xyz(i, j1, getPixelHeight(x, y + 1)));
                triangle1.add(Vector3d.xyz(i, j1, 0));
                polygonList.add(Polygon.fromPoints(curveTriangleList(widthPixel,curve, triangle1)));

                List<Vector3d> triangle2 = new ArrayList<>();
                triangle2.add(Vector3d.xyz(i, j, getPixelHeight(x, y)));
                triangle2.add(Vector3d.xyz(i, j, 0));
                triangle2.add(Vector3d.xyz(i, j1, 0));
                polygonList.add(Polygon.fromPoints(triangle2));

                if (curve==0) {
                    List<Vector3d> triangle3 = new ArrayList<>();
                    triangle3.add(Vector3d.xyz(i, j, 0));
                    triangle3.add(Vector3d.xyz(i, j1, 0));
                    triangle3.add(Vector3d.xyz(iMid, jMid, 0));
                    polygonList.add(Polygon.fromPoints(triangle3));
                }
            }

            if (y == 0) {
                List<Vector3d> triangle1 = new ArrayList<>();
                triangle1.add(Vector3d.xyz(i, j, getPixelHeight(x, y)));
                triangle1.add(Vector3d.xyz(i1, j, getPixelHeight(x + 1, y)));
                triangle1.add(Vector3d.xyz(i1, j, 0));
                polygonList.add(Polygon.fromPoints(curveTriangleList(widthPixel, curve, triangle1)));

                List<Vector3d> triangle2 = new ArrayList<>();
                triangle2.add(Vector3d.xyz(i, j, getPixelHeight(x, y)));
                triangle2.add(Vector3d.xyz(i, j, 0));
                triangle2.add(Vector3d.xyz(i1, j, 0));
                polygonList.add(Polygon.fromPoints(curveTriangleList(widthPixel, curve, triangle2)));

                if (curve==0) {
                    List<Vector3d> triangle3 = new ArrayList<>();
                    triangle3.add(Vector3d.xyz(i, j, 0));
                    triangle3.add(Vector3d.xyz(i1, j, 0));
                    triangle3.add(Vector3d.xyz(iMid, jMid, 0));
                    polygonList.add(Polygon.fromPoints(triangle3));
                }
            }

            if (x == width - 2) {
                List<Vector3d> triangle1 = new ArrayList<>();
                triangle1.add(Vector3d.xyz(i1, j, getPixelHeight(x + 1, y)));
                triangle1.add(Vector3d.xyz(i1, j1, getPixelHeight(x + 1, y + 1)));
                triangle1.add(Vector3d.xyz(i1, j1, 0));
                polygonList.add(Polygon.fromPoints(curveTriangleList(widthPixel, curve, triangle1)));

                List<Vector3d> triangle2 = new ArrayList<>();
                triangle2.add(Vector3d.xyz(i1, j, getPixelHeight(x + 1, y)));
                triangle2.add(Vector3d.xyz(i1, j, 0));
                triangle2.add(Vector3d.xyz(i1, j1, 0));
                polygonList.add(Polygon.fromPoints(curveTriangleList(widthPixel, curve, triangle2)));

                if (curve==0) {
                    List<Vector3d> triangle3 = new ArrayList<>();
                    triangle3.add(Vector3d.xyz(i1, j, 0));
                    triangle3.add(Vector3d.xyz(i1, j1, 0));
                    triangle3.add(Vector3d.xyz(iMid, jMid, 0));
                    polygonList.add(Polygon.fromPoints(triangle3));
                }
            }

            if (y == height - 2) {
                List<Vector3d> triangle1 = new ArrayList<>();
                triangle1.add(Vector3d.xyz(i, j1, getPixelHeight(x, y + 1)));
                triangle1.add(Vector3d.xyz(i1, j1, getPixelHeight(x + 1, y + 1)));
                triangle1.add(Vector3d.xyz(i1, j1, 0));
                polygonList.add(Polygon.fromPoints(curveTriangleList(widthPixel, curve, triangle1)));

                List<Vector3d> triangle2 = new ArrayList<>();
                triangle2.add(Vector3d.xyz(i, j1, getPixelHeight(x, y + 1)));
                triangle2.add(Vector3d.xyz(i, j1, 0));
                triangle2.add(Vector3d.xyz(i1, j1, 0));
                polygonList.add(Polygon.fromPoints(curveTriangleList(widthPixel, curve, triangle2)));

                if (curve==0) {
                    List<Vector3d> triangle3 = new ArrayList<>();
                    triangle3.add(Vector3d.xyz(i, j1, 0));
                    triangle3.add(Vector3d.xyz(i1, j1, 0));
                    triangle3.add(Vector3d.xyz(iMid, jMid, 0));
                    polygonList.add(Polygon.fromPoints(triangle3));
                }
            }

            List<Vector3d> triangle1 = new ArrayList<>();
            triangle1.add(Vector3d.xyz(i, j, getPixelHeight(x, y)));
            triangle1.add(Vector3d.xyz(i, j1, getPixelHeight(x, y + 1)));
            triangle1.add(Vector3d.xyz(i1, j, getPixelHeight(x + 1, y)));
            polygonList.add(Polygon.fromPoints(curveTriangleList(widthPixel, curve, triangle1)));

            List<Vector3d> triangle2 = new ArrayList<>();
            triangle2.add(Vector3d.xyz(i1, j1, getPixelHeight(x + 1, y + 1)));
            triangle2.add(Vector3d.xyz(i, j1, getPixelHeight(x, y + 1)));
            triangle2.add(Vector3d.xyz(i1, j, getPixelHeight(x + 1, y)));
            polygonList.add(Polygon.fromPoints(curveTriangleList(widthPixel, curve, triangle2)));

            if (curve!=0 && y==0)
            {
                List<Vector3d> triangle3 = new ArrayList<>();
                triangle3.add(Vector3d.xyz(i, 0, 0));
                triangle3.add(Vector3d.xyz(i, height*pixelWidth, 0));
                triangle3.add(Vector3d.xyz(i1, 0, 0));
                polygonList.add(Polygon.fromPoints(curveTriangleList(widthPixel, curve, triangle3)));

                List<Vector3d> triangle4 = new ArrayList<>();
                triangle4.add(Vector3d.xyz(i1, height*pixelWidth, 0));
                triangle4.add(Vector3d.xyz(i, height*pixelWidth, 0));
                triangle4.add(Vector3d.xyz(i1, 0, 0));
                polygonList.add(Polygon.fromPoints(curveTriangleList(widthPixel, curve, triangle4)));
            }
        }


    }

    private List<Vector3d> curveTriangleList(double width,double curve,List<Vector3d> triangleList)
    {
        if (curve == 0.0) return triangleList;

        double angle = Math.abs(curve);
        double a = (width / curve) * (180 / Math.PI);
        double d = Math.sin(angle * (360 / Math.PI)) * a;
        if (angle >= 180) d = 0;
        double s = (0 - angle / 2);

        List<Vector3d> triangleRes = new ArrayList<>();
        for (Vector3d  vector3d : triangleList)
        {
            double x = vector3d.getX();
            double y = vector3d.getY();
            double z = vector3d.getZ();

            double u = x / width;
            double r = s + (angle * u);
            double rt = r * (Math.PI / 180);
            double m = a + z;

            double newX = width / 2 + m * Math.sin(rt);
            double newZ = d + m * Math.cos(rt);
            triangleRes.add(Vector3d.xyz(newX,y,newZ));
        }
        return triangleRes;
    }


	
	
	public double getPixelHeight(int x, int y)
	{
		double layerHeight;
		double maxThickness=csgWorkData.getGenInstruction().getTextureMaxThickness();
		double minThickness=csgWorkData.getGenInstruction().getTextureMinThickness();
		
	
		int pixelColorInt = csgWorkData.getTexturedImage().getRGB(x, y);
        Color pixelColor = new Color(pixelColorInt);
        
        layerHeight =ColorUtil.colorToCMYK(pixelColor)[3];
        layerHeight*=(maxThickness-minThickness);
        layerHeight+=minThickness;
        return layerHeight;
	}

}
