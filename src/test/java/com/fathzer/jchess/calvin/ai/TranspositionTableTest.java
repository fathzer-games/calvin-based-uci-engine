package com.fathzer.jchess.calvin.ai;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import com.fathzer.games.ai.transposition.SizeUnit;
import com.kelseyde.calvin.board.Move;
import com.kelseyde.calvin.board.Piece;
import com.kelseyde.calvin.board.Square;

class TranspositionTableTest {
	@Test
	void test() {
		TT tt = new TT(1, SizeUnit.KB);
		Move mv = Move.fromUCI("d7d8", Move.PROMOTE_TO_QUEEN_FLAG);
		Move other = tt.toMove(tt.toInt(mv));
		assertEquals(Square.fromNotation("d7"), other.from());
		assertEquals(Square.fromNotation("d8"), other.to());
		assertEquals(Piece.QUEEN, other.promoPiece());
		
		mv = new Move(1,0);
		other = tt.toMove(tt.toInt(mv));
		assertEquals(1, other.from());
		assertEquals(0, other.to());
		assertFalse(other.isPromotion());
	}
}
