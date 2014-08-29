package no.mesan.sjakk.ui.chessboard;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.TexturePaint;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.font.TextLayout;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JPanel;

import no.mesan.sjakk.ui.Poof;
import no.mesan.sjakk.ui.SjakkGuiController;

public class BrettUI extends JPanel implements MouseListener, BrettLytter {

	private final static Color svart = new Color(0.5f, 0.5f, 0.5f);
	private final static Color hvit = new Color(1.0f, 1.0f, 1.0f);

	private final String[] bokstaver = new String[] { "A", "B", "C", "D", "E",
			"F", "G", "H" };
	private final String[] tall = new String[] { "8", "7", "6", "5", "4", "3",
			"2", "1" };

	private final BrettModell modell;
	private int ruteStorrelse = 0;
	private final int marg = 5;
	// private String image =
	// "Marble_Chess_Board_Texture_1_by_FantasyStock.jpg";

	private final String bildefil = "board53.png";

	private int brettMarg;
	private final int brettPaddingTillegg = 40;

	private final ImageIcon bakgrunnsbilde;
	private int panelStorrelse;
	
	private int bredde=-1;
	private int hoyde=-1;
	private boolean rotert = false;
//	private boolean uavgjort = false;
//	private boolean vunnet = false;
//	private String melding;
//	private String melding2;
	
	
	private Poof poof;
	private SjakkGuiController controller;
	
	private int tekstStorrelse = 25;
	private Color tekstFarge1 = Color.black;
	private Color tekstFarge2 = Color.white;
	
	public BrettUI(SjakkGuiController controller) {
		this.modell = controller.hentBrettModell();
		this.controller = controller;
		modell.leggTilBrettLytter(this);

		addMouseListener(this);
		bakgrunnsbilde = new ImageIcon(getClass().getClassLoader().getResource(
				bildefil));
		init();
		poof = new Poof(this);
	}

	/**
	 * Calculate all the squares positions
	 */
	private void init() {
		panelStorrelse = Math.min(getWidth() - (marg * 2), getHeight()
				- (marg * 2));
		brettMarg = panelStorrelse / brettPaddingTillegg * 2;
		final int rutebrettStorrelse = Math.min(getWidth()
				- ((marg + brettMarg) * 2), getHeight()
				- ((marg + brettMarg) * 2));
		ruteStorrelse = rutebrettStorrelse / 8;
		final int ruteStorrelse = rutebrettStorrelse / 8;
		boolean erHvit = true;
		boolean yOffset = false;
		int fil = 1;
		int rekke = 8;
		final List<Rute> ruter = new ArrayList<Rute>();
		for (int y = 0; y < 8; y++) {
			yOffset = y % 2 == 0;// ? false : true;
			for (int x = 0; x < 8; x++) {
				erHvit = x % 2 == 0;// ? true : false;
				if (yOffset)
					erHvit = !erHvit;
				final Rute rute = new Rute(marg + brettMarg + (x * ruteStorrelse), 
						marg + brettMarg + (y * ruteStorrelse), ruteStorrelse, fil, rekke,
						erHvit ? hvit : svart);
				ruter.add(rute);
				fil++;
			}
			fil = 1;
			rekke--;
		}
		modell.leggTilEllerOppdaterRuter(ruter);

		for (final BrikkeUI b : modell.getSvarteBrikker()) {
			final Point senterAvRute = modell.getSenterAvRute(b
					.getRutekoordinat());
			b.setCenter(senterAvRute);
		}
		for (final BrikkeUI b : modell.getHviteBrikker()) {
			final Point senterAvRute = modell.getSenterAvRute(b
					.getRutekoordinat());
			b.setCenter(senterAvRute);
		}

	}

