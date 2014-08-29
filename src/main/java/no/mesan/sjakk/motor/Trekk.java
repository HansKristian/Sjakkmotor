package no.mesan.sjakk.motor;

import chesspresso.Chess;
import chesspresso.move.Move;
import chesspresso.position.Position;

/**
 * Trekk i et sjakkparti.
 */
public class Trekk {
	private final Move move;
	private final Brikke brikkeSomSlaas;

	public Trekk(final Move move, final Position positionBeforeMove) {
		this.move = move;
		final int toSqi = Move.getToSqi(move.getShortMoveDesc());
		final int capturedPiece = positionBeforeMove.getPiece(toSqi);
		brikkeSomSlaas = Brikke.fraChesspressoKonstant(capturedPiece);
	}

	/**
	 * Chesspressoversjon av trekk.
	 * 
	 * @return
	 */
	public Move move() {
		return move;
	}

	/**
	 * Er trekket et slagtrekk?
	 * 
	 * @return
	 */
	public boolean erSlag() {
		return move.isCapturing();
	}

	/**
	 * Setter trekket motstander sjakk?
	 * 
	 * @return
	 */
	public boolean erSjakk() {
		return move.isCheck();
	}

	/**
	 * Setter trekket motstander sjakk matt?
	 * 
	 * @return
	 */
	public boolean erSjakkMatt() {
		return move.isMate();
	}

	/**
	 * Er trekket kort rokad?
	 * 
	 * @return
	 */
	public boolean erKortRokade() {
		return move.isShortCastle();
	}

	/**
	 * Er trekket lang rokade?
	 * 
	 * @return
	 */
	public boolean erLangRokade() {
		return move.isLongCastle();
	}

	/**
	 * Er trekket bondeforvandling? Dvs en bonde som har kommet til motsatt side
	 * av brettet og kan forvandles til offiser.
	 * 
	 * @return
	 */
	public boolean erBondeforvandling() {
		return move.isPromotion();
	}

	/**
	 * Returnerer brikken som flyttes i trekket.
	 * 
	 * @return
	 */
	public Brikke brikkeSomFlyttes() {
		return Brikke.fraChesspressoKonstant(move.getMovingPiece());
	}

	/**
	 * Returnerer brikken som slås i trekket. Dersom trekket ikke er et
	 * slagtekk, men rolig, returneres {@code Brikke.INGEN}.
	 * 
	 * @return
	 */
	public Brikke brikkeSomSlaas() {
		return brikkeSomSlaas;
	}

	/**
	 * Returnerer brikken som en bonde foirvandles til i bondeforvandling.
	 * 
	 * @return
	 */
	public Brikke brikkeEtterBondeforvandling() {
		return Brikke.fraChesspressoKonstant(move.getPromo());
	}

	/**
	 * Returnerer ruten brikken flyttes fra. Rutene er representert med tall fra
	 * 0 til 63, der A1 er 0 og H8 er 63. Det finnes konstanter i {@link Chess}
	 * om man trenger det.
	 * 
	 * @return
	 */
	public int fraRute() {
		return move.getFromSqi();
	}

	/**
	 * Returnerer ruten brikken flyttes til. Rutene er representert med tall fra
	 * 0 til 63, der A1 er 0 og H8 er 63. Det finnes konstanter i {@link Chess}
	 * om man trenger det.
	 * 
	 * @return
	 */
	public int tilRute() {
		return move.getToSqi();
	}

	/**
	 * Returnerer raden brikken flyttes fra som et tall mellom 1 og 8.
	 * 
	 * @return
	 */
	public int fraRad() {
		return move.getRowFrom() + 1;
	}

	/**
	 * Returnerer linjen brikken flyttes fra som et tall mellom 1 og 8, der A er
	 * 1 og H er 8.
	 * 
	 * @return
	 */
	public int fraLinje() {
		return move.getColFrom() + 1;
	}

	/**
	 * Returnerer raden brikken flyttes til som et tall mellom 1 og 8
	 * 
	 * @return
	 */
	public int tilRad() {
		return Chess.sqiToRow(move.getToSqi()) + 1;
	}

	/**
	 * Returnerer linjen brikken flyttes til som et tall mellom 1 og 8, der A er
	 * 1 og H er 8.
	 * 
	 * @return
	 */
	public int tilLinje() {
		return Chess.sqiToCol(move.getToSqi());
	}
}
