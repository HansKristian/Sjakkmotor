package no.mesan.sjakk.ui;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BoundedRangeModel;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultBoundedRangeModel;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.PlainDocument;

import no.mesan.sjakk.motor.Brikke;
import no.mesan.sjakk.motor.Farge;
import no.mesan.sjakk.ui.chessboard.BrettModell;
import no.mesan.sjakk.ui.chessboard.BrikkeUI;
import no.mesan.sjakk.ui.eksternmotor.EksternSjakkmotor;
import no.mesan.sjakk.ui.eksternmotor.Motorbinge;
import no.mesan.sjakk.ui.eksternmotor.Vurdering;
import no.mesan.sjakk.ui.parti.Parti;
import no.mesan.sjakk.ui.parti.PartiLytter;

public class SjakkGuiController implements PartiLytter, ChangeListener {

	private static final int MAX_VURDERING = 2000;
	private static final int MIN_VURDERING = -2000;
	private final BrettModell model;
	private final BoundedRangeModel analyseModell;
	private final PlainDocument analyseDocument;
	private final BoundedRangeModel trekkIgjenModel;
	private final List<Logglytter> logglyttere = new ArrayList<Logglytter>();

	private boolean hvit = true;
	private AbstractAction startAction;
	private AbstractAction pauseAction;
	private AbstractAction stoppAction;

	private Parti parti;
	private final SpinnerNumberModel trekkTidModell;
	private final MotorComboBoxModel hvitMotorModell;
	private final MotorComboBoxModel svartMotorModell;
	private final MotorComboBoxModel analyseMotorModell;

	private final Motorbinge motorbinge;
	private EksternSjakkmotor analysemotor;
	private Action leggTilMotorAction;
	private final SpinnerNumberModel antallTrekkModell;
	
	private PartiResultatDialog resultatDialog;

	public SjakkGuiController() {
		this.model = lagDefaultBrettModell();
		this.analyseModell = new DefaultBoundedRangeModel(0, 1, MIN_VURDERING,
				MAX_VURDERING);
		analyseDocument = new PlainDocument();
		trekkIgjenModel = new DefaultBoundedRangeModel();
		trekkTidModell = new SpinnerNumberModel(1, 0.2, 1000, 0.2);
		trekkTidModell.addChangeListener(this);
		antallTrekkModell = new SpinnerNumberModel(999, 1, 999, 1);
		antallTrekkModell.addChangeListener(this);

		motorbinge = new Motorbinge();

		hvitMotorModell = new MotorComboBoxModel(motorbinge, false);
		svartMotorModell = new MotorComboBoxModel(motorbinge, false);
		analyseMotorModell = new MotorComboBoxModel(motorbinge, true);

		lagActions();
	}

	public BrettModell lagDefaultBrettModell() {
		final BrettModell modell = new BrettModell();
		return modell;
	}

	private void startParti() {
		if (parti != null && parti.erPauset()) {
			parti.start();
			return;
		}

		model.nyttParti();

		final EksternSjakkmotor hvitMotor = hvitMotorModell.getSelectedItem();
		final EksternSjakkmotor svarMotor = svartMotorModell.getSelectedItem();
		final EksternSjakkmotor analysemotor = analyseMotorModell
				.getSelectedItem();

		this.parti = new Parti(this, hvitMotor, svarMotor, analysemotor,
				hentTrekkTidMillis(), antallTrekkModell.getNumber().intValue());
		parti.start();
	}

	private void stoppParti() {
		this.parti.stopp();
	}

	private void pauseParti() {
		this.parti.pause();
	}

