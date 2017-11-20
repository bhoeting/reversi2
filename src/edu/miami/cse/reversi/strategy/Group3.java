package edu.miami.cse.reversi.strategy;
import edu.miami.cse.reversi.Board;
import edu.miami.cse.reversi.Square;
import edu.miami.cse.reversi.Strategy;
import java.util.ArrayList;
import java.util.Collections;

public class Group3 implements Strategy {

	private final long MAX_TIME = 1000;
	private long startTime;
	private int nodeCutoff = 0;
	private int epthCutoff = 0;

	@Override
	public Square chooseSquare(Board board) {

		/* The first thing we do is setting the start time. */
		startTime = System.currentTimeMillis();

		Board initialBoard;
		int allowedDepth = 3;
		int allowedNodes = 20;
		int alpha = -999;
		int beta = 999;
		boolean wantToMaximize = false;
		Square optimalMove;

		// Generate a state that we are able to work with locally
		// Check to see if there are available moves for the current player
		// Otherwise, pass and generate new local board
		if (!board.getCurrentPossibleSquares().isEmpty()) {
			initialBoard = board;
		} else {
			initialBoard = board.pass();
		}

		// Send to brendan
		ArrayList<Square> possibileMoves = new ArrayList<>(board.getCurrentPossibleSquares());

		// System.out.print("Before: ");
		// for(Square s : possibileMoves){
		// System.out.print(s.toString() + " ");
		// }
		// System.out.println();
		// possibileMoves = Heuristics.orderMoves(board, possibileMoves);
		// System.out.print("After: ");
		// for(Square s : possibileMoves){
		// System.out.print(s.toString() + " ");
		// }
		//
		// System.out.println();

		possibileMoves = orderMoves(board, possibileMoves);

		// Start with an arbitrary move, in the future we will do an intelligent
		// move selection
		// send wantToMaximize as false to start because the immediate
		// proceeding recursive call will be a minimization

		// Gets the first element
		optimalMove = possibileMoves.get(0);

		// Gets a random element
		// optimalMove = possibileMoves.get(new
		// Random().nextInt(possibileMoves.size()));

		int currentUtilityScore = a_b_Pruning(initialBoard.play(optimalMove), alpha, beta, allowedDepth, allowedNodes--,
				!wantToMaximize);

		for (Square s : possibileMoves) {
			if (alpha >= beta || initialBoard.isComplete())
				break;

			if (MAX_TIME - (System.currentTimeMillis() - startTime) < MAX_TIME - 900) {
//				System.out.println("Ran Out of Time");
				return optimalMove;
			}
			// update aplha/beta based on the currentUtilityScore
			if (wantToMaximize) {
				alpha = alpha > currentUtilityScore ? alpha : currentUtilityScore;
			} else {
				beta = beta > currentUtilityScore ? beta : currentUtilityScore;
			}

			// For each of the possible moves we want to call a Minimization or
			// Maximization of the subtree
			int proceedingUtilityScore = a_b_Pruning(initialBoard.play(s), alpha, beta, allowedDepth, allowedNodes--,
					!wantToMaximize);

			// Is the utility of a testMove higher than our currentUtility, if
			// so, this is our better move
			// we would want to update the aplha accordingly
			// Is the utility of testMove lower than the currentUtility, if we
			// are testing for the opponent minimization
			// we would want to update the beta accordingly and use this move to
			// minimize opponent opportunity

			if (wantToMaximize) {
				if (proceedingUtilityScore > currentUtilityScore) {
					currentUtilityScore = proceedingUtilityScore;
					optimalMove = s;
				}
				alpha = alpha > currentUtilityScore ? alpha : currentUtilityScore;
			} else {
				if (proceedingUtilityScore < currentUtilityScore) {
					currentUtilityScore = proceedingUtilityScore;
					optimalMove = s;
				}
				beta = beta > currentUtilityScore ? beta : currentUtilityScore;
			}

		}

		// System.out.println("Node Cutoff: " + nodeCutoff + " Depth Cutoff: " +
		// depthCutoff);

		return optimalMove;
	}

