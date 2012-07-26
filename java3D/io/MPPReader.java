/* ------------------------------------------------------------
   About:      Microfabrication Path-Plan (MPP) reader class
   Author:     Michael C. Petrovich
               michael.c.petrovich@gmail.com
   
   Copyright © 2003-2007 Michael C. Petrovich
------------------------------------------------------------ */

package	java3D.io;

import java3D.storage.*;
import java.io.*;
import java.util.*;

public class	MPPReader extends ObjectReader
{
	public GraphicObject	read(File file)
	{
		try
		{
			Group	object = new Group();
			Scanner	in = new Scanner(file);
			
			// Ignores any non-data before the data set
			while (in.hasNext() && !in.next().equals("..."))
				;
			
			Group	path = new Group();
			while (in.hasNext())
			{
				double	x, y, z, velocity, intensity;
				
				// Reads data
				x = in.nextDouble();
				if (!in.hasNext())		// EOF
					break;
				y = in.nextDouble();
				z = in.nextDouble();
				velocity = in.nextDouble();
				intensity = in.nextDouble();
				
				// Appends points to the current path while the laser intensity is non-zero
				if (intensity == 0)
				{
					path.clear();
				}
				path.add(new Point3D(x, y, z));
				
				// Loads a completed path
				if (path.size() >= 2)
				{
					object.add(path);
					path.clear();
					path.add(new Point3D(x, y, z));
				}
			}
			
			in.close();
			return object;
		}
		catch (IOException e)
		{
			return null;
		}
	}
}