package com.fathzer.jchess.calvin;

import static com.fathzer.chess.utils.Pieces.*;

import com.fathzer.chess.utils.adapters.MoveData;
import com.kelseyde.calvin.board.Castling;
import com.kelseyde.calvin.board.ChessVariant;
import com.kelseyde.calvin.board.Move;
import com.kelseyde.calvin.board.Piece;

public class CalvinMoveData implements MoveData<Move, CalvinMoveGenerator> {
	private int movingIndex;
	private int movingPiece;
	private int movingDestination;
	private int capturedIndex;
	private int captured;
	private int promotion;
	private int castlingRookIndex;
	private int castlingRookDestinationIndex;

	@Override
	public int getMovingIndex() {
		return movingIndex;
	}

	@Override
	public int getMovingPiece() {
		return movingPiece;
	}

	@Override
	public int getMovingDestination() {
		return movingDestination;
	}

	@Override
	public int getCapturedType() {
		return captured;
	}

	@Override
	public int getCapturedIndex() {
		return capturedIndex;
	}

	@Override
	public int getPromotionType() {
		return promotion;
	}

	@Override
	public int getCastlingRookIndex() {
		return castlingRookIndex<0 ? -1 : castlingRookIndex;
	}

	@Override
	public int getCastlingRookDestinationIndex() {
		return castlingRookDestinationIndex;
	}

	@Override
	public boolean update(Move move, CalvinMoveGenerator board) {
		this.movingIndex = move.from();
		this.movingPiece = board.getPieceAt(movingIndex);
		int movingType = Math.abs(movingPiece);
		if (movingType==0) {
			return false;
		} else if (movingType==KING) {
			this.promotion = 0;
			if (move.isCastling()) {
				if (board.getBoard().variant()==ChessVariant.CHESS960) {
					//FIXME Does not work in Chess960 (see Board#makeCastleMove)
					throw new UnsupportedOperationException();
				}
				this.captured = 0;
				this.movingDestination = move.to();
		        final boolean kingside = Castling.isKingside(this.movingIndex, this.movingDestination);
				final boolean white = board.getBoard().isWhite();
				this.castlingRookIndex = Castling.rookFrom(kingside, white)^56;
				this.castlingRookDestinationIndex = Castling.rookTo(kingside, white)^56;
			} else {
				this.castlingRookIndex = -1;
				this.movingDestination = move.to();
				fillStandardCapture(board);
			}
		} else {
			// Not a king move => no castling
			this.castlingRookIndex=-1;
			this.movingDestination = move.to();
			if (move.isEnPassant()) {
				this.captured = PAWN;
				final boolean white = board.getBoard().isWhite();
				this.capturedIndex = (white ? this.movingDestination - 8 : this.movingDestination + 8)^56;
				this.promotion = 0;
			} else {
				this.promotion = move.isPromotion() ? move.promoPiece().ordinal()+1 : 0;
				fillStandardCapture(board);
			}
		}
		this.movingIndex = this.movingIndex^56;
		this.movingDestination = this.movingDestination^56;
		return true;
	}

	private void fillStandardCapture(CalvinMoveGenerator board) {
		final Piece capturedPiece = board.getBoard().pieceAt(movingDestination);
		if (capturedPiece!=null) {
			this.captured = capturedPiece.ordinal()+1;
			this.capturedIndex = this.movingDestination^56;
		}
	}
}
