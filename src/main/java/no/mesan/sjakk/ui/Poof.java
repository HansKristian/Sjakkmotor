package no.mesan.sjakk.ui;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;

import javax.imageio.ImageIO;
import javax.swing.SwingUtilities;

public class Poof {

	private String image = "/poof.png";
	private int spriteSize = 128;
	private int squareSize;
	
	private BufferedImage[] animation;
	
	int current = 0;
	private boolean isRunning;
	private int x;
	private int y;
	
	private boolean deliverNext;
	
	private int delay = 50;
	private BufferedImage currentImage;
	private Component component;
	public Poof(Component component){
		this.component = component;

	}
	
	public void animate(int centerX, int centerY, int squareSize){
		squareSize+=50;
		this.squareSize = squareSize;
		animation = splitImage();
		x = centerX-(squareSize/2);
		y = centerY-(squareSize/2);
		
		isRunning = true;
		deliverNext = true;
		component.repaint();
	}
	
	private boolean sleeping = false;
	public void paint(final Graphics g, BufferedImage img){
		g.drawImage(img, x, y, null);
		if (sleeping)
			return;
		deliverNext  = false;
		new Thread(){
			public void run(){
				try {
					sleeping = true;
					Thread.sleep(delay);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				sleeping = false;
				deliverNext = true;
				try {
					SwingUtilities.invokeAndWait(new Runnable(){
						public void run(){
							component.repaint();
						}
					});
				} catch (InvocationTargetException | InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}.start();
	}
	
	public void setRunning(boolean isRunning){
		this.isRunning = isRunning;
	}
	
	public boolean isRunning(){
		return isRunning;
	}
	
	public BufferedImage next(){
		if (current>4){
			current = 0;
			return null;
		}
		if (deliverNext==false){
			return currentImage;
		}
		currentImage = animation[current];
		current++;
		deliverNext = false;
		return currentImage;
	}
	
	private BufferedImage[] splitImage(){
		BufferedImage img = null;
		try {
			URL url = this.getClass().getResource(image);
		    img = ImageIO.read(url);
		} catch (IOException e) {
			e.printStackTrace();
		}
		BufferedImage[] images = new BufferedImage[5];
		for (int i=0;i<5;i++){
			images[i] = new BufferedImage(squareSize, squareSize, img.getType());
			Graphics2D g = images[i].createGraphics();
            g.drawImage(img, 0, 0, 
            				squareSize, squareSize, 
            				0, spriteSize*i, 
            				spriteSize, spriteSize*i+spriteSize, null);  

			g.dispose();
			
		}
		return images;
	}
}
