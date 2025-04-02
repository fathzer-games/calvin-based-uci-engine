package com.fathzer.jchess.calvin.ai;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import com.fathzer.calvin.FENtoMoveGeneratorBuilder;
import com.fathzer.calvin.MoveBuilder;
import com.fathzer.games.MoveGenerator;
import com.fathzer.games.ai.DepthFirstSearchParameters;
import com.fathzer.games.ai.Negamax;
import com.fathzer.games.ai.SearchContext;
import com.fathzer.games.ai.SearchParameters;
import com.fathzer.games.ai.evaluation.EvaluatedMove;
import com.fathzer.games.ai.evaluation.Evaluation;
import com.fathzer.games.ai.evaluation.Evaluation.Type;
import com.fathzer.games.ai.iterativedeepening.IterativeDeepeningEngine;
import com.fathzer.games.ai.iterativedeepening.SearchHistory;
import com.fathzer.games.ai.evaluation.Evaluator;
import com.fathzer.games.util.SelectiveComparator;
import com.fathzer.games.util.exec.ExecutionContext;
import com.fathzer.jchess.calvin.CalvinMoveGenerator;
import com.fathzer.jchess.calvin.ai.eval.NaiveEvaluator;
import com.fathzer.jchess.calvin.uci.CalvinBasedEngine;
import com.kelseyde.calvin.board.Move;

class MinimaxEngineTest implements FENtoMoveGeneratorBuilder, MoveBuilder {
	CalvinMoveGenerator fromFEN(String fen, Function<CalvinMoveGenerator, SelectiveComparator<Move>> moveComparatorBuilder) {
		final CalvinMoveGenerator mv = from(fen);
		mv.setMoveComparatorBuilder(moveComparatorBuilder);
		return mv;
	}
	
	String toUCI(Move mv) {
		return Move.toUCI(mv);
	}

	@Test
	void blackPlayingTest() {
		final IterativeDeepeningEngine<Move, CalvinMoveGenerator> mme4 = CalvinBasedEngine.buildEngine(NaiveEvaluator::new, 3);
		mme4.getDeepeningPolicy().setSize(Integer.MAX_VALUE);
		final List<EvaluatedMove<Move>> moves = getBests(mme4, fromFEN("7k/5p1Q/5P1N/5PPK/6PP/8/8/8 b - - 6 5", StrictMoveEvaluator::new));
//show(moves);
		assertEquals(1, moves.size());
		assertEquals("h8h7", toUCI(moves.get(0).getMove()));
		assertEquals(-800, moves.get(0).getScore());
	}
	
	private void show(Collection<EvaluatedMove<Move>> moves) {
		System.out.println(moves);
	}
	
	private <M, B extends MoveGenerator<M>> List<EvaluatedMove<M>> getBests(IterativeDeepeningEngine<M, B> engine, B moveGenerator) {
		final SearchHistory<M> bestMoves = engine.getBestMoves(moveGenerator);
		return bestMoves.getAccurateMoves();
	}
	
	@Test
	void test() {
		List<EvaluatedMove<Move>> moves;
		final IterativeDeepeningEngine<Move, CalvinMoveGenerator> mme4 = CalvinBasedEngine.buildEngine(NaiveEvaluator::new, 4);
		mme4.getDeepeningPolicy().setSize(Integer.MAX_VALUE);
		
		// 3 possible Mats in 1 with whites
		moves = getBests(mme4, fromFEN("7k/5p2/5PQN/5PPK/6PP/8/8/8 w - - 6 5", StrictMoveEvaluator::new));
//show(moves);
		assertEquals(6, moves.size());
		{
			final Evaluation max = moves.get(0).getEvaluation();
			assertEquals(Type.WIN, max.getType());
			assertEquals(1, max.getCountToEnd());
			assertTrue(moves.get(3).getEvaluation().compareTo(max)<0);
			moves.stream().limit(3).forEach(m -> assertEquals(max, m.getEvaluation()));
		}

		Evaluation max;
		Move mv;
		// Mat in 1 with blacks
		System.out.println("------------------");
		moves = getBests(mme4, fromFEN("1R6/8/8/7R/k7/ppp1p3/r2bP3/1K6 b - - 6 5", StrictMoveEvaluator::new));
//show(moves);
		assertEquals(7, moves.size());
		max = moves.get(0).getEvaluation();
		assertEquals(Type.WIN, max.getType());
		assertEquals(1, max.getCountToEnd());
		mv = moves.get(0).getMove();
		assertEquals("c3c2", toUCI(mv));
		// Warning, due to transposition table effects, the second best move (M+3) can be detected even if we search at depth 4!
		assertTrue(moves.get(1).getScore()<moves.get(0).getScore());
		
		// Check in 2
		System.out.println("------------------");
		moves = getBests(mme4, fromFEN("8/8/8/8/1B6/NN6/pk1K4/8 w - - 0 1", StrictMoveEvaluator::new));
//show(moves);
		max = moves.get(0).getEvaluation();
		assertEquals(Type.WIN, max.getType());
		assertEquals(2, max.getCountToEnd());
		assertTrue(moves.get(1).getScore()<max.getScore());
		mv = moves.get(0).getMove();
		assertEquals("b3a1", toUCI(mv));
		
		// Check in 2 with blacks
		System.out.println("------------------");
		moves = getBests(mme4, from("8/4k1KP/6nn/6b1/8/8/8/8 b - - 0 1"));
//show(moves);
		max = moves.get(0).getEvaluation();
		assertEquals(Type.WIN, max.getType());
		assertEquals(2, max.getCountToEnd());
		assertTrue(moves.get(1).getScore()<max.getScore());
		assertEquals("g6h8", toUCI(moves.get(0).getMove()));
		
		
		// Check in 3
		System.out.println("------------------");
		IterativeDeepeningEngine<Move, CalvinMoveGenerator> engine = CalvinBasedEngine.buildEngine(NaiveEvaluator::new, 6);
		engine.getDeepeningPolicy().setSize(3);
		engine.getDeepeningPolicy().setAccuracy(100);
		moves = getBests(engine, fromFEN("r2k1r2/pp1b2pp/1b2Pn2/2p5/Q1B2Bq1/2P5/P5PP/3R1RK1 w - - 0 1", StrictMoveEvaluator::new));
//show(moves);
		mv = moves.get(0).getMove();
		assertEquals("d1d7", toUCI(mv));
	}
	
