package com.fathzer.jchess.calvin.ai;

import static org.junit.jupiter.api.Assertions.*;

import static com.kelseyde.calvin.board.Move.NO_FLAG;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.fathzer.calvin.FENtoMoveGeneratorBuilder;
import com.fathzer.calvin.MoveBuilder;
import com.fathzer.games.util.MoveList;
import com.fathzer.jchess.calvin.CalvinMoveGenerator;
import com.kelseyde.calvin.board.Move;
import com.kelseyde.calvin.utils.notation.FEN;

class StrictMoveEvaluatorTest implements FENtoMoveGeneratorBuilder, MoveBuilder {

	@Test
	void test() {
		final CalvinMoveGenerator board = from(FEN.STARTPOS);
		final StrictMoveEvaluator c = new StrictMoveEvaluator(board);
		
		// Ensure first is A8
		assertTrue(c.compare(move("a8a6", NO_FLAG), move("a7a6", NO_FLAG))<0);
		assertTrue(c.compare(move("a8a6", NO_FLAG), move("b8a6", NO_FLAG))<0);
		
		// Ensure Last is H1
		assertTrue(c.compare(move("h1h6", NO_FLAG),move("h2h6", NO_FLAG))>0);
		assertTrue(c.compare(move("h1h6", NO_FLAG),move("g8h6", NO_FLAG))>0);
	}

	@Test
	void test2() {
		CalvinMoveGenerator board = from("5B2/8/7p/8/8/NN6/pk1K4/8 b - - 0 1");
		final StrictMoveEvaluator cmp = new StrictMoveEvaluator(board);
		
		MoveList<Move> moves = new MoveList<>();
		moves.setComparator(cmp);
		moves.addAll(board.getLegalMoves());
		moves.sort();
		
		final Move queenPromo = move("a2a1", Move.PROMOTE_TO_QUEEN_FLAG);
		final Move rookPromo = move("a2a1", Move.PROMOTE_TO_ROOK_FLAG);
		final Move bishopPromo = move("a2a1", Move.PROMOTE_TO_BISHOP_FLAG);
		final Move knightPromo = move("a2a1", Move.PROMOTE_TO_KNIGHT_FLAG);
		final Move knightCaught = move(board, "b2b3");
		final Move pawnAdvance = move(board, "h6h5");

		
		final List<Move> expected = Arrays.asList(queenPromo, rookPromo, knightCaught, bishopPromo, knightPromo, pawnAdvance);
		// Warning, move generator can return pseudo legal moves that should be ignored in sorting comparison
		// in order to not have to rewrite test when generator changes from pseudo legals to strictly legal moves
		assertEquals(expected, moves.stream().filter(expected::contains).toList());
		
		assertTrue(cmp.compare(bishopPromo,rookPromo)>0);
		assertTrue(cmp.compare(knightPromo, bishopPromo)>0);
	}

}
