package de.a0h.quadruple.gui.desktop;

import java.awt.Button;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Label;
import java.awt.Panel;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.text.SimpleDateFormat;
import java.util.TimeZone;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

import de.a0h.quadruple.ai.Ai;
import de.a0h.quadruple.ai.AiExecutionListener;
import de.a0h.quadruple.ai.AsyncAiExecutor;
import de.a0h.quadruple.ai.minimax.AbPruningAi;
import de.a0h.quadruple.game.FieldIterator;
import de.a0h.quadruple.game.Format;
import de.a0h.quadruple.game.Game;
import de.a0h.quadruple.game.GameImplIntArray1D;
import de.a0h.quadruple.game.WinnerDetector;
import de.a0h.quadruple.gui.common.GuiState;
import de.a0h.quadruple.gui.common.GuiState.GamePhase;
import de.a0h.quadruple.gui.common.GuiState.Player;
import de.a0h.quadruple.gui.common.GuiState.PlayerColor;
import de.a0h.quadruple.gui.common.GuiState.PlayerSide;
import de.a0h.quadruple.gui.common.GuiState.PlayerType;
import de.a0h.quadruple.gui.desktop.FieldView.Loc;

/**
 * development times
 * 
 * 2019-10-22, Tue, 19:00-24:00:<br/>
 * 1h view<br/>
 * 1h controller (rudimentary placing a stone)<br/>
 * 1h game mechanics<br/>
 * 2h ai<br/>
 * 
 * 2019-10-23, Wed, 00:00-02:00:<br/>
 * 2h ai<br/>
 * 
 * 2019-10-23, Wed, 11:00-17:30:<br/>
 * 2h ai: now ai easily beats me, but it feels buggy; loses against other ai on
 * internet<br/>
 * 3h ai: now ai beats other ais on internet at most difficult levels<br/>
 * 1h30m gui: more functionality for game setups<br/>
 * 
 * 2019-10-24, Thu, 11:30-14:30:<br/>
 * 3h gui: more functionality for game setups<br/>
 * 
 * 2019-10-24, Thu, 15:30-16:30:<br/>
 * 1h gui: more logical structure<br/>
 * 
 * 2019-10-24, Thu, 18:00-19:00:<br/>
 * 1h refactoring view model and game model<br/>
 * 
 * 2019-10-24, Thu, 19:30-24:00:<br/>
 * 2h refactoring<br/>
 * 1h gui bugfixes<br/>
 * 1h gui more beautiful<br/>
 * 30m alpha-beta-pruning
 * 
 * 2019-10-25, Fri, 00:30-03:30:<br/>
 * 30m alpha-beta-pruning<br/>
 * 1h bugfixing<br/>
 * 1h added highlighting of winning stones<br/>
 * 1h running ai against ai
 * 
 * 2019-10-25, Fri, 11:00-16:00:<br/>
 * 3h added gui features and game setup features<br/>
 * 1h refactoring for added features<br/>
 * 1h bugfixes<br/>
 * 
 * shippable product is ready now<br/>
 * open tasks:<br/>
 * add platform: port to android<br/>
 * enhance ai: make ai win in as few moves as possible, not just win<br/>
 * enhance ai: heuristic should respect current player (important)<br/>
 * add feature: multiplayer over internet<br/>
 * add feature: game history, replay<br/>
 * 
 * 2019-10-26, Sat, 00:00-00:45:<br/>
 * 30m reading about custom views in android<br/>
 * 15m starting to port app to android<br/>
 * 
 * 2019-10-26, Sat, 11:00-13:00:<br/>
 * 2h porting app to android<br/>
 * 
 * 2019-10-28, Mon, 15:00-17:00:<br/>
 * 2h porting to android<br/>
 * 
 * 2019-10-28, Mon, 23:00-24:00:<br/>
 * 1h porting to android<br/>
 * 
 * 2019-10-29, Tue, 00:00-01:45:<br/>
 * 1h45m porting to android<br/>
 * 
 * 2019-10-29, Tue, 11:00-13:15:<br/>
 * 2h15m porting to android<br/>
 * 
 * 2019-10-29, Tue, 16:00-19:00<br/>
 * 3h porting to android<br/>
 * 
 * 2019-10-30, Wed, 10:30-17:00<br/>
 * 6h30m porting to android<br/>
 * 
 * learned for android: - how to create custom view classes and integrate them
 * into the layout xml<br/>
 * -- preparing how to draw -- drawing in onDraw() -- layout supporting with
 * onMeasrue() - fragments -- lifecycle -- how to layout -- how to integrate
 * into activity -- DialogFragment - RadioButton and RadioGroup - Spinner
 * 
 * 2019-10-30, Wed, 20:15-24:00<br/>
 * 1h refactoring<br/>
 * 2h45 porting to android<br/>
 * 
 * shippable product for android is ready now missing: - author/copyright info -
 * icons - text for play store - website
 * 
 * 2019-10-31, Thu, 00:00-00:30<br/>
 * 30m made icons<br/>
 * 
 * 2019-10-31, Thu, 12:00-20:00<br/>
 * 8h trying alternative approaches for game models and winner/potentials check
 * in order to improve performance<br/>
 * 
 * The alternative approaches turned out to be slower than the existing models
 * and algorithms, which startles me. Even the 6x7.implementation in just one
 * 64bit integer is slower than the not so optimal two-dimensional array
 * implementation.
 * 
 * 2019-10-31, Thu, 21:00-22:15<br/>
 * 1h15m further trying out to improve performance. i can't believe that the
 * 64bit integer implementation is intrinsically slower.<br/>
 * 
 * 2019-11-01, Fri, 11:00-13:00<br/>
 * 2h refactoring and trying new game models<br/>
 * 
 * 2019-11-01, Fri, 14:00-24:00<br/>
 * 4h refactoring game models and making Ai better<br/>
 * 6h debugging, wasted<br/>
 * 
 * 2019-11-02, Sat, 00:00-00:45<br/>
 * 45m wasted with debugging. i couldn't figure out that problem with ghostly
 * changing values of game model while being accessed by just one thread. late
 * cache coherence updates? memory semantics? very unsatisfying.<br/>
 * 
 * 2019-11-02, Sat, 12:15-13:30<br/>
 * 1h15m debugging. found the problem. it was a rather complex connection of
 * multiple circumstances which i used to trick myself: the WinnerDetector was
 * not thread-safe. a timer for regular UI updates had its own thread. through a
 * sloppy implementation of a getter in a game model, the ui update thread
 * accessed the WinnerDetector, which then caused a race condition between the
 * ui thread and the background ai thread. solution: make the WinnerDetector
 * thread safe.
 * 
 * 2019-11-02, Sat, 14:15-16:00<br/>
 * 1h45 bugfixing, refactoring, migrating to maven<br/>
 * 
 * 2019-11-03, Sun, 14:45-15:45<br/>
 * 1h finished migrating to maven<br/>
 * 
 * 2019-11-04, Mon, 12:00-12:30<br/>
 * 30m tests, bugfixing<br/>
 * 
 * 2019-11-05, Tue, 19:30-23:00<br/>
 * 3h30 refactoring, bugfixing<br/>
 * 
 * 2019-11-06, Wed, 13:30-14:30<br/>
 * 1h refactoring, bugfixing<br/>
 * 
 * 2019-11-07, Thu, 15:30-16:30<br/>
 * 30m refactoring<br/>
 * 30m testing other game implementations</br>
 * 
 * 2019-11-08, Fri, 17:00-18:00<br/>
 * 1h continued game impl tests (no luck in terms of speed increase)<br/>
 * 
 * 2019-11-09, Sat, 14:00-16:30<br/>
 * 2h30m refactoring: split ai into bare ai type and background thread
 * wrapper<br/>
 * 
 * 2019-11-10, Sun, 15:30-16:30<br/>
 * 1h ai refactoring and subsequent bugfixing<br/>
 * 
 * 2019-11-14, Thu, 15:00-16:00<br/>
 * 1h ai refactoring and subsequent bugfixing<br/>
 * 
 * 2019-11-15, Fri, 14:00-15:00<br/>
 * 1h ai refactoring and subsequent bugfixing<br/>
 * 
 * 2019-11-19, Tue, 16:30-17:30<br/>
 * 1h ai refactoring and subsequent bugfixing<br/>
 * 
 * 2019-11-20, Wed, 12:00-14:00<br/>
 * 2h ai refactoring and subsequent bugfixing<br/>
 * 
 * 2019-11-20, Wed, 18:00-21:00<br/>
 * 2h finished ai refactoring and subsequent bugfixing<br/>
 * 1h started deep learning based ai<br/>
 * 
 * 2019-11-21, Thu, 13:00-18:00<br/>
 * 5h fixing bug in alpha-beta-pruning. it was only alpha-pruning previously. no
 * beta.<br/>
 * 
 * 2019-11-21, Thu, 19:00-22:00<br/>
 * 3h started to make ai win faster and loose slower<br/>
 * 
 * 2019-11-21, Thu, 19:00-22:00<br/>
 * 3h started making ai win quickly and loose slowly<br/>
 * 
 * 2019-11-22, Fri, 14:00-15:00<br/>
 * 30m finished making ai win quickly and loose slowly<br/>
 * 
 * 2019-11-22, Fri, 15:15-17:45<br/>
 * 2h30m continued deep learning ai<br/>
 * 
 * 2019-11-26, Tue, 13:00-14:00<br/>
 * 1h maths on paper for deep learning ai<br/>
 * 
 * 2019-11-27, Wed, 10:30-11:30<br/>
 * 1h maths on paper for deep learning ai<br/>
 * 
 * 2019-11-28, Thu, 15:30-18:00<br/>
 * 2h30m deep learning ai<br/>
 * 
 * 2019-11-28, Thu, 18:30-19:00<br/>
 * 2h30m deep learning ai<br/>
 * 
 * 2019-11-29, Fri, 14:20-19:00<br/>
 * 3h deep learning ai<br/>
 * 
 * 2019-12-02, Mon, 13:00-13:30<br/>
 * 30m stats for deep learning<br/>
 * 
 * 2019-12-02, Mon, 18:46-21:38<br/>
 * 3h tests and bugfixing for deep learning<br/>
 * 
 * 2019-12-05, Thu, 15:00-19:00<br/>
 * 4h visualization for deep learning results and testing various topologies;
 * the deep learning nets don't behave consistently. some networks learn well,
 * but slightly bigger networks don't learn at all or they learn extremely
 * slowly. i think that there is a bug.<br/>
 * 
 * 2019-12-06, Fri, 13:30-16:00<br/>
 * 2h30m reading about and testing with different activation functions<br/>
 * 
 * 2019-12-07, Sat, ?<br/>
 * 1h deep learning debugging<br/>
 * 
 * 2019-12-08, Sun, 19:00-22:00<br/>
 * 3h neural net visualization for finding bugs<br/>
 * 
 * 2019-12-09, Mon, 11:45-16:45<br/>
 * 2h neural net visualization for finding bugs<br/>
 * 1h added more vector functions to mini-num lib<br/>
 * 2h changing mini-num lib to use pluggable generic vector functions and
 * subsequent refactoring<br/>
 * 
 * 2019-12-09, Mon, 18:10-20:40<br/>
 * 2h30m neural net visualization for searching bugs<br/>
 * 
 * 2019-12-10, Tue, 13:15-15:30<br/>
 * 2h15m neural net bug search<br/>
 * 
 * 2019-12-10, Tue, 19:30-22:00<br/>
 * 2h30m neural net bug search<br/>
 * 
 * 2019-12-11, Wed, 11:00-11:30<br/>
 * 30m neural net bug search<br/>
 * 
 * 2019-12-11, Wed, 14:30-22:30<br/>
 * 6h neural net bug search<br/>
 * 
 * 2019-12-11, Wed, 23:15-23:45<br/>
 * 30m neural net bug search<br/>
 * 
 * 2019-12-12, Thu, 11:15-11:45<br/>
 * 30m neural net bug search<br/>
 * 
 * 2019-12-13, Fri, 10:00-14:00<br/>
 * 2h neural net bug search: implemented gradient checking; gradients seem
 * wrong<br/>
 * 
 * 2019-12-14, Sat, 11:00-13:00<br/>
 * 2h neural net bug search: using tensorflow to verify my own code<br/>
 * 
 * 2019-12-15, Sun, 13:15-16:45<br/>
 * 3h30m neural net bug search: tensorflow gradients are exactly as my
 * analytical gradients, so my initial gradient code was right; the numerical
 * gradients, implemented for checking, were wrong, probably due to precision
 * problems<br/>
 * 
 * 2019-12-15, Sun, 17:00-17:20<br/>
 * 20m neural net bug search<br/>
 * 
 * 2019-12-15, Sun, 18:00-19:20<br/>
 * 20m neural net bug search<br/>
 * 
 * 2019-12-16, Mon, 09:50-12:20<br/>
 * 2h30m neural net bug search: found bug: biases after matrix transforms were
 * missing, but they are required. there was no mistake in existing code.
 * instead, required code was missing.<br/>
 * 
 * 2019-12-16, Mon, 12:30-16:30<br/>
 * 4h enhancing and cleaning deep learning code<br/>
 * 
 * 2019-12-18, Wed, 09:30-16:30<br/>
 * 7h enhancing and cleaning deep learning code; started implementing
 * dl-graph-api<br/>
 * 
 * 2019-12-19, Thu, 12:00-15:00<br/>
 * 3h implementing graph-api<br/>
 * 
 * 2019-12-19, Thu, 16:00-18:00<br/>
 * 2h implementing graph-api<br/>
 * 
 * 2019-12-20, Fri, 10:40-15:40<br/>
 * 5h implementing graph-api<br/>
 * 
 * 2019-12-22, Sun, 11:00-12:00<br/>
 * 1h implementing graph-api<br/>
 * 
 * 2019-12-22, Sun, 13:00-15:00<br/>
 * 2h implementing graph-api<br/>
 * 
 * 2019-12-23, Mon, 15:30-19:30<br/>
 * 4h implementing graph-api<br/>
 * 
 * 2019-12-25, Wed, 13:30-16:30<br/>
 * 3h implementing graph-api<br/>
 * 
 * 2019-12-25, Wed, 13:30-16:30<br/>
 * 3h implementing graph-api<br/>
 * 
 * 2019-12-29, Sun, 14:00-18:00<br/>
 * 4h implementing deep learning graph-api<br/>
 * 
 * 2019-12-30, Mon, 18:00-21:00<br/>
 * 3h implementing deep learning graph-api<br/>
 * 
 * 2019-12-30, Mon, 22:30-24:00<br/>
 * 1h30m implementing deep learning graph-api; tanh+sigmoid working fine<br/>
 * 
 * 2019-12-31, Tue, 12:00-13:00<br/>
 * 1h implementing deep learning graph-api; relu and identity working as
 * expected<br/>
 * 
 * 2020-01-01, Wed, 17:50-21:50<br/>
 * 4h graph-api: compiler for operation graph to java source<br/>
 * 
 * 2020-01-02, Thu, 11:30-17:00<br/>
 * 5h30m graph-api: compiler for operation graph to java source<br/>
 * 
 * 2020-01-02, Thu, 18:30-24:00<br/>
 * 5h30m graph-api: in-ram-compiler for java source code<br/>
 * 
 * 2020-01-03, Fri, 12:00-14:00<br/>
 * 2h graph-api: in-ram-compiler for java source code<br/>
 * 
 * 2020-01-03, Fri, 15:00-16:00<br/>
 * 1h graph-api: in-ram-compiler for java source code<br/>
 * 
 * 2020-01-04, Sat, 18:05-18:50<br/>
 * 45m graph-api: in-ram-compiler for java source code<br/>
 * 
 * 2020-01-06, Mon, 09:05-09:50<br/>
 * 45m graph-api: in-ram-compiler for java source code; compiling an operation
 * on the fly and loading it into the jvm works now. the operation to java is
 * not complete though<br/>
 * 
 * 2020-01-07, Tue, 13:45-15:15<br/>
 * 1h30m graph-api: compiler for operation graph to java source<br/>
 * 
 * 2020-01-07, Tue, 17:00-20:45<br/>
 * 3h45m graph-api: compiler for operation graph to java source<br/>
 * 
 * 2020-01-08, Wed, 12:00-16:30<br/>
 * 4h30 graph-api: compiler for operation graph to java source<br/>
 * 
 * 2020-01-08, Wed, 20:45-24:00<br/>
 * 3h15 graph-api: compiler for operation graph to java source<br/>
 * 
 * 2020-01-09, Thu, 00:00-02:15<br/>
 * 2h15 graph-api: compiler for operation graph to java source<br/>
 * 
 * 2020-01-10, Fri, 09:30-10:30<br/>
 * 1h graph-api: compiler for operation graph to java source<br/>
 * 
 * 2020-01-10, Fri, 14:00-17:00<br/>
 * 3h graph-api: compiler for operation graph to java source<br/>
 * 
 * 2020-01-11, Sat, 12:00-15:30<br/>
 * 3h30m graph-api: compiler for operation graph to java source<br/>
 * 
 * 2020-01-11, Sat, 18:00-20:15<br/>
 * 2h15m graph-api: compiler for operation graph to java source<br/>
 * 
 * 2020-01-12, Sun, 12:15-14:15<br/>
 * 2h graph-api: compiler for operation graph to java source<br/>
 * 
 * 2020-02-07, Fri, 12:30-17:30<br/>
 * 5h graph-api: compiler for operation graph to java source<br/>
 * 
 * 2020-02-10, Mon, 09:30-15:30<br/>
 * 6h graph-api: compiler for operation graph to java source<br/>
 * 
 * 2020-02-12, Wed, 18:10-21:10<br/>
 * 3h graph-api: compiler for operation graph to java source<br/>
 * 
 * 2020-02-13, Thu, 10:30-13:00<br/>
 * 2h30m graph-api: compiler for operation graph to java source<br/>
 * 
 * 2020-02-13, Thu, 14:00-21:30<br/>
 * 2h graph-api: compiler for operation graph to java source<br/>
 * 
 * 2020-02-14, Fri, 18:30-20:00<br/>
 * 1h30m graph-api: compiler for operation graph to java source<br/>
 * 
 * 2020-02-15, Sat, 12:30-18:30<br/>
 * 6h graph-api: compiler for operation graph to java source<br/>
 * 
 * 2020-02-16, Sun, 15:00-17:00<br/>
 * 2h graph-api: compiler for operation graph to java source<br/>
 * 
 * 2020-02-17, Mon, 08:15-08:45<br/>
 * 0h30m graph-api: compiler for operation graph to java source is feature
 * complete<br/>
 * 
 * 2020-02-19, Wed, 16:00-18:00<br/>
 * 0h30m graph-api: compiler for operation graph to java source; bugfix<br/>
 * 
 * 2020-02-21, Fri, 13:30-15:00<br/>
 * 1h30m graph-api: compiler for operation graph to java source; bugfix<br/>
 * 
 * 2020-02-21, Fri, 15:00-16:00<br/>
 * 1h using mini-deeplearn for connect four AI<br/>
 * 
 * 2020-02-29, Sat, 13:00-14:00<br/>
 * 1h using mini-deeplearn for connect four AI<br/>
 * 
 * 2020-03-02, Mon, 15:00-18:00<br/>
 * 3h using mini-deeplearn for connect four AI<br/>
 * 
 * 2020-03-04, Wed, 15:00-17:00<br/>
 * 2h using mini-deeplearn for connect four AI<br/>
 * 
 * 2020-03-24, Tue, 16:45-<br/>
 * h using mini-deeplearn for connect four AI<br/>
 */
