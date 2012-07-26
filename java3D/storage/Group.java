/* ------------------------------------------------------------
   About:      Graphical object container class
               Stores non-hierarchal GraphicObject-based objects.
   Author:     Michael C. Petrovich
               michael.c.petrovich@gmail.com
   
   Copyright © 2003-2007 Michael C. Petrovich
------------------------------------------------------------ */

package java3D.storage;

import java3D.storage.GroupListener.GroupUpdate;
import java.awt.Graphics;
import java.util.*;

public class	Group implements GraphicObject
{
	protected java.util.Vector<GraphicObject>	objects = new java.util.Vector<GraphicObject>();
	protected Attributes	attr;		// Initially unassigned for efficiency
	
	// Event handling
	private java.util.List<GroupListener>	listeners;		// Initially unassigned for efficiency
	private int	numHolds = 0;
	
	
	/* Constructors
	---------------------------------------------------------------------- */
	public	Group()
	{}
	
	public	Group(GraphicObject... objects)
	{
		setList(objects);
	}
	
	public	Group(Attributes attr, GraphicObject... objects)
	{
		setList(objects);
		setAttr(attr);
	}
	
	public	Group(java.util.List<GraphicObject> objects)
	{
		setList(objects);
	}
	
	
	/* Data access
	---------------------------------------------------------------------- */
	public GraphicObject	get(int index)
	// Returns a copy of the element at the specified position
	{
		return objects.get(index).clone();
	}
	
	public void	add(GraphicObject... objects)
	// Adds the given objects to the group
	{
		holdUpdates(true);
		
		for (GraphicObject obj : objects)
		{
			this.objects.add(obj.clone());
		}
		
		holdUpdates(false);
	}
	
	public void	add(java.util.List<GraphicObject> objects)
	// Adds the objects in the given list to the group
	{
		add(objects.toArray(new GraphicObject[0]));
	}
	
	public void	set(GraphicObject object)
	// Replaces the current objects ONLY
	{
		set(object, getAttr());
	}
	
	public void	setAll(GraphicObject object)
	// Replaces the current objects AND attributes
	{
		set(object, object.getAttr());
	}
	
	public void	set(GraphicObject object, Attributes attr)
	// Replaces the current objects AND attributes
	{
		holdUpdates(true);
		
		if (object instanceof Group)
		{
			Group	group = (Group) object;
			clear();
			add(group.objects);
			setAttr(attr);
		}
		
		holdUpdates(false);
	}
	
	public void	setList(GraphicObject... objects)
	// For multiple objects, only existing objects are replaced
	// For a single object, existing objects AND attributes are replaced
	{
		holdUpdates(true);
		
		if (objects.length == 1)
		// Single group passed
		{
			setAll(objects[0]);
		}
		else
		// Multiple objects passed
		{
			clear();
			add(objects);
		}
		
		holdUpdates(false);
	}
	
	public void	setList(java.util.List<GraphicObject> objects)
	// Replaces existing objects with objects in the given list
	{
		holdUpdates(true);
		
		clear();
		add(objects);
		
		holdUpdates(false);
	}
	
	public void	clear()
	// Removes all existing objects
	{
		holdUpdates(true);
		
		objects.clear();
		
		holdUpdates(false);
	}
	
	public int	size()
	// Returns the number of objects in the group
	{
		return objects.size();
	}
	
	public java.util.List<GraphicObject>	toList()
	// Returns a copy of the objects as a list
	{
		return new java.util.Vector<GraphicObject>(objects);
	}
	
	public GraphicObject	clone()
	{
		return new Group(this);
	}
	
	public boolean	equals(Group group)
	{
		return objects.equals(group.objects);
	}
	
	public String	toString()
	{
		String	str = new String();
		str += "Size: " + objects.size() + "\n";
		for (GraphicObject obj : objects)
		{
			str += obj + "\n";
		}
		return str;
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
		holdUpdates(true);
		
		allocateAttr();
		this.attr.set(attr);
		
		holdUpdates(false, GroupUpdate.Attributes);
	}
	
	public void	setAttrToAll(Attributes attr)
	{
		holdUpdates(true);
		
		setAttr(attr);
		for (GraphicObject obj : objects)
		{
			obj.setAttrToAll(attr);
		}
		
		holdUpdates(false, GroupUpdate.Attributes);
	}
	
	private void	allocateAttr()
	{
		if (attr == null)
		{
			attr = new Attributes();
		}
	}
	
	
	/* Event handling
	---------------------------------------------------------------------- */
	public void	addGroupListener(GroupListener l)
	{
		allocateListeners();
		listeners.add(l);
	}
	
	public void	update()
	{
		update(GroupUpdate.All);
	}
	
	public void	update(GroupUpdate update)
	// Notifies group listeners of any changes
	{
		if (!isHolding())
		{
			for (GroupListener l : listeners)
			{
				l.groupUpdate(update);
			}
		}
	}
	
	public void	holdUpdates(boolean doHold)
	{
		holdUpdates(doHold, GroupUpdate.All);
	}
	
	public void	holdUpdates(boolean doHold, GroupUpdate update)
	// Temporarily suppresses group listener notifications
	{
		if (listeners == null)
			return;
		
		if (doHold)
		{
			numHolds++;
		}
		else
		{
			numHolds--;
		}
		
		// Notifies listeners if all holds have been removed
		if (numHolds <= 0)
		{
			numHolds = 0;
			update(update);
		}
	}
	
