package no.mesan.sjakk.motor;

import chesspresso.Chess;

/**
 * De to sidene i et sjakkparti.
 */
public enum Farge {
	HVIT, SVART;

	/**
	 * Konverterer farge fra chesspressos konstanter.
	 * 
	 * @param farge
	 * @return
	 */
	public static Farge fraChesspressoKonstant(final int farge) {
		switch (farge) {
		case Chess.WHITE:
			return HVIT;
		case Chess.BLACK:
			return SVART;
		default:
			return null;
		}
	}
}
