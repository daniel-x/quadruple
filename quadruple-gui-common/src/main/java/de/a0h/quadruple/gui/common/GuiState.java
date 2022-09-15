package de.a0h.quadruple.gui.common;

import de.a0h.quadruple.game.Game;

public class GuiState {

	/**
	 * PLAYER_LABEL_TEXT[Player.FRST.idx | Player.SCND.idx].
	 */
	public static final String[] PLAYER_LABEL_TEXT = { "Player 1:", "Player 2:" };

	public Player mLeftPlayer = Player.FRST;

	public PlayerColor mLeftPlayerColor = PlayerColor.A;

	public GamePhase mPhase = GamePhase.BEFORE_GAME;

	/**
	 * playerType[Player.FRST.idx | Player.SCND.idx].
	 */
	public PlayerType[] mPlayerType = { PlayerType.AI, PlayerType.AI };

	/**
	 * In milliseconds.
	 */
	public long mLastMoveStartTime;

	/**
	 * elapsedTime[Player.FRST.idx | Player.SCND.idx] in milliseconds.
	 */
	public long[] mElapsedTime = { 0, 0 };

	public Player getPlayer(PlayerSide playerSide) {

		if (playerSide == PlayerSide.LEFT) {
			return mLeftPlayer;

		} else {
			return mLeftPlayer.opponent();
		}
	}

	public PlayerType getPlayerType(PlayerSide playerSide) {
		Player player = getPlayer(playerSide);

		PlayerType result = mPlayerType[player.idx];

		return result;
	}

	public PlayerColor getPlayerColor(PlayerSide playerSide) {

		if (playerSide == PlayerSide.LEFT) {
			return mLeftPlayerColor;

		} else {
			return mLeftPlayerColor.opponent();
		}
	}

	public void switchPlayers() {
		mLeftPlayer = mLeftPlayer.opponent();
		mLeftPlayerColor = mLeftPlayerColor.opponent();
	}

	public void switchPlayerColor() {
		mLeftPlayerColor = mLeftPlayerColor.opponent();
	}

	public void changePlayerType(PlayerSide playerSide) {
		Player player = getPlayer(playerSide);

		changePlayerType(player);
	}

	public void switchPlayerType() {
		PlayerType tmp = mPlayerType[Player.FRST.idx];
		mPlayerType[Player.FRST.idx] = mPlayerType[Player.SCND.idx];
		mPlayerType[Player.SCND.idx] = tmp;
	}

	public void changePlayerType(Player player) {
		boolean humanPlayer = (mPlayerType[player.idx] == PlayerType.HUMAN);

		mPlayerType[player.idx] = humanPlayer ? PlayerType.AI : PlayerType.HUMAN;
	}

	public void switchStartingPlayer() {
		switchPlayers();
		switchPlayerColor();
		switchPlayerType();
	}

	public String toString() {
		return getClass().getSimpleName() + "[left:" + mLeftPlayer + " " + mLeftPlayerColor + " " + mPlayerType + "]";
	}

	public enum GamePhase {
		BEFORE_GAME, RUNNING, ENDED
	}

	/**
	 * This is a clean and standard way to define players for games like connect
	 * four, tic tac toe, or chess. The reason that this enum is not used by the
	 * game mechanics and the ai is simply that enums are not suitable for high
	 * performance code.
	 */
	public static enum Player {
		FRST(Game.PLAYER_FRST), SCND(Game.PLAYER_SCND), NONE(Game.PLAYER_NONE);

		public int idx;

		Player(int idx) {
			this.idx = idx;
		}

		public Player opponent() {
			if (this == FRST) {
				return SCND;
			} else if (this == SCND) {
				return FRST;
			}

			throw new IllegalArgumentException("no opponent for " + this);
		}

		public static Player forIdx(int idx) {
			if (idx == FRST.idx) {
				return FRST;
			} else if (idx == SCND.idx) {
				return SCND;
			} else if (idx == NONE.idx) {
				return NONE;
			}

			throw new IllegalArgumentException("unknown player idx: " + idx);
		}
	}

	/**
	 * Unlike the game model, the GUI uses left and right as the main way to
	 * distinguish players.
	 */
	public static enum PlayerSide {

		LEFT("left"), RIGHT("right");

		String label;

		PlayerSide(String label) {
			this.label = label;
		}

		public PlayerSide opponent() {
			if (this == LEFT) {
				return RIGHT;
			} else if (this == RIGHT) {
				return LEFT;
			}

			throw new IllegalArgumentException("no other side for " + this);
		}
	}

	/**
	 * Colors, also defines suggested RGB values for display.
	 */
	public static enum PlayerColor {
		A, B, NONE;

		public PlayerColor opponent() {
			if (this == A) {
				return B;
			} else if (this == B) {
				return A;
			}

			throw new IllegalArgumentException("no other color for " + this);
		}
	}

	public static enum PlayerType {
		HUMAN("Human"), AI("AI");

		public String label;

		PlayerType(String label) {
			this.label = label;
		}

		public String toString() {
			return label;
		}
	}

	public PlayerSide getPlayerSide(Player player) {
		if (player == mLeftPlayer) {
			return PlayerSide.LEFT;
		} else if (player == mLeftPlayer.opponent()) {
			return PlayerSide.RIGHT;
		}

		throw new IllegalArgumentException("can't determine side of player " + player);
	}

	public PlayerColor getPlayerColor(Player player) {
		if (player == mLeftPlayer) {
			return mLeftPlayerColor;
		} else if (player == mLeftPlayer.opponent()) {
			return mLeftPlayerColor.opponent();
		} else {
			return PlayerColor.NONE;
		}
	}
}
