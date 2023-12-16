package ggo.pixestl.csg;

import eu.mihosoft.jcsg.CSG;
import eu.mihosoft.jcsg.Polygon;
import ggo.pixestl.util.CSGUtil;
import ggo.pixestl.util.StreamUtil;

import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.InflaterInputStream;
import java.util.zip.ZipOutputStream;

public abstract class CSGThread implements Runnable
{
    final private ExecutorService executorService;
    protected final CSGWorkData csgWorkData;
    final private List<CSGThreadRow> threadByRowList;
    protected CSG csg = null;
    private boolean allComplete;
    final private Class threadRowClass;
    private File tempResult=null;
    private OutputStream os = null;
    private InputStream is = null;
    private boolean writeTemp;

    public CSGThread(Class threadRowClass,CSGWorkData instruction)
    {
        this.threadRowClass=threadRowClass;
        this.csgWorkData=instruction;
        this.executorService = Executors.newFixedThreadPool(csgWorkData.getGenInstruction().getRowThreadNumber());
        this.threadByRowList = new ArrayList<>();
        this.allComplete=false;

        if (instruction.getGenInstruction().isLowMemory())
        {

            try {
                tempResult = File.createTempFile("PIXEstL",instruction.getThreadName());
                tempResult.deleteOnExit();
                os = new DeflaterOutputStream(new BufferedOutputStream(Files.newOutputStream(tempResult.toPath())));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }
    protected synchronized void savePolygonListInTempFile(List<Polygon> polygonList)throws RuntimeException
    {
        if (polygonList.isEmpty()) return;
        for (Polygon p : polygonList)
        {
            try {
                String s = p.toStlString();
                os.write(s.getBytes());
            } catch (IOException e)
            {
                StreamUtil.closeStream(os);
                throw new RuntimeException(e);
            }
        }
        writeTemp=true;
    }

    public void run()
    {
        int height = getImageToProcess().getHeight();
        for (int y = 0; y < height; y++)
        {
            CSGThreadRow threadRow= CSGThreadRowFactory.newInstance(this,threadRowClass);
            if (threadRow == null ) continue;
            threadRow.init(csgWorkData,y);
            threadByRowList.add(threadRow);
            executorService.execute(threadRow);
        }

        executorService.shutdown();
        try
        {
            allComplete = executorService.awaitTermination(csgWorkData.getGenInstruction().getRowThreadTimeout(), TimeUnit.SECONDS);
        }
        catch (InterruptedException e)
        {
            allComplete=false;
        }

        if (!allComplete) return;

        if(csgWorkData.getGenInstruction().isLowMemory())
        {
            reverseStream();
        }
        else
        {
            List<Polygon> polys = new ArrayList<>();
            for (CSGThreadRow threadByRow : threadByRowList)
            {
                polys.addAll(threadByRow.getPolygonList());
            }
            if (polys.size() > 0) {
                csg = CSG.fromPolygons(polys);
            }
        }

        postProcessing();
    }

    public void reverseStream()
    {
        try {
            os.close();
        }
        catch (IOException e) {
            StreamUtil.closeStream(os);
            throw new RuntimeException(e);
        }

        if (writeTemp)
        {
            try {
                is = new InflaterInputStream(new BufferedInputStream(Files.newInputStream(tempResult.toPath())));
            } catch (IOException e) {
                StreamUtil.closeStream(is);
                throw new RuntimeException(e);
            }

        }
    }

    abstract public void postProcessing();
    abstract public BufferedImage getImageToProcess();

    public void writeSTLString(ZipOutputStream zipOut) throws IOException
    {
        if (csgWorkData.getGenInstruction().isLowMemory())
        {
            zipOut.write("solid v3d.csg\n".getBytes());
            byte[] buffer = new byte[8096];
            int bytesRead;
            while ((bytesRead = is.read(buffer)) != -1) {
                zipOut.write(buffer, 0, bytesRead);
            }
            zipOut.write("endsolid v3d.csg\n".getBytes());
        }
        else
        {
            CSGUtil.writeStlStream(csg,zipOut);
        }


    }
    public boolean hasSTL()
    {
        return csg != null || is != null;
    }

    public String getThreadName()
    {
        return csgWorkData.getThreadName();
    }

    public boolean isAllComplete() {
        return allComplete;
    }
}
