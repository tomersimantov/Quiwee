package com.GUI.states;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.image.BufferedImage;
import java.util.concurrent.Callable;
import javax.swing.JLabel;
import com.GUI.constants.ColorConstants;
import com.GUI.constants.FontConstants;
import com.GUI.states.StateManager.Substate;
import com.util.GUI.swing.components.InteractiveLabel;
import com.util.GUI.swing.containers.Window;
import com.util.GUI.swing.state_management.State;
import com.util.files.ImageHandler;
import com.util.math.DimensionalHandler;

public class EntryState extends State
{
	private static final BufferedImage IMAGE = ImageHandler.loadImage("entry_background.png");
	private static final String LOGO_PATH = "logo.png";
	private static final String[] DESCRIPTION = {
		"Quiwee is a free to use queue managing and handling application.",
		"Create a user for your business and start managing your clients with great ease!"
	};
	
	public EntryState(Window window) {
		super(window, 2);
		
		createPanel(new GridBagLayout(), DimensionalHandler.adjust(window.getDimension(), 100, 60), null);
		createPanel(new GridBagLayout(), DimensionalHandler.adjust(window.getDimension(), 100, 40), null);
		
		JLabel logo = new JLabel(ImageHandler.loadIcon(LOGO_PATH));
		addComponent(0, logo, 0, 0);
		
		gridConstraints.insets = new Insets(50, 10, 10, 10);
		JLabel[] descriptionLabels = new JLabel[DESCRIPTION.length];
		for (int i = 0, y = 1; i < descriptionLabels.length; i++, y++) {
			descriptionLabels[i] = new JLabel(DESCRIPTION[i]);
			descriptionLabels[i].setFont(FontConstants.SMALL_LABEL_FONT);
			descriptionLabels[i].setForeground(ColorConstants.TEXT_COLOR_BRIGHT);
			
			addComponent(0, descriptionLabels[i], 0, y);
			gridConstraints.insets.top = 0;
		}
		
		InteractiveLabel login = new InteractiveLabel("Login");
		login.setForeground(ColorConstants.TEXT_COLOR_BRIGHT);
		login.setFont(FontConstants.TITLE_FONT);
		login.enableSelectionColor(false);
		login.setFunction(new Callable<Void>() {
			@Override
			public Void call() throws Exception {
				StateManager.setState(Substate.LOGIN);
				return null;
			}
		});
		gridConstraints.insets = new Insets(10, 10, 10, 30);
		addComponent(1, login, 0, 0);
		
		InteractiveLabel register = new InteractiveLabel("Sign Up");
		register.setForeground(ColorConstants.TEXT_COLOR_BRIGHT);
		register.setFont(FontConstants.TITLE_FONT);
		register.enableSelectionColor(false);
		register.setFunction(new Callable<Void>() {
			@Override
			public Void call() throws Exception {
				StateManager.setState(Substate.REGISTER);
				return null;
			}
		});
		gridConstraints.insets = new Insets(10, 30, 10, 10);
		addComponent(1, register, 2, 0);
	}
	
	@Override
	public void applyPanels() {
		insertPanel(0, BorderLayout.CENTER);
		insertPanel(1, BorderLayout.SOUTH);
	}
	
	@Override
	public void paintComponent(Graphics g, Dimension windowDim) {
		g.drawImage(IMAGE, 0, 0, null);
		
		Dimension size = panes[1].getPreferredSize();
		int mainRectHeight = size.height / 3;
		int mainRectY = panes[0].getPreferredSize().height + mainRectHeight;
		
		//main rectangle
		g.setColor(ColorConstants.COLOR_3);
		g.fillRect(0, mainRectY, size.width, mainRectHeight);
		
		//small rectangles
		Color smallRectColor = new Color(ColorConstants.COLOR_3.getRed(),
										 ColorConstants.COLOR_3.getGreen(),
										 ColorConstants.COLOR_3.getBlue(), 100);
		
		g.setColor(smallRectColor);
		int smallRectHeight = (int) (mainRectHeight / 5);
		g.fillRect(0, mainRectY - smallRectHeight , size.width, smallRectHeight);
		g.fillRect(0, mainRectY + mainRectHeight, size.width, smallRectHeight);
	}
}