@SuppressWarnings("serial")
public class QuadrupleDesktopApp extends Frame implements AiExecutionListener, WindowListener, ActionListener {

	/**
	 * The model of the game that we are going to display, or game state.
	 */
	Game mGame;

	/**
	 * View model of how the game is set up, or gui state.
	 */
	GuiState mGuiState;

	protected static final String EMOJI_HAPPY = "😊";
	protected static final String EMOJI_WAITING = "⌛️";

	protected static final String FORMAT_PLAYER_WON = "Player %s won! 😊";
	protected static final String FORMAT_PLAYER_DRAWS = "Player %s draws. %s";
	protected static final String FORMAT_NO_GAME = "😊 Click Start! 😊";

	protected SimpleDateFormat ELAPSED_TIME_FORMAT = new SimpleDateFormat("HH:mm:ss.SSS");

	Timer mElapsedTimeUpdater = new Timer("elapsedTimeUpdater");

	FieldView mFieldView;
	PlayerPanel playerLPnl = new PlayerPanel(true);
	PlayerPanel playerRPnl = new PlayerPanel(false);
	Label statusLbl = new Label(" Player 1 won! ", Label.CENTER);
	Panel toolbar = new Panel();

	Button startStopBtn = new Button("Start");
	Button switchColorsBtn = new Button("Switch Colors");
	Button switchPlayersBtn = new Button("<->");
	Button switchStartingPlayerBtn = new Button("Switch Starting Player");

