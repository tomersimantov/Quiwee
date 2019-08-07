package com.GUI.windows;
import java.awt.Color;
import java.awt.Dimension;
import com.GUI.constants.ColorConstants;
import com.util.GUI.swing.containers.Window;

public class AuthenticationWindow extends Window
{
	private static final long serialVersionUID = 8161943524725746352L;
	private static final Dimension DIM = new Dimension(450, 400);
	
	public AuthenticationWindow() {
		super(MainWindow.TITLE);
		setVisible(false);
	}

	@Override
	public Color getColor() { return ColorConstants.BASE_COLOR; }

	@Override
	public Dimension getDimension() { return DIM; }

	@Override
	protected boolean DisplayRightAway() { return false; }
}
