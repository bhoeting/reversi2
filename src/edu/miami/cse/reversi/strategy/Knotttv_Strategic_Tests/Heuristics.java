package edu.miami.cse.reversi.strategy.Knotttv_Strategic_Tests;

import java.util.ArrayList;
import java.util.Collections;

import edu.miami.cse.reversi.Board;
import edu.miami.cse.reversi.Square;

public class Heuristics {

	// Orders an array of "SmartMoves" from highest heuristic value to lowest that way it is able to analyze the best move first

	public static ArrayList<Square> orderMoves(Board board, ArrayList<Square> squares) {	
		ArrayList<SmartSquare> smartSquares = new ArrayList<SmartSquare>();

		for (Square square : squares){	
			SmartSquare smartSquare = new SmartSquare(square);
			calculateHeuristics(smartSquare) ;
			smartSquares.add(smartSquare);
		}

		Collections.sort(smartSquares);
		squares.clear();
		for (Square square : smartSquares){
			squares.add(square);
		}
		return squares;
	}

	// Calculates heuristic for given move
	public static void calculateHeuristics(SmartSquare square) {
		square.updateHeuristic(  isCenter2x2Count(square) ? 1 : 0 );
//		square.updateHeuristic(outerSquareCount * 1);
//		square.updateHeuristic(edgeCount * 5);
		square.updateHeuristic(isCornerSquare(square) ? 10 : 0);
	}

	// Early in the game
	public boolean isEarly(Board board) {
		return board.getMoves().size() < 20;
	}

	// Late in the game
	public boolean isLate(Board board) {
		return board.getMoves().size() > 40;
	}


	// Returns if the possibility is  an inner square within (2x2) tile
	public static boolean isCenter2x2Count(SmartSquare s) {
        if ((s.getRow() == 3 && s.getColumn() == 3) ||
                (s.getRow() == 3 && s.getColumn() == 4) ||
                (s.getRow() == 4 && s.getColumn() == 3) ||
                (s.getRow() == 4 && s.getColumn() == 4))
            return true;
        return false;
	}

	// Returns number of outer square (4x4) tiles obtained
//	public static int getCenter4x4Count(Board board, Player user) {
//		int count = 0;
//		for (int i = 2; i <= 5; i++) {
//			for (int j = 2; j <= 5; j++) {
//				if (board.getSquareOwners().get(new Square(i, j)).equals(user))
//					count++;
//			}
//		}
//
//		return count;
//	}

	// Returns number of edges obtained
//	public static int getEdgeCount(Board board, Player user) {
//		int count = 0;
//		for (int i = 1; i <= 6; i++) {
//			if (board.getSquareOwners().get(new Square(i, 0)).equals(user))
//				count++;
//			if (board.getSquareOwners().get(new Square(0, i)).equals(user))
//				count++;
//		}
//		for (int i = 1; i <= 6; i++) {
//			if (board.getSquareOwners().get(new Square(i, 7)).equals(user))
//				count++;
//			if (board.getSquareOwners().get(new Square(7, i)).equals(user))
//				count++;
//		}
//
//		return count;
//	}

    public static boolean isCornerSquare(SmartSquare s) {
        if ((s.getRow() == 0 && s.getColumn() == 0) ||
                (s.getRow() == 0 && s.getColumn() == 7) ||
                (s.getRow() == 7 && s.getColumn() == 0) ||
                (s.getRow() == 7 && s.getColumn() == 7))
            return true;
        return false;
    }


	private static class SmartSquare extends Square implements Comparable {
		int heuristic;

		public SmartSquare(Square square) {
			super(square.getRow(), square.getColumn());
			this.heuristic = 0;
		}

		public void updateHeuristic(int value) {
			heuristic += value;
		}

		@Override
		public int compareTo(Object o) {
			SmartSquare other = (SmartSquare) o;
			if (this.heuristic > other.heuristic)
				return -1;
			if (this.heuristic < other.heuristic)
				return 1;
			return 0;
		}

	}

}


