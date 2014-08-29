package no.mesan.sjakk.motor.grensesnitt;

import java.util.Timer;
import java.util.TimerTask;

import no.mesan.sjakk.motor.AbstraktSjakkmotor;
import no.mesan.sjakk.motor.Posisjon;
import no.mesan.sjakk.motor.Trekk;

import com.fluxchess.jcpi.AbstractEngine;
import com.fluxchess.jcpi.commands.EngineAnalyzeCommand;
import com.fluxchess.jcpi.commands.EngineDebugCommand;
import com.fluxchess.jcpi.commands.EngineInitializeRequestCommand;
import com.fluxchess.jcpi.commands.EngineNewGameCommand;
import com.fluxchess.jcpi.commands.EnginePonderHitCommand;
import com.fluxchess.jcpi.commands.EngineReadyRequestCommand;
import com.fluxchess.jcpi.commands.EngineSetOptionCommand;
import com.fluxchess.jcpi.commands.EngineStartCalculatingCommand;
import com.fluxchess.jcpi.commands.EngineStopCalculatingCommand;
import com.fluxchess.jcpi.commands.ProtocolBestMoveCommand;
import com.fluxchess.jcpi.commands.ProtocolInformationCommand;
import com.fluxchess.jcpi.commands.ProtocolInitializeAnswerCommand;
import com.fluxchess.jcpi.commands.ProtocolReadyAnswerCommand;
import com.fluxchess.jcpi.models.GenericMove;

public class Grensenitthaandtering extends AbstractEngine {

	private static final long EXTRA_TIMEOUT = 10;
	private Posisjon posisjon;
	private final AbstraktSjakkmotor sjakkmotor;

	public Grensenitthaandtering(final AbstraktSjakkmotor chessAI) {
		this.sjakkmotor = chessAI;
	}

	@Override
	public void receive(final EngineInitializeRequestCommand command) {
		this.posisjon = Posisjon.startposisjon();
		getProtocol().send(
				new ProtocolInitializeAnswerCommand(sjakkmotor.navn(),
						sjakkmotor.lagetAv()));
	}

	@Override
	public void receive(final EngineReadyRequestCommand command) {
		getProtocol().send(new ProtocolReadyAnswerCommand(""));
	}

	@Override
	public void receive(final EngineNewGameCommand command) {
		this.posisjon = Posisjon.startposisjon();
	}

	@Override
	public void receive(final EngineAnalyzeCommand command) {
		final String fen = command.board.toString();
		posisjon = new Posisjon(fen);
		for (final GenericMove genericMove : command.moveList) {
			final short move = Verktoy.convertGenericMoveToMove(genericMove,
					posisjon);
			posisjon.gjorTrekk(move);
		}
	}

	@Override
	public void receive(final EngineStartCalculatingCommand command) {
		final long searchTime = Verktoy.kalkulerSoeketid(command, posisjon);
		startEngineInThread(searchTime);
		startTimer(searchTime);
	}

	@Override
	public void receive(final EngineStopCalculatingCommand command) {
		hentBesteTrekkOgSendInfo();
	}

	@Override
	protected void quit() {
		sjakkmotor.besteTrekk();
		System.exit(0);
	}

	@Override
	public void receive(final EngineSetOptionCommand command) {
		// We have no options, yet.
	}

	@Override
	public void receive(final EngineDebugCommand command) {
		// We do not support debug, yet.
	}

	@Override
	public void receive(final EnginePonderHitCommand command) {
		// We do not support pondering, yet.
	}

	private void startEngineInThread(final long searchTime) {
		new Thread() {
			@Override
			public void run() {
				try {
					final Runnable r = new Runnable() {
						@Override
						public void run() {
							sjakkmotor.sok(posisjon);
						}
					};
					final TimeOut timeOut = new TimeOut(r, searchTime
							+ EXTRA_TIMEOUT, true);
					timeOut.execute();
				} catch (final InterruptedException e) {
				}
			}
		}.start();
	}

	private void startTimer(final long searchTime) {
		final Timer timer = new Timer();
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				hentBesteTrekkOgSendInfo();
			}
		}, searchTime);
	}

	private void hentBesteTrekkOgSendInfo() {
		final Trekk besteTrekk = sjakkmotor.besteTrekk();
		if (besteTrekk != null) {
			final GenericMove genericMove = Verktoy
					.moveToGenericMove(besteTrekk.move());
			getProtocol().send(new ProtocolBestMoveCommand(genericMove, null));
		} else {
			final ProtocolInformationCommand command = new ProtocolInformationCommand();
			command.setString("Obs! Fikk null som beste trekk fra sjakkmotor");
			getProtocol().send(command);
		}
	}
}
