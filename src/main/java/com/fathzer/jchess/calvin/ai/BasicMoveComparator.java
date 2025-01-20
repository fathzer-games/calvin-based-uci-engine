package com.fathzer.jchess.calvin.ai;

import com.fathzer.chess.utils.AbstractDefaultMoveComparator;
import com.fathzer.jchess.calvin.BasicMoveDecoder;
import com.fathzer.jchess.calvin.CalvinMoveGenerator;
import com.kelseyde.calvin.board.Move;

/** A move comparator that considers a catch is better than other moves and taking a high value piece with a small value piece is better than the opposite.
 */
public class BasicMoveComparator extends AbstractDefaultMoveComparator<Move, CalvinMoveGenerator> {

	public BasicMoveComparator(CalvinMoveGenerator board) {
		super(board);
	}

	@Override
	public int getMovingPiece(CalvinMoveGenerator board, Move move) {
		return board.getPieceAt(move.from());
	}

	@Override
	public int getCapturedType(CalvinMoveGenerator board, Move move) {
		return BasicMoveDecoder.getCapturedType(board, move);
	}

	@Override
	public int getPromotionType(CalvinMoveGenerator board, Move move) {
		return BasicMoveDecoder.getPromotionType(board, move);
	}
}
