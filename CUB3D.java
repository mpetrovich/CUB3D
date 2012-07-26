/* ------------------------------------------------------------
   About:      CUB3D 3D environment
   Author:     Michael C. Petrovich
               michael.c.petrovich@gmail.com
   
   Version:    0.2.5
   
   Copyright © 2003-2007 Michael C. Petrovich
------------------------------------------------------------ */

import java3D.*;
import java.awt.*;
import javax.swing.*;
import javax.swing.border.*;

public class	CUB3D
{
	public static final String	NAME = new String("CUB3D");
	public static final String	VERSION = new String("0.2.5");
	
	private static final int	WINDOW_WIDTH = 600;
	private static final int	WINDOW_VBUF  = 25;
	private static final Color	backgroundColor = Color.WHITE;
	private static final Color	centerColor = Color.WHITE;
	private static final Color	titleColor = Color.BLACK;
	
	public static void	main(String[] args)
	{
		Environment	envr = new Environment();
		
		// Creates the panels
		JFrame	window = new JFrame(NAME + " " + VERSION);
		JPanel	windowPanel = new JPanel();
		JPanel	centerPanel = new JPanel();
		JPanel	graphPanel = new JPanel();
		JPanel	sidePanel = new JPanel();
		JPanel	objPanel = new ObjectPanel(window, envr);
		JPanel	camPanel = new CameraPanel(window, envr);
		centerPanel.add(new GraphicsPanel(window, envr));
		sidePanel.add(objPanel);
		sidePanel.add(new JPanel());
		sidePanel.add(camPanel);
		windowPanel.add(sidePanel);
		windowPanel.add(centerPanel);
		
		// Sets the panel dimensions
		Dimension	size = new Dimension();
		size.width = Math.max(objPanel.getPreferredSize().width, camPanel.getPreferredSize().width);
		size.height = objPanel.getPreferredSize().height + camPanel.getPreferredSize().height + WINDOW_VBUF;
		Dimension	objSize = new Dimension(size.width, objPanel.getPreferredSize().height);
		Dimension	camSize = new Dimension(size.width, camPanel.getPreferredSize().height);
		sidePanel.setMinimumSize(size);
		sidePanel.setMaximumSize(size);
		sidePanel.setPreferredSize(size);
		objPanel.setMinimumSize(objSize);
		objPanel.setMaximumSize(objSize);
		objPanel.setPreferredSize(objSize);
		camPanel.setMinimumSize(camSize);
		camPanel.setMaximumSize(camSize);
		camPanel.setPreferredSize(camSize);
		centerPanel.setSize(new Dimension(WINDOW_WIDTH, size.height + WINDOW_VBUF));
		centerPanel.setPreferredSize(centerPanel.getSize());
		graphPanel.setSize(centerPanel.getSize());
		graphPanel.setPreferredSize(graphPanel.getSize());
		windowPanel.setPreferredSize(new Dimension(size.width + graphPanel.getWidth(), centerPanel.getHeight()));
		
		// Prepares the center panel
		centerPanel.setBorder(BorderFactory.createTitledBorder(
                              BorderFactory.createLineBorder(Color.black, 2), "3D View", 
                              TitledBorder.CENTER, TitledBorder.ABOVE_TOP, 
                              new Font("Arial", Font.PLAIN, 16), titleColor));
		centerPanel.setBackground(centerColor);
		
		// Sets the layouts
		windowPanel.setLayout(new BoxLayout(windowPanel, BoxLayout.X_AXIS));
		centerPanel.setLayout(new GridLayout(1, 1));
		sidePanel.setLayout(new BoxLayout(sidePanel, BoxLayout.Y_AXIS));
		sidePanel.setAlignmentY(java.awt.Component.TOP_ALIGNMENT);
		objPanel.setAlignmentX(java.awt.Component.LEFT_ALIGNMENT);
		camPanel.setAlignmentX(java.awt.Component.LEFT_ALIGNMENT);
		
		// Initializes the window
		window.setBackground(backgroundColor);
		window.setContentPane(windowPanel);
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		window.setVisible(true);
		window.pack();
	}
}
