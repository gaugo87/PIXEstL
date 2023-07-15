package ggo.pixestl.palette;

import java.awt.Color;
import java.util.HashMap;
import java.util.Map;

import ggo.pixestl.util.ColorUtil;

public class ColorCombi {
	
	Map<String,ColorLayer> layers;
	
	private ColorCombi()
	{
		layers = new HashMap<>();
	}
	
	public ColorCombi(ColorLayer colorLayer)
	{
		this();
		layers.put(colorLayer.getHexCode(),colorLayer);		
	}
	
	public ColorCombi combineLithoColorLayer(ColorLayer colorLayer2,int nbLayerMax) throws CloneNotSupportedException {
		if (layers.containsKey(colorLayer2.getHexCode())) return null;
		if (getTotalLayers()+colorLayer2.getLayer() > nbLayerMax) return null;
		ColorCombi c = clone();
		c.layers.put(colorLayer2.getHexCode(),colorLayer2);
		return c;				
	}
	
	public int getTotalColors()
	{
		return layers.size();
	}
	
	public int getTotalLayers()
	{
		int totalLayers=0;
		for (ColorLayer lithoColorLayer : layers.values())
		{
			totalLayers+=lithoColorLayer.getLayer();
		}
		return totalLayers;
	}
	
	public Color getColor()
	{
		double c=0,m=0,y=0,k=0;
		for (ColorLayer lithoColorLayer : layers.values())
		{
			/*System.out.print(lithoColorLayer.getHexCode()+"["+lithoColorLayer.getLayer()+"]");*/
			c+=lithoColorLayer.getC();	
			m+=lithoColorLayer.getM();
			y+=lithoColorLayer.getY();
			k+=lithoColorLayer.getK();
		}
		Color color = ColorUtil.cmykToColor(c<1?c:1, m<1?m:1, y<1?y:1, k<1?k:1);
		/*String string = ;
		System.out.println("="+ColorUtil.colorToHexCode(color));*/
		return color;
		
	}

	protected ColorCombi clone() throws CloneNotSupportedException {
		ColorCombi c = new ColorCombi();
		c.layers.putAll(this.layers);
		return c;		
	}
	
	public int getLayerHeight(String hexCode)
	{
		ColorLayer lCL = layers.get(hexCode);
		return lCL!=null?lCL.getLayer():0;
	}
	
	
	public int getLayerPosition(Palette palette, String hexCode)
	{
		int priority = palette.getPriorityColor(hexCode);
		int nbLayerBeforeThisColor=0;
		for (String hex : layers.keySet())
		{
			if (hexCode.equals(hex)) continue;
			int curPriority= palette.getPriorityColor(hex);
			if (curPriority<priority)
			{
				nbLayerBeforeThisColor+=layers.get(hex).getLayer();
			}			
		}
		return nbLayerBeforeThisColor;
	}

	public Map<String, ColorLayer> getLayers() {
		return layers;
	}

}
