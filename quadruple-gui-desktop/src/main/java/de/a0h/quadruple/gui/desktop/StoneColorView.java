package de.a0h.quadruple.gui.desktop;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

@SuppressWarnings("serial")
public class StoneColorView extends Canvas {

	public static final float INSETS = 0.1f;
	public static final float RING_WIDTH = 0.15f;

	public static final int STANDARD_SIZE_INT = QuadrupleDesktopApp.getDefaultFont().getSize() * 350 / 100;

	public static final Dimension STANDARD_SIZE = new Dimension(STANDARD_SIZE_INT, STANDARD_SIZE_INT);

	protected Color ringColor;

	public static void useAntiAliasingIfAvailable(Graphics g) {
		if (g instanceof Graphics2D) {
			Graphics2D g2d = (Graphics2D) g;
			g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		}
	}

	@Override
	public void paint(Graphics g) {
		useAntiAliasingIfAvailable(g);

		int size = Math.min(getWidth(), getHeight());

		int inset = Math.round(size * INSETS);
		int ovalSize = size - 2 * inset;

		if (ringColor != null) {
			g.setColor(ringColor);
			g.fillOval(inset, inset, ovalSize, ovalSize);

			inset += Math.round(size * RING_WIDTH);
			ovalSize = size - 2 * inset;
		}

		g.setColor(getForeground());
		g.fillOval(inset, inset, ovalSize, ovalSize);
	}

	@Override
	public Dimension getPreferredSize() {
		return STANDARD_SIZE;
	}
}
