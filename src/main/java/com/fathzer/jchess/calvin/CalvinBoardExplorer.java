package com.fathzer.jchess.calvin;

import com.fathzer.chess.utils.adapters.BoardExplorer;
import com.kelseyde.calvin.board.Bits;
import com.kelseyde.calvin.board.Board;
import com.kelseyde.calvin.board.Piece;

class CalvinBoardExplorer implements BoardExplorer {
	private final Board board;
	private int index;
	private int piece;
	
	CalvinBoardExplorer(Board board) {
		this.board = board;
		this.index = -1;
		next();
	}
	
	@Override
	public boolean next() {
		index++;
		if (index>=64) {
			return false;
		}
		piece = toPiece(board.pieceAt(index), Bits.contains(board.getWhitePieces(), index));
		return piece != 0 || next();
	}
	
	static int toPiece(Piece type, boolean white) {
		final int pieceCode = type==null ? 0 : type.ordinal()+1;
		return white ? pieceCode : -pieceCode;
	}

	public int getIndex() {
		// Warning lines are inverted in Calvin 
		return index^56;
	}

	public int getPiece() {
		return piece;
	}
}
