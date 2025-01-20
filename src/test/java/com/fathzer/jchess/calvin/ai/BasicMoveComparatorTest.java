package com.fathzer.jchess.calvin.ai;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;

import org.junit.jupiter.api.Test;

import com.fathzer.calvin.FENtoMoveGeneratorBuilder;
import com.fathzer.games.util.MoveList;
import com.fathzer.jchess.calvin.CalvinMoveGenerator;
import com.kelseyde.calvin.board.Move;

class BasicMoveComparatorTest implements FENtoMoveGeneratorBuilder {

	@Test
	void test() {
		final CalvinMoveGenerator board = from("Q2n4/4P3/8/5P2/8/qK3p1k/1P6/8 w - - 0 1");
		final BasicMoveComparator cmp = new BasicMoveComparator(board);
		final Move queenPawnCatch = Move.fromUCI("a8f3");
		final Move queenQueenCatch = Move.fromUCI("a8a3");
		final Move kingCatch = Move.fromUCI("b3a3");
		final Move pawnMove = Move.fromUCI("f5f6");
		final Move pawnPromo = Move.fromUCI("e7e8", Move.PROMOTE_TO_QUEEN_FLAG);
		final Move pawnCatchPromo = Move.fromUCI("e7d8", Move.PROMOTE_TO_QUEEN_FLAG);
		
		Move[] moves = new Move[] {pawnMove, queenQueenCatch, queenPawnCatch, kingCatch, pawnCatchPromo, pawnPromo};
//		Arrays.stream(moves).map(m -> m.toString()+":"+cmp.applyAsInt(m)).forEach(System.out::println);
		final MoveList<Move> sorted = new MoveList<>();
		sorted.setComparator(cmp);
		sorted.addAll(Arrays.asList(moves));
		sorted.sort();
		assertEquals(Arrays.asList(pawnCatchPromo, queenQueenCatch, kingCatch, pawnPromo, queenPawnCatch, pawnMove), sorted);
		assertFalse(cmp.test(pawnMove));
		assertTrue(cmp.compare(kingCatch, queenQueenCatch)>0);
		assertTrue(cmp.compare(queenPawnCatch, kingCatch)>0);
	}

}
