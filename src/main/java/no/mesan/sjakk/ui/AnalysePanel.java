package no.mesan.sjakk.ui;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JTextField;

import org.jfree.chart.ChartPanel;

public class AnalysePanel extends JPanel {
	private final SjakkGuiController controller;
	private ChartPanel pnlGraf;
	
	private JLabel lblTrekkIgjen;
	private JProgressBar progressTrekkIgjen;

	public AnalysePanel(final SjakkGuiController controller) {
		this.controller = controller;
		initComponents();
		intiGui();
	}

	private void intiGui() {

		setLayout(new GridBagLayout());
		add(lblTrekkIgjen, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
				GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5,
						5, 0, 5), 0, 0));
		add(progressTrekkIgjen, new GridBagConstraints(1, 0, 1, 1, 1.0, 0.0,
				GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
				new Insets(5, 0, 0, 5), 0, 0));
		add(pnlGraf, new GridBagConstraints(0, 1, 2, 1, 1.0, 1.0, GridBagConstraints.NORTH, GridBagConstraints.BOTH, new Insets(
						5, 5, 5, 5), 0, 0));
	}

	private void initComponents() {
		lblTrekkIgjen = new JLabel("Partiet:");

		progressTrekkIgjen = new JProgressBar(controller.getTrekkIgjenModel());
		progressTrekkIgjen.setStringPainted(true);

		AnalyseGraf graf = new AnalyseGraf(controller.getAnalyseModell());
		controller.hentBrettModell().leggTilBrettLytter(graf);
		
		pnlGraf = new ChartPanel(graf.getGraf(), true, true, false, false, true);
		
		
	}
}
