package com.fathzer.jchess.calvin;

import java.util.List;
import java.util.function.Function;

import com.fathzer.games.MoveGenerator;
import com.fathzer.games.HashProvider;
import com.fathzer.games.Status;
import com.fathzer.games.util.MoveList;
import com.fathzer.games.util.SelectiveComparator;
import com.kelseyde.calvin.board.Bits;
import com.kelseyde.calvin.board.Board;
import com.kelseyde.calvin.board.Draw;
import com.kelseyde.calvin.board.Move;
import com.kelseyde.calvin.board.Piece;
import com.kelseyde.calvin.movegen.MoveGenerator.MoveFilter;

public class CalvinMoveGenerator implements MoveGenerator<Move>, HashProvider {
	private final com.kelseyde.calvin.movegen.MoveGenerator generator;
	private final Board board;
	private Function<CalvinMoveGenerator, SelectiveComparator<Move>> moveComparatorBuilder;
	private SelectiveComparator<Move> comparator;
	
	public CalvinMoveGenerator(Board board) {
		this.board = board;
		this.generator = new com.kelseyde.calvin.movegen.MoveGenerator();
	}
	
	@Override
	public boolean isWhiteToMove() {
		return board.isWhite();
	}

	@Override
	public boolean makeMove(Move move, MoveConfidence confidence) {
		if (MoveConfidence.UNSAFE==confidence) {
			if (!generator.isPseudoLegal(board, move)) {
				return false;
			}
	        board.makeMove(move);
	        boolean legal = !generator.isCheck(board, !board.isWhite());
	        if (!legal) {
	        	board.unmakeMove();
	        }
	        return legal;
		} else {
			return board.makeMove(move);
		}
	}
	
	@Override
	public void unmakeMove() {
		board.unmakeMove();
	}
	
	@Override
	public List<Move> getMoves() {
		final List<Move> moves = generator.generateMoves(board);
		if (comparator==null) {
			return moves;
		}
		final MoveList<Move> ml = new MoveList<>(moves, comparator);
		ml.sort();
		return ml;
	}
	
	@Override
	public List<Move> getLegalMoves() {
		return generator.generateMoves(board);
	}

	@Override
	public long getHashKey() {
		return board.key();
	}
	
	public Board getBoard() {
		return this.board; 
	}

	public void setMoveComparatorBuilder(Function<CalvinMoveGenerator, SelectiveComparator<Move>> moveComparatorBuilder) {
		this.moveComparatorBuilder = moveComparatorBuilder;
		if (moveComparatorBuilder==null) {
			comparator = null;
		} else {
			comparator = moveComparatorBuilder.apply(this);
		}
	}

	@Override
	public Status getContextualStatus() {
		return Draw.isEffectiveDraw(board) ? Status.DRAW : Status.PLAYING;
	}

	@Override
	public Status getEndGameStatus() {
		if (generator.isCheck(board)) {
			return board.isWhite() ? Status.BLACK_WON : Status.WHITE_WON;
		} else {
			return Status.DRAW;
		}
	}

	@Override
	public MoveGenerator<Move> fork() {
		final CalvinMoveGenerator result = new CalvinMoveGenerator(board.copy());
		result.setMoveComparatorBuilder(moveComparatorBuilder);
		return result;
	}
	
	/** Returns the piece at the given index
	 * @param index The index given in the Calvin coordinates system (a1 corresponds to index 0)
	 * @return The piece at the given index
	 */
	public int getPieceAt(int index) {
		final Piece pieceType = board.pieceAt(index);
		if (pieceType==null) {
			return 0;
		}
		final int result = pieceType.ordinal() + 1;
		return Bits.contains(board.getWhitePieces(), index) ? result : -result;		
	}
	
	public boolean isCheck() {
		return generator.isCheck(board);
	}
	
	public List<Move> getCaptures() {
		final List<Move> moves = generator.generateMoves(board, MoveFilter.CAPTURES_ONLY);
		if (comparator!=null) {
			moves.sort(comparator);
		}
		return moves;
	}
}
