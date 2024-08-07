package ggo.pixestl.palette;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import ggo.pixestl.generator.GenInstruction;
import ggo.pixestl.util.ColorUtil;

public class ColorCombi {
	
	List<ColorLayer> layers;
	
	private ColorCombi()
	{
		layers = new ArrayList<>();
	}
	
	public ColorCombi(ColorLayer colorLayer)
	{
		this();
		layers.add(colorLayer);
		layers.sort(new ColorLayer.LayerComparator());
	}
	
	public ColorCombi combineLithoColorLayer(ColorLayer colorLayer2,int nbLayerMax)
	{
		for (ColorLayer c : layers)
		{
			if (c.getHexCode().equals(colorLayer2.getHexCode())) return null;
		}
		if (getTotalLayers()+colorLayer2.getLayer() > nbLayerMax) return null;
		ColorCombi c = duplicate();
		c.layers.add(colorLayer2);
		return c;
	}

	public ColorCombi combineLithoColorCombi(ColorCombi ColorCombi2)
	{
		ColorCombi c = duplicate();
		c.layers.addAll(ColorCombi2.getLayers());
		return c;
	}

	public void addLayer(ColorLayer colorLayer)
	{
		layers.add(colorLayer);
	}


	
	public int getTotalColors()
	{
		return layers.size();
	}
	
	public int getTotalLayers()
	{
		int totalLayers=0;
		for (ColorLayer lithoColorLayer : layers)
		{
			totalLayers+=lithoColorLayer.getLayer();
		}
		return totalLayers;
	}
	
	public Color getColor(GenInstruction genInstruction)
	{
		double c=0,m=0,y=0,k=0;
		for (ColorLayer lithoColorLayer : layers)
		{
			if (genInstruction.isDebug()) {
				System.out.print(lithoColorLayer.getHexCode() + "[" + lithoColorLayer.getLayer() + "]");
			}
			c+=lithoColorLayer.getC();	
			m+=lithoColorLayer.getM();
			y+=lithoColorLayer.getY();
			k+=lithoColorLayer.getK();
		}
		Color color = ColorUtil.cmykToColor(c<1?c:1, m<1?m:1, y<1?y:1, k<1?k:1);
		if (genInstruction.isDebug()) {
			System.out.println("=" + ColorUtil.colorToHexCode(color));
		}
		return color;
		
	}

	protected ColorCombi duplicate() {
		ColorCombi c = new ColorCombi();
		c.layers.addAll(this.layers);
		return c;		
	}

	protected void factorize()
	{
		List<ColorLayer> newLayers = new ArrayList<>();
		for (int i=0; i< getLayers().size();i++)
		{
			ColorLayer currL= getLayers().get(i);
			if (i==0)
			{
				newLayers.add(currL);
				continue;
			}
			ColorLayer lastL= newLayers.get(newLayers.size()-1);

			if (lastL.getHexCode().equals(currL.getHexCode()))
			{
				newLayers.remove(lastL);
				newLayers.add(new ColorLayer(lastL.getHexCode(),lastL.getLayer()+currL.getLayer(),lastL.getC(),lastL.getM(),lastL.getY(),lastL.getK()));
			}
			else {
				newLayers.add(currL);
			}
		}
		layers=newLayers;
	}

	public List<ColorLayer> getLayerList(String hexCode)
	{
		List<ColorLayer> resLayerList = new ArrayList<>();
		for (ColorLayer layer : layers) {
			if (layer.getHexCode().equals(hexCode)) {
				resLayerList.add(layer);
			}
		}
		return resLayerList;
	}

	public int getLayerPosition(ColorLayer layer )
	{

		int nbLayerBeforeThisColor=0;
		for (ColorLayer curLayer : layers)
		{
			if (curLayer == layer)
			{
				break;
			}
			//else
			nbLayerBeforeThisColor+=curLayer.getLayer();
		}
		return nbLayerBeforeThisColor;
	}
	public List<ColorLayer> getLayers() {
		return layers;
	}

}
