/* ------------------------------------------------------------
   About:      Object reader class
   Author:     Michael C. Petrovich
               michael.c.petrovich@gmail.com
   
   Copyright © 2003-2007 Michael C. Petrovich
------------------------------------------------------------ */

package	java3D.io;

import java3D.storage.*;
import java.io.*;

public abstract class	ObjectReader
{
	abstract GraphicObject	read(File file);
	
	public static GraphicObject	read(String filename)
	// Auto-detects the file format and returns the object loaded from the file
	{
		// Selects a file reader
		ObjectReader	reader = null;
		if (filename.toLowerCase().endsWith(".stl"))
		// Stereolithography (STL) format
		{
			reader = new STLReader();
		}
		else
		if ( filename.toLowerCase().endsWith(".mpp") || 
		     filename.toLowerCase().endsWith(".txt") )
		// Microfab (MPP) format
		{
			reader = new MPPReader();
		}
		
		// Reads the file
		if (reader != null)
		{
			return reader.read(new File(filename));
		}
		else
		// Invalid file format
		{
			return null;
		}
	}
}