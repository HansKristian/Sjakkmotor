package no.mesan.sjakk.ui.chessboard;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;

import no.mesan.sjakk.motor.Brikke;
import no.mesan.sjakk.motor.Farge;
import no.mesan.sjakk.ui.imagemapper.BrikkeImageMapper;
import no.mesan.sjakk.ui.imagemapper.BrikkeUnicodeMapper;

public class BrikkeUI {
	private Brikke brikke;
	private Farge farge;
	private int rutekoordinat;
	private BufferedImage image;
	private Point center;
	private boolean rotert = false;
	private int bildeBredde;
	private int bildeHoyde;
	
	public BrikkeUI(Brikke brikke, Farge farge, int squareCoordinate){
		this.brikke = brikke;
		this.farge = farge;
		this.rutekoordinat = squareCoordinate;
		image = BrikkeImageMapper.getBildeForBrikke(this, 50, new BrikkeUnicodeMapper());
		bildeBredde = image.getWidth();
		bildeHoyde = image.getHeight();
	}
	
	public void oppdaterBilde(BrikkeUI nyBrikke){
		image = nyBrikke.image;
	}
	public void oppdaterBrikke(BrikkeUI nyBrikke){
		brikke = nyBrikke.brikke;
		oppdaterBilde(nyBrikke);
	}
	public int getRutekoordinat(){
		return rutekoordinat;
	}
	public void setRutekoordinat(int rutekoordinat){
		this.rutekoordinat = rutekoordinat;
	}
	public Brikke getBrikke(){
		return brikke;
	}
	public Farge getFarge(){
		return farge;
	}
	public Image getImage(){
		return image;
	}
	
	public void setCenter(Point center){
		this.center = center;
	}
	
	public void paint(Graphics g, int size){
		if (rotert){
			double rotationRequired = Math.toRadians(180);
			double locationX = image.getWidth() / 2;
			double locationY = image.getHeight() / 2;
			AffineTransform tx = AffineTransform.getRotateInstance(rotationRequired, locationX, locationY);
			AffineTransformOp op = new AffineTransformOp(tx, AffineTransformOp.TYPE_BILINEAR);
			g.drawImage(op.filter(image, null), center.x, center.y, null);
		}
		else{
			g.drawImage(image, center.x-(bildeBredde/2), center.y-(bildeHoyde/2), null);
		}
	}
	
}
