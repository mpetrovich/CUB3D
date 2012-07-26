/* ------------------------------------------------------------
   About:      Stereolithography (STL) format reader class
   Author:     Michael C. Petrovich
               michael.c.petrovich@gmail.com
   
   Copyright © 2003-2007 Michael C. Petrovich
------------------------------------------------------------ */

package	java3D.io;

import java3D.storage.*;
import java.io.*;
import java.util.*;

public class	STLReader extends ObjectReader
{
	public GraphicObject	read(File file)
	{
		try
		{
			Group	object = new Group();
			Scanner	in = new Scanner(file);
			
			// Ignores any non-data before the data set
			while (in.hasNext() && !in.next().equalsIgnoreCase("facet"))
				;
			
			// Reads the data set
			while (in.hasNext())
			{
				// Skips non-data
				while (in.hasNext() && !in.hasNextDouble())		// Ignores "facet normal"
					in.next();
				if (!in.hasNext())		// EOF
					break;
				in.nextDouble();		// Ignores normal vector x component
				in.nextDouble();		// Ignores normal vector y component
				in.nextDouble();		// Ignores normal vector z component
				in.next();		// Ignores "outer"
				in.next();		// Ignores "loop"
				if (!in.hasNext())		// Unexpected EOF
					break;
				
				// Reads data
				Group	polygon = new Group();
				for (int i = 0; in.next().equalsIgnoreCase("vertex"); i++)
				{
					double	x, y, z;
					x = in.nextDouble();
					y = in.nextDouble();
					z = in.nextDouble();
					polygon.add(new Point3D(x, y, z));
				}
				object.add(polygon);
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