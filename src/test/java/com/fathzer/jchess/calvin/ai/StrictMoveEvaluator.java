package com.fathzer.jchess.calvin.ai;

import com.fathzer.jchess.calvin.CalvinMoveGenerator;
import com.kelseyde.calvin.board.Move;
import com.kelseyde.calvin.board.Piece;

public class StrictMoveEvaluator extends BasicMoveComparator {

	public StrictMoveEvaluator(CalvinMoveGenerator board) {
		super(board);
	}

	@Override
	public int compare(Move m1, Move m2) {
		if (m1.equals(m2)) {
			return 0;
		}
		int cmp = super.compare(m1, m2);
		if (cmp==0) {
			cmp = (m1.from()^56) - (m2.from()^56);
			if (cmp==0) {
				cmp = (m1.to()^56) - (m2.to()^56);
				if (cmp==0) {
					// pawn promotions to Bishop or knight
					return m1.promoPiece()==Piece.KNIGHT ? 1 : -1;
				}
			}
		}
		return cmp;
	}
}
