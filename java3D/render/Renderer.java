/* ------------------------------------------------------------
   About:      Rendering class
               Encapsulates rendering algorithms and storage.
   Author:     Michael C. Petrovich
               michael.c.petrovich@gmail.com
   
   Copyright © 2003-2007 Michael C. Petrovich
------------------------------------------------------------ */

package java3D.render;

import java3D.storage.*;
import java.awt.Graphics;
import java.util.*;

public class	Renderer
{
	/* Public
	---------------------------------------------------------------------- */
	public	Renderer()
	{}
	
	public GraphicObject	render(GraphicObject object, Camera camera)
	// Caches and renders the given object
	{
		Group	group = new Group(object);
		this.camera = camera;
		
		switch (camera.getRenderQuality())
		{
			// Fixed point pixel size, no distance-based sorting, no fill
			case Low:
				// Disables fill
				Attributes	attr = group.getAttr();
				attr.showFill = false;
				group.setAttrToAll(attr);
				
				original = group.toList();
				calculateProjections();
				break;
			
			// Zoom-proportional point pixel size, distance-based sorting
			case Med:
				original = group.toList();
				calculateDistances();
				sortByDistance();
				calculateProjections();
				break;
			
			// Points as separate objects, polygon subdivision
			case High:
				original = group.toList();
				subdivide();
				calculateDistances();
				sortByDistance();
				calculateProjections();
				break;
		}
		
		return new Group(rendered);
	}
	
	public void	paint(Graphics g)
	// Draws rendered objects
	{
		for (GraphicObject obj : rendered)
		{
			obj.paint(g);
		}
	}
	
	
	/* Internals
	---------------------------------------------------------------------- */
	private Camera	camera;
	private java.util.List<GraphicObject>	original = null;
	private java.util.List<SortObject>	sorted = new java.util.Vector<SortObject>();
	private java.util.List<GraphicObject>	rendered = new java.util.Vector<GraphicObject>();
	
	private class	SortObject
	// Encapsulsates objects for distance-based sorting
	{
		public GraphicObject	object;
		public double	distance;
		
		public	SortObject(GraphicObject object)
		{
			this.object = object;
			distance = 0;
		}
		
		public	SortObject(GraphicObject object, double distance)
		{
			this.object = object;
			this.distance = distance;
		}
	}
	
	private void	calculateDistances()
	// Computes and stores camera-object distances
	{
		sorted.clear();
		for (GraphicObject obj : original)
		{
			Point3D	center = (Point3D) obj.getCenter();
			Point3D	origin = camera.getCameraLocation();
			double	distance = center.getDistanceFrom(origin);
			sorted.add(new SortObject(obj, distance));
		}
	}
	
	private void	sortByDistance()
	// Sorts objects in order of descending camera-object distance
	{
		Collections.sort(sorted, new Comparator<SortObject>()
		{
			public int compare(SortObject o1, SortObject o2)
			{
				if (o2.distance - o1.distance > 0)
				{
					return 1;
				}
				else
				if (o2.distance - o1.distance < 0)
				{
					return -1;
				}
				else
				{
					return 0;
				}
			}
		} );
	}
	
	private void	calculateProjections()
	// Computes and stores the 2D screen object projections
	{
		rendered.clear();
		
		switch (camera.getRenderQuality())
		{
			case Low:
				for (GraphicObject obj : original)
				{
					rendered.add( obj.render(camera) );
				}
				break;
			
			case Med:
			case High:
				for (SortObject sobj : sorted)
				{
					rendered.add( sobj.object.render(camera) );
				}
				break;
		}
	}
	
	private void	subdivide()
	// Subdivides original polygons
	{
		java.util.List<GraphicObject>	subdivided = new java.util.Vector<GraphicObject>();
		for (GraphicObject obj : original)
		{
			if (obj instanceof Group)
			{
				Group	group = (Group) obj;
				switch (group.size())
				{
					// Triangle
					case 3:
						Point3D	v0 = new Point3D( group.get(0) );
						Point3D	v1 = new Point3D( group.get(1) );
						Point3D	v2 = new Point3D( group.get(2) );
						
						// Calculates midpoints
						Point3D	mid01 = new Point3D();
						Point3D	mid12 = new Point3D();
						Point3D	mid20 = new Point3D();
						mid01.set( v0 .getSum( (Point)
						           ((Point) group.get(1)) .getDifference( (Point) group.get(0) ) .getProduct( 0.5 ) ));
						mid12.set( v1 .getSum( (Point)
						           ((Point) group.get(2)) .getDifference( (Point) group.get(1) ) .getProduct( 0.5 ) ));
						mid20.set( v2 .getSum( (Point)
						           ((Point) group.get(0)) .getDifference( (Point) group.get(2) ) .getProduct( 0.5 ) ));
						
						// Creates subdivided triangles
						Attributes	attr = group.getAttr();
						//attr.showEdges = false;
						attr.edgeOpacity = 0.25;
						attr.showPoints = false;
						Group	tri0 = new Group(attr, v0, mid01, mid20);
						Group	tri1 = new Group(attr, v1, mid01, mid12);
						Group	tri2 = new Group(attr, v2, mid12, mid20);
						Group	tri3 = new Group(attr, mid01, mid12, mid20);
						
						subdivided.add(tri0);
						subdivided.add(tri1);
						subdivided.add(tri2);
						subdivided.add(tri3);
						
						attr = group.getAttr();
						attr.showFill = false;
						group.setAttr(attr);
						break;
				}
			}
		}
		//original.clear();
		original.addAll(subdivided);
	}
}