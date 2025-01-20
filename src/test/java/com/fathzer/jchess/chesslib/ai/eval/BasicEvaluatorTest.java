package com.fathzer.jchess.chesslib.ai.eval;

import static org.junit.jupiter.api.Assertions.*;

import static com.fathzer.games.MoveGenerator.MoveConfidence.*;

import org.junit.jupiter.api.Test;

import com.fathzer.calvin.FENtoMoveGeneratorBuilder;
import com.fathzer.calvin.MoveBuilder;
import com.fathzer.games.MoveGenerator.MoveConfidence;
import com.fathzer.games.ai.evaluation.Evaluator;
import com.fathzer.jchess.calvin.CalvinMoveGenerator;
import com.fathzer.jchess.calvin.ai.eval.NaiveEvaluator;
import com.kelseyde.calvin.board.Move;
import com.kelseyde.calvin.utils.notation.FEN;

class BasicEvaluatorTest implements FENtoMoveGeneratorBuilder, MoveBuilder {
	
	private static class ATest implements FENtoMoveGeneratorBuilder {
		private final NaiveEvaluator eval;
		private final CalvinMoveGenerator mvg;
		
		private ATest(String fen, int expectedEval) {
			this.mvg = from(fen);
			this.eval = new NaiveEvaluator();
			this.eval.init(mvg);
			assertEquals(expectedEval, eval.evaluate(mvg));
		}
		
		private void test(Move move, int expectedEval) {
			eval.prepareMove(mvg, move);
			assertTrue(mvg.makeMove(move, UNSAFE));
			eval.commitMove();
			final int incEvaluation = eval.evaluate(mvg);
			if (expectedEval!=incEvaluation) {
				mvg.unmakeMove();
				eval.unmakeMove();
			}
			assertEquals (expectedEval, incEvaluation, "Error for move "+move+" on "+FEN.toFEN(mvg.getBoard()));
		}
	}
	
	@Test
	void testCurrentPlayer() {
		ATest test = new ATest("rn1qkb1r/1ppb1ppp/4pn2/pP1p4/3P1B2/4P3/P1P2PPP/RN1QKBNR w KQkq a6 0 6", 0);
		// En passant from white
		test.test(Move.fromUCI("b5a6", Move.EN_PASSANT_FLAG), -100);
		// No capture from black
		test.test(Move.fromUCI("f8d6"), 100);
		// Capture from white
		test.test(Move.fromUCI("f4d6"), -400);
		// Capture from black
		test.test(Move.fromUCI("c7d6"), 100);
		// No Capture from white
		test.test(Move.fromUCI("b1c3"), -100);
		// Castling from black
		test.test(Move.fromUCI("e8g8", Move.CASTLE_FLAG), 100);
	}
	
	@Test
	void testFork() {
		CalvinMoveGenerator mvg = from("r2qkb1r/1ppb1ppp/4pn2/pP1p4/3P1B2/4P3/P1P2PPP/RN1QKBNR w KQkq a6 0 6");
		Evaluator<Move, CalvinMoveGenerator> eval = new NaiveEvaluator();
		eval.init(mvg);
		assertEquals(300, eval.evaluate(mvg));
		
		CalvinMoveGenerator mvg2 = (CalvinMoveGenerator) mvg.fork();
		Evaluator<Move, CalvinMoveGenerator> eval2 = eval.fork();
		assertEquals(300, eval2.evaluate(mvg2));
		// En passant from white
		Move move = Move.fromUCI("b5a6", Move.EN_PASSANT_FLAG);
		eval2.prepareMove(mvg2, move);
		assertTrue(mvg2.makeMove(move, MoveConfidence.UNSAFE));
		eval2.commitMove();
		assertEquals(-400, eval2.evaluate(mvg2));
		assertEquals(300, eval.evaluate(mvg));
	}
}
