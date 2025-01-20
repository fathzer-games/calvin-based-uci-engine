package com.fathzer.jchess.calvin;

import com.fathzer.calvin.FENtoMoveGeneratorBuilder;
import com.fathzer.chess.utils.adapters.BoardExplorer;
import com.fathzer.chess.utils.adapters.BoardExplorerBuilder;
import com.fathzer.chess.utils.test.AbstractBoardExplorerBuilderTest;

class ExplorerBuilderTest extends AbstractBoardExplorerBuilderTest<CalvinMoveGenerator> implements FENtoMoveGeneratorBuilder, BoardExplorerBuilder<CalvinMoveGenerator>{

	@Override
	protected BoardExplorerBuilder<CalvinMoveGenerator> getBuilder() {
		return this;
	}

	@Override
	public CalvinMoveGenerator toBoard(String fen) {
		return from(fen);
	}

	@Override
	public BoardExplorer getExplorer(CalvinMoveGenerator board) {
		return new CalvinBoardExplorer(board.getBoard());
	}
}
