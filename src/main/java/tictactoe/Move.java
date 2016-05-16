package tictactoe;

public class Move {
	public Side side;
	public int position;
	
	public Move(Side side, int position) {
		this.side = side;
		this.position = position;
	}
}
