package de.a0h.quadruple.gui.common;

public class ColorScheme {

	public static final float LIGHTNESS_FACTOR_STONE_WON = 1.6f;
	public static final float LIGHTNESS_FACTOR_STONE_NONE = 1.6f;

	public final ProtoColor frame;
	public final ProtoColor stoneA;
	public final ProtoColor stoneB;
	public final ProtoColor stoneNone;
	public final ProtoColor background;
	public final ProtoColor stoneAWon;
	public final ProtoColor stoneBWon;

	/**
	 * @param frame      must be present
	 * @param stoneA     must be present
	 * @param stoneB     must be present
	 * @param stoneNone  may be null
	 * @param background may be null
	 * @param stoneAWon  may be null
	 * @param stoneBWon  may be null
	 */
	public ColorScheme(//
			ProtoColor frame, //
			ProtoColor stoneA, //
			ProtoColor stoneB, //
			ProtoColor stoneNone, //
			ProtoColor background, //
			ProtoColor stoneAWon, //
			ProtoColor stoneBWon //
	) {
		this.frame = frame;
		this.stoneA = stoneA;
		this.stoneB = stoneB;
		this.stoneNone = (stoneNone != null) ? stoneNone : frame.lightness(LIGHTNESS_FACTOR_STONE_NONE);
		this.background = (background != null) ? background : frame;
		this.stoneAWon = (stoneAWon != null) ? stoneAWon : stoneA.lightness(LIGHTNESS_FACTOR_STONE_WON);
		this.stoneBWon = (stoneBWon != null) ? stoneBWon : stoneB.lightness(LIGHTNESS_FACTOR_STONE_WON);
	}

	public static final ColorScheme CASINO_GREEN_RED_BLACK = new ColorScheme( //
			new ProtoColor(1, 106, 40), //
			new ProtoColor(220, 0, 0), //
			new ProtoColor(0, 0, 0), //
			null, //
			null, //
			null, //
			new ProtoColor(70, 70, 70) //
	);

	public static final ColorScheme CLASSIC_BLUE_RED_YELLOW = new ColorScheme( //
			new ProtoColor(60, 60, 200), //
			new ProtoColor(220, 30, 30), //
			new ProtoColor(220, 220, 30), //
			new ProtoColor(180, 180, 180), //
			new ProtoColor(235, 235, 235), //
			null, //
			null //
	);

	public static final ColorScheme SOFT_GREEN_RED_YELLOW = new ColorScheme( //
			new ProtoColor(88, 140, 115), //
			new ProtoColor(150, 75, 75), //
			new ProtoColor(238, 222, 144), //
			new ProtoColor(155, 191, 174), //
			null, //
			null, //
			null //
	);

	public static class ProtoColor {

		public final int r;
		public final int g;
		public final int b;

		public ProtoColor(int r, int g, int b) {
			this.r = r;
			this.g = g;
			this.b = b;
		}

		public ProtoColor lightness(float factor) {
			int r = Math.round(this.r * factor);
			int g = Math.round(this.g * factor);
			int b = Math.round(this.b * factor);

			r = Math.max(r, 0);
			g = Math.max(g, 0);
			b = Math.max(b, 0);

			r = Math.min(r, 255);
			g = Math.min(g, 255);
			b = Math.min(b, 255);

			return new ProtoColor(r, g, b);
		}
	}
}
