package com.fathzer.calvin;

import java.util.Collection;
import java.util.stream.Collectors;

import com.fathzer.jchess.calvin.CalvinMoveGenerator;
import com.kelseyde.calvin.board.Square;
import com.kelseyde.calvin.board.Move;

public interface MoveBuilder {
	default Move move(CalvinMoveGenerator board, String uci) {
		final Move template = Move.fromUCI(uci);
		return board.getLegalMoves().stream()
        .filter(template::matches)
        .findAny()
        .orElseThrow(() -> new IllegalArgumentException("Illegal move " + uci));
	}

	default Move move(String uci, int flags) {
		final Move template = Move.fromUCI(uci);
		return new Move(template.from(), template.to(), flags);
	}
	
	default String asString(Collection<Move> moves) {
		return moves.stream().map(Move::toUCI).collect(Collectors.joining(", ", "[", "]"));
	}
	
	default int getIndex(String cell) {
		return Square.fromNotation(cell);
	}
}