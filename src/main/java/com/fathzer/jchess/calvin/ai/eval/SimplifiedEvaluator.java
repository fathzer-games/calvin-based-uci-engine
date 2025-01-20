package com.fathzer.jchess.calvin.ai.eval;

import com.fathzer.chess.utils.adapters.MoveData;
import com.fathzer.chess.utils.evaluators.simplified.AbstractIncrementalSimplifiedEvaluator;
import com.fathzer.chess.utils.evaluators.simplified.SimplifiedState;
import com.fathzer.jchess.calvin.CalvinExplorerBuilder;
import com.fathzer.jchess.calvin.CalvinMoveData;
import com.fathzer.jchess.calvin.CalvinMoveGenerator;
import com.kelseyde.calvin.board.Move;

public class SimplifiedEvaluator extends AbstractIncrementalSimplifiedEvaluator<Move, CalvinMoveGenerator> implements CalvinExplorerBuilder {
	private SimplifiedEvaluator(SimplifiedState state) {
		super(state);
	}

	public SimplifiedEvaluator() {
		super();
	}

	@Override
	public MoveData<Move, CalvinMoveGenerator> get() {
		return new CalvinMoveData();
	}

	@Override
	protected AbstractIncrementalSimplifiedEvaluator<Move, CalvinMoveGenerator> fork(SimplifiedState state) {
		return new SimplifiedEvaluator(state);
	}
}
