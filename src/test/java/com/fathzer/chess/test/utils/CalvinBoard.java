package com.fathzer.chess.test.utils;

import java.util.List;

import com.fathzer.chess.utils.model.IBoard;
import com.fathzer.games.MoveGenerator.MoveConfidence;
import com.fathzer.jchess.calvin.CalvinMoveGenerator;
import com.kelseyde.calvin.board.Move;

public class CalvinBoard implements IBoard<Move>{
    private final CalvinMoveGenerator board;

    public CalvinBoard(CalvinMoveGenerator board) {
        this.board = board;
    }

    @Override
    public List<Move> getMoves() {
        return board.getMoves();
    }

    @Override
    public boolean makeMove(Move mv) {
        return board.makeMove(mv, MoveConfidence.LEGAL);
    }

    @Override
    public void unmakeMove() {
        board.unmakeMove();
    }

    public CalvinMoveGenerator getBoard() {
        return board;
    }
}