	private int a_b_Pruning(Board instanceBoard, int alpha, int beta, int allowedDepth, int allowedNodes,
			boolean wantToMaximize) {
		// returns the score at the end of this recursive branch, to be compared
		// to the other scores, higher is better for us
		// we want to maximize the score

		if (allowedDepth <= 0) {
			// depthCutoff++;
			return Math.abs(instanceBoard.getPlayerSquareCounts().get(instanceBoard.getCurrentPlayer())
					- instanceBoard.getPlayerSquareCounts().get(instanceBoard.getCurrentPlayer().opponent()));
		}

		if (instanceBoard.isComplete()) {
			return Math.abs(instanceBoard.getPlayerSquareCounts().get(instanceBoard.getCurrentPlayer())
					- instanceBoard.getPlayerSquareCounts().get(instanceBoard.getCurrentPlayer().opponent()));
		}

		// if(allowedNodes <= 0) {
		// nodeCutoff++;
		// return
		// Math.abs(instanceBoard.getPlayerSquareCounts().get(instanceBoard.getCurrentPlayer())
		// -
		// instanceBoard.getPlayerSquareCounts().get(instanceBoard.getCurrentPlayer().opponent()));
		// }

		allowedDepth--;

		// Send to brendan
		ArrayList<Square> instancePossibileMoves = new ArrayList<>(instanceBoard.getCurrentPossibleSquares());

		// No Possible Moves, Pass
		if (instancePossibileMoves.size() == 0) {
			return a_b_Pruning(instanceBoard.pass(), alpha, beta, allowedDepth, allowedNodes--, !wantToMaximize);
		}

		int optimalUtility = wantToMaximize ? -999 : 999;

		for (Square s : instancePossibileMoves) {
			if (alpha >= beta)
				break;

			int proceedingUtilityInstanceScore = a_b_Pruning(instanceBoard.play(s), alpha, beta, allowedDepth,
					allowedNodes--, !wantToMaximize);
			if (wantToMaximize) {
				optimalUtility = optimalUtility > proceedingUtilityInstanceScore ? optimalUtility
						: proceedingUtilityInstanceScore;
				alpha = alpha > optimalUtility ? alpha : optimalUtility;
			} else {
				optimalUtility = optimalUtility < proceedingUtilityInstanceScore ? optimalUtility
						: proceedingUtilityInstanceScore;
				beta = beta > optimalUtility ? beta : optimalUtility;
			}
		}
		return optimalUtility;
	}

	//Orders moves from greatest heuristic value to least
	public static ArrayList<Square> orderMoves(Board board, ArrayList<Square> squares) {
		ArrayList<SmartSquare> smartSquares = new ArrayList<SmartSquare>();

		for (Square square : squares) {
			SmartSquare smartSquare = new SmartSquare(square);
			calculateHeuristics(smartSquare);
			smartSquares.add(smartSquare);
		}

		Collections.sort(smartSquares);
		squares.clear();
		for (Square square : smartSquares) {
			squares.add(square);
		}
		return squares;
	}

	// Calculates heuristic for given move
	public static void calculateHeuristics(SmartSquare square) {
		// square.updateHeuristic( isCenter2x2(square) ? 2 : 0 );
		square.updateHeuristic(isCornerSquare(square) ? 20 : 0);
		square.updateHeuristic(isCenter4x4(square) ? 5 : 0);
		square.updateHeuristic(isEdge(square) ? 10 : 0);
		square.updateHeuristic(reliquishesCorner(square) ? -20 : 0);
		square.updateHeuristic(relinquishesEdge(square) ? -10 : 0);
	}

	//If square is one away from the edge
	private static boolean relinquishesEdge(SmartSquare s) {
		return (s.getRow() == 1 || s.getRow() == 6 || s.getColumn() == 1 || s.getColumn() == 6);
	}

	// Returns number of outer square (4x4) tiles obtained
	public static boolean isCenter4x4(SmartSquare s) {
		for (int i = 2; i <= 5; i++) {
			for (int j = 2; j <= 5; j++) {
				if (s.getRow() == i && s.getColumn() == j)
					return true;
			}
		}
		return false;
	}

	//If 
	public static boolean isEdge(SmartSquare s) {
		return (s.getRow() == 0 || s.getRow() == 7 || s.getColumn() == 0 || s.getColumn() == 7);
	}
	
	private static boolean reliquishesCorner(SmartSquare s) {
		if ((s.getRow() == 0 && s.getColumn() == 1) || (s.getRow() == 1 && s.getColumn() == 0)
				|| (s.getRow() == 1 && s.getColumn() == 7) || (s.getRow() == 0 && s.getColumn() == 6)
				|| (s.getRow() == 7 && s.getColumn() == 1) || (s.getRow() == 6 && s.getColumn() == 0)
				|| (s.getRow() == 7 && s.getColumn() == 6) || (s.getRow() == 6 && s.getColumn() == 7)
				|| (s.getRow() == 1 && s.getColumn() == 1) || (s.getRow() == 1 && s.getColumn() == 6)
				|| (s.getRow() == 6 && s.getColumn() == 1) || (s.getRow() == 6 && s.getColumn() == 6))
			return true;
		return false;
	}
	
	public static boolean isCornerSquare(SmartSquare s) {
		if ((s.getRow() == 0 && s.getColumn() == 0) || (s.getRow() == 0 && s.getColumn() == 7)
				|| (s.getRow() == 7 && s.getColumn() == 0) || (s.getRow() == 7 && s.getColumn() == 7))
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

