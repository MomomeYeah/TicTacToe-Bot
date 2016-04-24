package tests;

import static org.junit.Assert.*;

import java.util.ArrayList;

import org.junit.Test;

import tictactoe.Side;
import tictactoe.Solver;

public class SolverTest {

	@Test
	public void testsCompletesLine() {
		String marks[] = new String[]{"X", "X", null, null, null, null, null, null, null};
		ArrayList<String> gameState = new ArrayList<String>();
		for (String s : marks) {
			gameState.add(s);
		}
		
		Solver solver = new Solver(Side.CROSS, gameState);
		int position = solver.getMove();
		
		assertEquals(2, position);
	}
	
	@Test
	public void testsCompleteColumn() {
		String marks[] = new String[]{"X", null, null, "X", null, null, null, null, null};
		ArrayList<String> gameState = new ArrayList<String>();
		for (String s : marks) {
			gameState.add(s);
		}
		
		Solver solver = new Solver(Side.CROSS, gameState);
		int position = solver.getMove();
		
		assertEquals(6, position);
	}
	
	@Test
	public void testsCompleteTopLeftBottomRightDiagonal() {
		String marks[] = new String[]{"X", null, null, null, "X", null, null, null, null};
		ArrayList<String> gameState = new ArrayList<String>();
		for (String s : marks) {
			gameState.add(s);
		}
		
		Solver solver = new Solver(Side.CROSS, gameState);
		int position = solver.getMove();
		
		assertEquals(8, position);
	}
	
	@Test
	public void testsCompleteTopRighttBottomLeftDiagonal() {
		String marks[] = new String[]{null, null, "X", null, "X", null, null, null, null};
		ArrayList<String> gameState = new ArrayList<String>();
		for (String s : marks) {
			gameState.add(s);
		}
		
		Solver solver = new Solver(Side.CROSS, gameState);
		int position = solver.getMove();
		
		assertEquals(6, position);
	}

}
