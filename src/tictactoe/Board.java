package tictactoe;

import java.util.ArrayList;

public class Board {
	
	private class Cell {
		private boolean filled;
		private Side side;
		
		public Cell() {
			this.filled = false;
		}
		
		public boolean isAvailable() {
			return !this.filled;
		}
		
		public boolean fill(Side side) {
			if (isAvailable()) {
				this.filled = true;
				this.side = side;
				return true;
			}
			return false;
		}
		
		public boolean isFilledBy(Side side) {
			if (this.filled && this.side == side) {
				return true;
			}
			return false;
		}
		
		public Side getFilledBy() {
			if(this.filled) {
				return this.side;
			}
			
			return null;
		}
		
		public String toString() {
			if (!this.filled) {
				return "-";
			} else if (this.side == Side.NOUGHT) {
				return "O";
			} else {
				return "X";
			}
		}
		
		@SuppressWarnings("unused")
		public void print() {
			System.out.println(this.toString());
		}
	}

	private int boardSize;
	private Cell[][] cells;
	private Side currentTurn;
	private boolean complete;
	private Side winner;
	
	public Board(int boardSize) {
		this.boardSize = boardSize;
		this.cells = new Cell[this.boardSize][this.boardSize];
		for (int row = 0; row < this.boardSize; row++) {
			for (int column = 0; column < this.boardSize; column++) {
				this.cells[row][column] = new Cell();
			}
		}
		
		this.currentTurn = Side.CROSS;
		this.complete = false;
		this.winner = null;
	}
	
	public Board() {
		this(2);
	}
	
	public Board copy() {
		Board board = new Board(this.boardSize);
		for (int row = 0; row < this.boardSize; row++) {
			for (int column = 0; column < this.boardSize; column++) {
				Side side = this.cells[row][column].getFilledBy();
				if(side != null) {
					board.cells[row][column].fill(side);
				}
			}
		}
		board.currentTurn = this.currentTurn;
		board.evaluate();
		
		return board;
	}
	
	public String toString() {
		String boardString = new String();
		
		for (int row = 0; row < this.boardSize; row++) {
			for (int column = 0; column < this.boardSize; column++) {
				boardString += " " + this.cells[row][column].toString() + " ";
			}
			boardString += "\n";
		}
		return boardString;
	}
	
	public void print() {
		System.out.println(this.toString());
	}
	
	public boolean inBounds(int position) {
		if (position >=0 && position < (this.boardSize*this.boardSize)) {
			return true;
		}
		return false;
	}
	
	public ArrayList<Integer> getAvailablePositions() {
		ArrayList<Integer> positions = new ArrayList<Integer>();
		for (int row = 0; row < this.boardSize; row++) {
			for (int column = 0; column < this.boardSize; column++) {
				if (this.cells[row][column].isAvailable()) {
					positions.add(new Integer(row * this.boardSize + column));
				}
			}
		}
		
		return positions;
	}
	
	public Side getCurrentTurn() {
		return this.currentTurn;
	}
	
	public boolean makeMove(Move move) {
		if (this.complete) {
			return false;
		}
		
		int row = (int) Math.floor(move.position / this.boardSize);
		int column = move.position % this.boardSize;
		
		boolean success;
		if (move.side == this.currentTurn && inBounds(move.position)) {
			success = this.cells[row][column].fill(move.side);
			if (success) {
				evaluate();
				this.currentTurn = this.currentTurn == Side.CROSS ? Side.NOUGHT : Side.CROSS;
				return true;
			}
		}
		return false;
	}
	
	public boolean isSideWinner(Side side) {
		boolean isWinner = false;
		
		// complete row
		for (int row = 0; row < this.boardSize; row++) {
			isWinner = true;
			for (int column = 0; column < this.boardSize; column++) {
				if (!this.cells[row][column].isFilledBy(side)) {
					isWinner = false;
					break;
				}
			}
			if (isWinner) {
				return true;
			}
		}
		
		// complete column
		for (int column = 0; column < this.boardSize; column++) {
			isWinner = true;
			for (int row = 0; row < this.boardSize; row++) {
				if (!this.cells[row][column].isFilledBy(side)) {
					isWinner = false;
					break;
				}
			}
			if (isWinner) {
				return true;
			}
		}
		
		// complete diagonal top left to bottom right
		isWinner = true;
		for (int row = 0; row < this.boardSize; row++) {
			for (int column = 0; column < this.boardSize; column++) {
				if (row == column) {
					if (!this.cells[row][column].isFilledBy(side)) {
						isWinner = false;
						break;
					}
				}
			}
		}
		if (isWinner) {
			return true;
		}
		
		// complete diagonal top right to bottom left
		isWinner = true;
		for (int row = 0; row < this.boardSize; row++) {
			for (int column = 2; column >= 0; column--) {
				if (this.boardSize - 1 - row == column) {
					if (!this.cells[row][column].isFilledBy(side)) {
						isWinner = false;
						break;
					}
				}
			}
		}
		if (isWinner) {
			return true;
		}

		return false;
	}
	
	public void evaluate() {
		if (isSideWinner(Side.CROSS)) {
			this.winner = Side.CROSS;
			this.complete = true;
		} else if (isSideWinner(Side.NOUGHT)) {
			this.winner = Side.NOUGHT;
			this.complete = true;
		}
		
		if (!this.complete) {
			boolean isComplete = true;
			for (int row = 0; row < this.boardSize; row++) {
				for (int column = 0; column < this.boardSize; column++) {
					if (this.cells[row][column].isAvailable()) {
						isComplete = false;
					}
				}
			}
			this.complete = isComplete;
		}
	}
	
	public boolean getComplete() {
		return this.complete;
	}
	
	public Side getWinner() {
		return this.winner;
	}
	
	public static void main(String args[]) {
		Board board = new Board();
		board.print();
		
		board.makeMove(new Move(Side.CROSS, 0));
		board.print();
		
		board.makeMove(new Move(Side.NOUGHT, 7));
		board.print();
		
		ArrayList<Integer> positions = board.getAvailablePositions();
		for (Integer i : positions) {
			System.out.println(i);
		}
		
		board.evaluate();
		System.out.println(board.getWinner());
		
		board.makeMove(new Move(Side.CROSS, 4));
		board.makeMove(new Move(Side.NOUGHT, 1));
		board.print();
		
		board.evaluate();
		System.out.println(board.getWinner());
		
		board.makeMove(new Move(Side.CROSS, 8));
		board.makeMove(new Move(Side.NOUGHT, 3));
		board.print();
		
		board.evaluate();
		System.out.println(board.getWinner());
		
	}
}
