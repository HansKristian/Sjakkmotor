package no.mesan.sjakk.ui;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;

import no.mesan.sjakk.ui.eksternmotor.EksternSjakkmotor;
import no.mesan.sjakk.ui.imagemapper.BrikkeUnicodeMapper;

public class OptionPanel extends JPanel {

	private static final String TOOLTIP_LEGG_TIL_MOTOR = "Legg til motor. Eksekverbar fil eller jar-fil. Motor må støtte UCI";

	private static final String TOOLTIP_ANTALL_TREKK = "Avslutt partiet etter gitt antall trekk";

	private static final String TOOLTIP_TID_PER_TREKK = "Sekunder per trekk";

	private static final String TOOLTIP_START = "Start parti";

	private static final String TOOLTIP_PAUSE = "Pause parti";

	private static final String TOOLTIP_STOPP = "Stopp parti";

	private static final String TOOLTIP_HVIT = "Hvit spiller";

	private static final String TOOLTIP_SVART = "Svart spiller";

	private static final String TOOLTIP_ANALYSE = "Analysemotor";

	private final SjakkGuiController controller;

	private JButton btnLeggTilMotor;
	private JLabel lblHvit;
	private JLabel lblSvart;
	private JLabel lblAnalyse;
	private JComboBox<EksternSjakkmotor> cmbMotorHvit;
	private JComboBox<EksternSjakkmotor> cmbMotorSvart;
	private JComboBox<EksternSjakkmotor> cmbMotorAnalyse;
	private JSpinner spnTrekktid;
	private JLabel lblTrekktid;
	private JSpinner spnAntallTrekk;
	private JLabel lblAntallTrekk;
	private JButton btnStart;
	private JButton btnPause;
	private JButton btnStopp;
	private LoggPanel pnlLogg;
	private JPanel buttonPanel;
	private JPanel tidPanel;

	public OptionPanel(final SjakkGuiController controller) {
		this.controller = controller;
		initComponents();
		intiGui();

	}

