package com.fathzer.jchess.lichess;

import java.io.IOException;
import java.io.InputStream;
import java.util.function.Supplier;

import com.fathzer.jchess.calvin.CalvinMoveGenerator;
import com.fathzer.jchess.calvin.uci.CalvinBasedEngine;
import com.fathzer.jchess.uci.UCIMove;
import com.kelseyde.calvin.board.Move;
import com.kelseyde.calvin.utils.notation.FEN;

public class DefaultOpenings extends AbstractDefaultOpenings<Move, CalvinMoveGenerator> {
	
	public DefaultOpenings(Supplier<InputStream> stream, boolean zipped) throws IOException {
		super(stream, zipped);
	}
	
	@Override
	protected Move fromUCI(CalvinMoveGenerator board, String move) {
		return CalvinBasedEngine.fromUCI(UCIMove.from(move), board);
	}

	@Override
	protected String toXFEN(CalvinMoveGenerator board) {
		return FEN.toFEN(board.getBoard()); //TODO Not sure it's good (verify there's no problem with non capturable en-passant
	}
}
