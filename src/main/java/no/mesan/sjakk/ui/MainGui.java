package no.mesan.sjakk.ui;

import java.awt.Dimension;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;

import no.mesan.sjakk.ui.chessboard.BrettUI;

public class MainGui extends JPanel {

	private final SjakkGuiController controller;

	private BrettUI pnlBrett;
	private OptionPanel pnlValg;
	private AnalysePanel pnlAnalyse;
	private JSplitPane leftRightSplitPane;
	private JSplitPane topBottomSplitPane;

	public MainGui(final SjakkGuiController controller) {
		this.controller = controller;
		initComponents();
		initGui();
	}

	private void initComponents() {
		pnlBrett = new BrettUI(controller);
		pnlValg = new OptionPanel(controller);
		pnlAnalyse = new AnalysePanel(controller);
		leftRightSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		topBottomSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
	}

	private void initGui() {
		leftRightSplitPane.setLeftComponent(pnlBrett);
		leftRightSplitPane.setRightComponent(pnlValg);
		leftRightSplitPane.setResizeWeight(0.70);
		pnlBrett.setPreferredSize(new Dimension(500, 500));

		topBottomSplitPane.setTopComponent(leftRightSplitPane);
		topBottomSplitPane.setBottomComponent(pnlAnalyse);
		topBottomSplitPane.setResizeWeight(0.75);

		setLayout(new GridBagLayout());

		add(topBottomSplitPane, new GridBagConstraints(0, 0, 1, 1, 1.0, 1.0,
				GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(0,
						0, 0, 0), 0, 0));
	}

	/**
	 * @param args
	 */
	public static void main(final String[] args) {

		try {
			for (final LookAndFeelInfo info : UIManager
					.getInstalledLookAndFeels()) {
				if ("Nimbus".equals(info.getName())) {
					UIManager.setLookAndFeel(info.getClassName());
					break;
				}
			}
		} catch (final Exception e) {
			e.printStackTrace();
			// If Nimbus is not available, you can set the GUI to another look
			// and feel.
		}

		final JFrame frame = new JFrame();

		final SjakkGuiController controller = new SjakkGuiController();
		final MainGui gui = new MainGui(controller);
		
		frame.add(gui);
		frame.setSize(1024, 768);
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		//FYDDA...
		PartiResultatDialog w = new PartiResultatDialog(frame);
		w.setOpacity(0.6f);
		w.setSize(500,  200);
        w.setLocationRelativeTo(frame);
        controller.setResultatDialog(w);
	}
}
