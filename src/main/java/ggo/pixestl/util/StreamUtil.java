package ggo.pixestl.util;

import java.io.Closeable;
import java.io.IOException;

public class StreamUtil {

    public static void closeStream(Closeable s)
    {
        if (s == null) return;
        try {
            s.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


}
