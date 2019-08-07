package com.GUI.windows;
import java.awt.Color;
import java.awt.Dimension;
import javax.swing.JFrame;
import com.GUI.constants.ColorConstants;
import com.util.GUI.swing.containers.Window;

public class MainWindow extends Window
{
	private static final long serialVersionUID = 3923886508152842668L;
	public static final String TITLE = "Qwiwee";
	public static final Dimension DIM = new Dimension(1000, 700);
	
	public MainWindow() {
		super(TITLE);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

	@Override
	public Color getColor() { return ColorConstants.BASE_COLOR; }

	@Override
	public Dimension getDimension() { return DIM; }

	@Override
	protected boolean DisplayRightAway() { return false; }
}