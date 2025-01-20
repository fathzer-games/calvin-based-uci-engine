package com.fathzer.calvin;

import static org.junit.jupiter.api.Assertions.*;
import static com.fathzer.games.MoveGenerator.MoveConfidence.*;

import org.junit.jupiter.api.Test;

import com.fathzer.chess.utils.Pieces;
import com.fathzer.games.MoveGenerator.MoveConfidence;
import com.fathzer.jchess.calvin.CalvinMoveGenerator;
import com.kelseyde.calvin.board.Castling;
import com.kelseyde.calvin.board.ChessVariant;
import com.kelseyde.calvin.board.Move;
import com.kelseyde.calvin.board.Square;

class Chess960BoardTest implements MoveBuilder, FENtoMoveGeneratorBuilder {
	
	@Override
	public CalvinMoveGenerator from(String fen) {
		final CalvinMoveGenerator result = FENtoMoveGeneratorBuilder.super.from(fen);
		result.getBoard().setVariant(ChessVariant.CHESS960);
		return result;
	}

	@Test
	void test() {
		final CalvinMoveGenerator board = from("rnbqkbnr/pppppppp/8/8/8/2PP4/PP2PPPP/2RK3R w KQ - 0 1");
		assertTrue(board.makeMove(move(board, "d1c1"), UNSAFE));
		assertEquals(Pieces.ROOK, board.getPieceAt(getIndex("d1")));
		assertEquals(Pieces.KING, board.getPieceAt(getIndex("c1")));
		final long key = board.getHashKey();
		board.unmakeMove();
		assertTrue(board.makeMove(move(board, "d1d2"), UNSAFE));
		assertTrue(board.makeMove(move(board, "b8c6"), UNSAFE));
		assertTrue(board.makeMove(move(board, "d2c2"), UNSAFE));
		assertTrue(board.makeMove(move(board, "c6b8"), UNSAFE));
		assertTrue(board.makeMove(move(board, "c1e1"), UNSAFE));
		assertTrue(board.makeMove(move(board, "b8c6"), UNSAFE));
		assertTrue(board.makeMove(move(board, "c2c1"), UNSAFE));
		assertTrue(board.makeMove(move(board, "c6b8"), UNSAFE));
		assertTrue(board.makeMove(move(board, "e1d1"), UNSAFE));
		assertEquals(Pieces.ROOK, board.getPieceAt(getIndex("d1")));
		assertEquals(Pieces.KING, board.getPieceAt(getIndex("c1")));
		assertEquals(key, board.getHashKey());
	}
	
	@Test
	void testDangerousCastling() {
		// Test castling where king seems safe ... but is not because it does not move and the rook does not defend it anymore
		final CalvinMoveGenerator board = from("nrk1brnb/pp1ppppp/8/2p5/3P4/1N1Q1N2/1PP1PPPP/qRK1BR1B w KQkq - 2 10");
		final Move move = move("c1b1", Move.CASTLE_FLAG);
		assertFalse(board.makeMove(move, UNSAFE));
		assertFalse(board.getLegalMoves().contains(move));
	}
	
	@Test
	void testTrickyLegalCastling() {
		// Rook is attacked, but the castling is legal
		final CalvinMoveGenerator board = from("nrk2rnb/pp1ppppp/6b1/q1p5/3P2Q1/1N3N2/1P2PPPP/1RK1BR1B w KQkq - 2 10");
		final Move move = move("c1b1", Move.CASTLE_FLAG);
		assertTrue(board.getLegalMoves().contains(move));
		assertTrue(board.makeMove(move, UNSAFE));
		board.unmakeMove();
	}
	
	@Test
	void notRightRookCastling() {
		final String fenWithRook = "rn2k2r/ppp1pp1p/3p2p1/5bn1/P7/2N2B2/1PPPPP2/2BNK1RR w Kkq - 4 11";
		var board = from(fenWithRook);
		// Verify e1-g1 which is a castling with the wrong rook is not in legal moves
		assertFalse(board.getLegalMoves().stream().map(Move::toUCI).toList().contains("e1g1"));
		// Can't castling with the wrong rook
		final Move wrongMove = move("e1g1", Move.CASTLE_FLAG);
		assertFalse(board.makeMove(wrongMove, MoveConfidence.UNSAFE));
		if (board.getMoves().stream().map(Move::toUCI).toList().contains(Move.toUCI(wrongMove))) {
			assertFalse(board.makeMove(wrongMove, MoveConfidence.PSEUDO_LEGAL));
		}
		
		final String fenWithInnerRook = "rn2k1r1/ppp1pp1p/3p2p1/5bn1/P7/2N2B2/1PPPPP2/2BNK1RR w Gkq - 4 11";
		board = from(fenWithInnerRook);
		assertTrue(Castling.kingsideAllowed(board.getBoard().getState().rights, true));     
		assertFalse(Castling.queensideAllowed(board.getBoard().getState().rights, true));     
		assertTrue(Castling.kingsideAllowed(board.getBoard().getState().rights, false));     
		assertTrue(Castling.queensideAllowed(board.getBoard().getState().rights, false));
		
		assertEquals("g1", Square.toNotation(Castling.getRook(board.getBoard().getState().rights, true, true)));
		
		// Verify e1-h1 which is a castling with the wrong rook is not in legal moves
		assertFalse(board.getLegalMoves().stream().map(Move::toUCI).toList().contains("e1h1"));
		// Verify making e1-h1 move fails
		assertFalse(board.makeMove(move("e1h1", Move.CASTLE_FLAG), MoveConfidence.UNSAFE));
		// Verify castling with the right rook succeeds
		assertTrue(board.makeMove(move("e1g1", Move.CASTLE_FLAG), MoveConfidence.UNSAFE));
		board.unmakeMove();
		// Verify it still succeeds if wrong rook moves
		assertTrue(board.makeMove(move(board, "h1h2"), MoveConfidence.UNSAFE));
		assertTrue(board.makeMove(move(board, "h7h6"), MoveConfidence.UNSAFE));
		assertTrue(board.makeMove(move("e1g1", Move.CASTLE_FLAG), MoveConfidence.UNSAFE));
	}
}
