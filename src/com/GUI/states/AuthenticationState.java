package com.GUI.states;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.border.Border;
import javax.swing.border.MatteBorder;
import com.GUI.constants.ColorConstants;
import com.GUI.constants.FontConstants;
import com.util.GUI.swing.components.FocusField;
import com.util.GUI.swing.containers.Window;
import com.util.GUI.swing.state_management.State;
import com.util.math.DimensionalHandler;

public abstract class AuthenticationState extends State
{
	protected static class AuthLabel extends JLabel
	{
		private static final long serialVersionUID = 9143176152684551671L;
		
		/**
		 * @param name - The lable's name
		 */
		public AuthLabel(String name) {
			super(name + ":");
			setForeground(ColorConstants.TEXT_COLOR_DARK);
			setFont(LABEL_FONT);
		}
	}
	
	protected static class AuthField extends FocusField
	{
		private static final long serialVersionUID = -1237907748253241305L;
		private static final Dimension DIM = new Dimension(200, 30);
		private static final Border BORDER = new MatteBorder(1, 1, 1, 1, Color.BLACK);
		
		/**
		 * @param defText - The default text inside the field
		 */
		public AuthField(String defText) {
			super(defText);
			setPreferredSize(DIM);
			setBorder(BORDER);
		}
	}
	
	protected AuthLabel emailLab, nameLab, passwordLab, repeatLab;
	protected AuthField emailField, nameField, passField, repeatField;
	protected JButton button;
	protected JLabel error;
	
	public AuthenticationState(Window window) {
		super(window, 3);
		createPanel(new GridBagLayout(), DimensionalHandler.adjust(window.getDimension(), 100, 20), null);
		createPanel(new GridBagLayout(), DimensionalHandler.adjust(window.getDimension(), 100, 60), null);
		createPanel(new GridBagLayout(), DimensionalHandler.adjust(window.getDimension(), 100, 20), null);
		
		//title
		JLabel icon = new JLabel(getActionIcon());
		gridConstraints.insets = new Insets(40, -340, 10, 10);
		addComponent(0, icon, 0, 0);
		
		JLabel title = new JLabel(getTitle());
		title.setFont(FontConstants.TITLE_FONT);
		title.setForeground(ColorConstants.TEXT_COLOR_BRIGHT);
		gridConstraints.insets = new Insets(40, -180, 10, 10);
		addComponent(0, title, 1, 0);
		
		//labels
		this.emailLab = new AuthLabel("Email");
		this.nameLab = new AuthLabel("Full name");
		this.passwordLab = new AuthLabel("Password");
		this.repeatLab = new AuthLabel("Repeat password");
		
		//text fields
		this.emailField = new AuthField("my_email@domain.com");
		this.nameField = new AuthField("Israel Israeli");
		this.passField = new AuthField("must contain 8-20 characters");
		passField.encode(true);
		
		this.repeatField = new AuthField("must contain 8-20 characters");
		repeatField.encode(true);
		
		//action button
		Dimension buttonDim = DimensionalHandler.adjust(AuthField.DIM, 50, 100);
		
		this.button = new JButton(getActionName());
		button.setSize(buttonDim);
		button.setMaximumSize(buttonDim);
		button.setMinimumSize(buttonDim);
		button.setPreferredSize(buttonDim);
		button.setForeground(ColorConstants.COLOR_3);
		button.setFont(LABEL_FONT);
		button.setFocusable(false);
		button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				buttonAction();
			}
		});
		
		gridConstraints.insets = new Insets(10, 270, 10, 10);
		addComponent(2, button, 1, 5);
		
		this.error = new JLabel("-");
		error.setForeground(window.getColor());
		error.setFont(LABEL_FONT);
		gridConstraints.insets.left = 10;
		addComponent(2, error, 1, 6);
		
		panes[0].setFocusable(true);
		panes[0].requestFocus();
	}
	
	@Override
	public void applyPanels() {
		insertPanel(0, BorderLayout.NORTH);
		insertPanel(1, BorderLayout.CENTER);
		insertPanel(2, BorderLayout.SOUTH);
	}
	
	protected void fail(String message) {
		error.setText(message);
		error.setForeground(Color.RED);
	}
	
	protected void addComponent(Component c, int x, int y) {
		addComponent(1, c, x, y);
	}
	
	@Override
	public void paintComponent(Graphics g, Dimension windowDim) {
		//vertical line
		g.setColor(new Color(175, 199, 215));
		g.fillRect(0, 0, 10, windowDim.height);
		
		//horizontal line
		g.setColor(new Color(100, 160, 202));
		g.fillRect(0, 30, windowDim.width, 50);
		 
		//horizontal thin line
		g.setColor(new Color(170, 188, 201));
		g.fillRect(10, 80, windowDim.width, 6);
	}
	
	/**
	 * @return the title of the state.
	 */
	protected abstract String getTitle();
	
	/**
	 * @return the text inside the action button.
	 */
	protected abstract String getActionName();
	
	/**
	 * @return the icon that appears next to the title.
	 */
	protected abstract ImageIcon getActionIcon();
	
	/**
	 * This method is the action to perform when the action button is pressed. 
	 */
	protected abstract void buttonAction();
}