	@Override
	public void brikkeFlyttet(final int fraRute, final int tilRute,
			final String trekk) {
		final BrikkeUI brikke = model.hentBrikkePaaRute(fraRute);
		final BrikkeUI tattBrikke = model.hentBrikkePaaRute(tilRute);

		model.flyttBrikke(brikke, tilRute);

		// Rokader
		if (brikke.getBrikke() == Brikke.KONGE
				&& Math.abs(fraRute - tilRute) == 2) {

			// (tilRute > fraRute) er kort rokade, motsatt er lang
			final int taarnFraRute = tilRute > fraRute ? tilRute + 1
					: tilRute - 2;
			final int taarnTilRute = tilRute > fraRute ? tilRute - 1
					: tilRute + 1;
			final BrikkeUI taarn = model.hentBrikkePaaRute(taarnFraRute);
			model.flyttBrikke(taarn, taarnTilRute);
		}

		// En passant
		if (brikke.getBrikke() == Brikke.BONDE && tattBrikke == null
				&& Math.abs(fraRute - tilRute) % 8 != 0) {
			final int tattBondeRute = tilRute > fraRute ? tilRute - 8
					: tilRute + 8;
			final BrikkeUI tattBonde = model.hentBrikkePaaRute(tattBondeRute);
			model.drepBrikke(tattBonde);
		}

		// analyser trekk og promoter brikke
		final char lastChar = trekk.charAt(trekk.length() - 1);
		BrikkeUI ui = null;
		String promotering = "";
		if (lastChar == 'q') {
			ui = new BrikkeUI(Brikke.DRONNING, brikke.getFarge(), 0);
			promotering = " NY DRONNING :-) ";
		}
		if (lastChar == 'r') {
			ui = new BrikkeUI(Brikke.TAARN, brikke.getFarge(), 0);
			promotering = " NYTT TAARN";
		}
		if (lastChar == 'n') {
			ui = new BrikkeUI(Brikke.SPRINGER, brikke.getFarge(), 0);
			promotering = " NY SPRINGER";
		}
		if (lastChar == 'b') {
			ui = new BrikkeUI(Brikke.LOEPER, brikke.getFarge(), 0);
			promotering = " NY LOEPER";
		}
		;
		if (ui != null) {
			model.promoterBrikke(brikke, ui);
		}
		final StringBuilder sb = new StringBuilder();
		// sb.append(parti.trekkNummer());
		// sb.append(": ");
		sb.append(trekk.toUpperCase());
		sb.append(promotering);
		fireLoggBrikkeFlyttet(sb.toString(), tattBrikke != null);

		oppdaterAntallTrekk();

		hvit = !hvit;
	}

	private void oppdaterAntallTrekk() {
		trekkIgjenModel.setMinimum(0);
		trekkIgjenModel.setMaximum(parti.pauseEtterAntallTrekk());
		trekkIgjenModel.setValue(parti.trekkNummer());
	}

	@Override
	public void nyVurdering(final Farge sideITrekket, final Vurdering vurdering) {

		int verdi = sideITrekket == Farge.HVIT ? vurdering.verdi() : -vurdering
				.verdi();
		String analyseStreng = null;

		if (vurdering.erMatt()) {
			analyseStreng = (verdi < 0 ? "Hvit" : "Svart") + " er matt i "
					+ Math.abs(verdi) + " trekk!";
			verdi = verdi > 0 ? MAX_VURDERING : MIN_VURDERING;
		} else if (verdi == 0) {
			analyseStreng = "Uavgjort!";
		} else {
			analyseStreng = (verdi > 0 ? "Hvit" : "Svart")
					+ " leder med score " + verdi;
		}

		analyseModell.setValue(verdi);

		oppdaterTekstIDocument(analyseDocument, analyseStreng);
	}

	private void oppdaterTekstIDocument(final PlainDocument plainDocument,
			final String analyseStreng) {
		try {
			if (plainDocument.getLength() > 0) {
				plainDocument.remove(0, plainDocument.getLength());
			}
			plainDocument.insertString(0, analyseStreng, null);
		} catch (final BadLocationException e) {
			e.printStackTrace();
		}
	}

	
	
	@Override
	public void partiAvgjort(final Farge vinner, final String melding) {
		final String tekst = vinner + " vant. " + melding;
		oppdaterTekstIDocument(analyseDocument, tekst);
		stoppetKnappetilgang();
//		model.partiVunnet(vinner + " vant ", melding);
		resultatDialog.setMeldinger(vinner +" vant", melding);
	}

	@Override
	public void partiUavgjort(final String melding) {
		oppdaterTekstIDocument(analyseDocument, melding);
		stoppetKnappetilgang();
//		model.uavgjort("Uavgjort", melding);
		resultatDialog.setMeldinger("Uavgjort", melding);
	}

	@Override
	public void motorLogg(final String logglinje) {
		// System.out.println("Motorlogg: " + logglinje);
		fireMotorlogg(logglinje);
	}

	public void leggTilLogglytter(final Logglytter logglytter) {
		logglyttere.add(logglytter);
	}

	public void fjernLogglytter(final Logglytter logglytter) {
		logglyttere.remove(logglytter);
	}

	private void fireLoggBrikkeFlyttet(String kommando, final boolean tattBrikke) {
		kommando = kommando.substring(0, 2) + (tattBrikke ? " x " : " -> ")
				+ kommando.substring(2);
		final Logglytter[] lyttere = logglyttere.toArray(new Logglytter[] {});
		for (final Logglytter l : lyttere) {
			l.brikkeFlyttet(kommando, hvit, tattBrikke);
		}
	}

	private void fireMotorlogg(final String logg) {
		final Logglytter[] lyttere = logglyttere.toArray(new Logglytter[] {});
		for (final Logglytter l : lyttere) {
			l.debug(logg);
		}
	}

