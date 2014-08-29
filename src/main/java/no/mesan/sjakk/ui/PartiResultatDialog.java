package no.mesan.sjakk.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.geom.RoundRectangle2D;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class PartiResultatDialog extends JDialog{

	private JPanel pnlContent;
	
	private JLabel lblMessage;
	private JLabel lblDescription;
	private JButton btnHide;
	
    private Point initialClick;

	public PartiResultatDialog(final JFrame parent){
		super(parent);
		
		setLayout(new GridBagLayout());

        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
            	setShape(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 20, 20));
            }
        });
        
        initComponents();
        initGui();
        setUndecorated(true);
        
        addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                initialClick = e.getPoint();
                getComponentAt(initialClick);
            }
        });
        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {

                // get location of Window
                int thisX = getLocation().x;
                int thisY = getLocation().y;

                // Determine how much the mouse moved since the initial click
                int xMoved = (thisX + e.getX()) - (thisX + initialClick.x);
                int yMoved = (thisY + e.getY()) - (thisY + initialClick.y);

                // Move window to this position
                int X = thisX + xMoved;
                int Y = thisY + yMoved;
                setLocation(X, Y);
            }
        });
	}
	
	private Action getHideAction(){
		return new AbstractAction("X") {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				setVisible(false);
			}
		};
	}
	
	private void initComponents() {
		pnlContent = new JPanel(new GridBagLayout());
		
		btnHide = new JButton(getHideAction());
		Font buttonFont = btnHide.getFont();
		btnHide.setFont(new Font(buttonFont.getFamily(), Font.BOLD, 20));
		
		lblMessage = new JLabel();
		lblDescription = new JLabel();
		
		Font descriptionFont = lblDescription.getFont();
		Font messageFont = lblMessage.getFont();
		messageFont = new Font(messageFont.getFamily(), Font.PLAIN, 40);
		descriptionFont = new Font(descriptionFont.getFamily(), Font.PLAIN, 25);
		lblMessage.setFont(messageFont);
		lblDescription.setFont(descriptionFont);
		
		lblMessage.setText("Hvit vant");
		lblDescription.setText("Svart gjorde et ulovlig trekk");
		
		lblMessage.setForeground(Color.white);
		lblDescription.setForeground(Color.white);
	}


	private void initGui() {
		setLayout(new BorderLayout());
		
		pnlContent.add(btnHide, 		new GridBagConstraints(0, 0, 1, 1, 1.0, 0.0, GridBagConstraints.NORTHEAST, GridBagConstraints.NONE, new Insets(5,0,0,5), 0, 0));
		pnlContent.add(lblMessage, 		new GridBagConstraints(0, 1, 1, 1, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0,0,0,0), 0, 0));
		pnlContent.add(lblDescription, 	new GridBagConstraints(0, 2, 1, 1, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0,20,0,0), 0, 0));

		add(pnlContent, BorderLayout.CENTER);
		pnlContent.setBackground(Color.black);
	}


	public void setMeldinger(String melding, String beskrivelse){
		lblMessage.setText(melding);
		lblDescription.setText(beskrivelse);
		setVisible(true);
	}
}
