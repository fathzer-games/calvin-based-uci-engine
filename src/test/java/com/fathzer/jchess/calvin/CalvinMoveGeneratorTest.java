package com.fathzer.jchess.calvin;

import static org.junit.jupiter.api.Assertions.*;

import static com.fathzer.games.MoveGenerator.MoveConfidence.*;
import static com.kelseyde.calvin.board.Move.*;

import java.util.List;

import org.junit.jupiter.api.Test;

import com.fathzer.calvin.FENtoMoveGeneratorBuilder;
import com.fathzer.calvin.MoveBuilder;
import com.kelseyde.calvin.board.Move;

class CalvinMoveGeneratorTest implements MoveBuilder, FENtoMoveGeneratorBuilder {

	@Test
	void testInvalidMovesWithUnsafe() {
		CalvinMoveGenerator mvg = from("rn1qk2r/1ppb1ppp/P2Bpn2/3p4/3P4/4P3/P1P2PPP/RN1QKBNR b KQkq - 0 7");
		List<Move> movesList = mvg.getMoves();
		
		// Castling with free cells in check
		Move move = move("e8g8", CASTLE_FLAG);
		assertFalse(mvg.makeMove(move, UNSAFE));
		
		// Move an non existing piece
		move = move("h6h5", NO_FLAG);
		assertFalse(mvg.makeMove(move, UNSAFE));
		
		// Move a piece of the wrong color
		move = move("d1d3", NO_FLAG);
		assertFalse(mvg.makeMove(move, UNSAFE));
		
		// Move a piece through another piece 
		move = move("d6b8", NO_FLAG);
		assertFalse(mvg.makeMove(move, UNSAFE));
		
		// Piece takes own piece
		move = move("f6d5", NO_FLAG);
		assertFalse(mvg.makeMove(move, UNSAFE));
		
		// Piece goes to unreachable cell
		move = move("d5a5", NO_FLAG);
		assertFalse(movesList.contains(move));
		assertFalse(mvg.makeMove(move, UNSAFE));

		// Pawn takes no piece
		move = move("h7g6", NO_FLAG);
		assertFalse(movesList.contains(move));
		assertFalse(mvg.makeMove(move, UNSAFE));
		
		// Pawn goes on opponent piece
		move = move("d5d4", NO_FLAG);
		assertFalse(movesList.contains(move));
		assertFalse(mvg.makeMove(move, UNSAFE));

		// Pawn goes on own piece
		move = move("f7f6", NO_FLAG);
		assertFalse(mvg.makeMove(move, UNSAFE));
		
		// Imaginary promotion
		move = move("g7g6", PROMOTE_TO_QUEEN_FLAG);
		assertFalse(mvg.makeMove(move, UNSAFE));

		
		mvg = from("3rk2r/PR3p1p/4pn2/3p2pP/3P2bb/4P2N/P1P2PP1/1N2K2R w k - 0 7");
		movesList = mvg.getMoves();

		// Promotion to a pawn
		move = move("a7a8", NO_FLAG);
		assertFalse(movesList.contains(move));
		assertFalse(mvg.makeMove(move, UNSAFE));

		// Promotion of a piece that is not a pawn
		move = move("b7b8", PROMOTE_TO_QUEEN_FLAG);
		assertFalse(mvg.makeMove(move, UNSAFE));
		
		// imaginary en-passant
		move = move("h5g6", EN_PASSANT_FLAG);
		assertFalse(mvg.makeMove(move, UNSAFE));

		// Pinned piece
		move = move("f2f3", NO_FLAG);
		assertFalse(mvg.makeMove(move, UNSAFE));
		
		// King goes to in check cell
		move = move("e1e2", NO_FLAG);
		assertFalse(mvg.makeMove(move, UNSAFE));

		// Illegal castling because castling is not available
		move = move("e1g1", CASTLE_FLAG);
		assertFalse(mvg.makeMove(move, UNSAFE));

		mvg = from("3rk2r/PR3p1p/4pn2/3p2pP/1b1P2b1/4P2N/P1P2PP1/1N2K2R w Kk - 0 7");
		// Illegal castling because king is in check
		move = move("e1g1", CASTLE_FLAG);
		assertFalse(mvg.makeMove(move, UNSAFE));
	}
	
	@Test
	void testValidMovesWithUnsafe() {

		CalvinMoveGenerator mvg = from("rn1qk2r/1ppb1ppp/P2Bpn2/3p4/3P4/4P3/P1P2PPP/RN1QKBNR b KQkq - 0 7");
		
		// Pawn catch
		assertTrue(mvg.makeMove(move(mvg, "c7d6"), UNSAFE));
		mvg.unmakeMove();
		
		//TODO
		
	}
}
