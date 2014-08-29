package no.mesan.sjakk.ui.imagemapper;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.GeneralPath;
import java.awt.geom.PathIterator;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import no.mesan.sjakk.motor.Farge;
import no.mesan.sjakk.ui.chessboard.BrikkeUI;

public class BrikkeUnicodeMapper implements BrikkeMapper {

	// String[] pieces = {
	// "\u2654","\u2655","\u2656","\u2657","\u2658","\u2659",
	// "\u265A","\u265B","\u265C","\u265D","\u265E","\u265F"
	// };
	//
	public static final int KING = 0, QUEEN = 1, CASTLE = 2, BISHOP = 3,
			KNIGHT = 4, PAWN = 5;
	public static final int[] order = new int[] { CASTLE, KNIGHT, BISHOP,
			QUEEN, KING, BISHOP, KNIGHT, CASTLE };
	static final String[] pieces = { "\u2654", "\u2655", "\u2656", "\u2657",
			"\u2658", "\u2659" };

	public static final Color[] outlineColors = {Color.DARK_GRAY, new Color(203, 203, 197)};
	
	public static final Color[] pieceColors = { new Color(203, 203, 197),
			new Color(50, 50, 50)
	// new Color(80, 122, 222)
	};
	public static final int WHITE = 0, BLACK = 1;

	@Override
	public BufferedImage brikkeTilImage(final BrikkeUI brikke, final int size) {
		if (brikke.getFarge() == Farge.HVIT) {
			switch (brikke.getBrikke()) {
			case KONGE:
				return getImageForChessPiece(KING, WHITE, true, 54);
			case DRONNING:
				return getImageForChessPiece(QUEEN, WHITE, true, 54);
			case TAARN:
				return getImageForChessPiece(CASTLE, WHITE, true, 54);
			case LOEPER:
				return getImageForChessPiece(BISHOP, WHITE, true, 54);
			case SPRINGER:
				return getImageForChessPiece(KNIGHT, WHITE, true, 54);
			case BONDE:
				return getImageForChessPiece(PAWN, WHITE, true, 54);
			default:
				break;
			}
		}

		if (brikke.getFarge() == Farge.SVART) {
			switch (brikke.getBrikke()) {
			case KONGE:
				return getImageForChessPiece(KING, BLACK, true, 54);
			case DRONNING:
				return getImageForChessPiece(QUEEN, BLACK, true, 54);
			case TAARN:
				return getImageForChessPiece(CASTLE, BLACK, true, 54);
			case LOEPER:
				return getImageForChessPiece(BISHOP, BLACK, true, 54);
			case SPRINGER:
				return getImageForChessPiece(KNIGHT, BLACK, true, 54);
			case BONDE:
				return getImageForChessPiece(PAWN, BLACK, true, 54);
			default:
				break;
			}
		}
		return null;
	}

	/**
	 * takk til :
	 * http://stackoverflow.com/questions/18686199/fill-unicode-characters
	 * -in-labels
	 * 
	 * @param shape
	 * @return
	 */
	public static ArrayList<Shape> separateShapeIntoRegions(final Shape shape) {
		final ArrayList<Shape> regions = new ArrayList<Shape>();

		final PathIterator pi = shape.getPathIterator(null);
		GeneralPath gp = new GeneralPath();
		while (!pi.isDone()) {
			final double[] coords = new double[6];
			final int pathSegmentType = pi.currentSegment(coords);
			final int windingRule = pi.getWindingRule();
			gp.setWindingRule(windingRule);
			if (pathSegmentType == PathIterator.SEG_MOVETO) {
				gp = new GeneralPath();
				gp.setWindingRule(windingRule);
				gp.moveTo(coords[0], coords[1]);
			} else if (pathSegmentType == PathIterator.SEG_LINETO) {
				gp.lineTo(coords[0], coords[1]);
			} else if (pathSegmentType == PathIterator.SEG_QUADTO) {
				gp.quadTo(coords[0], coords[1], coords[2], coords[3]);
			} else if (pathSegmentType == PathIterator.SEG_CUBICTO) {
				gp.curveTo(coords[0], coords[1], coords[2], coords[3],
						coords[4], coords[5]);
			} else if (pathSegmentType == PathIterator.SEG_CLOSE) {
				gp.closePath();
				regions.add(new Area(gp));
			} else {
				System.err.println("Unexpected value! " + pathSegmentType);
			}

			pi.next();
		}

		return regions;
	}

	public static BufferedImage getImageForChessPiece(final int piece,
			final int side, final boolean gradient, final int size) {
		final Font font = new Font("Sans-Serif", Font.PLAIN, size);

		final int sz = font.getSize();
		final BufferedImage bi = new BufferedImage(sz, sz,
				BufferedImage.TYPE_INT_ARGB);
		final Graphics2D g = bi.createGraphics();
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
		g.setRenderingHint(RenderingHints.KEY_DITHERING,
				RenderingHints.VALUE_DITHER_ENABLE);
		g.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION,
				RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);

		final FontRenderContext frc = g.getFontRenderContext();
		final GlyphVector gv = font.createGlyphVector(frc, pieces[piece]);

		final Shape shape1 = gv.getOutline();
		final Rectangle r = shape1.getBounds();
		final int spaceX = sz - r.width;
		final int spaceY = sz - r.height;
		final AffineTransform trans = AffineTransform.getTranslateInstance(-r.x
				+ (spaceX / 2), -r.y + (spaceY / 2));

		final Shape shapeCentered = trans.createTransformedShape(shape1);

		final Shape imageShape = new Rectangle2D.Double(0, 0, sz, sz);
		final Area imageShapeArea = new Area(imageShape);
		final Area shapeArea = new Area(shapeCentered);
		imageShapeArea.subtract(shapeArea);
		final ArrayList<Shape> regions = separateShapeIntoRegions(imageShapeArea);
		g.setStroke(new BasicStroke(1));
		g.setColor(pieceColors[side]);
		final Color baseColor = pieceColors[side];
		if (gradient) {
			final Color c1 = baseColor.brighter();
			final Color c2 = baseColor;
			final GradientPaint gp = new GradientPaint(sz / 2 - (r.width / 4),
					sz / 2 - (r.height / 4), c1, sz / 2 + (r.width / 4), sz / 2
							+ (r.height / 4), c2, false);
			g.setPaint(gp);
		} else {
			g.setColor(baseColor);
		}

		for (final Shape region : regions) {
			final Rectangle r1 = region.getBounds();
			if (r1.getX() < 0.001 && r1.getY() < 0.001) {
			} else {
				g.fill(region);
			}
		}
		g.setColor(outlineColors[side]);
		g.fill(shapeArea);
		g.dispose();

		return bi;
	}
	// private BufferedImage stringToImage(String string, int size){
	// JLabel label = new JLabel(string);
	// Font font = new Font("Sans-Serif", Font.PLAIN, 50);
	// label.setForeground(new Color(50,50,59));
	// // label.setBackground(new Color(255-50,255-50,255-50));
	// // label.setOpaque(true);
	// label.setFont(font);
	// label.setSize(size, size);
	//
	// BufferedImage image = new BufferedImage(size, size,
	// BufferedImage.TYPE_INT_ARGB);
	// Graphics g = image.getGraphics();
	// label.paintComponents(g);
	// label.paint(g);
	// return image;
	// }
}
