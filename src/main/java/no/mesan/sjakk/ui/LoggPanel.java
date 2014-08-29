package no.mesan.sjakk.ui;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

public class LoggPanel extends JPanel implements Logglytter {

	private final SjakkGuiController controller;

	private JButton btnVisDebug;
	private JTextPane txtLogg;
	private JScrollPane scrLogg;

	public LoggPanel(final SjakkGuiController controller) {
		this.controller = controller;
		controller.leggTilLogglytter(this);
		initComponents();
		initGui();
	}

	private void initComponents() {
		btnVisDebug = new JButton(controller.getVisDebugVindu(this));
		txtLogg = new JTextPane();
		scrLogg = new JScrollPane(txtLogg);
	}

	private void initGui() {
		setLayout(new GridBagLayout());

		add(btnVisDebug, new GridBagConstraints(0, 0, 1, 1, 1.0, 0.0,
				GridBagConstraints.EAST, GridBagConstraints.HORIZONTAL,
				new Insets(0, 0, 5, 0), 0, 0));
		add(scrLogg, new GridBagConstraints(0, 1, 1, 1, 1.0, 1.0,
				GridBagConstraints.EAST, GridBagConstraints.BOTH, new Insets(0,
						0, 0, 0), 0, 0));

	}

	@Override
	public void debug(final String debugStreng) {
		// TODO Auto-generated method stub

	}

	@Override
	public void brikkeFlyttet(final String kommando, final boolean hvit,
			final boolean tattBrikke) {
		final StyledDocument doc = txtLogg.getStyledDocument();

		// Define a keyword attribute
		final SimpleAttributeSet keyWord = new SimpleAttributeSet();
		if (tattBrikke && hvit) {
			StyleConstants.setForeground(keyWord, Color.RED);
			StyleConstants.setBackground(keyWord, Color.WHITE);
		} else if (tattBrikke && !hvit) {
			StyleConstants.setForeground(keyWord, Color.RED);
			StyleConstants.setBackground(keyWord, Color.BLACK);
		} else if (hvit) {
			StyleConstants.setForeground(keyWord, Color.BLACK);
			StyleConstants.setBackground(keyWord, Color.WHITE);

		} else {
			StyleConstants.setForeground(keyWord, Color.WHITE);
			StyleConstants.setBackground(keyWord, Color.BLACK);
		}
		StyleConstants.setBold(keyWord, true);

		// Add some text
		try {
			doc.insertString(doc.getLength(), kommando + "\n", keyWord);
			txtLogg.setCaretPosition(doc.getLength());
		} catch (final Exception e) {
			System.out.println(e);
		}
	}

}
