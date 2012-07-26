/* ------------------------------------------------------------
   About:      Camera event listener class
   Author:     Michael C. Petrovich
               michael.c.petrovich@gmail.com
   
   Copyright © 2003-2007 Michael C. Petrovich
------------------------------------------------------------ */

package java3D.storage;

public interface	CameraListener
{
	enum	CameraUpdate { All, Zoom }
	
	void	cameraUpdate(CameraUpdate update);
}