/* ------------------------------------------------------------
   About:      Group event listener class
   Author:     Michael C. Petrovich
               michael.c.petrovich@gmail.com
   
   Copyright © 2003-2007 Michael C. Petrovich
------------------------------------------------------------ */

package java3D.storage;

public interface	GroupListener
{
	enum	GroupUpdate { All, Attributes }
	
	void	groupUpdate(GroupUpdate update);
}