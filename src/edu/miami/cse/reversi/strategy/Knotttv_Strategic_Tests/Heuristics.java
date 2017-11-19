package edu.miami.cse.reversi.strategy.Knotttv_Strategic_Tests;

import java.util.Arrays;

import edu.miami.cse.reversi.Board;
import edu.miami.cse.reversi.Move;
import edu.miami.cse.reversi.Player;
import edu.miami.cse.reversi.Square;

public class Heuristics {

	// Returns best move from array of moves

	// Orders an array of "SmartMoves" from highest heuristic value to lowest
	static public Object[] orderMoves(Board board, Object[] possibleMoves) {
		for (int i = 0; 0 < possibleMoves.length; i++)
			calculateHeuristics(board, (SmartSquare)possibleMoves[i]);
		Arrays.sort(possibleMoves);
		return possibleMoves;
	}

	// Calculates heuristic for given move
	public static void calculateHeuristics(Board board, SmartSquare square) {
		Board tempBoard = board;
		Player user = board.getCurrentPlayer();

		//Counts totals before you make a move
		int old2x2 = getCenter2x2Count(tempBoard, user);
		int old4x4 = getCenter4x4Count(tempBoard, user);
		int oldEdges = getEdgeCount(tempBoard, user);
		int oldCorners = getCornerCount(tempBoard, user);

		tempBoard.play(square);

		//Counts totals after you make a move
		int middleSquareCount = getCenter2x2Count(tempBoard, user) - old2x2;
		int outerSquareCount = getCenter4x4Count(tempBoard, user) - old4x4;
		int edgeCount = getEdgeCount(tempBoard, user) - oldEdges;
		int cornerCount = getCornerCount(tempBoard, user) - oldCorners;

		//
		square.updateHeuristic(middleSquareCount * 1);
		square.updateHeuristic(outerSquareCount * 1);
		square.updateHeuristic(edgeCount * 5);
		square.updateHeuristic(cornerCount * 10);

		// if (relinquishesCenter2x2(tempBoard, player))
		// heuristic -= 1;
		// if (relinquishesCenter4x4(tempBoard, player))
		// heuristic -= 2;
		// if (relinquishesEdge(tempBoard, player))
		// heuristic -= 5;
		// if (relinquishesCorner(tempBoard, player))
		// heuristic -= 10;

		return;

	}

	// Early in the game
	public boolean isEarly(Board board) {
		return board.getMoves().size() < 20;
	}

	// Late in the game
	public boolean isLate(Board board) {
		return board.getMoves().size() > 40;
	}

	// Returns number of inner square (2x2) tiles obtained
	public static int getCenter2x2Count(Board board, Player user) {
		int count = 0;
		if (board.getSquareOwners().get(new Square(3, 3)).equals(user))
			count++;
		if (board.getSquareOwners().get(new Square(3, 4)).equals(user))
			count++;
		if (board.getSquareOwners().get(new Square(4, 3)).equals(user))
			count++;
		if (board.getSquareOwners().get(new Square(4, 4)).equals(user))
			count++;
		return count;
	}

	// Returns number of outer square (4x4) tiles obtained
	public static int getCenter4x4Count(Board board, Player user) {
		int count = 0;
		for (int i = 2; i <= 5; i++) {
			for (int j = 2; j <= 5; j++) {
				if (board.getSquareOwners().get(new Square(i, j)).equals(user))
					count++;
			}
		}

		return count;
	}

	// Returns number of edges obtained
	public static int getEdgeCount(Board board, Player user) {
		int count = 0;
		for (int i = 1; i <= 6; i++) {
			if (board.getSquareOwners().get(new Square(i, 0)).equals(user))
				count++;
			if (board.getSquareOwners().get(new Square(0, i)).equals(user))
				count++;
		}
		for (int i = 1; i <= 6; i++) {
			if (board.getSquareOwners().get(new Square(i, 7)).equals(user))
				count++;
			if (board.getSquareOwners().get(new Square(7, i)).equals(user))
				count++;
		}

		return count;
	}

	// Returns number of corners obtained
	public static int getCornerCount(Board board, Player user) {
		int count = 0;
		if (board.getSquareOwners().get(new Square(0, 0)).equals(user))
			count++;
		if (board.getSquareOwners().get(new Square(0, 7)).equals(user))
			count++;
		if (board.getSquareOwners().get(new Square(7, 0)).equals(user))
			count++;
		if (board.getSquareOwners().get(new Square(7, 7)).equals(user))
			count++;

		return count;
	}

	// Extension of Move class
	private class SmartSquare extends Square implements Comparable {
		int heuristic;

		public SmartSquare(int row, int column) {
			super(row, column);
			this.heuristic = 0;
		}

		public void updateHeuristic(int value) {
			heuristic += value;
		}

		@Override
		public int compareTo(Object o) {
			SmartSquare other = (SmartSquare) o;
			if (this.heuristic > other.heuristic)
				return 1;
			if (this.heuristic < other.heuristic)
				return -1;
			return 0;
		}

	}

}
