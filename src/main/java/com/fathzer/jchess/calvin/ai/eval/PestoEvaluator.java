package com.fathzer.jchess.calvin.ai.eval;

import com.fathzer.chess.utils.adapters.MoveData;
import com.fathzer.chess.utils.evaluators.pesto.AbstractIncrementalPestoEvaluator;
import com.fathzer.chess.utils.evaluators.pesto.PestoState;
import com.fathzer.jchess.calvin.CalvinExplorerBuilder;
import com.fathzer.jchess.calvin.CalvinMoveData;
import com.fathzer.jchess.calvin.CalvinMoveGenerator;
import com.kelseyde.calvin.board.Move;

public class PestoEvaluator extends AbstractIncrementalPestoEvaluator<Move, CalvinMoveGenerator> implements CalvinExplorerBuilder {
	private PestoEvaluator(PestoState state) {
		super(state);
	}

	public PestoEvaluator() {
		super();
	}

	@Override
	public MoveData<Move, CalvinMoveGenerator> get() {
		return new CalvinMoveData();
	}

	@Override
	protected AbstractIncrementalPestoEvaluator<Move, CalvinMoveGenerator> fork(PestoState state) {
		return new PestoEvaluator(state);
	}
}
