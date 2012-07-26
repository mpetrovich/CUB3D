/* ------------------------------------------------------------
   About:      Projector class
               Abstract base class for 3D-to-2D projection renderers
   Author:     Michael C. Petrovich
               michael.c.petrovich@gmail.com
   
   Copyright © 2003-2007 Michael C. Petrovich
------------------------------------------------------------ */

package java3D.render;

import java3D.storage.*;

public abstract class	Projector
{
	public abstract String	getType();		// Returns the name of the projection type
	public abstract Point2D	project3Dto2D(Point3D point3D, Camera camera);
	
	public Point2D	project2DtoScreen(Point2D point2D, Camera camera)
	{
		// Transforms 2D Cartesian coordinates to screen coordinates
		Point2D	screen2D = new Point2D(point2D);
		Point2D	screenOrigin = camera.getScreenOrigin();
		screen2D.set( screenOrigin.getX() + point2D.getX() * camera.getZoom() ,
		              screenOrigin.getY() - point2D.getY() * camera.getZoom() );
		return screen2D;
	}
	
	public Point2D	project3DtoScreen(Point3D point3D, Camera camera)
	{
		return project2DtoScreen(project3Dto2D(point3D, camera), camera);
	}
}