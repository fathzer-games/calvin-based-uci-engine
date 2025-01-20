package com.fathzer.jchess.calvin;

import static org.junit.jupiter.api.Assertions.*;

import static com.kelseyde.calvin.board.Move.*;

import org.junit.jupiter.api.Test;

import com.fathzer.calvin.FENtoMoveGeneratorBuilder;
import com.fathzer.calvin.MoveBuilder;
import com.fathzer.chess.utils.adapters.MoveData;
import com.fathzer.chess.utils.test.AbstractMoveDataTest;
import com.kelseyde.calvin.board.Move;

class CalvinMoveDataTest extends AbstractMoveDataTest<Move, CalvinMoveGenerator> implements FENtoMoveGeneratorBuilder, MoveBuilder {
	private static final int[] PROMO_CODES = new int[] {0, 0, PROMOTE_TO_KNIGHT_FLAG, PROMOTE_TO_BISHOP_FLAG, PROMOTE_TO_ROOK_FLAG, PROMOTE_TO_QUEEN_FLAG};
	
	@Override
	protected MoveData<Move, CalvinMoveGenerator> buildMoveData() {
		return new CalvinMoveData();
	}
	
	@Override
	protected CalvinMoveGenerator toBoard(String fen) {
		return from(fen);
	}

	@Override
	protected Move toMove(int from, int to, int promotionType, CalvinMoveGenerator board) {
		Move template = new Move(from^56, to^56, PROMO_CODES[promotionType]);
		try {
			return move(board, toUCI(template));
		} catch (IllegalArgumentException e) {
			//TODO Warning Does not work on castling illegal moves 
			return template;
		}
	}

	@Test
	void testSquareIndex() {
		Move mv = move("a8b8", NO_FLAG);
		assertEquals(0^56, mv.from());
		assertEquals(1^56, mv.to());
		mv = move("a1h1", NO_FLAG);
		assertEquals(56^56, mv.from());
		assertEquals(63^56, mv.to());
	}
}
