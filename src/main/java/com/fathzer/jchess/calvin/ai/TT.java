package com.fathzer.jchess.calvin.ai;

import com.fathzer.games.ai.transposition.OneLongEntryTranspositionTable;
import com.fathzer.games.ai.transposition.SizeUnit;
import com.fathzer.jchess.calvin.CalvinMoveGenerator;
import com.kelseyde.calvin.board.Move;

public class TT extends OneLongEntryTranspositionTable<Move, CalvinMoveGenerator> {
	public TT(int size, SizeUnit unit) {
		super(size, unit);
	}

	@Override
	protected int toInt(Move move) {
		if (move==null) {
			return 0;
		}
		return move.value();
	}

	@Override
	protected Move toMove(int value) {
		if (value==0) {
			return null;
		}
		return new Move((short)value);
	}
}