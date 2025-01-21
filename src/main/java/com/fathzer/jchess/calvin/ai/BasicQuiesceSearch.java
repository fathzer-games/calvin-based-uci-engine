package com.fathzer.jchess.calvin.ai;

import java.util.List;

import com.fathzer.chess.utils.evaluators.quiesce.AbstractBasicQuiesceEvaluator;
import com.fathzer.games.ai.SearchContext;
import com.fathzer.jchess.calvin.CalvinMoveGenerator;
import com.kelseyde.calvin.board.Move;

public class BasicQuiesceSearch extends AbstractBasicQuiesceEvaluator<Move, CalvinMoveGenerator> {
	@Override
	protected List<Move> getMoves(SearchContext<Move, CalvinMoveGenerator> context, int quiesceDepth) {
		final CalvinMoveGenerator gamePosition = context.getGamePosition();
		return gamePosition.isCheck() ? gamePosition.getMoves() : gamePosition.getCaptures();
	}

	@Override
	protected boolean isCheck(SearchContext<Move, CalvinMoveGenerator> context) {
		return context.getGamePosition().isCheck();
	}
}
