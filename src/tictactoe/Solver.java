package tictactoe;

import java.util.ArrayList;

public class Solver {
	
	private static class PotentialMove {
		private int position;
		private int score;
		
		public PotentialMove(int position) {
			this.position = position;
			this.score = 0;
		}
		
		public void setScore(int score) {
			this.score = score;
		}
	}
	
	public Side side;
	public Board board;

	public Solver(Side side) {
		this.side = side;
		this.board = new Board();
	}
	
	public Solver(Side side, ArrayList<String> gameState) {
		this(side);
		this.board.setState(side, gameState);
	}
	
	public static int evaluatePosition(Board board, Side side) {
		Side winningSide = board.getWinner();
		if (winningSide == null) {
			return 0;
		} else if (winningSide == side) {
			return 1;
		} else {
			return -1;
		}
	}
	
	public static Board copyBoardMakeMove(Board board, Integer position) {
		Board boardCopy = board.copy();
		boardCopy.makeMove(new Move(boardCopy.getCurrentTurn(), position));
		
		return boardCopy;
	}
	
	public static PotentialMove pickMove(Board board) {
		ArrayList<Integer> availablePositions = board.getAvailablePositions();
		Side currentTurn = board.getCurrentTurn();
		
		if (availablePositions.size() == 1) {
			Integer position = availablePositions.get(0);
			Board boardCopy = copyBoardMakeMove(board, position);
			PotentialMove pm = new PotentialMove(position);
			pm.setScore(Solver.evaluatePosition(boardCopy, currentTurn));
			return pm;
		} else {
			ArrayList<PotentialMove> pmList = new ArrayList<PotentialMove>();
			for (Integer position : availablePositions) {
				Board boardCopy = copyBoardMakeMove(board, position);
				PotentialMove pm = new PotentialMove(position);
				int positionScore = Solver.evaluatePosition(boardCopy, currentTurn);
				if (positionScore == 0) {
					pm.setScore(-1 * pickMove(boardCopy).score);
				} else {
					pm.setScore(positionScore);
				}
				pmList.add(pm);
			}
			PotentialMove retPM = pmList.get(0);
			for (PotentialMove pm : pmList) {
				if (pm.score > retPM.score) {
					retPM = pm;
				}
			}
			return retPM;
		}
	}
	
	public Move makeMove() {
		PotentialMove pm = Solver.pickMove(this.board);
		Move move = new Move(this.side, pm.position);
		if (this.board.makeMove(move)) {
			return move;
		}

		return null;
	}
	
	public int getMove() {
		PotentialMove pm = pickMove(this.board);
		return pm.position;
	}
	
	public static void main(String args[]) {
		
		String marks[] = new String[]{"X", "O", "X", null, "X", null, null, "O", "O"};
		ArrayList<String> gameState = new ArrayList<String>();
		for (String s : marks) {
			gameState.add(s);
		}
		
		Solver solver = new Solver(Side.CROSS, gameState);
		solver.board.print();
		solver.makeMove();
		solver.board.print();
		
		/*
		Solver solverX = new Solver(Side.CROSS);
		Solver solverO = new Solver(Side.NOUGHT);
		
		while (!solverX.board.getComplete()) {
			
			Move move;
			if ((move = solverX.makeMove()) != null) {
				solverX.board.print();
				solverO.board.makeMove(move);
			}

			
			if (!solverX.board.getComplete()) {
				if ((move = solverO.makeMove()) != null) {
					solverO.board.print();
					solverX.board.makeMove(move);
				}
			}
			
			
		}*/
		
	}

}
