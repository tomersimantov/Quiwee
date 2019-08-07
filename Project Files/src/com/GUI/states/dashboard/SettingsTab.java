package com.GUI.states.dashboard;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Insets;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import com.GUI.constants.ColorConstants;
import com.main.User;
import com.util.GUI.swing.components.FocusField;
import com.util.IO.StringVerifier;

public class SettingsTab extends Tab
{
	private static final long serialVersionUID = -2232661139446087215L;
	private static final Dimension FIELD_DIM = new Dimension(180, 25);
	
	private FocusField currentPassF, newPassF, repeatF;
	
	public SettingsTab() {
		super(false, false, true);
		
		//labels
		JLabel currentPassLab = new JLabel("Current password");
		currentPassLab.setForeground(ColorConstants.TEXT_COLOR_DARK);
		currentPassLab.setFont(DESCRIPTION_FONT);
		gbc.insets = new Insets(10, -150, 10, 10);
		addComponent(currentPassLab, 0, 0);
		
		JLabel newPassLab = new JLabel("New password");
		newPassLab.setForeground(ColorConstants.TEXT_COLOR_DARK);
		newPassLab.setFont(DESCRIPTION_FONT);
		gbc.insets.left = -125;
		addComponent(newPassLab, 0, 1);
		
		JLabel repeatPassLab = new JLabel("Repeat password");
		repeatPassLab.setForeground(ColorConstants.TEXT_COLOR_DARK);
		repeatPassLab.setFont(DESCRIPTION_FONT);
		gbc.insets.left = -147;
		gbc.insets.bottom = 250;
		addComponent(repeatPassLab, 0, 2);
		
		//text fields
		gbc.insets = new Insets(10, 10, 10, 10);
		
		this.currentPassF = new FocusField();
		currentPassF.encode(true);
		currentPassF.setPreferredSize(FIELD_DIM);
		addComponent(currentPassF, 1, 0);
		
		this.newPassF = new FocusField("must contain 8-20 characters");
		newPassF.encode(true);
		newPassF.setPreferredSize(FIELD_DIM);
		addComponent(newPassF, 1, 1);
		
		this.repeatF = new FocusField("must contain 8-20 characters");
		repeatF.encode(true);
		repeatF.setPreferredSize(FIELD_DIM);
		gbc.insets.bottom = 250;
		addComponent(repeatF, 1, 2);
	}
	
	@Override
	protected String getTitle() { return "Settings"; }

	@Override
	protected String[] getDescription() {
		return new String[] {
			"Update your personal information and preferences."
		};
	}

	@Override
	protected Color getTabColor() { return new Color(167, 54, 54); }

	@Override
	protected void addButtonFunction() {}

	@Override
	protected void deleteButtonFunction() {}

	@Override
	protected void saveButtonFunction() {
		String currentPassword = User.getPass();
		int messageType = JOptionPane.WARNING_MESSAGE;
		String title = "";
		String message = "Something went wrong. Please try again.";
		
		//verify current password
		if (!currentPassF.getText().equals(currentPassword)) {
			messageType = JOptionPane.ERROR_MESSAGE;
			message = "Entered an incorrect password.";
		}
		
		else if (!StringVerifier.verifyPassword(newPassF.getDecodedPassword(), 8, 20)) {
			messageType = JOptionPane.ERROR_MESSAGE;
			message = "New password must be between 8 and 20 characters.";
		}
		
		else if (!newPassF.getDecodedPassword().equals(repeatF.getDecodedPassword())) {
			messageType = JOptionPane.ERROR_MESSAGE;
			message = "Please repeat your password again.";
		}
		
		else if (User.changePassword(newPassF.getDecodedPassword())) {
			messageType = JOptionPane.INFORMATION_MESSAGE;
			message = "Your password was updated successfully.";
			newPassF.clear();
			repeatF.clear();
		}
		
		JOptionPane.showMessageDialog(this, message, title, messageType, null);
	}
}