	@Test
	void moreTests() {
		final CalvinMoveGenerator board = from("8/8/8/3kr3/8/8/5PPP/7K w - - 0 1");
		final Evaluator<Move,CalvinMoveGenerator> basicEvaluator = new NaiveEvaluator();
		basicEvaluator.init(board);
		SearchContext<Move, CalvinMoveGenerator> context = SearchContext.get(board, () -> basicEvaluator);
		try (ExecutionContext<SearchContext<Move, CalvinMoveGenerator>> exec = ExecutionContext.get(1, context)) {
			Negamax<Move, CalvinMoveGenerator> ai = new Negamax<>(exec);
			List<Move> l = new ArrayList<>();
			l.add(move(board, "h1g1"));
			l.add(move(board, "f2f3"));
			l.add(move(board, "f2f4"));
			final DepthFirstSearchParameters params = new DepthFirstSearchParameters(4, Integer.MAX_VALUE, 0);
			final List<EvaluatedMove<Move>> eval = ai.getBestMoves(l, params).getCut();
			assertEquals(3, eval.size());
			for (EvaluatedMove<Move> e : eval) {
				assertEquals(Type.LOOSE, e.getEvaluation().getType());
				assertEquals(1, e.getEvaluation().getCountToEnd());
			}
		}
	}
	
	@Test
	@Disabled
	void iterativeTest() {
		//TODO This test is disabled, it tests mat in 1 should not be returned when best moves are mat in 3
		// when ai is called with a reasonable non null accuracy
		// Currently, the only way to achieve this is to have a custom win/loose evaluation with a gap higher than the accuracy
		// I should think more about it...
		IterativeDeepeningEngine<Move, CalvinMoveGenerator> engine = CalvinBasedEngine.buildEngine(NaiveEvaluator::new, 8);
		engine.setParallelism(4);
		engine.getDeepeningPolicy().setSize(1);
		engine.getDeepeningPolicy().setAccuracy(300);
		engine.getDeepeningPolicy().setMaxTime(15000);
		// Tests that loose in 1 are not in the best moves (was a bug in fist iterative engine version)
		final List<EvaluatedMove<Move>> moves = getBests(engine, from("4n2r/2k1Q2p/5B2/2N5/2B2R2/1P6/3PKPP1/6q1 b - - 2 46"));
		assertEquals(2, moves.size());
		assertEquals(3, moves.get(0).getEvaluation().getCountToEnd());
		assertEquals(3, moves.get(1).getEvaluation().getCountToEnd());
	}
	
	@Test
	void iterativeTest2() {
		IterativeDeepeningEngine<Move, CalvinMoveGenerator> engine = CalvinBasedEngine.buildEngine(NaiveEvaluator::new, 4);
		engine.setParallelism(4);
		engine.getDeepeningPolicy().setSize(1);
		engine.getDeepeningPolicy().setAccuracy(100);
		engine.getDeepeningPolicy().setMaxTime(15000);
		// Tests that loosing move is not in the best moves (was a bug in fist iterative engine version)
		final List<EvaluatedMove<Move>> moves = getBests(engine, from("3bkrnr/p2ppppp/7q/2p5/8/2P5/PP1PPPPP/RNBQKBNR b KQk - 0 1"));
		for (EvaluatedMove<Move> ev : moves) {
			assertEquals(Type.EVAL, ev.getEvaluation().getType());
		}
	}
	
	@Test
	@Disabled
	void bug20230813() {
		// Not a bug, just a problem with evaluation function
		CalvinMoveGenerator board = from("8/8/8/4p1k1/3bK3/8/7p/8 b - - 0 1");
		IterativeDeepeningEngine<Move, CalvinMoveGenerator> engine = CalvinBasedEngine.buildEngine(NaiveEvaluator::new, 4);
		engine.getDeepeningPolicy().setSize(Integer.MAX_VALUE);
		System.out.println(engine.getBestMoves(board));
	}

	@Test
	void bug20230911() {
		CalvinMoveGenerator board = from("8/4k3/8/R7/8/8/8/4K2R w K - 0 1");
		IterativeDeepeningEngine<Move, CalvinMoveGenerator> engine = CalvinBasedEngine.buildEngine(NaiveEvaluator::new, 8);
		engine.setParallelism(1);
		engine.getDeepeningPolicy().setSize(1);
		engine.getDeepeningPolicy().setAccuracy(0);
		final SearchHistory<Move> history = engine.getBestMoves(board);
		List<EvaluatedMove<Move>> bestMoves = history.getAccurateMoves();
		System.out.println(bestMoves);
		assertEquals(2, bestMoves.size());
	}


	@Test
	@Disabled
	void bug20230821() {
		// Not a bug, just a problem with evaluation function
		IterativeDeepeningEngine<Move, CalvinMoveGenerator> engine = CalvinBasedEngine.buildEngine(NaiveEvaluator::new, 7);
		System.out.println(engine.getBestMoves(from("8/6k1/6p1/1N6/6K1/R7/4B3/8 w - - 21 76")).getAccurateMoves().get(0));
	}
}
