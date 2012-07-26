/* ------------------------------------------------------------
   About:      Object panel class
               Provides control for object properties.
   Author:     Michael C. Petrovich
               michael.c.petrovich@gmail.com
   
   Copyright © 2003-2007 Michael C. Petrovich
------------------------------------------------------------ */

import java3D.*;
import java3D.storage.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.border.*;
import java.io.*;
import java.util.*;

public class	ObjectPanel extends JPanel 
            	            implements GroupListener, 
            	                       ActionListener, ItemListener
{
	// Callbacks
	private JFrame	window;
	private Environment	envr;
	
	// Load
	private java.awt.FileDialog	openDialog;
	private String	directory, filename;
	private	JLabel	filenameLabel;
	private JButton	loadButton;
	
	// Properties
	private JCheckBox	fillCheckBox, edgeCheckBox, pointCheckBox;
	private ColorBox	fillColorBox, edgeColorBox, pointColorBox;
	private JTextField	fillOpacityField, edgeOpacityField, pointOpacityField;
	private JTextField	pointRadiusField;
	
	// Constants
	private static final int	POINT_RADIUS_FIELD_SIZE = 5;
	private static final int	OPACITY_FIELD_SIZE = 4;
	private final Color	backgroundColor = Color.WHITE;
	private final Color	titleColor = Color.BLACK;
	
	
	/* Constructors
	---------------------------------------------------------------------- */
	public	ObjectPanel(JFrame window, Environment envr)
	{
		this.window = window;
		this.envr = envr;
		
		buildPanel();
		envr.object.addGroupListener(this);
		
		// Forces initial update
		groupUpdate(GroupListener.GroupUpdate.All);
	}
	
	private void	buildPanel()
	{
		setBorder(BorderFactory.createTitledBorder(
		          BorderFactory.createLineBorder(Color.black, 2), "Object", 
		          TitledBorder.CENTER, TitledBorder.ABOVE_TOP, 
		          new Font("Arial", Font.PLAIN, 16), titleColor));
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		setBackground(backgroundColor);
		
		Attributes	attr = envr.object.getAttr();
		Dimension	colorBoxDim = new Dimension(30, 20);
		
		
		// Load dialog
		openDialog = new java.awt.FileDialog(window, "Load Object", java.awt.FileDialog.LOAD);
		directory = new String("../../../Input");
		filename = new String("");
		
		
		// Load
		JPanel	sourcePanel = new JPanel();
		sourcePanel.add(loadButton = new JButton("Load..."));
		//window.getRootPane().setDefaultButton(loadButton);
		sourcePanel.add(filenameLabel = new JLabel(""));
		add(sourcePanel);
		loadButton.addActionListener(this);
		
		
		// Fill
		JPanel	fillPanel = new JPanel();
		fillPanel.add(fillCheckBox = new JCheckBox("Fill:", attr.showFill));
		fillPanel.add(fillColorBox = new ColorBox(this));
		fillColorBox.addMouseListener(fillColorBox);
		fillColorBox.setMinimumSize(colorBoxDim);
		fillColorBox.setMaximumSize(colorBoxDim);
		fillColorBox.setPreferredSize(colorBoxDim);
		
		fillPanel.add(new JLabel(" Opacity:"));
		fillPanel.add(fillOpacityField = new JTextField(OPACITY_FIELD_SIZE));
		add(fillPanel);
		fillCheckBox.addItemListener(this);
		fillOpacityField.addActionListener(this);
		
		
		// Edge
		JPanel	edgePanel = new JPanel();
		edgePanel.add(edgeCheckBox = new JCheckBox("Edge:", attr.showEdges));
		edgePanel.add(edgeColorBox = new ColorBox(this));
		edgeColorBox.addMouseListener(edgeColorBox);
		edgeColorBox.setMinimumSize(colorBoxDim);
		edgeColorBox.setMaximumSize(colorBoxDim);
		edgeColorBox.setPreferredSize(colorBoxDim);
		
		edgePanel.add(new JLabel(" Opacity:"));
		edgePanel.add(edgeOpacityField = new JTextField(OPACITY_FIELD_SIZE));
		add(edgePanel);
		edgeCheckBox.addItemListener(this);
		edgeOpacityField.addActionListener(this);
		
		
		// Point
		JPanel	pointPanel = new JPanel();
		pointPanel.add(pointCheckBox = new JCheckBox("Point:", attr.showPoints));
		pointPanel.add(pointColorBox = new ColorBox(this));
		pointColorBox.addMouseListener(pointColorBox);
		pointColorBox.setMinimumSize(colorBoxDim);
		pointColorBox.setMaximumSize(colorBoxDim);
		pointColorBox.setPreferredSize(colorBoxDim);
		
		pointPanel.add(new JLabel(" Opacity:"));
		pointPanel.add(pointOpacityField = new JTextField(OPACITY_FIELD_SIZE));
		add(pointPanel);
		pointCheckBox.addItemListener(this);
		pointOpacityField.addActionListener(this);
		
		
		// Point radius
		JPanel	pointRadiusPanel = new JPanel();
		pointRadiusPanel.add(new JLabel("Point radius:"));
		pointRadiusPanel.add(pointRadiusField = new JTextField(POINT_RADIUS_FIELD_SIZE));
		add(pointRadiusPanel);
		pointRadiusField.addActionListener(this);
	}
	
	private class	ColorBox extends JPanel implements MouseListener
	// A solid-color button that launches a color chooser when clicked
	{
		private ObjectPanel	panel;
		
		public	ColorBox(ObjectPanel panel)
		{
			this.panel = panel;
			setBorder(BorderFactory.createLineBorder(Color.black));
		}
		
		public void	mouseClicked(MouseEvent e)
		{
			Color	color = JColorChooser.showDialog(panel, "Choose Color", getBackground());
			if (color != null)
			{
				setBackground(color);
				panel.colorUpdate();
			}
		}
		
		public void	mousePressed(MouseEvent e)  {}
		public void	mouseReleased(MouseEvent e)  {}
		public void	mouseEntered(MouseEvent e)  {}
		public void	mouseExited(MouseEvent e)  {}
	}
	
	public void	paint(Graphics g)
	{
		super.paint(g);
	}
	
	
	/* Event handling - Java3D
	---------------------------------------------------------------------- */
	public void	groupUpdate(GroupUpdate update)
	{
		Attributes	attr = envr.object.getAttr();
		fillCheckBox.setSelected(attr.showFill);
		edgeCheckBox.setSelected(attr.showEdges);
		pointCheckBox.setSelected(attr.showPoints);
		fillColorBox.setBackground(attr.fillColor);
		fillOpacityField.setText((int) (attr.fillOpacity*100) + " %");
		edgeColorBox.setBackground(attr.edgeColor);
		edgeOpacityField.setText((int) (attr.edgeOpacity*100) + " %");
		pointColorBox.setBackground(attr.pointColor);
		pointOpacityField.setText((int) (attr.pointOpacity*100) + " %");
		pointRadiusField.setText(attr.pointRadius + "");
	}
	
	public void	colorUpdate()
	{
		Attributes	attr = envr.object.getAttr();
		attr.fillColor = fillColorBox.getBackground();
		attr.edgeColor = edgeColorBox.getBackground();
		attr.pointColor = pointColorBox.getBackground();
		envr.object.setAttrToAll(attr);
	}
	
	
	/* Event handling - Java GUI
	---------------------------------------------------------------------- */
	public void	actionPerformed(ActionEvent e)
	{
		Object	source = e.getSource();
		
		// Loads a file
		if (source == loadButton)
		{
			// Disables the load button
			String	prevText = loadButton.getText();
			loadButton.setText("Loading...");
			loadButton.setEnabled(false);
			
			// Initializes and shows the dialog
			openDialog.setDirectory(directory);
			openDialog.setFile(filename);
			openDialog.setVisible(true);
			
			// Gets the selected filepath
			String	newDirectory = openDialog.getDirectory();
			String	newFilename = openDialog.getFile();
			
			// Loads the file
			if (newFilename != null)
			{
				if (envr.load(newDirectory + newFilename))
				// Load successful
				{
					directory = newDirectory;
					filename = newFilename;
					filenameLabel.setText(filename);
				}
				
				else
				// Load failed
				{
					Object[]	options = { "OK" };
					JOptionPane.showOptionDialog(null, "An error occurred while loading the file.", 
				                "Load Error", JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE, 
				                null, options, options[0]);
				}
			}
			
			// Reenables the load button
			loadButton.setText(prevText);
			loadButton.setEnabled(true);
		}
		else
		
		// Changes the object fill opacity
		if (source == fillOpacityField)
		{
			Attributes	attr = envr.object.getAttr();
			StringTokenizer	st = new StringTokenizer(fillOpacityField.getText(), " %");
			attr.fillOpacity = Double.parseDouble(st.nextToken()) / 100.0;
			if (attr.fillOpacity < 0)
				attr.fillOpacity = 0;
			if (attr.fillOpacity > 1)
				attr.fillOpacity = 1;
			envr.object.setAttrToAll(attr);
		}
		else
		
		// Changes the object edge opacity
		if (source == edgeOpacityField)
		{
			Attributes	attr = envr.object.getAttr();
			StringTokenizer	st = new StringTokenizer(edgeOpacityField.getText(), " %");
			attr.edgeOpacity = Double.parseDouble(st.nextToken()) / 100.0;
			if (attr.edgeOpacity < 0)
				attr.edgeOpacity = 0;
			if (attr.edgeOpacity > 1)
				attr.edgeOpacity = 1;
			envr.object.setAttrToAll(attr);
		}
		else
		
		// Changes the object point opacity
		if (source == pointOpacityField)
		{
			Attributes	attr = envr.object.getAttr();
			StringTokenizer	st = new StringTokenizer(pointOpacityField.getText(), " %");
			attr.pointOpacity = Double.parseDouble(st.nextToken()) / 100.0;
			if (attr.pointOpacity < 0)
				attr.pointOpacity = 0;
			if (attr.pointOpacity > 1)
				attr.pointOpacity = 1;
			envr.object.setAttrToAll(attr);
		}
		else
		
		// Changes the object point radius
		if (source == pointRadiusField)
		{
			Attributes	attr = envr.object.getAttr();
			StringTokenizer	st = new StringTokenizer(pointRadiusField.getText(), " ");
			attr.pointRadius = Double.parseDouble(st.nextToken());
			envr.object.setAttrToAll(attr);
		}
	}
	
	public void	itemStateChanged(ItemEvent e)
	{
		Object	source = e.getItemSelectable();
		
		// Toggles the fill
		if (source == fillCheckBox)
		{
			Attributes	attr = envr.object.getAttr();
			attr.showFill = fillCheckBox.isSelected();
			envr.object.setAttrToAll(attr);
		}
		else
		
		// Toggles the edge
		if (source == edgeCheckBox)
		{
			Attributes	attr = envr.object.getAttr();
			attr.showEdges = edgeCheckBox.isSelected();
			envr.object.setAttrToAll(attr);
		}
		else
		
		// Toggles the points
		if (source == pointCheckBox)
		{
			Attributes	attr = envr.object.getAttr();
			attr.showPoints = pointCheckBox.isSelected();
			envr.object.setAttrToAll(attr);
		}
	}
}