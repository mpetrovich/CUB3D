/* ------------------------------------------------------------
   About:      Camera panel class
               Provides control for camera properties.
   Author:     Michael C. Petrovich
               michael.c.petrovich@gmail.com
   
   Copyright © 2003-2007 Michael C. Petrovich
------------------------------------------------------------ */

import java3D.*;
import java3D.storage.*;
import java3D.render.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;
import java.util.*;

public class	CameraPanel extends JPanel 
            	            implements CameraListener, 
            	                       ActionListener, ItemListener
{
	// Callbacks
	private JFrame	window;
	private Environment	envr;
	
	private JPanel	inputPanel;
	
	// View
	private JLabel    	viewYLabel, viewZLabel;
	private JTextField	viewYField, viewZField;
	
	// Zoom
	private JLabel    	zoomLabel;
	private JTextField	zoomField;
	
	// Rotation origin
	private JLabel    	rotateLabel;
	private JTextField	rotateXField, rotateYField, rotateZField;
	
	// Projection mode
	private JPanel	renderPanel;
	private JLabel	modeLabel;
	private JComboBox	modeComboBox;
	private final String	ISOMETRIC="isometric", PERSPECTIVE="perspective";
	private String []	modeNames = { ISOMETRIC, PERSPECTIVE };
	private JPanel	modeCards, isoCard, perspCard;
	
	// Render quality
	private JLabel	qualityLabel;
	private JComboBox	qualityComboBox;
	private final String	LOW="low", MED="med", HIGH="high";
	private String []	qualityNames = { LOW, MED, HIGH };
	
	// Perspective
	private JLabel	perspUnitLabel, extentsMultLabel;
	private JTextField	perspUnitField, extentsMultField;
	
	// Live updating
	private boolean	isActive;
	private JCheckBox	activeCheckBox;
	
	// Constants
	private static final int	FIELD_SIZE = 5;
	private static final int	FIELD_SIZE_LARGE = 7;
	private final Color	backgroundColor = Color.WHITE;
	private final Color	titleColor = Color.BLACK;
	
	
	/* Constructors
	---------------------------------------------------------------------- */
	public	CameraPanel(JFrame window, Environment envr)
	{
		this.window = window;
		this.envr = envr;
		
		isActive = true;
		buildPanel();
		envr.camera.addCameraListener(this);
		
		// Forces initial update
		cameraUpdate(CameraListener.CameraUpdate.All);
	}
	
	private void	buildPanel()
	{
		setBorder(BorderFactory.createTitledBorder(
		          BorderFactory.createLineBorder(Color.black, 2), "Camera", 
		          TitledBorder.CENTER, TitledBorder.ABOVE_TOP, 
		          new Font("Arial", Font.PLAIN, 16), titleColor));
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		setBackground(backgroundColor);
		
		
		// Active display
		JPanel	updatePanel = new JPanel();
		updatePanel.add(activeCheckBox = new JCheckBox("Live updating", isActive));
		activeCheckBox.addItemListener(this);
		add(updatePanel);
		
		
		// Render settings
		renderPanel = new JPanel();
		renderPanel.setLayout(new BoxLayout(renderPanel, BoxLayout.Y_AXIS));
		
		// Projection mode
		JPanel	renderPanel = new JPanel();
		JPanel	modePanel = new JPanel();
		modePanel.add(modeLabel = new JLabel("Mode:"));
		modePanel.add(modeComboBox = new JComboBox(modeNames));
		modeComboBox.addActionListener(this);
		renderPanel.add(modePanel);
		
		// Render quality
		JPanel	qualityPanel = new JPanel();
		qualityPanel.add(qualityLabel = new JLabel("Quality:"));
		qualityPanel.add(qualityComboBox = new JComboBox(qualityNames));
		qualityComboBox.addActionListener(this);
		renderPanel.add(qualityPanel);
		
		add(renderPanel);
		
		
		// Input fields
		inputPanel = new JPanel();
		inputPanel.setLayout(new BoxLayout(inputPanel, BoxLayout.Y_AXIS));
		
		// Horizontal angle (Z)
		JPanel	horizPanel = new JPanel();
		horizPanel.add(viewZLabel = new JLabel("Horizontal:", JLabel.RIGHT));
		horizPanel.add(viewZField = new JTextField("", FIELD_SIZE));
		viewZField.addActionListener(this);
		inputPanel.add(horizPanel);
		
		
		// Vertical angle (Y)
		JPanel	vertPanel = new JPanel();
		vertPanel.add(viewYLabel = new JLabel("Vertical:", JLabel.RIGHT));
		vertPanel.add(viewYField = new JTextField("", FIELD_SIZE));
		viewYField.addActionListener(this);
		inputPanel.add(vertPanel);
		
		
		// Zoom
		JPanel	zoomPanel = new JPanel();
		zoomPanel.add(zoomLabel = new JLabel("Zoom:"));
		zoomPanel.add(zoomField = new JTextField("", FIELD_SIZE_LARGE));
		zoomField.addActionListener(this);
		inputPanel.add(zoomPanel);
		
		
		// Rotation origin
		JPanel	originPanel = new JPanel();
		originPanel.add(rotateLabel = new JLabel("Origin: ", JLabel.RIGHT));
		originPanel.add(new JLabel("("));
		originPanel.add(rotateXField = new JTextField("", FIELD_SIZE));
		originPanel.add(new JLabel(","));
		originPanel.add(rotateYField = new JTextField("", FIELD_SIZE));
		originPanel.add(new JLabel(","));
		originPanel.add(rotateZField = new JTextField("", FIELD_SIZE));
		originPanel.add(new JLabel(")"));
		rotateXField.addActionListener(this);
		rotateYField.addActionListener(this);
		rotateZField.addActionListener(this);
		inputPanel.add(originPanel);
		
		
		// Projection settings
		modeCards = new JPanel(new CardLayout());
		isoCard = new JPanel();
		perspCard = new JPanel();
		perspCard.setLayout(new BoxLayout(perspCard, BoxLayout.Y_AXIS));
		
		// Perspective unit distance
		JPanel	perspUnitPanel = new JPanel();
		perspUnitPanel.add(perspUnitLabel = new JLabel("Unit distance: ", JLabel.RIGHT));
		perspUnitPanel.add(perspUnitField = new JTextField("", FIELD_SIZE));
		perspCard.add(perspUnitPanel);
		perspUnitField.addActionListener(this);
		modeCards.add(isoCard, ISOMETRIC);
		
		// Perspective extents multiplier
		JPanel	perspMultPanel = new JPanel();
		perspMultPanel.add(extentsMultLabel = new JLabel("Range multiplier: ", JLabel.RIGHT));
		perspMultPanel.add(extentsMultField = new JTextField("", FIELD_SIZE));
		perspCard.add(perspMultPanel);
		extentsMultField.addActionListener(this);
		modeCards.add(perspCard, PERSPECTIVE);
		
		inputPanel.add(modeCards, BorderLayout.CENTER);
		add(inputPanel);
	}
	
	public void	paint(Graphics g)
	{
		super.paint(g);
	}
	
	
	/* Event handling - Java3D
	---------------------------------------------------------------------- */
	public void	cameraUpdate(CameraUpdate update)
	{
		// Updates parameter field editability
		activeCheckBox.setSelected(isActive);
		viewYLabel.setEnabled(isActive);
		viewYField.setEnabled(isActive);
		viewZLabel.setEnabled(isActive);
		viewZField.setEnabled(isActive);
		zoomLabel.setEnabled(isActive);
		zoomField.setEnabled(isActive);
		rotateLabel.setEnabled(isActive);
		rotateXField.setEnabled(isActive);
		rotateYField.setEnabled(isActive);
		rotateZField.setEnabled(isActive);
		perspUnitLabel.setEnabled(isActive);
		perspUnitField.setEnabled(isActive);
		extentsMultLabel.setEnabled(isActive);
		extentsMultField.setEnabled(isActive);
		
		// Updates parameter field contents
		if (isActive)
		{
			Formatter	f;
			
			// View angles - Y
			f = new Formatter();
			f.format("%.2f ¡", envr.camera.getViewAngles().getY());
			viewYField.setText(f.toString());
			
			// View angles - Z
			f = new Formatter();
			f.format("%.2f ¡", envr.camera.getViewAngles().getZ());
			viewZField.setText(f.toString());
			
			// Zoom
			f = new Formatter();
			double	zoom = envr.camera.getZoom();
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
			zoomField.setText(f.toString());
			
			// Rotation origin - X
			f = new Formatter();
			f.format("%.2f", envr.camera.getRotateOrigin().getX());
			rotateXField.setText(f.toString());
			
			// Rotation origin - Y
			f = new Formatter();
			f.format("%.2f", envr.camera.getRotateOrigin().getY());
			rotateYField.setText(f.toString());
			
			// Rotation origin - Z
			f = new Formatter();
			f.format("%.2f", envr.camera.getRotateOrigin().getZ());
			rotateZField.setText(f.toString());
			
			// Perspective unit
			f = new Formatter();
			f.format("%.2f", envr.camera.getPerspectiveUnit());
			perspUnitField.setText(f.toString());
			
			// Perspective extents multiplier
			f = new Formatter();
			f.format("%.2f", envr.camera.getRangeMultiplier());
			extentsMultField.setText(f.toString());
		}
		
		// Updates projection mode
		if (envr.camera.getProjector() instanceof IsometricProjector)
		{
			modeComboBox.setSelectedItem(ISOMETRIC);
		}
		else
		if (envr.camera.getProjector() instanceof PerspectiveProjector)
		{
			modeComboBox.setSelectedItem(PERSPECTIVE);
		}
		
		// Updates render quality
		switch (envr.camera.getRenderQuality())
		{
			case Low:
				qualityComboBox.setSelectedItem(LOW);
				break;
			
			case Med:
				qualityComboBox.setSelectedItem(MED);
				break;
			
			case High:
				qualityComboBox.setSelectedItem(HIGH);
				break;
		}
		
		// Updates projector settings
		CardLayout	cl = (CardLayout) modeCards.getLayout();
		cl.show(modeCards, (String) modeComboBox.getSelectedObjects()[0]);
	}
	
	
	/* Event handling - Java GUI
	---------------------------------------------------------------------- */
	public void	actionPerformed(ActionEvent e)
	{
		envr.camera.holdUpdates(true);
		
		Object	source = e.getSource();
		
		// Saves camera input parameters
		if (isActive)
		{
			StringTokenizer	st;
			Point3D	p3D;
			Point2D	p2D;
			
			// View angles - Y
			if (source == viewYField)
			{
				st = new StringTokenizer(viewYField.getText(), " ¡");
				p3D = envr.camera.getViewAngles();
				p3D.setY( Double.parseDouble(st.nextToken()) );
				envr.camera.setViewAngles(p3D);
			}
			else
			
			// View angles - Z
			if (source == viewZField)
			{
				st = new StringTokenizer(viewZField.getText(), " ¡");
				p3D = envr.camera.getViewAngles();
				p3D.setZ( Double.parseDouble(st.nextToken()) );
				envr.camera.setViewAngles(p3D);
			}
			else
			
			// Zoom
			if (source == zoomField)
			{
				st = new StringTokenizer(zoomField.getText(), " %");
				envr.camera.setZoomTo( Double.parseDouble(st.nextToken()) / 100.0 );
			}
			else
			
			// Rotation origin - X
			if (source == rotateXField)
			{
				st = new StringTokenizer(rotateXField.getText(), "");
				p3D = envr.camera.getRotateOrigin();
				p3D.setX( Double.parseDouble(st.nextToken()) );
				envr.camera.setRotateOrigin(p3D);
			}
			else
			
			// Rotation origin - Y
			if (source == rotateYField)
			{
				st = new StringTokenizer(rotateYField.getText(), "");
				p3D = envr.camera.getRotateOrigin();
				p3D.setY( Double.parseDouble(st.nextToken()) );
				envr.camera.setRotateOrigin(p3D);
			}
			else
			
			// Rotation origin - Z
			if (source == rotateZField)
			{
				st = new StringTokenizer(rotateZField.getText(), "");
				p3D = envr.camera.getRotateOrigin();
				p3D.setZ( Double.parseDouble(st.nextToken()) );
				envr.camera.setRotateOrigin(p3D);
			}
			else
			
			// Perspective unit
			if (source == perspUnitField)
			{
				st = new StringTokenizer(perspUnitField.getText(), "");
				envr.camera.setPerspectiveUnit( Double.parseDouble(st.nextToken()) );
			}
			else
			
			// Perspective extents multiplier
			if (source == extentsMultField)
			{
				st = new StringTokenizer(extentsMultField.getText(), "");
				envr.camera.setRangeMultiplier( Double.parseDouble(st.nextToken()) );
			}
		}
		
		// Projection mode
		if (source == modeComboBox)
		{
			String	projName = (String) modeComboBox.getSelectedItem();
			if (projName == ISOMETRIC)
			{
				envr.camera.setProjector(envr.camera.isometricProjector);
			}
			else
			if (projName == PERSPECTIVE)
			{
				envr.camera.setProjector(envr.camera.perspectiveProjector);
			}
		}
		
		// Render quality
		if (source == qualityComboBox)
		{
			String	qualityName = (String) qualityComboBox.getSelectedItem();
			if (qualityName == LOW)
			{
				envr.camera.setRenderQuality(Camera.RenderQuality.Low);
			}
			else
			if (qualityName == MED)
			{
				envr.camera.setRenderQuality(Camera.RenderQuality.Med);
			}
			else
			if (qualityName == HIGH)
			{
				envr.camera.setRenderQuality(Camera.RenderQuality.High);
			}
		}
		
		envr.camera.holdUpdates(false);
	}
	
	public void	itemStateChanged(ItemEvent e)
	{
		Object	source = e.getSource();
		
		// Toggles active display
		if (source == activeCheckBox)
		{
			isActive = !isActive;
			cameraUpdate(CameraListener.CameraUpdate.All);		// Lightweight update only
		}
	}
}