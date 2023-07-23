package ggo.pixestl.csg;

import ggo.pixestl.csg.color.CSGThreadColorRow;
import ggo.pixestl.csg.plate.CSGThreadSupportRow;
import ggo.pixestl.csg.texture.CSGThreadTextureRow;
import ggo.pixestl.csg.texture.CSGThreadTextureRowWithTransparency;

abstract public class CSGThreadRowFactory {

    public static CSGThreadRow newInstance(Class c)
    {
        if (c.equals(CSGThreadColorRow.class))
        {
            return new CSGThreadColorRow();
        }
        else if (c.equals(CSGThreadTextureRow.class))
        {
            return new CSGThreadTextureRow();
        }
        else if (c.equals(CSGThreadTextureRowWithTransparency.class))
        {
            return new CSGThreadTextureRowWithTransparency();
        }
        else if (c.equals(CSGThreadSupportRow.class))
        {
            return new CSGThreadSupportRow();
        }
        return null;
    }
}
