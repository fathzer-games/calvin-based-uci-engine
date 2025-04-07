package com.fathzer.jchess.calvin.uci;

import static com.fathzer.jchess.uci.Engine.*;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

import com.fathzer.games.ai.Negamax;
import com.fathzer.games.ai.SearchContext;
import com.fathzer.games.ai.evaluation.EvaluatedMove;
import com.fathzer.games.ai.evaluation.Evaluator;
import com.fathzer.games.ai.iterativedeepening.FirstBestMoveSelector;
import com.fathzer.games.ai.iterativedeepening.IterativeDeepeningEngine;
import com.fathzer.games.ai.iterativedeepening.SearchHistory;
import com.fathzer.games.ai.moveselector.MoveSelector;
import com.fathzer.games.ai.moveselector.RandomMoveSelector;
import com.fathzer.games.ai.moveselector.StaticMoveSelector;
import com.fathzer.games.ai.time.BasicTimeManager;
import com.fathzer.games.ai.transposition.SizeUnit;
import com.fathzer.games.ai.transposition.TranspositionTable;
import com.fathzer.games.perft.FromPositionMoveGeneratorBuilder;
import com.fathzer.games.util.PhysicalCores;
import com.fathzer.games.util.exec.ExecutionContext;
import com.fathzer.jchess.calvin.CalvinMoveGenerator;
import com.fathzer.jchess.calvin.ai.BasicMoveComparator;
import com.fathzer.jchess.calvin.ai.BasicQuiesceSearch;
import com.fathzer.jchess.calvin.ai.ChessDeepeningPolicy;
import com.fathzer.jchess.calvin.ai.DefaultLogger;
import com.fathzer.jchess.calvin.ai.TT;
import com.fathzer.jchess.calvin.ai.eval.NaiveEvaluator;
import com.fathzer.jchess.calvin.ai.eval.PestoEvaluator;
import com.fathzer.jchess.calvin.ai.eval.SimplifiedEvaluator;
import com.fathzer.jchess.calvin.time.RemainingMoveOracle;
import com.fathzer.jchess.uci.UCIMove;
import com.fathzer.jchess.uci.extended.Displayable;
import com.fathzer.jchess.uci.helper.AbstractEngine;
import com.fathzer.jchess.uci.helper.DeferredReadMoveLibrary;
import com.fathzer.jchess.uci.helper.EvaluatorConfiguration;
import com.kelseyde.calvin.board.Board;
import com.kelseyde.calvin.board.ChessVariant;
import com.kelseyde.calvin.board.Move;
import com.kelseyde.calvin.utils.notation.FEN;

@Id("JChessOnCalvin")
@Author("Jean-Marc Astesana (Fathzer), Move generator is from Dan Kelsey")
@Chess960Supported
public class CalvinBasedEngine extends AbstractEngine<Move, CalvinMoveGenerator> implements FromPositionMoveGeneratorBuilder<Move, CalvinMoveGenerator>, Displayable {
	private static final List<EvaluatorConfiguration<Move, CalvinMoveGenerator>> EVALUATORS = Arrays.asList(
			new EvaluatorConfiguration<>("pesto",PestoEvaluator::new),
			new EvaluatorConfiguration<>("simplified",SimplifiedEvaluator::new),
			new EvaluatorConfiguration<>("naive",NaiveEvaluator::new)
		);
	
	private final DeferredReadMoveLibrary<Move, CalvinMoveGenerator> ownBook;

	private ChessVariant variant;

	public CalvinBasedEngine() {
		this (null);
		variant = ChessVariant.STANDARD;
	}

	public CalvinBasedEngine(DeferredReadMoveLibrary<Move, CalvinMoveGenerator> ownBook) {
		super (buildEngine(EVALUATORS.get(0).getBuilder(), 20), new BasicTimeManager<>(RemainingMoveOracle.INSTANCE));
		setEvaluators(EVALUATORS);
		this.ownBook = ownBook;
	}
	
	DeferredReadMoveLibrary<Move, CalvinMoveGenerator> getOwnBook() {
		return ownBook;
	}

	@Override
	public boolean hasOwnBook() {
		return ownBook!=null;
	}

	@Override
	public void setOwnBook(boolean activate) {
		engine.setOpenings(activate?ownBook:null);
	}
	
	@Override
	public void setChess960(boolean chess960Mode) {
		this.variant = chess960Mode ? ChessVariant.CHESS960 : ChessVariant.STANDARD;
	}

	@Override
	public void setStartPosition(String fen) {
		board = fromPosition(fen);
		board.setMoveComparatorBuilder(BasicMoveComparator::new);
	}
	
	@Override
	public UCIMove toUCI(Move move) {
		return UCIMove.from(Move.toUCI(move));
	}
	
	@Override
	protected Move toMove(UCIMove move) {
		return fromUCI(move, board);
	}
	
	public static Move fromUCI(UCIMove move, CalvinMoveGenerator board) {
		final Move candidate = Move.fromUCI(move.toString());
		final Optional<Move> legal = board.getLegalMoves().stream().filter(m -> m.matches(candidate)).findAny();
		return legal.orElseThrow(()->new IllegalArgumentException(move.toString()+ "is not a valid move"));
	}

	@Override
	public String getBoardAsString() {
		return board.getBoard().toString();
	}

	@Override
	public String getFEN() {
		return board==null ? null : FEN.toFEN(board.getBoard());
	}

	@Override
	public CalvinMoveGenerator fromPosition(String fen) {
		final Board internalBoard = FEN.toBoard(fen, variant);
		return new CalvinMoveGenerator(internalBoard);
	}
	
	public static IterativeDeepeningEngine<Move, CalvinMoveGenerator> buildEngine(Supplier<Evaluator<Move, CalvinMoveGenerator>> evaluatorBuilder, int maxDepth) {
		final IterativeDeepeningEngine<Move, CalvinMoveGenerator> engine = new IterativeDeepeningEngine<>(new ChessDeepeningPolicy(maxDepth), new TT(16, SizeUnit.MB), evaluatorBuilder) {
			@Override
			protected Negamax<Move, CalvinMoveGenerator> buildAI(ExecutionContext<SearchContext<Move, CalvinMoveGenerator>> context) {
				final Negamax<Move, CalvinMoveGenerator> negaMax = (Negamax<Move, CalvinMoveGenerator>) super.buildAI(context);
				negaMax.setQuiesceEvaluator(new BasicQuiesceSearch());
				return negaMax;
			}
			
		};
		engine.setLogger(new DefaultLogger(engine));
		engine.setParallelism(PhysicalCores.count()>1 ? 2 : 1);
		engine.getDeepeningPolicy().setMaxTime(60000);
		return engine;
	}

	@Override
	protected EvaluatedMove<Move> getSelected(CalvinMoveGenerator b, SearchHistory<Move> history) {
		final BasicMoveComparator c = new BasicMoveComparator(b);
		final MoveSelector<Move, SearchHistory<Move>> stmv = new StaticMoveSelector<>(c::evaluate);
		final MoveSelector<Move, SearchHistory<Move>> selector = new FirstBestMoveSelector<>();
		selector.setNext(stmv.setNext(new RandomMoveSelector<>()));
		return history.getBestMove(selector);
	}

	@Override
	protected TranspositionTable<Move, CalvinMoveGenerator> buildTranspositionTable(int sizeInMB) {
		return new TT(sizeInMB, SizeUnit.MB);
	}
}
