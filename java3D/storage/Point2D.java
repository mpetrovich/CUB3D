/* ------------------------------------------------------------
   About:      2D Point class
   Author:     Michael C. Petrovich
               michael.c.petrovich@gmail.com
   
   Copyright © 2003-2007 Michael C. Petrovich
------------------------------------------------------------ */

package java3D.storage;

import java.awt.Graphics;
import java.util.*;

public class	Point2D implements Point
{
	protected double	x=0, y=0;
	protected Attributes	attr;		// Initially unassigned for efficiency
	
	
	/* Constructors
	---------------------------------------------------------------------- */
	public	Point2D()
	{}
	
	public	Point2D(double x, double y)
	{
		set(x, y);
	}
	
	public	Point2D(GraphicObject object)
	{
		setAll(object);
	}
	
	
	/* Data access
	---------------------------------------------------------------------- */
	public double	getX()
	{
		return x;
	}
	
	public double	getY()
	{
		return y;
	}
	
	public void	setX(double x)
	{
		this.x = x;
	}
	
	public void	setY(double y)
	{
		this.y = y;
	}
	
	public void	set(double x, double y)
	{
		setX(x);
		setY(y);
	}
	
	public void	set(GraphicObject object)
	// Replaces the current point ONLY
	{
		set(object, getAttr());
	}
	
	public void	setAll(GraphicObject object)
	// Replaces the current point AND attributes
	{
		set(object, object.getAttr());
	}
	
	public void	set(GraphicObject object, Attributes attr)
	// Replaces the current point AND attributes
	{
		if (object instanceof Point2D)
		{
			Point2D	p = (Point2D) object;
			set(p.getX(), p.getY());
			setAttr(attr);
		}
	}
	
	public GraphicObject	clone()
	{
		return new Point2D(this);
	}
	
	public boolean	equals(Point2D p)
	{
		return getX() == p.getX() && 
		       getY() == p.getY() ;
	}
	
	public String	toString()
	{
		return new String("(" + getX() + ", " + getY() + ")");
	}
	
	
	/* Attributes
	---------------------------------------------------------------------- */
	public Attributes	getAttr()
	{
		allocateAttr();
		return attr.clone();
	}
	
	public void	setAttr(Attributes attr)
	{
		allocateAttr();
		this.attr.set(attr);
	}
	
	public void	setAttrToAll(Attributes attr)
	{
		setAttr(attr);
	}
	
	private void	allocateAttr()
	{
		if (attr == null)
		{
			attr = new Attributes();
		}
	}
	
	
	/* Extents
	---------------------------------------------------------------------- */
	public Point	getExtents(Comparator<Double> comp)
	{
		return new Point2D(this);
	}
	
	public double	getExtentsMaxRange()
	{
		return 0;
	}
	
	
	/* Rendering
	---------------------------------------------------------------------- */
	public Point	getCenter()
	{
		return new Point2D(this);
	}
	
	public GraphicObject	render(Camera camera)
	{
		return camera.render(this);
	}
	
	public void	paint(Graphics g)
	{
		g.setColor(attr.getPointColor());
		if (attr.pixelRadius > 0)
		{
			double	sx = x - attr.pixelRadius;
			double	sy = y - attr.pixelRadius;
			double	dia = 2 * attr.pixelRadius;
			g.fillOval((int) sx, (int) sy, (int) dia, (int) dia);
		}
		else
		{
			g.drawRect((int) x, (int) y, 1, 1);
		}
	}
	
	
	/* Mathematics
	---------------------------------------------------------------------- */
	public double	getDistanceFrom(Point p)
	{
		if (p instanceof Point2D)
		{
			Point2D	p2D = (Point2D) p;
			return Math.sqrt( (x - p2D.getX()) * (x - p2D.getX()) + 
							  (y - p2D.getY()) * (y - p2D.getY()) );
		}
		return 0;
	}
	
	public void	rotate(Point angles)
	{
		rotate(angles, new Point2D());
	}
	
	public void	rotate(Point angles, Point origin)
	{
		if (angles instanceof Point2D)
		{
			Point2D	angles2D = (Point2D) angles;
			
			// Normalizes the rotation origin
			setDifference(origin);
			
			// Rotates the point around the normalized origin
			if (angles2D.getX() != 0)
			{
				double	pointAngle = Math.atan2(y, x) - Math.toRadians(angles2D.getX());
				double	distance = getDistanceFrom(new Point2D());
				x = distance * Math.cos(pointAngle);
				y = distance * Math.sin(pointAngle);
			}
			
			// Un-normalizes the rotation origin
			setSum(origin);
		}
	}
	
	public GraphicObject	getNegative()
	{
		Point2D	r = new Point2D(this);
		r.set(-x, -y);
		return r;
	}
	
	public GraphicObject	getSum(Point p)
	{
		if (p instanceof Point2D)
		{
			Point2D	p2D = (Point2D) p;
			Point2D	r = new Point2D(this);
			r.set( x + p2D.getX() , 
			       y + p2D.getY() );
			return r;
		}
		return null;
	}
	
	public GraphicObject	getDifference(Point p)
	{
		if (p instanceof Point2D)
		{
			Point2D	p2D = (Point2D) p;
			Point2D	r = new Point2D(this);
			r.set( x - p2D.getX() , 
			       y - p2D.getY() );
			return r;
		}
		return null;
	}
	
	public GraphicObject	getProduct(double factor)
	{
		Point2D	r = new Point2D(this);
		r.set( x * factor , 
		       y * factor );
		return r;
	}
	
	public GraphicObject	getQuotient(double factor)
	{
		Point2D	r = new Point2D(this);
		r.set( x / factor , 
		       y / factor );
		return r;
	}
	
	public void	setNegative()
	{
		set((Point) getNegative());
	}
	
	public void	setSum(Point p)
	{
		set((Point) getSum(p));
	}
	
	public void	setDifference(Point p)
	{
		set((Point) getDifference(p));
	}
	
	public void	setProduct(double factor)
	{
		set((Point) getProduct(factor));
	}
	
	public void	setQuotient(double factor)
	{
		set((Point) getQuotient(factor));
	}
}