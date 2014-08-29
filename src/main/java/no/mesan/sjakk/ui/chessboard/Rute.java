package no.mesan.sjakk.ui.chessboard;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;

import chesspresso.Chess;

public class Rute {

	private int x;
	private int y;
	private int storrelse;
	private Color farge;
	private int fil;
	private int rekke;
	private boolean valgt;
	private int rutekoordinat;
	
	private Color fraFarge = new Color(0.5f,0.5f,1.0f,0.3f);
	private Color tilFarge = new Color(0.5f,1.0f,0.5f,0.3f);
	private boolean valgtFra;
	private boolean valgtTil;
	
	/**
	 * x actual pixel position
	 * y actual pixel position
	 * size the size of the square in pixels
	 * col ranges from 1 to 8 and represents A through H
	 * row ranges from 1 to 8 and represents 1 through 8
	 * color the color used to pain the component. //might be removed..
	 * 
	 * */
	public Rute(int x, int y, int storrelse, int fil, int rekke, Color farge){
		this.farge = farge;
		this.x = x;
		this.y = y;
		this.storrelse = storrelse;
		this.fil = fil;
		this.rekke = rekke;
		rutekoordinat = Chess.coorToSqi(fil-1, rekke-1);
	}
	
	public void setValgt(boolean valgt){
		this.valgt = valgt;
	}
	public void setValgtFra(boolean valgtFra){
		this.valgtFra = valgtFra;
		
	}
	public void setValgtTil(boolean valgtTil){
		this.valgtTil = valgtTil;
	}
	
	public void fjernValg(){
		valgt = false;
		valgtFra = false;
		valgtTil = false;
	}
	public void oppdater(Rute rute){
		oppdater(rute.x, rute.y, rute.storrelse);
	}
	public void oppdater(int x, int y, int size){
		this.x = x;
		this.y = y;
		this.storrelse = size;
	}
	
	public int getX(){
		return x;
	}
	public int getY(){
		return y;
	}
	public int getSize(){
		return storrelse;
	}
	
	/**
	 * Returns the squareCoordinate 0-indexed. 0->63
	 * @return
	 */
	public int hentRutekoordinat(){
		return rutekoordinat;
	}
	
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + rutekoordinat;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Rute other = (Rute) obj;
		if (rutekoordinat != other.rutekoordinat)
			return false;
		return true;
	}

	public boolean inneholder(int x, int y){
		Rectangle rect = new Rectangle(this.x, this.y, storrelse, storrelse);
		return rect.contains(x, y);
	}

	public void paint(Graphics g){
		Color oldColor = g.getColor();
//		g.setColor(color);
//		g.fillRect(x, y, size, size);
		
		Graphics2D gr = (Graphics2D) g;
		if (valgt){
			gr.setStroke(new BasicStroke(3));
			gr.setColor(Color.red);
			gr.drawRect(x, y, storrelse, storrelse);
		}
		
		if (valgtFra){
			gr.setColor(fraFarge);
		}
		if (valgtTil){
			gr.setColor(tilFarge);
		}
		if (valgtFra||valgtTil){
			gr.fillRect(x, y, storrelse, storrelse);
		}
		
		g.setColor(oldColor);
	}
	
	public String toString(){
		return "x="+x+" y="+y+" storrelse="+storrelse +" fil="+fil+" rekke="+rekke+ "koord="+rutekoordinat;
	}
	
}
