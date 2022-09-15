package de.a0h.quadruple.gui.desktop;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import de.a0h.quadruple.game.Game;
import de.a0h.quadruple.gui.common.ColorScheme;
import de.a0h.quadruple.gui.common.ColorScheme.ProtoColor;
import de.a0h.quadruple.gui.common.GuiState;
import de.a0h.quadruple.gui.common.GuiState.Player;
import de.a0h.quadruple.gui.common.GuiState.PlayerColor;

@SuppressWarnings("serial")
public class FieldView extends Canvas implements MouseListener, ComponentListener {

	public static final float INSETS_FIELD = 0.1f;
	public static final float INSETS_FACTOR_STONE = 0.1f;

	public static final ColorScheme colorScheme = ColorScheme.SOFT_GREEN_RED_YELLOW;

	public static final Color COLOR_FRAME = createColor(colorScheme.frame);
	public static final Color COLOR_STONE_A = createColor(colorScheme.stoneA);
	public static final Color COLOR_STONE_B = createColor(colorScheme.stoneB);
	public static final Color COLOR_STONE_NONE = createColor(colorScheme.stoneNone);
	public static final Color COLOR_BACKGROUND = createColor(colorScheme.background);
	public static final Color COLOR_STONE_A_WON = createColor(colorScheme.stoneAWon);
	public static final Color COLOR_STONE_B_WON = createColor(colorScheme.stoneBWon);

	Game game;

	GuiState guiState;

	public static class Loc {
		public int i;
		public int j;

		public Loc() {
			set(-1, -1);
		}

		public void set(int i, int j) {
			this.i = i;
			this.j = j;
		}
	}

	/**
	 * Used for showing which stones won.
	 */
	Loc[] highlightLoc = { new Loc(), new Loc(), new Loc(), new Loc() };

	int w;
	int h;

	int fX;
	int fY;
	int fW;
	int fH;

	int sW;
	int sX;
	int sH;
	int sY;

	QuadrupleDesktopApp listener;

	public FieldView(Game game, GuiState guiState, QuadrupleDesktopApp fieldGuiListener) {
		this.game = game;
		this.guiState = guiState;
		this.listener = fieldGuiListener;

		setBackground(COLOR_BACKGROUND);

		addMouseListener(this);
		addComponentListener(this);
	}

	public void clearHighlights() {
		for (int k = 0; k < 4; k++) {
			highlightLoc[k].set(-1, -1);
		}
	}

	private static Color createColor(ProtoColor protoColor) {
		return new Color(protoColor.r, protoColor.g, protoColor.b);
	}

	@Override
	public Dimension getPreferredSize() {
		return getFieldDimension(85);
	}

	@Override
	public Dimension getMinimumSize() {
		return getFieldDimension(10);
	}

	protected Dimension getFieldDimension(int stoneSize) {
		int w = (int) (stoneSize * 7 / (1 - 2 * INSETS_FIELD));
		int h = (int) (stoneSize * 6 / (1 - 2 * INSETS_FIELD));

		return new Dimension(w, h);
	}

	@Override
	public void update(Graphics g) {
		paint(g);
	}

	public static void useAntiAliasingIfAvailable(Graphics g) {
		if (g instanceof Graphics2D) {
			Graphics2D g2d = (Graphics2D) g;
			g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		}
	}

	@Override
	public void paint(Graphics g) {
		useAntiAliasingIfAvailable(g);

		g.setColor(COLOR_BACKGROUND);

		g.fillRect(0, 0, w, fY);
		g.fillRect(0, fY + fH, w, fY + 1);
		g.fillRect(0, fY, fX, fH + 1);
		g.fillRect(fX + fW, fY, fX + 1, fH + 1);

		g.setColor(COLOR_FRAME);
		g.fillRect(fX, fY, fW, fH);

		if (game != null) {
			paintStones(g, false);
		}
	}

	public void paintStones() {
		Graphics g = getGraphics();
		useAntiAliasingIfAvailable(g);
		paintStones(g, true);
		g.dispose();
	}

	public void paintStone(int i, int j) {
		Graphics g = getGraphics();
		useAntiAliasingIfAvailable(g);
		paintStone(g, i, j, true);
		g.dispose();
	}