	@Override
	public void paint(final Graphics g) {
		super.paint(g);

		final Graphics2D gr = (Graphics2D) g;
		// gr.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
		// RenderingHints.VALUE_ANTIALIAS_ON);
		if (rotert) {
			gr.rotate(Math.toRadians(180), getWidth() / 2, getHeight() / 2);
		}

		g.setColor(Color.black);
		g.fillRect(0, 0, getWidth(), getHeight());

		final int cwidth = getWidth();
		final int cheight = getHeight();
		if (cwidth != bredde || cheight != hoyde) {
			init();
			bredde = cwidth;
			hoyde = cheight;
		}
		
		g.drawImage(bakgrunnsbilde.getImage(), marg, marg,panelStorrelse, panelStorrelse, this);
		
		
		g.setFont(new Font("Arial", Font.BOLD, tekstStorrelse));
		paintHTexts(g);
		paintVTexts(g);
		paintRuter(g);
		
		if (poof.isRunning()){
			BufferedImage next = poof.next();
			if (next==null){

				poof.setRunning(false);
			}
			poof.paint(g, next);
		}

		paintWhitePieces(g);
		paintBlackPieces(g);
		
//		if (uavgjort){
//			paintString(gr,melding, melding2);
//			return;
//		}
//		if (vunnet){
//			paintString(gr, melding, melding2);
//			return;
//		}
	}
	private void paintString(Graphics2D g2, String melding, String melding2){
		Graphics2D gr = (Graphics2D)g2.create();
		Font f = new Font("Arial", Font.BOLD, 80);
		gr.setFont(f);
		
		TextLayout text = new TextLayout(melding, f, gr.getFontRenderContext());
	    AffineTransform transform = new AffineTransform();
	    Shape outline = text.getOutline(null);
	    Rectangle outlineBounds = outline.getBounds();
	    transform = gr.getTransform();
	    transform.translate((panelStorrelse / 2 - (outlineBounds.width / 2)), (panelStorrelse / 2 + (outlineBounds.height / 3)));
	    gr.transform(transform);
	    
	    TexturePaint paint = new TexturePaint(lagSjakkmonster(), new Rectangle2D.Double(0,0,30,30));
	    gr.setPaint(paint);
	    gr.drawString(melding,0,0);
	    gr.setColor(Color.white);
	    
	    gr.setStroke(new BasicStroke(2));
	    gr.draw(outline);
	    gr.setClip(outline);
	    gr.dispose();
	    gr = (Graphics2D)g2.create();
	    
	    f = new Font("Arial", Font.BOLD, 30);
		gr.setFont(f);
		
		text = new TextLayout(melding2, f, gr.getFontRenderContext());
	    transform = new AffineTransform();
	    outline = text.getOutline(null);
	    outlineBounds = outline.getBounds();
	    transform = gr.getTransform();
	    transform.translate(panelStorrelse / 2 - (outlineBounds.width / 2), panelStorrelse / 2 + (outlineBounds.height*2));
	    gr.transform(transform);
	    
	    gr.setPaint(Color.red);
	    gr.drawString(melding,0,0);
	    gr.setColor(Color.black);
	    
	    gr.setStroke(new BasicStroke(2));
	    gr.draw(outline);
	    gr.setClip(outline);
	    gr.dispose();
	    
	}

	
	private BufferedImage lagSjakkmonster(){
		BufferedImage img = new BufferedImage(30, 30, BufferedImage.TYPE_INT_ARGB);
		Graphics g = img.getGraphics();
		
		g.setColor(Color.black);
		g.fillRect(0, 0, 15, 15);
		
		g.setColor(Color.white);
		g.fillRect(15, 0, 15, 15);
		
		g.setColor(Color.white);
		g.fillRect(0, 15, 15, 15);
		
		g.setColor(Color.black);
		g.fillRect(15, 15, 15, 15);
		g.dispose();
		return img;
	}

	private void paintWhitePieces(Graphics g){
		
		for (BrikkeUI p: modell.getHviteBrikker()){
			p.paint(g, ruteStorrelse);
		}
	}

	private void paintBlackPieces(final Graphics g) {
		for (final BrikkeUI p : modell.getSvarteBrikker()) {
			p.paint(g, ruteStorrelse);
		}
	}

	/**
	 * "Paints" the squares on the board, only for selections
	 * 
	 * @param g
	 */
	private void paintRuter(final Graphics g) {
		for (final Rute r : modell.getRuter()) {
			r.paint(g);
		}
	}

