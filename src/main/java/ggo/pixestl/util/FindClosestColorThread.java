package ggo.pixestl.util;

import ggo.pixestl.generator.GenInstruction;

import java.awt.*;
import java.util.List;

public class FindClosestColorThread implements Runnable
{
    final private Color targetColor;
    final private java.util.List<Color> colors;
    final private GenInstruction.ColorDistanceComputation colorDistanceComputation;

    final private int x,y;

    private Color closestColor;
    public FindClosestColorThread(int x, int y, Color targetColor, List<Color> colors, GenInstruction.ColorDistanceComputation colorDistanceComputation)
    {
        this.x=x;
        this.y=y;
        this.targetColor=targetColor;
        this.colors=colors;
        this.colorDistanceComputation=colorDistanceComputation;

    }
    @Override
    public void run()
    {
        closestColor= ColorUtil.findClosestColor(targetColor,colors,colorDistanceComputation);
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public Color getClosestColor() {
        return closestColor;
    }
}
