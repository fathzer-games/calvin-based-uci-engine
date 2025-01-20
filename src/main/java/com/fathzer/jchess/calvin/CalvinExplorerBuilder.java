package com.fathzer.jchess.calvin;

import com.fathzer.chess.utils.adapters.BoardExplorer;
import com.fathzer.chess.utils.adapters.BoardExplorerBuilder;

public interface CalvinExplorerBuilder extends BoardExplorerBuilder<CalvinMoveGenerator> {

	@Override
	default BoardExplorer getExplorer(CalvinMoveGenerator board) {
		return new CalvinBoardExplorer(board.getBoard());
	}

}
