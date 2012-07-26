/* ------------------------------------------------------------
   About:      Perspective projector class
   Author:     Michael C. Petrovich
               michael.c.petrovich@gmail.com
   
   Copyright © 2003-2007 Michael C. Petrovich
------------------------------------------------------------ */

package java3D.render;

import java3D.storage.*;

public class	PerspectiveProjector extends Projector
{
	public	PerspectiveProjector()
	{}
	
	public String	getType()
	{
		return "perspective";
	}
	
	public Point2D	project3Dto2D(Point3D point3D, Camera camera)
	{
		// Temporarily sets the camera rotate radius to the object size
		double	rotateRadius = camera.getRangeMultiplier() * camera.getExtentsMaxRange();
		
		// Adjusts for the camera view angle
		Point3D	rotated = new Point3D(point3D);
		Point3D	rotateOrigin = camera.getRotateOrigin();
		rotated.rotate(camera.getViewAngles(), rotateOrigin);
		Point2D	point2D = new Point2D(rotated.getY(), rotated.getZ());
		
		// Calculates the perspective scaling factor
		double	distance = point3D.getDistanceFrom(camera.getCameraLocation(rotateRadius));
		distance /= camera.getPerspectiveUnit() * rotateRadius;
		double	scaling = Math.pow(0.5, distance);
		
		// Calculates the 2D point shift due to the vanishing point
		Point2D	vanishPoint = new Point2D(rotateOrigin.getY(), rotateOrigin.getZ());
		Point2D	shift = (Point2D) point2D.getDifference(vanishPoint);
		shift.setProduct(scaling);
		point2D.set(vanishPoint.getSum(shift));
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
					attr.pixelRadius = (int) (attr.pointRadius * camera.getZoom() * scaling);
					point2D.setAttr(attr);
					break;
			}
		}
		
		return point2D;
	}
}