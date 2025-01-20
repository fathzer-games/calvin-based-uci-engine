package com.fathzer.jchess.calvin;

import static com.fathzer.chess.utils.Pieces.PAWN;

import com.kelseyde.calvin.board.Bits;
import com.kelseyde.calvin.board.Move;
import com.kelseyde.calvin.board.Piece;

public final class BasicMoveDecoder {
	private BasicMoveDecoder() {
		super();
	}
	
	public static int getMovingPiece(CalvinMoveGenerator board, Move move) {
		return board.getPieceAt(move.from());
	}
	
	public static int getCapturedType(CalvinMoveGenerator board, Move move) {
		if (move.isEnPassant()) {
			return PAWN;
		}
		final int to = move.to();
		final Piece piece = board.getBoard().pieceAt(to);
		// Be aware of castling in chess 960 where we can consider the king captures its own rook!
		return board.isWhiteToMove()== (Bits.contains(board.getBoard().getPieces(true), to)) ? 0 : fromPieceType(piece);
	}
	
	public static int getPromotionType(CalvinMoveGenerator board, Move move) {
		return fromPieceType(move.promoPiece());
	}
	
	private static int fromPieceType(Piece type) {
		return type==null ? 0 : type.ordinal()+1;
	}
}
