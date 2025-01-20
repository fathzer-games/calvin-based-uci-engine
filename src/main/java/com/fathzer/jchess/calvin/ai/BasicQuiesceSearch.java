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
		final List<Move> moves = gamePosition.isCheck() ? gamePosition.getMoves() : gamePosition.getCaptures();
		try {
			moves.sort(new BasicMoveComparator(gamePosition));
			System.out.println("Nice on "+moves.getClass());
		} catch (UnsupportedOperationException e) {
			System.out.println("Fuck on "+moves.getClass());
			final BasicMoveComparator c = new BasicMoveComparator(gamePosition);
			moves.sort(c);
		}
		return moves;
	}

	@Override
	protected boolean isCheck(SearchContext<Move, CalvinMoveGenerator> context) {
		return context.getGamePosition().isCheck();
	}
}
