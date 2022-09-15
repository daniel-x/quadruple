package de.a0h.quadruple.game;

import java.util.Arrays;
import java.util.Scanner;

public class GameLog {

	public int h;
	public int w;

	public int[] moveList;
	public int moveCount;

	public int winner;

	public GameLog(int h, int w) {
		this(h, w, Game.PLAYER_NONE, h * w);
	}

	public GameLog(int h, int w, int winner, int capacity) {
		this.h = h;
		this.w = w;
		this.winner = winner;
		this.moveCount = 0;
		moveList = new int[capacity];
	}

	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}

		if (obj.getClass() != getClass()) {
			return false;
		}

		GameLog other = (GameLog) obj;

		if (h != other.h) {
			return false;
		}

		if (w != other.w) {
			return false;
		}

		if (winner != other.winner) {
			return false;
		}

		if (moveCount != other.moveCount) {
			return false;
		}

		if (!Arrays.equals(moveList, 0, moveCount, other.moveList, 0, moveCount)) {
			return false;
		}

		return true;
	}

	public static GameLog parse(String str) {
		Scanner scanner = new Scanner(str);
		scanner.useDelimiter("[ ;:\\*]+");

		int h = scanner.nextInt();
		int w = scanner.nextInt();
		String winnerStr = scanner.next("[xX■oO●_]");
		int winner = Format.getPlayer(winnerStr.charAt(0));
		int moveCount = scanner.nextInt();

		GameLog result = new GameLog(h, w, winner, moveCount);
		for (int i = 0; i < moveCount; i++) {
			int move = scanner.nextInt();
			result.log(move);
		}

		scanner.close();

		return result;
	}

	public String toString() {
		StringBuilder buf = new StringBuilder(moveCount * 2 + 10);

		buf.append(h).append("*").append(w).append(";");
		buf.append(Format.getSymbol(winner)).append(";");
		buf.append(moveCount).append(": ");

		for (int i = 0; i < moveCount; i++) {
			buf.append(moveList[i]).append(" ");
		}

		return buf.toString();
	}

	public void clear() {
		Arrays.fill(moveList, 0);
		moveCount = 0;
	}

	public void log(int move) {
		moveList[moveCount] = move;
		moveCount++;
	}

	public void assignFrom(GameLog src) {
		h = src.h;
		w = src.w;
		moveCount = src.moveCount;

		if (moveList.length != src.moveList.length) {
			moveList = new int[src.moveList.length];
		}

		System.arraycopy(src.moveList, 0, moveList, 0, src.moveList.length);

		winner = src.winner;
	}
}