	public Action getVisDebugVindu(final LoggPanel parent) {
		return new AbstractAction("Vis debug", null) {
			@Override
			public void actionPerformed(final ActionEvent e) {
				final JDialog dialog = new JDialog();
				dialog.setAlwaysOnTop(true);

				final DebugPanel pnlDebug = new DebugPanel();
				leggTilLogglytter(pnlDebug);
				dialog.setLayout(new BorderLayout());
				dialog.add(pnlDebug, BorderLayout.CENTER);
				dialog.setSize(400, 400);
				dialog.setLocationRelativeTo(parent);
				dialog.addWindowListener(new WindowAdapter() {
					@Override
					public void windowClosing(final WindowEvent e) {
						fjernLogglytter(pnlDebug);
						System.out.println("fjernet...");
					}
				});
				dialog.setVisible(true);

				System.out.println("Viser debugvinduet...");
			}
		};
	}

	public BrettModell hentBrettModell() {
		return model;
	}

	public BoundedRangeModel getAnalyseModell() {
		return analyseModell;
	}

	public Document getAnalyseDocument() {
		return analyseDocument;
	}

	public ComboBoxModel<EksternSjakkmotor> hvitMotorModell() {
		return hvitMotorModell;
	}

	public ComboBoxModel<EksternSjakkmotor> svartMotorModell() {
		return svartMotorModell;
	}

	public ComboBoxModel<EksternSjakkmotor> analyseMotorModell() {
		return analyseMotorModell;
	}

	public SpinnerModel trekkTidModell() {
		return trekkTidModell;
	}

	public Action startAction() {
		return startAction;
	}

	public Action pauseAction() {
		return pauseAction;
	}

	public Action stoppAction() {
		return stoppAction;
	}

	private void lagActions() {
		this.startAction = new AbstractAction("", new ImageIcon(this.getClass()
				.getResource("/media-playback-start.png"))) {
			@Override
			public void actionPerformed(final ActionEvent e) {
				startParti();
				startetKnappetilgang();
			}
		};

		this.pauseAction = new AbstractAction("", new ImageIcon(this.getClass()
				.getResource("/media-playback-pause.png"))) {

			@Override
			public void actionPerformed(final ActionEvent e) {
				pauseParti();
			}

		};

		this.stoppAction = new AbstractAction("", new ImageIcon(this.getClass()
				.getResource("/media-playback-stop.png"))) {

			@Override
			public void actionPerformed(final ActionEvent e) {
				stoppParti();
				stoppetKnappetilgang();
			}
		};

		this.leggTilMotorAction = new AbstractAction("", new ImageIcon(this
				.getClass().getResource("/document-open.png"))) {

			@Override
			public void actionPerformed(final ActionEvent e) {
				final JFileChooser jFileChooser = new JFileChooser();
				jFileChooser
						.setDialogTitle("Legg til motor(er). Kjørbar fil eller jar-fil.");
				jFileChooser.setMultiSelectionEnabled(true);
				final int returnVal = jFileChooser.showOpenDialog(null);
				if (returnVal == JFileChooser.APPROVE_OPTION) {
					final File[] selectedFiles = jFileChooser
							.getSelectedFiles();
					for (final File file : selectedFiles) {
						motorbinge.leggTilMotor(file.getAbsolutePath(), false);
					}
				}

			}
		};

		stoppAction.setEnabled(false);
		pauseAction.setEnabled(false);
	}

	private void stoppetKnappetilgang() {
		startAction.setEnabled(true);
		pauseAction.setEnabled(false);
		stoppAction.setEnabled(false);
	}

	private void startetKnappetilgang() {
		startAction.setEnabled(false);
		pauseAction.setEnabled(true);
		stoppAction.setEnabled(true);
	}

	private void pausetKnappetilgang() {
		startAction.setEnabled(true);
		pauseAction.setEnabled(false);
		stoppAction.setEnabled(true);
	}

	@Override
	public void stateChanged(final ChangeEvent e) {
		if (parti != null) {
			parti.oppdaterTidPerTrekk(hentTrekkTidMillis());
		}
	}

	private int hentTrekkTidMillis() {
		return (int) (trekkTidModell.getNumber().doubleValue() * 1000.0);
	}

	public Action leggTilMotorAction() {
		return leggTilMotorAction;
	}

	public SpinnerModel antallTrekkModell() {
		return antallTrekkModell;
	}

	@Override
	public void partiPauset() {
		pausetKnappetilgang();
	}

	public BoundedRangeModel getTrekkIgjenModel() {
		return trekkIgjenModel;
	}

	public void setResultatDialog(PartiResultatDialog resultatDialog) {
		this.resultatDialog = resultatDialog;
	}
}
