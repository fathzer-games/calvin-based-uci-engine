package com.fathzer.jchess.calvin.time;

import com.fathzer.chess.utils.AbstractVuckovicSolakOracle;
import com.fathzer.games.ai.time.RemainingMoveCountPredictor;
import com.fathzer.jchess.calvin.CalvinExplorerBuilder;
import com.fathzer.jchess.calvin.CalvinMoveGenerator;

/** A {@link RemainingMoveCountPredictor} that uses the function described in chapter 4 of <a href="http://facta.junis.ni.ac.rs/acar/acar200901/acar2009-07.pdf">Vuckovic and Solak paper</a>.
 */
public class RemainingMoveOracle extends AbstractVuckovicSolakOracle<CalvinMoveGenerator> implements CalvinExplorerBuilder {
	public static final RemainingMoveOracle INSTANCE = new RemainingMoveOracle();

	private RemainingMoveOracle() {
		super();
	}
}
