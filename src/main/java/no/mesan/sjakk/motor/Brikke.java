package no.mesan.sjakk.motor;

import chesspresso.Chess;

/**
 * Sjakkbrikke.
 */
public enum Brikke {
	INGEN(0), BONDE(100), SPRINGER(300), LOEPER(320), TAARN(500), DRONNING(900), KONGE(
			100000);

	private final int verdi;

	private Brikke(final int verdi) {
		this.verdi = verdi;
	}

	/**
	 * Returnerer verdien til brikken. Disse er lik dem som er brukt i
	 * rammeverket chesspresso (og er ganske standard).
	 * 
	 * @return Verdien til brikken som "centipawns".
	 */
	public int verdi() {
		return verdi;
	}

	/**
	 * Konverterer brikker fra chesspressos konstanter.
	 * 
	 * @param konstant
	 * @return
	 */
	public static Brikke fraChesspressoKonstant(final int konstant) {
		switch (konstant) {
		case Chess.NO_PIECE:
			return INGEN;
		case Chess.PAWN:
			return BONDE;
		case Chess.KNIGHT:
			return SPRINGER;
		case Chess.BISHOP:
			return LOEPER;
		case Chess.ROOK:
			return TAARN;
		case Chess.QUEEN:
			return DRONNING;
		case Chess.KING:
			return KONGE;
		default:
			return INGEN;
		}
	}
}
