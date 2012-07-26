/* ------------------------------------------------------------
   About:      Graphics panel class
               Main 3D environment viewer panel.
   Author:     Michael C. Petrovich
               michael.c.petrovich@gmail.com
   
   Copyright © 2003-2007 Michael C. Petrovich
------------------------------------------------------------ */

import java3D.*;
import java3D.storage.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;

public class	GraphicsPanel extends JPanel 
            	              implements CameraListener, GroupListener, 
            	                         MouseListener, MouseMotionListener, 
            	                         MouseWheelListener, KeyListener, 
            	                         ComponentListener
{
	// Callbacks
	private JFrame	window;
	private Environment	envr;
	
	// Settings
	private Color	backgroundColor = new Color(225, 225, 225);
	private	double	degreesPerPixel = 0.40;
	private	double	degreesPerKey   = 2.00;
	private double	zoomPerKey      = 1.05;
	private double	zoomPerWheel    = 1.02;
	private double	zoomInset       = 0.50;		// In fraction of panel size
	private double	pixelsPerKey    = 5;
	
	private java.awt.Point	prevMousePt;
	private	Point2D	prevScreenCenter;
	private double	prevArea = 0;
	
	
	/* Constructors
	---------------------------------------------------------------------- */
	public	GraphicsPanel(JFrame window, Environment envr)
	{
		this.window = window;
		this.envr = envr;
		
		buildPanel();
		envr.camera.addCameraListener(this);
		envr.object.addGroupListener(this);
		
		requestFocusInWindow(true);
	}
	
	private void	buildPanel()
	{
		setBackground(backgroundColor);
		
		addMouseListener(this);
		addMouseMotionListener(this);
		addMouseWheelListener(this);
		addKeyListener(this);
		addComponentListener(this);
	}
	
	public void	paint(Graphics g)
	{
		super.paint(g);
		envr.render();
		envr.paint(g);
	}
	
	
	/* Event handling - Java3D
	---------------------------------------------------------------------- */
	public void	cameraUpdate(CameraUpdate update)
	{
		repaint();
	}
	
	public void	groupUpdate(GroupUpdate update)
	{
		switch (update)
		{
			// Object data modified
			case All:
				envr.camera.holdUpdates(true);
				
				// Keeps zoom independent of view angles
				Point3D	viewAngles = envr.camera.getViewAngles();
				envr.camera.setViewAngles(new Point3D());
				
				// Zooms object so that it is centered in and fills the panel
				Rectangle	rect = new Rectangle(getSize());
				rect.grow( (int) (-rect.width * zoomInset/2), 
				           (int) (-rect.height * zoomInset/2) );
				envr.camera.setZoomTo(envr.object, rect);
				
				// Restores view angles
				envr.camera.setViewAngles(viewAngles);
				
				envr.camera.holdUpdates(false);
				requestFocusInWindow(true);
				break;
			
			// Only object attributes modified
			case Attributes:
				repaint();
		}
	}
	
	
	/* Event handling - Java GUI
	---------------------------------------------------------------------- */
	public void	componentResized(ComponentEvent e)
	{
		envr.camera.holdUpdates(true);
		
		double	area = getWidth() * getHeight();
		Point2D	screenCenter = new Point2D(getWidth()/2, getHeight()/2);
		
		if (prevArea > 0 && prevScreenCenter != null)
		{
			// Keeps camera centered in panel after resizing panel
			Point2D	diff = (Point2D) screenCenter.getDifference(prevScreenCenter);
			Point2D	origin = envr.camera.getScreenOrigin();
			envr.camera.setScreenOrigin((Point2D) origin.getSum(diff));
			
			// Zooms proportionally with the change in the panel area
			double	newZoom = envr.camera.getZoom() * Math.sqrt(area / prevArea);
			envr.camera.setZoomTo(newZoom);
		}
		
		prevArea = area;
		prevScreenCenter = screenCenter;
		
		envr.camera.holdUpdates(false);
	}
	
	public void	mouseReleased(MouseEvent e)
	{
		prevMousePt = null;
	}
	
	public void	mouseDragged(MouseEvent e)
	{
		envr.camera.holdUpdates(true);
		
		java.awt.Point	pt = e.getPoint();
		if (prevMousePt != null)
		{
			// Translates the camera screen origin
			if (e.isShiftDown() || e.getButton() == MouseEvent.BUTTON3)
			{
				Point2D	p = envr.camera.getScreenOrigin();
				p.setSum( new Point2D(pt.x - prevMousePt.x, pt.y - prevMousePt.y) );
				envr.camera.setScreenOrigin(p);
			}
			else
			
			// Rotates the camera view (in X)
			if (e.isAltDown())
			{
				Point3D	p = envr.camera.getViewAngles();
				p.setX( p.getX() + (pt.x - prevMousePt.x) * degreesPerPixel );
				envr.camera.setViewAngles(p);
			}
			
			// Rotates the camera view (in Y, Z)
			else
			{
				Point3D	p = envr.camera.getViewAngles();
				
				// Flips rotation direction when view is upside-down
				int	direction = 1;
				if (90 < Math.abs(p.getY()) && Math.abs(p.getY()) < 270)
				{
					direction = -1;
				}
				
				p.setY( p.getY() + (pt.y - prevMousePt.y) * degreesPerPixel );
				p.setZ( p.getZ() + (prevMousePt.x - pt.x) * degreesPerPixel * direction );
				envr.camera.setViewAngles(p);
			}
		}
		prevMousePt = pt;
		
		envr.camera.holdUpdates(false);
	}
	
	public void	mouseWheelMoved(MouseWheelEvent e)
	{
		envr.camera.holdUpdates(true);
		
		int	clicks = e.getWheelRotation();
		Point2D	center = new Point2D(getWidth()/2, getHeight()/2);
		
		if (e.isShiftDown())
		{
			// Zooms camera into rotation origin (usually at object center)
			if (clicks > 0)
			{
				envr.camera.setZoomTo(envr.camera.getZoom() * Math.pow(zoomPerWheel, clicks));
			}
			
			// Zooms camera out from rotation origin (usually at object center)
			else
			{
				envr.camera.setZoomTo(envr.camera.getZoom() / Math.pow(zoomPerWheel, -clicks));
			}
		}
		else
		{
			// Zooms camera into center of panel
			if (clicks > 0)
			{
				envr.camera.setZoomTo(envr.camera.getZoom() * Math.pow(zoomPerWheel, clicks), center);
			}
			
			// Zooms camera out from center of panel
			else
			{
				envr.camera.setZoomTo(envr.camera.getZoom() / Math.pow(zoomPerWheel, -clicks), center);
			}
		}
		
		envr.camera.holdUpdates(false);
	}
	
	public void	keyPressed(KeyEvent e)
	{
		envr.camera.holdUpdates(true);
		
		// Flips rotation direction when view is upside-down
		Point3D	angles = envr.camera.getViewAngles();
		int	direction = 1;
		if (90 < Math.abs(angles.getY()) && Math.abs(angles.getY()) < 270)
		{
			direction = -1;
		}
		
		switch (e.getKeyCode())
		{
			case KeyEvent.VK_RIGHT:
			case KeyEvent.VK_KP_RIGHT:
				// Shifts camera screen origin right
				if (e.isShiftDown())
				{
					Point2D	p = envr.camera.getScreenOrigin();
					p.setX(p.getX() - pixelsPerKey);
					envr.camera.setScreenOrigin(p);
				}
				else
				
				// Rotates camera right (in X)
				if (e.isAltDown())
				{
					Point3D	p = envr.camera.getViewAngles();
					p.setX(p.getX() + degreesPerKey);
					envr.camera.setViewAngles(p);
				}
				
				// Rotates camera right (in Z)
				else
				{
					Point3D	p = envr.camera.getViewAngles();
					p.setZ(p.getZ() + degreesPerKey * direction);
					envr.camera.setViewAngles(p);
				}
				break;
			
			case KeyEvent.VK_LEFT:
			case KeyEvent.VK_KP_LEFT:
				// Shifts camera screen origin left
				if (e.isShiftDown())
				{
					Point2D	p = envr.camera.getScreenOrigin();
					p.setX(p.getX() + pixelsPerKey);
					envr.camera.setScreenOrigin(p);
				}
				else
				
				// Rotates camera left (in X)
				if (e.isAltDown())
				{
					Point3D	p = envr.camera.getViewAngles();
					p.setX(p.getX() - degreesPerKey);
					envr.camera.setViewAngles(p);
				}
				
				// Rotates camera left (in Z)
				else
				{
					Point3D	p = envr.camera.getViewAngles();
					p.setZ(p.getZ() - degreesPerKey * direction);
					envr.camera.setViewAngles(p);
				}
				break;
			
			case KeyEvent.VK_UP:
			case KeyEvent.VK_KP_UP:
				// Shifts camera screen origin up
				if (e.isShiftDown())
				{
					Point2D	p = envr.camera.getScreenOrigin();
					p.setY(p.getY() + pixelsPerKey);
					envr.camera.setScreenOrigin(p);
				}
				
				// Rotates camera up (in Y)
				else
				{
					Point3D	p = envr.camera.getViewAngles();
					p.setY(p.getY() + degreesPerKey);
					envr.camera.setViewAngles(p);
				}
				break;
			
			case KeyEvent.VK_DOWN:
			case KeyEvent.VK_KP_DOWN:
				// Shifts camera screen origin down
				if (e.isShiftDown())
				{
					Point2D	p = envr.camera.getScreenOrigin();
					p.setY(p.getY() - pixelsPerKey);
					envr.camera.setScreenOrigin(p);
				}
				
				// Rotates camera down (in Y)
				else
				{
					Point3D	p = envr.camera.getViewAngles();
					p.setY(p.getY() - degreesPerKey);
					envr.camera.setViewAngles(p);
				}
				break;
		}
		
		envr.camera.holdUpdates(false);
	}
	
	public void	keyTyped(KeyEvent e)
	{
		envr.camera.holdUpdates(true);
		
		switch (e.getKeyChar())
		{
			// Zooms camera into center of panel
			case '=':
			{
				Point2D	center = new Point2D(getWidth()/2, getHeight()/2);
				envr.camera.setZoomTo(envr.camera.getZoom() * zoomPerKey, center);
				break;
			}
			
			// Zooms camera out from center of panel
			case '-':
			{
				Point2D	center = new Point2D(getWidth()/2, getHeight()/2);
				envr.camera.setZoomTo(envr.camera.getZoom() / zoomPerKey, center);
				break;
			}
			
			// Zooms camera into rotation origin (usually at object center)
			case '+':
				envr.camera.setZoomTo(envr.camera.getZoom() * zoomPerKey);
				break;
			
			// Zooms camera out from rotation origin (usually at object center)
			case '_':
				envr.camera.setZoomTo(envr.camera.getZoom() / zoomPerKey);
				break;
		}
		
		envr.camera.holdUpdates(false);
	}
	
	public void	mouseClicked(MouseEvent e)
	{
		requestFocusInWindow(true);
	}
	
	public void	mousePressed(MouseEvent e)  {}
	public void	mouseEntered(MouseEvent e)  {}
	public void	mouseExited(MouseEvent e)  {}
	public void	mouseMoved(MouseEvent e)  {}
	public void	keyReleased(KeyEvent e)  {}
	public void	componentHidden(ComponentEvent e)  {}
	public void	componentMoved(ComponentEvent e)  {}
	public void	componentShown(ComponentEvent e)  {}
}