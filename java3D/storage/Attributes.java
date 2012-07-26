/* ------------------------------------------------------------
   About:      Attributes class
               Stores common graphical object properties.
   Author:     Michael C. Petrovich
               michael.c.petrovich@gmail.com
   
   Copyright © 2003-2007 Michael C. Petrovich
------------------------------------------------------------ */

package java3D.storage;

import java.awt.Color;

public class	Attributes
{
	public Color	fillColor, edgeColor, pointColor;
	public boolean	showFill, showEdges, showPoints;
	public double	fillOpacity, edgeOpacity, pointOpacity;		// Range: [0, 1]
	public double	pointRadius;		// In coordinate units
	public int	pixelRadius;		// In pixels
	
	
	/* Constructors
	---------------------------------------------------------------------- */
	public	Attributes()
	{
		fillColor = Color.RED;
		edgeColor = Color.BLACK;
		pointColor = Color.BLUE;
		
		showFill = true;
		showEdges = true;
		showPoints = true;
		
		fillOpacity = 0.75;
		edgeOpacity = 1.00;
		pointOpacity = 0.50;
		
		pointRadius = 0.05;
		pixelRadius = 1;
	}
	
	public	Attributes(Attributes attr)
	{
		set(attr);
	}
	
	
	/* Data access
	---------------------------------------------------------------------- */
	public void	set(Attributes attr)
	{
		fillColor = attr.fillColor;
		edgeColor = attr.edgeColor;
		pointColor = attr.pointColor;
		
		showFill = attr.showFill;
		showEdges = attr.showEdges;
		showPoints = attr.showPoints;
		
		fillOpacity = attr.fillOpacity;
		edgeOpacity = attr.edgeOpacity;
		pointOpacity = attr.pointOpacity;
		
		pointRadius = attr.pointRadius;
		pixelRadius = attr.pixelRadius;
	}
	
	public Attributes	clone()
	{
		Attributes	attr = new Attributes();
		attr.set(this);
		return attr;
	}
	
	
	/* Color
	---------------------------------------------------------------------- */
	public Color	getFillColor()
	// Returns the composite fill color
	{
		return getCompositeColor(fillColor, fillOpacity, showFill);
	}
	
	public Color	getEdgeColor()
	// Returns the composite edge color
	{
		return getCompositeColor(edgeColor, edgeOpacity, showEdges);
	}
	
	public Color	getPointColor()
	// Returns the composite point color
	{
		return getCompositeColor(pointColor, pointOpacity, showPoints);
	}
	
	private static Color	getCompositeColor(Color color, double opacity, boolean show)
	{
		opacity = (show ? opacity : 0);
		return new Color(color.getRed(), color.getGreen(), color.getBlue(), (int)(opacity*255));
	}
}