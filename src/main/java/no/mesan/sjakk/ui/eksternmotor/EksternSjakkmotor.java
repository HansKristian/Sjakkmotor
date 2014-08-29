package no.mesan.sjakk.ui.eksternmotor;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.UUID;

public class EksternSjakkmotor {
	private enum MotorStatus {
		STOPPET, STARTET
	};

	private final String command;
	private BufferedReader bufferedReader;
	private PrintStream printStream;
	private String navn;
	private String lagetAv;
	private SjakkmotorLytter sjakkmotorLytter;
	private final String motorId;
	private MotorStatus motorStatus;
	private final boolean lokalKlasse;

	public EksternSjakkmotor(final String command, final boolean lokalKlasse) {
		this.lokalKlasse = lokalKlasse;
		this.motorStatus = MotorStatus.STOPPET;
		this.command = command;
		this.navn = command;
		motorId = UUID.randomUUID().toString();
	}

	public void setSjakkmotorLytter(final SjakkmotorLytter sjakkmotorLytter) {
		this.sjakkmotorLytter = sjakkmotorLytter;
	}

	public String motorId() {
		return motorId;
	}

	public String navn() {
		return navn;
	}

	public void startMotor() {
		motorStatus = MotorStatus.STARTET;
		try {
			final Process process = createProcess(command);
			final InputStream inputStream = process.getInputStream();
			final OutputStream outputStream = process.getOutputStream();
			bufferedReader = new BufferedReader(new InputStreamReader(
					inputStream));
			printStream = new PrintStream(outputStream);
			startMotorLytter();
			initierMotor();
		} catch (final IOException e) {
			e.printStackTrace();
		}
	}

	private Process createProcess(final String command) throws IOException {
		if (erLokalKlasse()) {
			final String classpath = buildLocalClasspath();
			return Runtime.getRuntime().exec(
					"java -cp " + classpath + " " + command);
		} else if (command.endsWith(".jar")) {
			return Runtime.getRuntime().exec("java -jar " + command);
		}

		return Runtime.getRuntime().exec(command);
	}

	private String buildLocalClasspath() {
		final StringBuilder stringBuilder = new StringBuilder();
		for (final URL url : ((URLClassLoader) (Thread.currentThread()
				.getContextClassLoader())).getURLs()) {
			stringBuilder.append(new File(url.getPath()));
			stringBuilder.append(System.getProperty("path.separator"));
		}
		final String classpath = stringBuilder.toString();
		return classpath;
	}

	private void startMotorLytter() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					String line = null;
					while ((line = bufferedReader.readLine()) != null) {
						behandleKommandoFraMotor(line);
					}
				} catch (final IOException e) {
					if (motorStatus != MotorStatus.STOPPET) {
						e.printStackTrace();
					}
				}
			}
		}).start();
	}

	private void behandleKommandoFraMotor(final String kommando) {
		sjakkmotorLytter.meldingMottat(motorId, kommando);

		if (kommando.startsWith("id")) {
			behandleIdFraMotor(kommando);
		} else if (kommando.trim().equals("uciok")) {
			behandleUciokFraMotor();
		} else if (kommando.trim().equals("readyok")) {
			behandleReadyokFraMotor();
		} else if (kommando.startsWith("info")) {
			behandleInfoFraMotor(kommando);
		} else if (kommando.startsWith("bestmove")) {
			behandleBestmoveFraMotor(kommando);
		}
	}

	private void behandleReadyokFraMotor() {
		sjakkmotorLytter.motorKlar(motorId);
	}

	private void behandleUciokFraMotor() {
		skrivKommandoTilMotor("isready");
	}

	private void behandleIdFraMotor(final String kommando) {
		if (kommando.startsWith("id name")) {
			navn = kommando.substring("id name".length()).trim();
			sjakkmotorLytter.navnMottatt(motorId, navn);
		} else if (kommando.startsWith("id author")) {
			lagetAv = kommando.substring("id author".length()).trim();
			sjakkmotorLytter.lagetAvMottatt(motorId, lagetAv);
		}
	}

	private void behandleBestmoveFraMotor(final String kommando) {
		final String[] strings = kommando.split(" ");
		final BesteTrekk besteTrekk = new BesteTrekk(strings[1]);
		sjakkmotorLytter.besteTrekkMottatt(motorId, besteTrekk);
	}

	private void behandleInfoFraMotor(final String kommando) {
		final String[] strings = kommando.split(" ");
		for (int i = 0; i < strings.length; i++) {
			if (strings[i].equals("cp")) {
				final Vurdering vurdering = new Vurdering(false,
						Integer.parseInt(strings[i + 1]));
				sjakkmotorLytter.vurderingMottatt(motorId, vurdering);
			} else if (strings[i].equals("mate")) {
				final Vurdering vurdering = new Vurdering(true,
						Integer.parseInt(strings[i + 1]));
				sjakkmotorLytter.vurderingMottatt(motorId, vurdering);
			}
		}
	}

	private void skrivKommandoTilMotor(final String kommando) {
		sjakkmotorLytter.meldingSendt(motorId, kommando);
		printStream.append(kommando + "\n");
		printStream.flush();
	}

	private void initierMotor() {
		skrivKommandoTilMotor("uci");
	}

	public void stoppMotor() {
		motorStatus = MotorStatus.STOPPET;
		skrivKommandoTilMotor("quit");
		printStream.close();
		try {
			bufferedReader.close();
		} catch (final IOException e) {
			e.printStackTrace();
		}
	}

	public void stoppKalkulering() {
		skrivKommandoTilMotor("stop");
	}

	public void startKalkulering(final String fen, final long tid) {
		skrivKommandoTilMotor("position fen " + fen);
		skrivKommandoTilMotor("go movetime " + tid);
	}

	public void startUendeligKalkulering(final String fen) {
		skrivKommandoTilMotor("position fen " + fen);
		skrivKommandoTilMotor("go infinite");
	}

	@Override
	public String toString() {
		final StringBuilder builder = new StringBuilder();
		builder.append(navn);
		builder.append(" (");
		builder.append(lagetAv);
		builder.append(")");
		return builder.toString();
	}

	public boolean erLokalKlasse() {
		return lokalKlasse;
	}
}
