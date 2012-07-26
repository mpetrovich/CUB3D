/* ------------------------------------------------------------
   About:      Camera class
               Stores common rendering properties.
   Author:     Michael C. Petrovich
               michael.c.petrovich@gmail.com
   
   Copyright © 2003-2007 Michael C. Petrovich
------------------------------------------------------------ */

package java3D.storage;

import java3D.storage.CameraListener.CameraUpdate;
import java3D.render.*;
import java.awt.*;
import javax.swing.*;
import java.util.*;

public class	Camera
{
	private Point3D	viewAngles;		// Specifies the angle of CW rotation around each axis
	private double	zoom;		// Fractional zoom
	private Point3D	rotateOrigin;		// 3D point around which the view rotates
	private Point2D	screenOrigin;		// Screen position of the 3D origin
	private double	rotateRadius;		// Viewing distance from the rotation origin
	public Projector	isometricProjector, perspectiveProjector;		// Cached projectors for public use
	private Projector	projector;		// Current projector
	private RenderQuality	quality;
	
	public enum	RenderQuality { Low, Med, High }
	
	// Perspective settings
	private double	extentsMaxRange;
	private double	rangeMultiplier;
	private double	perspectiveUnit;
	
	// Event handling
	private java.util.List<CameraListener>	listeners;		// Initially unassigned for efficiency
	private int	numHolds = 0;
	
	
	/* Constructors
	---------------------------------------------------------------------- */
	public	Camera()
	{
		viewAngles   = new Point3D();
		zoom         = 1.00;
		rotateOrigin = new Point3D();
		screenOrigin = new Point2D();
		rotateRadius = 1e3;
		isometricProjector   = new IsometricProjector();
		perspectiveProjector = new PerspectiveProjector();
		projector    = perspectiveProjector;
		quality      = RenderQuality.Med;
		
		// Perspective settings
		extentsMaxRange = 1;
		rangeMultiplier = 2;
		perspectiveUnit = 2;
	}
	
	public	Camera(Camera cam)
	{
		set(cam);
	}
	
	
	/* Data access - Accessors
	---------------------------------------------------------------------- */
	public Point3D	getViewAngles()
	{
		return (Point3D) viewAngles.clone();
	}
	
	public double	getZoom()
	{
		return zoom;
	}
	
	public Point3D	getRotateOrigin()
	{
		return (Point3D) rotateOrigin.clone();
	}
	
	public Point2D	getScreenOrigin()
	{
		return (Point2D) screenOrigin.clone();
	}
	
	public double	getRotateRadius()
	{
		return rotateRadius;
	}
	
	public Projector	getProjector()
	{
		return projector;
	}
	
	public RenderQuality	getRenderQuality()
	{
		return quality;
	}
	
	public double	getExtentsMaxRange()
	{
		return extentsMaxRange;
	}
	
	public double	getRangeMultiplier()
	{
		return rangeMultiplier;
	}
	
	public double	getPerspectiveUnit()
	{
		return perspectiveUnit;
	}
	
	public Point3D	getCameraLocation()
	{
		return getCameraLocation(rotateRadius);
	}
	
	public Point3D	getCameraLocation(double rotateRadius)
	// Returns the 3D location of the camera
	{
		Point3D	origin = new Point3D();
		origin.setX( rotateRadius * Math.cos(Math.toRadians( viewAngles.getY() )) 
		                          * Math.cos(Math.toRadians( viewAngles.getZ() )) );
		origin.setY( rotateRadius * Math.cos(Math.toRadians( viewAngles.getY() )) 
		                          * Math.sin(Math.toRadians( viewAngles.getZ() )) );
		origin.setZ( rotateRadius * Math.sin(Math.toRadians( viewAngles.getY() )) );
		return (Point3D) origin.getSum(rotateOrigin);
	}
	
	public Camera	clone()
	{
		return new Camera(this);
	}
	
	public String	toString()
	{
		String	s = new String();
		
		s += "Camera dump:\n";
		s += "View   = " + viewAngles + "\n";
		s += "Zoom   = " + zoom + "\n";
		s += "Rotate = " + rotateOrigin + "\n";
		s += "Screen = " + screenOrigin + "\n";
		s += "\n";
		
		return s;
	}
	
	
	/* Data access - Modifiers
	---------------------------------------------------------------------- */
	public void	setViewAngles(Point3D viewAngles)
	{
		holdUpdates(true);
		
		this.viewAngles.set(viewAngles);
		simplifyViewAngles();
		
		holdUpdates(false);
	}
	
