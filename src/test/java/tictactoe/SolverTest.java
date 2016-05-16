package tictactoe;

import static org.junit.Assert.*;

import java.util.ArrayList;

import org.junit.Test;

import tictactoe.Side;
import tictactoe.Solver;

public class SolverTest {
	
	public int getMoveFromGameStateArray(String marks[]) {
		ArrayList<String> gameState = new ArrayList<String>();
		for (String s : marks) {
			gameState.add(s);
		}
		
		Solver solver = new Solver(Side.CROSS, gameState);
		return solver.getMove();
	}

	@Test
	public void testsCompletesLine() {
		String marks[] = new String[]{"X", "X", null, null, null, null, null, null, null};
		int position = getMoveFromGameStateArray(marks);
		
		assertEquals(2, position);
	}
	
	@Test
	public void testsCompleteColumn() {
		String marks[] = new String[]{"X", null, null, "X", null, null, null, null, null};
		int position = getMoveFromGameStateArray(marks);
		
		assertEquals(6, position);
	}
	
	@Test
	public void testsCompleteTopLeftBottomRightDiagonal() {
		String marks[] = new String[]{"X", null, null, null, "X", null, null, null, null};
		int position = getMoveFromGameStateArray(marks);
		
		assertEquals(8, position);
	}
	
	@Test
	public void testsCompleteTopRighttBottomLeftDiagonal() {
		String marks[] = new String[]{null, null, "X", null, "X", null, null, null, null};
		int position = getMoveFromGameStateArray(marks);
		
		assertEquals(6, position);
	}
	
	@Test
	public void testsPreventsOppositionLine() {
		String marks[] = new String[]{"O", "O", null, null, null, null, null, null, null};
		int position = getMoveFromGameStateArray(marks);
		
		assertEquals(2, position);
	}
	
	@Test
	public void testsPreventsOppositionColumn() {
		String marks[] = new String[]{"O", null, null, "O", null, null, null, null, null};
		int position = getMoveFromGameStateArray(marks);
		
		assertEquals(6, position);
	}
	
	@Test
	public void testsPreventsOppositionTopLeftBottomRightDiagonal() {
		String marks[] = new String[]{"O", null, null, null, "O", null, null, null, null};
		int position = getMoveFromGameStateArray(marks);
		
		assertEquals(8, position);
	}
	
	@Test
	public void testsPreventsOppositionTopRightBottomLeftDiagonal() {
		String marks[] = new String[]{null, null, "O", null, "O", null, null, null, null};
		int position = getMoveFromGameStateArray(marks);
		
		assertEquals(6, position);
	}

}
