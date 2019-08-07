package com.GUI.constants;
import java.awt.Font;
import com.util.files.FontHandler;
import com.util.files.FontHandler.FontStyle;

public final class FontConstants
{
	public final static Font TITLE_FONT = FontHandler.load("Noto_Sans_KR", FontStyle.BOLD, 28);
	public final static Font SMALL_LABEL_FONT = FontHandler.load("Poppins", FontStyle.PLAIN, 16);
	public final static Font LARGE_LABEL_FONT = FontHandler.load("Poppins", FontStyle.PLAIN, 26);
}