	private boolean	isHolding()
	// Returns whether group listener notifications are being suppressed
	{
		return (listeners == null || numHolds > 0);
	}
	
	private void	allocateListeners()
	{
		if (listeners == null)
		{
			listeners = new java.util.Vector<GroupListener>();
		}
	}
	
	
	/* Extents
	---------------------------------------------------------------------- */
	public double	getExtentsMaxRange()
	// Returns the size of the greatest extents range
	{
		Point3D	minExtents = new Point3D(getMinExtents());
		Point3D	maxExtents = new Point3D(getMaxExtents());
		return Math.max( maxExtents.getX() - minExtents.getX() , 
		       Math.max( maxExtents.getY() - minExtents.getY() ,
		                 maxExtents.getZ() - minExtents.getZ() ));
	}
	
	private Point	getMinExtents()
	// Returns the smallest value in each dimension as an ordered triple
	{
		return getExtents(new Comparator<Double>()
		{
			public int	compare(Double d1, Double d2)
			{
				return (int) (d2 - d1);
			}
		});
	}
	
	private Point	getMaxExtents()
	// Returns the largest value in each dimension as an ordered triple
	{
		return getExtents(new Comparator<Double>()
		{
			public int	compare(Double d1, Double d2)
			{
				return (int) (d1 - d2);
			}
		});
	}
	
	public Point	getExtents(Comparator<Double> comp)
	{
		Point3D	extents = new Point3D();
		
		// Initializes the extents to the first element's extents
		if (objects.size() > 0)
		{
			extents = new Point3D(objects.get(0).getExtents(comp));
		}
		
		for (GraphicObject obj : objects)
		{
			// Gets the element's extents
			Point	subExtents = obj.getExtents(comp);
			Point2D	subExtents2D = null;
			Point3D	subExtents3D = null;
			if (subExtents instanceof Point2D)
			{
				subExtents2D = (Point2D) subExtents;
			}
			if (subExtents instanceof Point3D)
			{
				subExtents3D = (Point3D) subExtents;
			}
			
			// Saves the extreme values
			if (subExtents2D != null && comp.compare(subExtents2D.getX(), extents.getX()) > 0)
			{
				extents.setX( subExtents2D.getX() );
			}
			if (subExtents2D != null && comp.compare(subExtents2D.getY(), extents.getY()) > 0)
			{
				extents.setY( subExtents2D.getY() );
			}
			if (subExtents3D != null && comp.compare(subExtents3D.getZ(), extents.getZ()) > 0)
			{
				extents.setZ( subExtents3D.getZ() );
			}
		}
		
		return extents;
	}
	
	
	/* Rendering
	---------------------------------------------------------------------- */
	public Point	getCenter()
	// Returns the geometric center point of the group
	{
		if (objects.size() > 0)
		{
			Point3D	center = new Point3D();
			for (GraphicObject obj : objects)
			{
				center.setSum(obj.getCenter());
			}
			return (Point) center.getQuotient(objects.size());
		}
		return new Point3D();
	}
	
	public GraphicObject	render(Camera camera)
	{
		Group	rendered = new Group();
		for (GraphicObject obj : objects)
		{
			rendered.add(obj.render(camera));
		}
		rendered.setAttr(getAttr());
		return rendered;
	}
	
	public void	paint(Graphics g)
	{
		// Builds the 2D screen polygon
		java.awt.Polygon	polygon = null;
		for (GraphicObject obj : objects)
		{
			if (obj instanceof Point2D)
			{
				if (polygon == null)
				{
					polygon = new java.awt.Polygon();
				}
				
				Point2D	pt = (Point2D) obj;
				polygon.addPoint((int) pt.getX(), (int) pt.getY());
			}
		}
		
		// Draws the 2D screen polygon
		if (polygon != null)
		{
			// Draws filled polygon
			if (getAttr().showFill)
			{
				g.setColor(getAttr().getFillColor());
				g.fillPolygon(polygon);
			}
			
			// Draws unfilled polygon
			if (getAttr().showEdges)
			{
				g.setColor(getAttr().getEdgeColor());
				g.drawPolygon(polygon);
			}
			
			// Draws polygon vertices
			if (getAttr().showPoints)
			{
				for (GraphicObject obj : objects)
				{
					obj.paint(g);
				}
			}
		}
	}
	
	
	/* Mathematics
	---------------------------------------------------------------------- */
	public void	rotate(Point angles)
	{
		rotate(angles, new Point3D());
	}
	
	public void	rotate(Point angles, Point origin)
	{
		holdUpdates(true);
		
		for (GraphicObject obj : objects)
		{
			obj.rotate(angles, origin);
		}
		
		holdUpdates(false);
	}
	
	public GraphicObject	getNegative()
	{
		Group	group = new Group();
		for (GraphicObject obj : objects)
		{
			group.add(obj.getNegative());
		}
		return group;
	}
	
	public GraphicObject	getProduct(double factor)
	{
		Group	group = new Group();
		for (GraphicObject obj : objects)
		{
			group.add(obj.getProduct(factor));
		}
		return group;
	}
	
	public GraphicObject	getQuotient(double factor)
	{
		Group	group = new Group();
		for (GraphicObject obj : objects)
		{
			group.add(obj.getQuotient(factor));
		}
		return group;
	}
	
	public void	setNegative()
	{
		set((Group) getNegative());
	}
	
	public void	setProduct(double factor)
	{
		set((Group) getProduct(factor));
	}
	
	public void	setQuotient(double factor)
	{
		set((Group) getQuotient(factor));
	}
}