package com.fathzer.chess.test.utils;

import com.fathzer.jchess.calvin.CalvinMoveGenerator;
import com.kelseyde.calvin.board.Bits;
import com.kelseyde.calvin.board.ChessVariant;
import com.kelseyde.calvin.board.Move;
import com.kelseyde.calvin.board.Piece;
import com.kelseyde.calvin.board.Square;
import com.kelseyde.calvin.utils.notation.FEN;
import com.fathzer.chess.utils.model.BoardPieceScanner;
import com.fathzer.chess.utils.model.TestAdapter;
import com.fathzer.chess.utils.model.Variant;
import com.fathzer.chess.utils.test.Supports;

@Supports(Variant.CHESS960)
public class CalvinAdapter implements TestAdapter<CalvinBoard, Move>, BoardPieceScanner<CalvinBoard> {

    @Override
    public CalvinBoard fenToBoard(String fen, Variant variant) {
        final CalvinMoveGenerator from = new CalvinMoveGenerator(FEN.toBoard(fen, variant==Variant.CHESS960 ? ChessVariant.CHESS960 : ChessVariant.STANDARD));
        return new CalvinBoard(from);
    }

	@Override
	public int getPiece(CalvinBoard board, String algebraicNotation) {
		final int square = Square.fromNotation(algebraicNotation);
		final Piece piece = board.getBoard().getBoard().pieceAt(square);
		if (piece==null) {
			return 0;
		} else {
			final int pieceKind = piece.ordinal()+1;
			final boolean white = (Bits.of(square) & board.getBoard().getBoard().getWhitePieces())!=0;
			return white ? pieceKind : -pieceKind;
		}
	}
}