	public void	setZoom(double zoom)
	// Zooms without changing the screen origin
	{
		holdUpdates(true);
		
		this.zoom = zoom;
		
		holdUpdates(false);
	}
	
	public void	setZoomTo(double zoom)
	// Zooms without changing the screen position of the rotation origin
	{
		holdUpdates(true);
		
		// Saves the screen position of the current zoom
		Point2D	prev_rotateOrigin2D = (Point2D) rotateOrigin.render(this);
		setZoom(zoom);
		
		Point2D	rotateOrigin2D = (Point2D) rotateOrigin.render(this);
		Point2D	centerDiff     = (Point2D) prev_rotateOrigin2D.getDifference(rotateOrigin2D);
		setScreenOrigin( (Point2D) screenOrigin.getSum(centerDiff) );
		
		holdUpdates(false);
	}
	
	public void	setZoomTo(double zoom, Point2D pt)
	// Zooms without changing the screen position of the given point
	{
		holdUpdates(true);
		
		Point2D	diff = (Point2D) screenOrigin.getDifference(pt);
		diff.setProduct(zoom / this.zoom);
		screenOrigin.set( pt.getSum(diff) );
		setZoom(zoom);
		
		holdUpdates(false);
	}
	
	public void	setZoomTo(GraphicObject object, Rectangle rect)
	// Zooms so that the object is centered in and fills the given rectangle
	// TODO: Review process
	{
		holdUpdates(true);
		
		// Finds the screen size of the rendered object
		java3D.render.Renderer	R = new java3D.render.Renderer();
		GraphicObject	rendered;
		double	objectRange = 0;
		for (int i = 0; i < 5; i++)		// Capped to prevent endless looping
		{
			// Renders the object offline
			rendered = R.render(object, this);
			objectRange = rendered.getExtentsMaxRange();
			
			// Object is visible at current zoom
			if (objectRange > 1)
			{
				break;
			}
			
			// Zoom is too low to see object
			else
			{
				zoom *= 100;
			}
		}
		
		// Computes and applies the rectangle-tailored zoom
		double	screenRange = Math.min(rect.width, rect.height);
		double	newZoom = zoom * (screenRange / objectRange);
		setZoom(newZoom);
		
		// Centers the object on the middle of the rectangle
		Point2D	center = new Point2D(rect.x+rect.width/2, rect.y+rect.height/2);
		centerOn(center, object);
		
		holdUpdates(false);
	}
	
	public void	setRotateOrigin(Point3D rotateOrigin)
	{
		holdUpdates(true);
		
		this.rotateOrigin.set(rotateOrigin);
		
		holdUpdates(false);
	}
	
	public void	setScreenOrigin(Point2D screenOrigin)
	{
		holdUpdates(true);
		
		this.screenOrigin.set(screenOrigin);
		
		holdUpdates(false);
	}
	
	public void	setRotateRadius(double rotateRadius)
	{
		holdUpdates(true);
		
		this.rotateRadius = rotateRadius;
		
		holdUpdates(false);
	}
	
	public void	setProjector(Projector projector)
	{
		holdUpdates(true);
		
		this.projector = projector;
		
		holdUpdates(false);
	}
	
	public void	setRenderQuality(RenderQuality quality)
	{
		holdUpdates(true);
		
		this.quality = quality;
		
		holdUpdates(false);
	}
	
	public void	setExtentsMaxRange(double extentsMaxRange)
	{
		holdUpdates(true);
		
		this.extentsMaxRange = extentsMaxRange;
		
		holdUpdates(false);
	}
	
	public void	setRangeMultiplier(double rangeMultiplier)
	{
		holdUpdates(true);
		
		this.rangeMultiplier = rangeMultiplier;
		
		holdUpdates(false);
	}
	
	public void	setPerspectiveUnit(double perspectiveUnit)
	{
		holdUpdates(true);
		
		this.perspectiveUnit = perspectiveUnit;
		
		holdUpdates(false);
	}
	
