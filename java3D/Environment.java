/* ------------------------------------------------------------
   About:      Graphics environment class
               Encapsulates objects, camera, and renderer.
   Author:     Michael C. Petrovich
               michael.c.petrovich@gmail.com
   
   Copyright © 2003-2007 Michael C. Petrovich
------------------------------------------------------------ */

package java3D;

import java3D.storage.*;
import java3D.render.*;
import java3D.io.*;
import java.awt.Graphics;
import java.util.*;

public class	Environment
{
	public Group	object = new Group();
	public Camera	camera = new Camera();
	private Renderer	renderer = new Renderer();
	
	
	/* Constructors
	---------------------------------------------------------------------- */
	public	Environment()
	{}
	
	
	/* I/O
	---------------------------------------------------------------------- */
	public boolean	load(String filename)
	{
		GraphicObject	newObject = ObjectReader.read(filename);
		if (newObject != null)
		// Load successful
		{
			// Replaces objects only (not attributes)
			Attributes	attr = object.getAttr();
			object.set(newObject);
			object.setAttrToAll(attr);
			
			// Perspective settings
			camera.setExtentsMaxRange( object.getExtentsMaxRange() );
			
			return true;
		}
		else
		// Load failed
		{
			return false;
		}
	}
	
	
	/* Rendering
	---------------------------------------------------------------------- */
	public void	render()
	{
		renderer.render(object, camera);
	}
	
	public void	paint(Graphics g)
	{
		renderer.paint(g);
		camera.paint(g);
	}
}