	private void intiGui() {
		buttonPanel.setLayout(new GridBagLayout());
		tidPanel.setLayout(new GridBagLayout());
		setLayout(new GridBagLayout());

		tidPanel.add(lblTrekktid, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
				GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0,
						0, 0, 0), 0, 0));
		tidPanel.add(spnTrekktid, new GridBagConstraints(1, 0, 1, 1, 1.0, 0.0,
				GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
				new Insets(0, 0, 0, 20), 0, 0));
		tidPanel.add(lblAntallTrekk, new GridBagConstraints(2, 0, 1, 1, 0.0,
				0.0, GridBagConstraints.WEST, GridBagConstraints.NONE,
				new Insets(0, 0, 0, 0), 0, 0));
		tidPanel.add(spnAntallTrekk, new GridBagConstraints(3, 0, 1, 1, 1.0,
				0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
				new Insets(0, 0, 0, 0), 0, 0));

		buttonPanel.add(btnLeggTilMotor, new GridBagConstraints(0, 0, 1, 1,
				1.0, 0.0, GridBagConstraints.WEST,
				GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 5), 0, 0));
		buttonPanel.add(btnPause, new GridBagConstraints(1, 0, 1, 1, 1.0, 0.0,
				GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
				new Insets(0, 0, 0, 5), 0, 0));
		buttonPanel.add(btnStart, new GridBagConstraints(2, 0, 1, 1, 1.0, 0.0,
				GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
				new Insets(0, 0, 0, 5), 0, 0));
		buttonPanel.add(btnStopp, new GridBagConstraints(3, 0, 1, 1, 1.0, 0.0,
				GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
				new Insets(0, 0, 0, 0), 0, 0));

		add(pnlLogg, new GridBagConstraints(0, 0, 2, 1, 1.0, 1.0,
				GridBagConstraints.NORTH, GridBagConstraints.BOTH, new Insets(
						5, 5, 0, 5), 0, 0));
		add(tidPanel, new GridBagConstraints(0, 1, 2, 1, 1.0, 0.0,
				GridBagConstraints.NORTH, GridBagConstraints.HORIZONTAL,
				new Insets(5, 5, 0, 5), 0, 0));
		add(lblHvit, new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0,
				GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5,
						5, 0, 5), 0, 0));
		add(cmbMotorHvit, new GridBagConstraints(1, 2, 1, 1, 1.0, 0.0,
				GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
				new Insets(5, 0, 0, 5), 0, 0));
		add(lblSvart, new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0,
				GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5,
						5, 0, 5), 0, 0));
		add(cmbMotorSvart, new GridBagConstraints(1, 3, 1, 1, 1.0, 0.0,
				GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
				new Insets(5, 0, 0, 5), 0, 0));
		add(lblAnalyse, new GridBagConstraints(0, 4, 1, 1, 0.0, 0.0,
				GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5,
						5, 0, 5), 0, 0));
		add(cmbMotorAnalyse, new GridBagConstraints(1, 4, 1, 1, 1.0, 0.0,
				GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
				new Insets(5, 0, 0, 5), 0, 0));
		add(buttonPanel, new GridBagConstraints(0, 5, 2, 1, 1.0, 0.0,
				GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
				new Insets(5, 5, 5, 5), 0, 0));
	}

	private void initComponents() {
		buttonPanel = new JPanel();
		pnlLogg = new LoggPanel(controller);

		tidPanel = new JPanel();

		final BufferedImage hvit = BrikkeUnicodeMapper.getImageForChessPiece(
				BrikkeUnicodeMapper.KING, BrikkeUnicodeMapper.WHITE, true, 22);
		final BufferedImage svart = BrikkeUnicodeMapper.getImageForChessPiece(
				BrikkeUnicodeMapper.KING, BrikkeUnicodeMapper.BLACK, true, 22);
		lblHvit = new JLabel(new ImageIcon(hvit));
		lblHvit.setToolTipText(TOOLTIP_HVIT);
		lblSvart = new JLabel(new ImageIcon(svart));
		lblSvart.setToolTipText(TOOLTIP_SVART);
		lblTrekktid = new JLabel(new ImageIcon(this.getClass().getResource(
				"/appointment-new.png")));
		lblTrekktid.setToolTipText(TOOLTIP_TID_PER_TREKK);
		lblAnalyse = new JLabel(new ImageIcon(this.getClass().getResource(
				"/chart-line.png")));
		lblAnalyse.setToolTipText(TOOLTIP_ANALYSE);
		lblAntallTrekk = new JLabel(new ImageIcon(this.getClass().getResource(
				"/finish.png")));
		lblAntallTrekk.setToolTipText(TOOLTIP_ANTALL_TREKK);

		cmbMotorHvit = new JComboBox<>(controller.hvitMotorModell());
		cmbMotorHvit.setMinimumSize(new Dimension(0, 22));
		cmbMotorSvart = new JComboBox<>(controller.svartMotorModell());
		cmbMotorSvart.setMinimumSize(new Dimension(0, 22));
		cmbMotorAnalyse = new JComboBox<>(controller.analyseMotorModell());
		cmbMotorAnalyse.setMinimumSize(new Dimension(0, 22));

		spnTrekktid = new JSpinner(controller.trekkTidModell());
		spnTrekktid.setToolTipText(TOOLTIP_TID_PER_TREKK);
		spnAntallTrekk = new JSpinner(controller.antallTrekkModell());
		spnAntallTrekk.setToolTipText(TOOLTIP_ANTALL_TREKK);

		btnLeggTilMotor = new JButton(controller.leggTilMotorAction());
		btnLeggTilMotor.setToolTipText(TOOLTIP_LEGG_TIL_MOTOR);
		btnStart = new JButton(controller.startAction());
		btnStart.setToolTipText(TOOLTIP_START);
		btnPause = new JButton(controller.pauseAction());
		btnPause.setToolTipText(TOOLTIP_PAUSE);
		btnStopp = new JButton(controller.stoppAction());
		btnStopp.setToolTipText(TOOLTIP_STOPP);
	}
}
