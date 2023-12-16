package ggo.pixestl.csg;

import ggo.pixestl.csg.color.CSGThreadColorRow;
import ggo.pixestl.csg.plate.CSGThreadSupportRow;
import ggo.pixestl.csg.texture.CSGThreadTextureRow;
import ggo.pixestl.csg.texture.CSGThreadTextureRowWithTransparency;

abstract public class CSGThreadRowFactory {

    public static CSGThreadRow newInstance(CSGThread csgThread,Class c)
    {
        if (c.equals(CSGThreadColorRow.class))
        {
            return new CSGThreadColorRow(csgThread);
        }
        else if (c.equals(CSGThreadTextureRow.class))
        {
            return new CSGThreadTextureRow(csgThread);
        }
        else if (c.equals(CSGThreadTextureRowWithTransparency.class))
        {
            return new CSGThreadTextureRowWithTransparency(csgThread);
        }
        else if (c.equals(CSGThreadSupportRow.class))
        {
            return new CSGThreadSupportRow(csgThread);
        }
        return null;
    }
}
