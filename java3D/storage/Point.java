/* ------------------------------------------------------------
   About:      Point interface
               Base interface for all point graphical objects.
   Author:     Michael C. Petrovich
               michael.c.petrovich@gmail.com
   
   Copyright © 2003-2007 Michael C. Petrovich
------------------------------------------------------------ */

package java3D.storage;

public interface	Point extends GraphicObject
{
	// Mathematics
	double	getDistanceFrom(Point p);
	GraphicObject	getSum(Point p);
	GraphicObject	getDifference(Point p);
	void	setSum(Point p);
	void	setDifference(Point p);
}