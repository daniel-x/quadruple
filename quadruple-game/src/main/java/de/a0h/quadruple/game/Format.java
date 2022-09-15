package de.a0h.quadruple.game;

public class Format {

	protected static final String CURRENT_PLAYER_LABEL = "current_player: ";

	/**
	 * Instances of this class are stateless, thus they are also thread-safe.
	 */
	public static final Format INSTANCE = new Format();

	public Game parse(String s) {
		String[] line = s.trim().split("\n");

		int w = line[1].length() - 2;
		int h = line.length - 2;

		Game game = Game.create(h, w);
		game.restart();

		int indexOfCurrPlayer = s.indexOf(CURRENT_PLAYER_LABEL);
		indexOfCurrPlayer += CURRENT_PLAYER_LABEL.length();
		char currPlayerSymbol = s.charAt(indexOfCurrPlayer);
		int currPlayer = getPlayer(currPlayerSymbol);

		for (int j = 0; j < w; j++) {
			for (int i = h - 1; i >= 0; i--) {
				String currLine = line[i + 1];
				int player = getPlayer(currLine.charAt(j + 2));

				if (player == Game.PLAYER_NONE) {
					break;
				}

				game.setCurrentPlayer(player);

				game.move_nocheck(j);
			}
		}

		game.setCurrentPlayer(currPlayer);

		return game;
	}

	public static char getSymbol(int player) {
		switch (player) {
		case Game.PLAYER_NONE:
			return '_';
		case Game.PLAYER_FRST:
			return 'x';
		case Game.PLAYER_SCND:
			return 'o';
		default:
			throw new IllegalArgumentException("unknown player: " + player);
		}
	}

	public static int getPlayer(char symbol) {
		switch (symbol) {
		case 'x':
		case 'X':
		case '■':
			return Game.PLAYER_FRST;
		case 'o':
		case 'O':
		case '●':
			return Game.PLAYER_SCND;
		case '_':
			return Game.PLAYER_NONE;
		default:
			throw new IllegalArgumentException("unknown player symbol: " + symbol);
		}
	}

	public String toString(Game game) {
		return toStringBuilder(game, new StringBuilder()).toString();
	}

	public String toStringBuilder(Game game, StringBuilder buf) {
		buf.append(CURRENT_PLAYER_LABEL).append(getSymbol(game.getCurrentPlayer())).append('\n');
		for (int i = 0; i < game.getH(); i++) {

			buf.append(i).append(' ');

			for (int j = 0; j < game.getW(); j++) {
				int owner = game.getStone_nocheck(i, j);

				buf.append(getSymbol(owner));
			}

			buf.append('\n');
		}

		buf.append("  ");
		for (int j = 0; j < game.getW(); j++) {
			buf.append(j);
		}
		buf.append('\n');

		return buf.toString();
	}
}