	public void	set(Camera cam)
	{
		holdUpdates(true);
		
		setViewAngles(cam.viewAngles);
		setZoom(cam.zoom);
		setRotateOrigin(cam.rotateOrigin);
		setScreenOrigin(cam.screenOrigin);
		setRotateRadius(cam.rotateRadius);
		setRenderQuality(cam.quality);
		setExtentsMaxRange(cam.extentsMaxRange);
		setRangeMultiplier(cam.rangeMultiplier);
		setPerspectiveUnit(cam.perspectiveUnit);
		// Listeners are NOT copied
		
		holdUpdates(false);
	}
	
	public void	simplifyViewAngles()
	// Normalizes view angles to the range [0, 360)
	{
		holdUpdates(true);
		
		viewAngles.setX( getSimplifiedAngle(viewAngles.getX()) );
		viewAngles.setY( getSimplifiedAngle(viewAngles.getY()) );
		viewAngles.setZ( getSimplifiedAngle(viewAngles.getZ()) );
		
		holdUpdates(false);
	}
	
	public double	getSimplifiedAngle(double angle)
	// Normalizes the given angle to the range [0, 360)
	{
		while (angle <= -360 || 360 <= angle)
		{
			angle = angle - 360 * Math.signum(angle);
		}
		return angle;
	}
	
	public void	centerOn(Point2D pt, GraphicObject object)
	// Centers the camera on the given 2D screen point and on the object
	{
		holdUpdates(true);
		
		// Moves the rotation origin to the object's center
		setRotateOrigin( (Point3D) object.getCenter() );
		centerOn(pt);
		
		holdUpdates(false);
	}
	
	public void	centerOn(Point2D pt)
	// Centers the camera on the given 2D screen point
	{
		holdUpdates(true);
		
		// Moves the screen origin to the given point
		setScreenOrigin(pt);
		
		// Moves the rotation origin to the given point
		Point2D	rotateOrigin2D = (Point2D) rotateOrigin.render(this);
		Point2D	centerDiff     = (Point2D) screenOrigin.getDifference(rotateOrigin2D);
		setScreenOrigin( (Point2D) screenOrigin.getSum(centerDiff) );
		
		holdUpdates(false);
	}
	
	
	/* Event handling
	---------------------------------------------------------------------- */
	public void	addCameraListener(CameraListener l)
	{
		allocateListeners();
		listeners.add(l);
	}
	
	public void	update()
	{
		update(CameraUpdate.All);
	}
	
	public void	update(CameraUpdate update)
	// Notifies camera listeners of any changes
	{
		if (!isHolding())
		{
			for (CameraListener l : listeners)
			{
				l.cameraUpdate(update);
			}
		}
	}
	
	public void	holdUpdates(boolean doHold)
	{
		holdUpdates(doHold, CameraUpdate.All);
	}
	
	public void	holdUpdates(boolean doHold, CameraUpdate update)
	// Temporarily suppresses camera listener notifications
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
			update();
		}
	}
	
	private boolean	isHolding()
	// Returns whether camera listener notifications are being suppressed
	{
		return (listeners == null || numHolds > 0);
	}
	
	private void	allocateListeners()
	{
		if (listeners == null)
		{
			listeners = new java.util.Vector<CameraListener>();
		}
	}
	
	
	
	/* Rendering
	---------------------------------------------------------------------- */
	public Point2D	render(Point point)
	{
		if (point instanceof Point3D)
		{
			return projector.project3DtoScreen((Point3D) point, this);
		}
		
		if (point instanceof Point2D)
		{
			return projector.project2DtoScreen((Point2D) point, this);
		}
		
		return null;
	}
	
	public void	paint(Graphics g)
	{
		java.awt.Point	textPoint = new java.awt.Point(10, 0);
		int	lineSpacing = 20;
		Formatter	f;
		
		g.setColor(java.awt.Color.BLACK);
		
		// View angles
		f = new Formatter();
		f.format("(%.2f¡, %.2f¡)", viewAngles.getZ(), viewAngles.getY());
		g.drawString("View: " + f, textPoint.x, textPoint.y += lineSpacing);
		
		// Zoom
		f = new Formatter();
		if (zoom >= 1.00)
		{
			f.format("%d %%", (int) (zoom * 100.0));
		}
		else
		if (zoom >= 0.01)
		{
			f.format("%.2f %%", zoom * 100.0);
		}
		else
		{
			f.format("%.3f %%", zoom * 100.0);
		}
		g.drawString("Zoom: " + f, textPoint.x, textPoint.y += lineSpacing);
	}
}