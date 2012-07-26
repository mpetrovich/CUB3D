/* ------------------------------------------------------------
   About:      Isometric projector class
               Performs Sim City-style projection.
   Author:     Michael C. Petrovich
               michael.c.petrovich@gmail.com
   
   Copyright © 2003-2007 Michael C. Petrovich
------------------------------------------------------------ */

package java3D.render;

import java3D.storage.*;

public class	IsometricProjector extends Projector
{
	public	IsometricProjector()
	{}
	
	public String	getType()
	{
		return "isometric";
	}
	
	public Point2D	project3Dto2D(Point3D point3D, Camera camera)
	{
		// Adjusts for the camera view angle
		Point3D	rotated = new Point3D(point3D);
		rotated.rotate(camera.getViewAngles(), camera.getRotateOrigin());
		
		// Discards the 3rd dimension
		Point2D	point2D = new Point2D(rotated.getY(), rotated.getZ());
		point2D.setAttr(point3D.getAttr());
		
		// Calculates the point radius attribute
		Attributes	attr = point2D.getAttr();
		if (attr.showPoints)
		{
			switch (camera.getRenderQuality())
			{
				// Constant point pixel size
				case Low:
					attr.pixelRadius = (int) attr.pointRadius;
					point2D.setAttr(attr);
					break;
				
				// Point pixel size proportional to camera zoom
				case Med:
				case High:
					attr.pixelRadius = (int) (attr.pointRadius * camera.getZoom());
					point2D.setAttr(attr);
					break;
			}
		}
		
		return point2D;
	}
}