	private void paintStone(Graphics g, int i, int j, boolean clearBackground) {
		int y = fY + sY + fH * i / game.getH();
		int x = fX + sX + fW * j / game.getW();

		int playerIdx = game.getStone_nocheck(i, j);
		Player player = Player.forIdx(playerIdx);
		PlayerColor playerColor = guiState.getPlayerColor(player);

		boolean isHighlighted = false;
		for (int k = 0; k < 4; k++) {
			Loc loc = highlightLoc[k];
			isHighlighted |= (i == loc.i && j == loc.j);
		}

		int sWd = roundUpDiv(sW, 12);
		int sHd = roundUpDiv(sH, 12);

		if (clearBackground) {
			g.setColor(COLOR_FRAME);
			g.fillRect(x - sWd - 1, y - sHd - 1, sW + sWd * 2 + 2, sH + sHd * 2 + 2);
		}

		Color color;
		if (isHighlighted) {
			color = getGuiColor(playerColor, true);
			g.setColor(color);
			g.fillOval(x - sWd, y - sHd, sW + sWd * 2, sH + sHd * 2);

			g.setColor(COLOR_FRAME);
			g.fillOval(x - 2, y - 2, sW + 4, sH + 4);
		}

		color = getGuiColor(playerColor, false);
		g.setColor(color);
		g.fillOval(x, y, sW, sH);
	}

	private static int roundUpDiv(int numerator, int denominator) {
		return (numerator + denominator - 1) / denominator;
	}

	public Color getGuiColor(PlayerColor playerColor, boolean isHighlightColor) {
		if (playerColor == PlayerColor.A) {
			return isHighlightColor ? COLOR_STONE_A_WON : COLOR_STONE_A;
		} else if (playerColor == PlayerColor.B) {
			return isHighlightColor ? COLOR_STONE_B_WON : COLOR_STONE_B;
		} else if (playerColor == PlayerColor.NONE) {
			return COLOR_STONE_NONE;
		}

		throw new IllegalStateException("unknown player color: " + playerColor);
	}

	public void paintStones(Graphics g, boolean clearBackground) {
		for (int i = 0; i < game.getH(); i++) {
			for (int j = 0; j < game.getW(); j++) {
				paintStone(g, i, j, clearBackground);
			}
		}
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		int x = e.getX();
		int y = e.getY();

		x -= fX;
		y -= fY;

		if (x >= 0 && y >= 0 && x < fW && y < fH) {
			int i = y * game.getH() / fH;
			int j = x * game.getW() / fW;

			stoneLocationClicked(i, j);
		}
	}

	private int divRoundHalfUp(int num, int den) {
		return ((num << 1) + den) / (den << 1);
	}

	@Override
	public void componentResized(ComponentEvent e) {
		w = getWidth();
		h = getHeight();

		int gameW = game != null ? game.getW() : 7;
		int gameH = game != null ? game.getH() : 6;
		int visColCount = gameW + 1;
		int visRowCount = gameH + 1;

		int stoneSizeW = divRoundHalfUp(w, visColCount);
		int stoneSizeH = divRoundHalfUp(h, visRowCount);
		int stoneSize = Math.min(stoneSizeW, stoneSizeH);
		fX = (w - gameW * stoneSize) / 2;
		fY = (h - gameH * stoneSize) / 2;
		fW = w - 2 * fX;
		fH = h - 2 * fY;

		sW = (fW + gameW - 1) / gameW;
		sX = (int) (sW * INSETS_FACTOR_STONE);
		sW = (int) (sW * (1 - 2 * INSETS_FACTOR_STONE));
		sH = (fH + gameH - 1) / gameH;
		sY = (int) (sH * INSETS_FACTOR_STONE);
		sH = (int) (sH * (1 - 2 * INSETS_FACTOR_STONE));
	}

	private void stoneLocationClicked(int i, int j) {
		listener.onStoneLocationClicked(i, j);
	}

	@Override
	public void mouseClicked(MouseEvent e) {
	}

	@Override
	public void mouseEntered(MouseEvent e) {
	}

	@Override
	public void mouseExited(MouseEvent e) {
	}

	@Override
	public void mousePressed(MouseEvent e) {
	}

	@Override
	public void componentHidden(ComponentEvent arg0) {
	}

	@Override
	public void componentMoved(ComponentEvent arg0) {
	}

	@Override
	public void componentShown(ComponentEvent arg0) {
	}

}