	ReentrantLock mGameSetupLock = new ReentrantLock();

	Ai mAiL;
	Ai mAiR;
	AsyncAiExecutor asyncAiExecutor;

	public static void main(String[] args) {
		new QuadrupleDesktopApp().instanceMain(args);
	}

	@Override
	public void update(Graphics g) {
	}

	@Override
	public void paint(Graphics g) {
	}

	private void instanceMain(String[] args) {
		ELAPSED_TIME_FORMAT.setTimeZone(TimeZone.getTimeZone("GMT"));

		mGame = new GameImplIntArray1D(6, 7);

//		String problem = "" + //
//				"own_color: x, current_player: x\n" + //
//				"0 xx____x\n" + //
//				"1 oo____o\n" + //
//				"2 xoo___x\n" + //
//				"3 ooo___o\n" + //
//				"4 xxo___x\n" + //
//				"5 xo■■■_o\n" + //
//				"  0123456\n" //
//		;
//		mGame = Format.INSTANCE.parse(problem);

		String problem = "" + //
				"current_player: x\n" + //
				"0 _______\n" + //
				"1 __x____\n" + //
				"2 __o_o__\n" + //
				"3 x_ooxx_\n" + //
				"4 oxooxo_\n" + //
				"5 oxxxox_\n" + //
				"  0123456\n" //
		;
		mGame = Format.INSTANCE.parse(problem);

		mGuiState = new GuiState();

		mAiL = new AbPruningAi(6);
		mAiR = new AbPruningAi(9);

		asyncAiExecutor = new AsyncAiExecutor();

		asyncAiExecutor.listener = this;

		mFieldView = new FieldView(mGame, mGuiState, this);

		setTitle("Quadruple");

		GridBagConstraints gbc = new GridBagConstraints();

		setFont(getScaledFont(1.2f, Font.BOLD));
		statusLbl.setFont(getScaledFont(2f, Font.BOLD));
		statusLbl.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 36));

		playerLPnl.colorVw.ringColor = FieldView.COLOR_FRAME;
		playerRPnl.colorVw.ringColor = FieldView.COLOR_FRAME;

		toolbar.setLayout(new GridBagLayout());
		add(new Canvas(), toolbar, gbc, 0, 0, 1, 1, 100, 0, GridBagConstraints.BOTH);
		add(startStopBtn, toolbar, gbc, 1, 0, 1, 1, 0, 0, GridBagConstraints.BOTH);
		add(switchColorsBtn, toolbar, gbc, 2, 0, 1, 1, 0, 0, GridBagConstraints.BOTH);
		add(switchPlayersBtn, toolbar, gbc, 3, 0, 1, 1, 0, 0, GridBagConstraints.BOTH);
		add(switchStartingPlayerBtn, toolbar, gbc, 4, 0, 1, 1, 0, 0, GridBagConstraints.BOTH);
		add(new Canvas(), toolbar, gbc, 5, 0, 1, 1, 100, 0, GridBagConstraints.BOTH);

		setLayout(new GridBagLayout());
		add(mFieldView /* ... */, this, gbc, 0, 0, 3, 1, 0, 100, GridBagConstraints.BOTH);
		add(playerLPnl /* . */, this, gbc, 0, 1, 1, 1, 0, 0, GridBagConstraints.BOTH);
		add(statusLbl /* .. */, this, gbc, 1, 1, 1, 1, 100, 0, GridBagConstraints.BOTH);
		add(playerRPnl /* . */, this, gbc, 2, 1, 1, 1, 0, 0, GridBagConstraints.BOTH);
		add(toolbar /* .... */, this, gbc, 0, 2, 3, 1, 0, 0, GridBagConstraints.BOTH);

		updatePlayerLblColors();
		updatePlayerLblTexts();
		updatePlayerTypeBtns();
		updateStatusLbl();

		pack();

		Dimension scrSize = Toolkit.getDefaultToolkit().getScreenSize();
		int w = getWidth();
		int h = getHeight();
		setBounds((scrSize.width - w) / 2, (scrSize.height - h) / 2, w, h);

		addWindowListener(this);
		startStopBtn.addActionListener(this);
		switchColorsBtn.addActionListener(this);
		switchPlayersBtn.addActionListener(this);
		switchStartingPlayerBtn.addActionListener(this);

		playerLPnl.typeBtn.addActionListener(this);
		playerRPnl.typeBtn.addActionListener(this);

		setVisible(true);
	}

	protected static Font defaultFont = null;

	protected static Font getDefaultFont() {
		if (defaultFont == null) {
			Frame dummy = new Frame();
			dummy.setBounds(0, 0, 0, 0);
			dummy.setVisible(true);
			Graphics g = dummy.getGraphics();

			defaultFont = g.getFont();

			g.dispose();
			dummy.setVisible(false);
			dummy.dispose();
		}

		return defaultFont;
	}

	protected static Font getScaledFont(float factor, int additionalStyle) {
		Font defaultFont = getDefaultFont();

		int size = Math.round(defaultFont.getSize() * factor);
		int style = defaultFont.getStyle() | additionalStyle;
		Font font = new Font(defaultFont.getName(), style, size);

		return font;
	}

	public static void add(Component cmp, Container cnt, GridBagConstraints gbc, //
			int gridx, int gridy, int gridwidth, int gridheight, int weightx, int weighty, int fill) {
		gbc.gridx = gridx;
		gbc.gridy = gridy;
		gbc.gridwidth = gridwidth;
		gbc.gridheight = gridheight;
		gbc.weightx = weightx;
		gbc.weighty = weighty;
		gbc.fill = fill;

		cnt.add(cmp, gbc);
	}

	private void updateStatusLbl() {
		String text;

		switch (mGuiState.mPhase) {
		case BEFORE_GAME:
			text = FORMAT_NO_GAME;
			break;

		case RUNNING:
			Player currPlayer = Player.forIdx(mGame.getCurrentPlayer());
			String currPlayerStr = Integer.toString(currPlayer.idx + 1);
			text = String.format(FORMAT_PLAYER_DRAWS, currPlayerStr, EMOJI_WAITING);
			break;

		case ENDED:
			Player winner = Player.forIdx(mGame.determineWinner());
			String winnerStr = Integer.toString(winner.idx + 1);
			text = String.format(FORMAT_PLAYER_WON, winnerStr);
			break;

		default:
			throw new IllegalStateException("unknown GamePhase: " + mGuiState.mPhase);
		}

		statusLbl.setText(text);
	}

	private void updatePlayerTypeBtns() {
		playerLPnl.typeBtn.setLabel(mGuiState.getPlayerType(PlayerSide.LEFT).toString());
		playerRPnl.typeBtn.setLabel(mGuiState.getPlayerType(PlayerSide.RIGHT).toString());
	}

	private void updatePlayerLblColors() {
		Color guiColor;
		PlayerColor playerColor;

		playerColor = mGuiState.getPlayerColor(PlayerSide.LEFT);
		guiColor = mFieldView.getGuiColor(playerColor, false);
		playerLPnl.colorVw.setForeground(guiColor);

		playerColor = mGuiState.getPlayerColor(PlayerSide.RIGHT);
		guiColor = mFieldView.getGuiColor(playerColor, false);
		playerRPnl.colorVw.setForeground(guiColor);
	}

	private void updatePlayerLblTexts() {
		Player player;
		String text;

		player = mGuiState.getPlayer(PlayerSide.LEFT);
		text = GuiState.PLAYER_LABEL_TEXT[player.idx];
		playerLPnl.ordinalLbl.setText(text);

		player = mGuiState.getPlayer(PlayerSide.RIGHT);
		text = GuiState.PLAYER_LABEL_TEXT[player.idx];
		playerRPnl.ordinalLbl.setText(text);
	}

	@Override
	public void actionPerformed(ActionEvent e) {

		boolean lockSuccessful = false;
		try {
			lockSuccessful = mGameSetupLock.tryLock(1, TimeUnit.MILLISECONDS);
			if (lockSuccessful) {
				actionPerformed_gameSetupLocked(e);
			}
		} catch (InterruptedException ignored) {
		} finally {
			if (lockSuccessful) {
				mGameSetupLock.unlock();
			}
		}
	}

	private void actionPerformed_gameSetupLocked(ActionEvent e) {
		Object src = e.getSource();

		if (src == startStopBtn) {
			restartGame();

		} else if (src == switchColorsBtn) {
			mGuiState.switchPlayerColor();
			updatePlayerLblColors();
			mFieldView.paintStones();

		} else if (src == playerLPnl.typeBtn || src == playerRPnl.typeBtn) {
			PlayerSide playerSide = (src == playerLPnl.typeBtn) ? PlayerSide.LEFT : PlayerSide.RIGHT;
			mGuiState.changePlayerType(playerSide);
			updatePlayerTypeBtns();

		} else if (src == switchPlayersBtn) {
			mGuiState.switchPlayers();
			updatePlayerLblColors();
			updatePlayerTypeBtns();
			updatePlayerLblTexts();
			updateStatusLbl();

		} else if (src == switchStartingPlayerBtn) {
			mGuiState.switchStartingPlayer();
			updatePlayerLblTexts();

		}
	}

	private void restartGame() {
		asyncAiExecutor.cancel();

		if (!(mGuiState.mPhase == GamePhase.BEFORE_GAME && mGame.isRunning())) {
			mGame.restart();
		}

		mGuiState.mPhase = GamePhase.RUNNING;
		mGuiState.mElapsedTime[0] = 0;
		mGuiState.mElapsedTime[1] = 0;
		mGuiState.mLastMoveStartTime = System.currentTimeMillis();

		mElapsedTimeUpdater.schedule(new TimerTask() {
			@Override
			public void run() {
				Player currPlayer = Player.forIdx(mGame.getCurrentPlayer());

				if (currPlayer == Player.NONE) {
					cancel();
					return;
				}

				PlayerSide currSide = mGuiState.getPlayerSide(currPlayer);

				if (currSide == PlayerSide.LEFT) {
					updateLeftTimeTf();
				} else {
					updateRightTimeTf();
				}
			}
		}, 0, 123);

		mFieldView.clearHighlights();
		mFieldView.repaint();

		postMoveActions(0, 0);
	}

	public void onStoneLocationClicked(int i, int j) {
		if (mGame.isFull(j)) {
			return;
		}

		if (mGame.getCurrentPlayer() != Game.PLAYER_NONE) {
			PlayerType currPlayerType = mGuiState.mPlayerType[mGame.getCurrentPlayer()];

			if (currPlayerType == PlayerType.HUMAN) {
				makeMove(j);

			} else {
				// TODO: warn that AI is thinking
			}
		} else {
			// TODO: ask if game shall be restarted
		}
	}

	private void makeMove(int columnIdx) {
		long currTime = System.currentTimeMillis();
		long lastMoveDuration = currTime - mGuiState.mLastMoveStartTime;
		mGuiState.mLastMoveStartTime = currTime;

		mGuiState.mElapsedTime[mGame.getCurrentPlayer()] += lastMoveDuration;

		mGame.move(columnIdx);

		int rowIdx = mGame.getH() - mGame.getStoneCount(columnIdx);

		postMoveActions(rowIdx, columnIdx);
	}

	private void postMoveActions(int i, int j) {
		int winner = mGame.determineWinner();

		if (winner != Game.PLAYER_NONE) {
			markWinningStones();
			mGuiState.mPhase = GamePhase.ENDED;

		} else if (mGame.isFull()) {
			System.out.println("game ended in a draw");
			mGuiState.mPhase = GamePhase.ENDED;
		}

		updateStatusLbl();
		updateTimeTfs();
		mFieldView.paintStone(i, j);

		Player currPlayer = Player.forIdx(mGame.getCurrentPlayer());
		if (currPlayer != Player.NONE && mGuiState.mPlayerType[currPlayer.idx] == PlayerType.AI) {
			aiFindMoveAsync();
		}
	}

	private void updateTimeTfs() {
		updateLeftTimeTf();
		updateRightTimeTf();
	}

	private void updateLeftTimeTf() {
		Player player = mGuiState.getPlayer(PlayerSide.LEFT);
		String text = getElapsedTimeStr(player);
		playerLPnl.timeTf.setText(text);
	}

	private void updateRightTimeTf() {
		Player player = mGuiState.getPlayer(PlayerSide.RIGHT);
		String text = getElapsedTimeStr(player);
		playerRPnl.timeTf.setText(text);
	}

	protected String getElapsedTimeStr(Player player) {
		long elapsedTime = mGuiState.mElapsedTime[player.idx];

		if (player.idx == mGame.getCurrentPlayer()) {
			long currTime = System.currentTimeMillis();
			elapsedTime += currTime - mGuiState.mLastMoveStartTime;
		}

		String text = ELAPSED_TIME_FORMAT.format(elapsedTime);

		return text;
	}

	private void markWinningStones() {
		WinnerDetector wd = WinnerDetector.INSTANCE;

		wd.setGame(mGame);
		FieldIterator.INSTANCE.performFullIteration(wd, mGame.getH(), mGame.getW());

		int iDir = wd.mPrevI - wd.mCurrI;
		int jDir = wd.mPrevJ - wd.mCurrJ;

		for (int k = 0; k < 4; k++) {
			Loc loc = mFieldView.highlightLoc[k];

			loc.i = wd.mCurrI + k * iDir;
			loc.j = wd.mCurrJ + k * jDir;

			mFieldView.paintStone(loc.i, loc.j);
		}
	}

	private void aiFindMoveAsync() {
		Ai ai;

		PlayerSide currPlayerSide = mGuiState.getPlayerSide(Player.forIdx(mGame.getCurrentPlayer()));
		if (currPlayerSide == PlayerSide.LEFT) {
			ai = mAiL;
		} else {
			ai = mAiR;
		}

		Game copy = mGame.copy();
		asyncAiExecutor.startGetBestMove(copy, ai);
	}

	@Override
	public void aiMoveSearchStarted(Ai ai) {
	}

	@Override
	public void aiMoveSearchFinished(Ai ai) {
		int j = asyncAiExecutor.getBestMove();

		if (j == Ai.MOVE_NONE) {
			throw new IllegalStateException("no move found");

		} else {
			makeMove(j);
		}
	}

	@Override
	public void aiMoveSearchCancelled(Ai ai) {
		System.out.println("aiMoveSearchCancelled");
	}

	@Override
	public void aiMoveSearchProgressed(Ai ai, int i, int j) {
		mFieldView.paintStones();
	}

	@Override
	public void windowClosing(WindowEvent e) {
		System.exit(0);
	}

	@Override
	public void windowActivated(WindowEvent e) {
	}

	@Override
	public void windowClosed(WindowEvent e) {
	}

	@Override
	public void windowDeactivated(WindowEvent e) {
	}

	@Override
	public void windowDeiconified(WindowEvent e) {
	}

	@Override
	public void windowIconified(WindowEvent e) {
	}

	@Override
	public void windowOpened(WindowEvent e) {
	}
}
