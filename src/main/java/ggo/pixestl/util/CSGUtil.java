package ggo.pixestl.util;

import eu.mihosoft.jcsg.CSG;
import eu.mihosoft.jcsg.Polygon;

import java.io.IOException;
import java.io.OutputStream;

public class CSGUtil {

    static public void writeStlStream(CSG csg, OutputStream os) throws IOException
    {
        os.write("solid v3d.csg\n".getBytes());
        for (Polygon p : csg.getPolygons())
        {
            os.write(p.toStlString().getBytes());
        }
        os.write("endsolid v3d.csg\n".getBytes());
    }
}
