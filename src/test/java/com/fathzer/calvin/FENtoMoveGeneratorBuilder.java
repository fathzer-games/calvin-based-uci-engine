package com.fathzer.calvin;

import com.fathzer.jchess.calvin.CalvinMoveGenerator;
import com.kelseyde.calvin.utils.notation.FEN;

public interface FENtoMoveGeneratorBuilder {
	default CalvinMoveGenerator from(String fen) {
		return new CalvinMoveGenerator(FEN.toBoard(fen));
	}
}
