package no.mesan.sjakk.ui;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;

public class DebugPanel extends JPanel implements Logglytter{

	private JTextPane txtLogg;
	private JScrollPane scrLogg;
	
	public DebugPanel(){
		initComponents();
		initGui();
	}
	private void initComponents() {
		txtLogg = new JTextPane();
		scrLogg = new JScrollPane(txtLogg);
	}

	private void initGui() {
		setLayout(new GridBagLayout());
		add(scrLogg, 	 new GridBagConstraints(0,0, 1,1, 1.0,1.0, GridBagConstraints.EAST, GridBagConstraints.BOTH, new Insets(0,0,0,0), 0, 0));
		
	}
	@Override
	public void debug(String debugStreng) {
		debugStreng = "$:> "+debugStreng;
		Document doc = txtLogg.getDocument();
	    try {
			doc.insertString(doc.getLength(), debugStreng+"\n", null);
			txtLogg.setCaretPosition(doc.getLength());
		} catch (BadLocationException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void brikkeFlyttet(String kommando, boolean hvit, boolean tattBrikke) {
		// TODO Auto-generated method stub
		
	}

}
