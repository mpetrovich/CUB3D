/* ------------------------------------------------------------
   About:      Graphical object interface
               Base interface for all displayable objects.
   Author:     Michael C. Petrovich
               michael.c.petrovich@gmail.com
   
   Copyright © 2003-2007 Michael C. Petrovich
------------------------------------------------------------ */

package java3D.storage;

import java.awt.Graphics;
import java.util.*;

public interface	GraphicObject
{
	// Data access
	void	set(GraphicObject object);		// Replaces object ONLY
	void	set(GraphicObject object, Attributes attr);		// Replaces object AND attributes
	void	setAll(GraphicObject object);		// Replaces object AND attributes
	GraphicObject	clone();
	
	// Attributes
	Attributes	getAttr();
	void	setAttr(Attributes attr);
	void	setAttrToAll(Attributes attr);
	
	// Extents
	Point	getExtents(Comparator<Double> comp);
	double	getExtentsMaxRange();
	
	// Rendering
	Point	getCenter();
	GraphicObject	render(Camera camera);
	void	paint(Graphics g);
	
	// Mathematics
	void	rotate(Point angles);
	void	rotate(Point angles, Point origin);
	GraphicObject	getNegative();
	GraphicObject	getProduct(double factor);
	GraphicObject	getQuotient(double factor);
	void	setNegative();
	void	setProduct(double factor);
	void	setQuotient(double factor);
}