	/**
	 * Paints the characters on the left and right side of the board.
	 * 
	 * @param g
	 */
	private void paintHTexts(final Graphics g) {
		final int fontHoyde = g.getFontMetrics().getHeight();
		final int fontBredde = g.getFontMetrics().charWidth('E');

		final int topp = marg + (brettMarg / 2) + (fontHoyde / 3);
		final int bunn = marg + brettMarg + (ruteStorrelse * 8)
				+ (brettMarg / 2) + fontHoyde / 2;

		for (int i = 0; i < bokstaver.length; i++) {
			final int x = marg + brettMarg + (ruteStorrelse / 2)
					+ (ruteStorrelse * i) - (fontBredde / 2);
			g.setColor(tekstFarge1);
			g.drawString(bokstaver[i], x + 1, topp + 1);
			g.drawString(bokstaver[i], x + 1, bunn + 1);

			g.setColor(tekstFarge2);
			g.drawString(bokstaver[i], x, topp);
			g.drawString(bokstaver[i], x, bunn);

		}
	}

	/**
	 * Paints the numbers on the top and bottom side of the chess board
	 * 
	 * @param g
	 */
	private void paintVTexts(final Graphics g) {
		final int fontBredde = g.getFontMetrics().charWidth('E');
		final int fontHoyde = g.getFontMetrics().getHeight();

		final int venstre = marg + (brettMarg / 2) - (fontBredde / 2);
		final int hoyre = marg + brettMarg + (ruteStorrelse * 8)
				+ (brettMarg / 2) - (fontBredde / 3);

		for (int i = 0; i < tall.length; i++) {
			final int y = marg + brettMarg + (ruteStorrelse / 2)
					+ (ruteStorrelse * i) + fontHoyde / 3;
			g.setColor(tekstFarge1);
			g.drawString(tall[i], venstre + 1, y + 1);
			g.drawString(tall[i], hoyre + 1, y + 1);

			g.setColor(tekstFarge2);
			g.drawString(tall[i], venstre, y);
			g.drawString(tall[i], hoyre, y);
		}
	}

	int killcounter = 0;
	@Override
	public void mouseClicked(final MouseEvent e) {

		final Rute rute = modell.getRutePaPosisjon(e.getX() - marg, e.getY()
				- marg);
		if (rute == null)
			return;
		// model.selectRute(rute);
		if (killcounter == 6) {
			modell.flyttBrikke(modell.getSvarteBrikker()[11], 1);
			killcounter++;
		}
		if (killcounter == 5) {
			modell.flyttBrikke(modell.getSvarteBrikker()[11], 2);
			killcounter++;
		}
		if (killcounter == 4) {
			modell.flyttBrikke(modell.getSvarteBrikker()[11], 3);
			killcounter++;
		}
		if (killcounter == 3) {
			modell.flyttBrikke(modell.getSvarteBrikker()[11], 4);
			killcounter++;
		}
		if (killcounter == 2) {
			modell.flyttBrikke(modell.getSvarteBrikker()[11], 5);
			killcounter++;
		}
		if (killcounter == 1) {
			modell.flyttBrikke(modell.getSvarteBrikker()[11], 6);
			killcounter++;
		}
		if (killcounter == 0) {
			modell.flyttBrikke(modell.getSvarteBrikker()[11], 7);
			killcounter++;
		}

		// model.killPiece(model.getBlackPieces()[model.getBlackPieces().length-1]);

		// model.movePiece(model.getWhitePieces()[3],27);
		System.out.println(rute);
		// Point point =
		// model.getCenterForCoordinate(rute.getSquareCoordinate());
		// poof.animate(point.x, point.y, size);
	}

	@Override
	public void mousePressed(final MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseReleased(final MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseEntered(final MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseExited(final MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void modelUpdated() {
		repaint();
	}

	@Override
	public void pieceKilled(final BrikkeUI brikke) {
		final int squareCoordinate = brikke.getRutekoordinat();
		final Point point = modell.getSenterAvRute(squareCoordinate);
		poof.animate(point.x, point.y, ruteStorrelse);
	}

//	@Override
//	public void uavgjort(String melding, String melding2) {
////		this.melding2 = melding2;
////		uavgjort = true;
////		this.melding = melding;
//	}
//
//	@Override
//	public void partiVunnet(String melding, String melding2) {
////		this.melding2 = melding2;
////		vunnet = true;
////		this.melding = melding;
//		
//	}

	@Override
	public void resetBrett() {
		init();
//		this.melding2 = null;
//		this.melding = null;
//		this.vunnet = false;
//		this.uavgjort = false;
	}
}
