package com.fathzer.jchess.calvin.ai.eval;

import com.fathzer.chess.utils.evaluators.AbstractNaiveEvaluator;
import com.fathzer.jchess.calvin.BasicMoveDecoder;
import com.fathzer.jchess.calvin.CalvinExplorerBuilder;
import com.fathzer.jchess.calvin.CalvinMoveGenerator;
import com.kelseyde.calvin.board.Move;

public class NaiveEvaluator extends AbstractNaiveEvaluator<Move, CalvinMoveGenerator> implements CalvinExplorerBuilder {
	public NaiveEvaluator() {
		super();
	}

	private NaiveEvaluator(int score) {
		super(score);
	}

	@Override
	public NaiveEvaluator fork(int score) {
		return new NaiveEvaluator(score);
	}

	@Override
	protected int getCapturedType(CalvinMoveGenerator board, Move move) {
		return BasicMoveDecoder.getCapturedType(board, move);
	}

	@Override
	protected int getPromotionType(CalvinMoveGenerator board, Move move) {
		return BasicMoveDecoder.getPromotionType(board, move);
	}
}
