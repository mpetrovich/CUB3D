/* ------------------------------------------------------------
   About:      3D Point class
   Author:     Michael C. Petrovich
               michael.c.petrovich@gmail.com
   
   Copyright © 2003-2007 Michael C. Petrovich
------------------------------------------------------------ */

package java3D.storage;

import java.awt.Graphics;
import java.util.*;

public class	Point3D extends Point2D
{
	protected double	z=0;
	
	
	/* Constructors
	---------------------------------------------------------------------- */
	public	Point3D()
	{}
	
	public	Point3D(double x, double y, double z)
	{
		set(x, y, z);
	}
	
	public	Point3D(GraphicObject object)
	{
		setAll(object);
	}
	
	
	/* Data access
	---------------------------------------------------------------------- */
	public double	getZ()
	{
		return z;
	}
	
	public void	setZ(double z)
	{
		this.z = z;
	}
	
	public void	set(double x, double y, double z)
	{
		super.set(x, y);
		setZ(z);
	}
	
	public void	set(GraphicObject object, Attributes attr)
	// Replaces the current point AND attributes
	{
		super.set(object, attr);
		if (object instanceof Point3D)
		{
			Point3D	p = (Point3D) object;
			setZ(p.getZ());
		}
	}
	
	public GraphicObject	clone()
	{
		return new Point3D(this);
	}
	
	public boolean	equals(Point3D p)
	{
		return super.equals(p) && 
		       getZ() == p.getZ();
	}
	
	public String	toString()
	{
		return new String("(" + getX() + ", " + getY() + ", " + getZ() + ")");
	}
	
	
	/* Extents
	---------------------------------------------------------------------- */
	public Point	getExtents(Comparator<Double> comp)
	{
		return new Point3D(this);
	}
	
	public double	getExtentsMaxRange()
	{
		return 0;
	}
	
	
	/* Rendering
	---------------------------------------------------------------------- */
	public Point	getCenter()
	{
		return new Point3D(this);
	}
	
	public GraphicObject	render(Camera camera)
	{
		return camera.render(this);
	}
	
	public void	paint(Graphics g)
	{
		System.err.println("Error! Point3D.paint() called by: " + toString());
	}
	
	
	/* Mathematics
	---------------------------------------------------------------------- */
	public double	getDistanceFrom(Point3D p)
	{
		if (p instanceof Point3D)
		{
			Point3D	p3D = (Point3D) p;
			return Math.sqrt( (x - p3D.getX()) * (x - p3D.getX()) + 
							  (y - p3D.getY()) * (y - p3D.getY()) + 
							  (z - p3D.getZ()) * (z - p3D.getZ()) );
		}
		return 0;
	}
	
	public void	rotate(Point angles)
	{
		rotate(angles, new Point3D());
	}
	
	public void	rotate(Point angles, Point origin)
	{
		if (angles instanceof Point3D)
		{
			Point3D	angles3D = (Point3D) angles;
			
			// Normalizes the rotation origin
			setDifference(origin);
			
			// Rotates the point CW around the Z axis
			if (angles3D.getZ() != 0)
			{
				double	pointAngle = Math.atan2(y, x) - Math.toRadians(angles3D.getZ());
				double	distance = Math.hypot(x, y);
				x = distance * Math.cos(pointAngle);
				y = distance * Math.sin(pointAngle);
			}
			
			// Rotates the point CCW around the Y axis
			if (angles3D.getY() != 0)
			{
				double	pointAngle = Math.atan2(z, x) - Math.toRadians(angles3D.getY());
				double	distance = Math.hypot(x, z);
				x = distance * Math.cos(pointAngle);
				z = distance * Math.sin(pointAngle);
			}
			
			// Rotates the point CW around the X axis
			if (angles3D.getX() != 0)
			{
				double	pointAngle = Math.atan2(z, y) - Math.toRadians(angles3D.getX());
				double	distance = Math.hypot(y, z);
				y = distance * Math.cos(pointAngle);
				z = distance * Math.sin(pointAngle);
			}
			
			// Un-normalizes the rotation origin
			setSum(origin);
		}
	}
	
	public GraphicObject	getNegative()
	{
		Point3D	r = (Point3D) clone();
		r.set(-x, -y, -z);
		return r;
	}
	
	public GraphicObject	getSum(Point p)
	{
		if (p instanceof Point3D)
		{
			Point3D	p3D = (Point3D) p;
			Point3D	r = (Point3D) clone();
			r.set( x + p3D.getX() , 
			       y + p3D.getY() , 
			       z + p3D.getZ() );
			return r;
		}
		return new Point3D();
	}
	
	public GraphicObject	getDifference(Point p)
	{
		if (p instanceof Point3D)
		{
			Point3D	p3D = (Point3D) p;
			Point3D	r = (Point3D) clone();
			r.set( x - p3D.getX() , 
			       y - p3D.getY() , 
			       z - p3D.getZ() );
			return r;
		}
		return new Point3D();
	}
	
	public GraphicObject	getProduct(double factor)
	{
		Point3D	r = (Point3D) clone();
		r.set( x * factor , 
		       y * factor , 
		       z * factor );
		return r;
	}
	
	public GraphicObject	getQuotient(double factor)
	{
		Point3D	r = (Point3D) clone();
		r.set( x / factor , 
		       y / factor , 
		       z / factor );
		return r;
	}
}