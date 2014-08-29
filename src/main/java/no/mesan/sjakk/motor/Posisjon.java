package no.mesan.sjakk.motor;

import java.util.ArrayList;
import java.util.List;

import chesspresso.Chess;
import chesspresso.move.IllegalMoveException;
import chesspresso.move.Move;
import chesspresso.position.Position;

/**
 * En sjakkposisjon.<br>
 * <br>
 * Har tilstand og er mutable for å være rask nok til avanserte motorer.
 * Vanligvis bruker en motor kun ett posisjonsobjekt som den manipulerer med
 * metodene {@link #gjorTrekk(Trekk)} og {@link #taTilbakeSisteTrekk()} for å
 * utforske muligheter i posisjonen.<br>
 * <br>
 * Klassen har også metoder for å hente ut lovlige trekk i posisjonen. <br>
 * <br>
 * Klassen opererer med to representasjoner av trekk: Klassen {@link Trekk} og
 * primitivet <code>short</code>. Sistnevnte er med for å kunne tilby stor
 * hastighet til de som trenger dette.
 * 
 */
public class Posisjon {
	/** Storrelse på posisjonshash. **/
	private final static int POSISJONSHASH_STORRELSE = 100000;

	/** Brukt i sortering av slag. Indeks matcher konstanter i {@link Chess}. **/
	private final static int[] BRIKKEVERDIER = { 0, // Tom rute
			3, // Springer
			3, // Loeper
			5, // Taarn
			9, // Dronning
			1, // Bonde
			100 // Konge
	};

	/** Hash tabell for repetisjonsdeteksjon. **/
	private final long[][] posisjonsHash = new long[POSISJONSHASH_STORRELSE][2];

	/** Brettrepresentasjon. **/
	private final Position position;

	public Posisjon(final String fen) {
		this(new Position(fen));
	}

	private Posisjon(final Position position) {
		this.position = position;
		leggPosisjonTilHash();
	}

	/**
	 * Henter underliggende {@link Position} for posisjonen.
	 * 
	 * @return
	 */
	public Position position() {
		return position;
	}

	/**
	 * Legger til posijon i hash. Strategi er aa beholde eldste posisjon ved
	 * kollisjon. Fungerer i praksis godt.
	 */
	private void leggPosisjonTilHash() {
		final long hashCode = position.getHashCode();
		final int hashNokkel = Math.abs((int) hashCode
				% POSISJONSHASH_STORRELSE);

		if (posisjonsHash[hashNokkel][0] == hashCode) {
			posisjonsHash[hashNokkel][1]++;
		} else if (posisjonsHash[hashNokkel][0] == 0) {
			posisjonsHash[hashNokkel][0] = hashCode;
			posisjonsHash[hashNokkel][1] = 1;
		}

	}

	/**
	 * Fjerner en posisjon fra hash.
	 */
	private void fjernPosisjonFraHash() {
		final long hashCode = position.getHashCode();
		final int hashNokkel = Math.abs((int) hashCode
				% POSISJONSHASH_STORRELSE);
		if (posisjonsHash[hashNokkel][0] == hashCode) {
			if (posisjonsHash[hashNokkel][1] > 1) {
				posisjonsHash[hashNokkel][1]--;
			} else {
				posisjonsHash[hashNokkel][0] = 0;
				posisjonsHash[hashNokkel][1] = 0;
			}
		}
	}

	/**
	 * Setter opp en ny startposisjon og returnerer denne.
	 * 
	 * @return Startposisjon.
	 */
	public static Posisjon startposisjon() {
		return new Posisjon(Position.createInitialPosition());
	}

	/**
	 * Gjør et trekk i posisjonen (posisjonen endres).
	 * 
	 * @param trekk
	 *            Primitiv versjon av trekk.
	 */
	public void gjorTrekk(final short trekk) {
		try {
			position.doMove(trekk);
			leggPosisjonTilHash();
		} catch (final IllegalMoveException e) {
			throw new IllegalStateException(e);
		}
	}

	/**
	 * Gjør et trekk i posisjonen (posisjonen endres).
	 * 
	 * @param trekk
	 *            Trekket som skal utføres.
	 */
	public void gjorTrekk(final Trekk trekk) {
		gjorTrekk(trekk.move().getShortMoveDesc());
	}

	/**
	 * Henter siste trekk som ble utført for å nå denne posisjonen.
	 * 
	 * @return trekk.
	 */
	public Trekk sisteTrekkGjort() {
		final Move lastMove = position.getLastMove();
		taTilbakeSisteTrekk();
		final Trekk sisteTrekk = new Trekk(lastMove, position);
		gjorTrekk(sisteTrekk);

		return sisteTrekk;
	}

	/**
	 * Henter siste trekk som ble utført for å nå denne posisjonen.
	 * 
	 * @return Primitivt trekk.
	 */
	public short sisteTrekkGjortPrimitiv() {
		return position.getLastShortMove();
	}

	/**
	 * tar tilbake siste trekk. Posisjonen endres.
	 */
	public void taTilbakeSisteTrekk() {
		fjernPosisjonFraHash();
		position.undoMove();
	}

	/**
	 * Henter alle lovlige trekk i en posisjon som en short array.
	 * 
	 * @return En array av short.
	 */
	public short[] alleLovligeTrekkPrimitiv() {
		return position.getAllMoves();
	}

	/**
	 * Hvilken farge har neste trekk?
	 * 
	 * @return Fargen med neste trekk.
	 */
	public Farge sideITrekket() {
		return Farge.fraChesspressoKonstant(position.getToPlay());
	}

	/**
	 * Returnerer alle slagtrekk i posisjonen som primitiver.
	 * 
	 * @return En short array av trekk.
	 */
	public short[] alleSlagtrekkPrimitiv() {
		return position.getAllCapturingMoves();
	}

	/**
	 * Konverterer et primitivt trekk til et {@link Trekk}. Merk! Trekket må
	 * være lovlig i nåværende posisjon.
	 * 
	 * @param move
	 *            Trekket som skal konverteres.
	 * @return Et fullt {@link Trekk}.
	 */
	public Trekk fulltTrekkFraPrimitiv(final short move) {
		gjorTrekk(move);
		final Move fullMove = position.getLastMove();
		taTilbakeSisteTrekk();

		return new Trekk(fullMove, position);
	}

	/**
	 * Henter alle lovlige trekk i posisjonen.
	 * 
	 * @return En liste av {@link Trekk}
	 */
	public List<Trekk> alleTrekk() {
		final List<Trekk> fulleTrekk = new ArrayList<>();
		for (final short move : alleLovligeTrekkPrimitiv()) {
			fulleTrekk.add(fulltTrekkFraPrimitiv(move));
		}
		return fulleTrekk;
	}

	/**
	 * Henter alle lovlige slagtrekk i posisjonen.
	 * 
	 * @return En liste av {@link Trekk}
	 */
	public List<Trekk> alleSlagtrekk() {
		final List<Trekk> slagtrekk = new ArrayList<>();
		for (final short move : alleSlagtrekkPrimitiv()) {
			slagtrekk.add(fulltTrekkFraPrimitiv(move));
		}
		return slagtrekk;
	}

	/**
	 * Henter alle lovlige rolige trekk i posisjonen.
	 * 
	 * @return En liste av {@link Trekk}
	 */
	public List<Trekk> alleRoligeTrekk() {
		final List<Trekk> roligeTrekk = new ArrayList<>();
		for (final short move : alleRoligeTrekkPrimitiv()) {
			roligeTrekk.add(fulltTrekkFraPrimitiv(move));
		}
		return roligeTrekk;
	}

	/**
	 * Henter alle trekk i posisjonen som ikke er utslag som primitiver.
	 * 
	 * @return array av trekk.
	 */
	public short[] alleRoligeTrekkPrimitiv() {
		return position.getAllNonCapturingMoves();
	}

	/**
	 * Returnerer summen av alt materiell på brettet. Grunnlaget for summen er
	 * verdiene fra enumen {@link Brikke}. Merk! Positive verdier er bra for
	 * siden som er i trekket. <br>
	 * <br>
	 * Eks: En posisjon med kun kongene og én hvit bonde på brettet. Dersom det
	 * er hvit i trekket returneres verdien 100. Dersom det er svart i trekket
	 * returneres verdien -100.
	 * 
	 * @return
	 */
	public int sumAvMateriellPaaBrettet() {
		return position.getMaterial();
	}

	/**
	 * Returnerer brikken som står på en rute. Rutene nummereres fra 0 som er A1
	 * til 63 som er H8.
	 * 
	 * @param sqi
	 * @return
	 */
	public Brikke brikkePaaRute(final int sqi) {
		return Brikke.fraChesspressoKonstant(position.getPiece(sqi));
	}

	/**
	 * Returnerer fargen på brikken som står på en rute. Rutene nummereres fra 0
	 * som er A1 til 63 som er H8. <code>null</code> returneres om ruten er tom.
	 * 
	 * @param sqi
	 * @return
	 */
	public Farge fargePaaBrikkePaaRute(final int sqi) {
		return Farge.fraChesspressoKonstant(position.getColor(sqi));
	}

	/**
	 * Returnerer <code>true</code> dersom posisjonen er en repitisjon fra
	 * tidligere posisjon i spilltreet.
	 * 
	 * @return
	 */
	public boolean erRepetisjon() {
		final long hashCode = position.getHashCode();
		final int hashNokkel = Math.abs((int) hashCode
				% POSISJONSHASH_STORRELSE);
		return posisjonsHash[hashNokkel][0] == hashCode
				&& posisjonsHash[hashNokkel][1] > 1;
	}

	/**
	 * Returnerer <code>true</code> dersom posisjonen er repetert 3 ganger i
	 * spilltreet.
	 * 
	 * @return
	 */
	public boolean erTreGangerRepetisjon() {
		final long hashCode = position.getHashCode();
		final int hashNokkel = Math.abs((int) hashCode
				% POSISJONSHASH_STORRELSE);
		return posisjonsHash[hashNokkel][0] == hashCode
				&& posisjonsHash[hashNokkel][1] >= 3;
	}

	/**
	 * Sier om fargen som er i trekket er i sjakk.
	 * 
	 * @return
	 */
	public boolean erSjakk() {
		return position.isCheck();
	}

	/**
	 * Sier om fargen som er i trekket er sjakk matt.
	 * 
	 * @return
	 */
	public boolean erSjakkMatt() {
		return position.isMate();
	}

	/**
	 * Sier om fargen som er i trekket er patt.
	 * 
	 * @return
	 */
	public boolean erPatt() {
		return position.isStaleMate();
	}

	/**
	 * Sier om posisjonen avslutter partiet. (Patt, matt eller 50-trekks
	 * uavgjort)
	 * 
	 * @return
	 */
	public boolean erPartietSlutt() {
		return position.isTerminal() || erTreGangerRepetisjon();
	}

	/**
	 * Sier om posisjonen er uavgjort på 50-trekks regel.
	 * 
	 * @return
	 */
	public boolean erUavgjortPaa50trekkregel() {
		return position.getHalfMoveClock() >= 100;
	}

	/**
	 * Returnerer alle slagtrekk som primitiver, sortert etter <a
	 * href="https://chessprogramming.wikispaces.com/MVV-LVA">MVV/LVA</a>. NB!
	 * Ikke en sortering som bør brukes til å velge trekk, men den gir en del
	 * avanserte algoritmer bedre gjennomsnittlig kjøretid.
	 * 
	 * @return
	 */
	public short[] alleSlagtrekkPrimitivSortert() {
		final short[] alleSlagtrekkPrimitiv = alleSlagtrekkPrimitiv();
		sorterSlagtrekk(alleSlagtrekkPrimitiv);
		return alleSlagtrekkPrimitiv;
	}

	/**
	 * Sorterer primitive trekk. Bruker insertion sort. Funker bra for små
	 * arrays.
	 * 
	 * @param slagtrekk
	 */
	private void sorterSlagtrekk(final short[] slagtrekk) {
		for (int i = 1; i < slagtrekk.length; i++) {
			final short x = slagtrekk[i];
			int j = i;
			while (j > 0 && !erBedreSlag(slagtrekk[j - 1], x)) {
				slagtrekk[j] = slagtrekk[j - 1];
				j--;
			}
			slagtrekk[j] = x;
		}
	}

	/**
	 * Strategi: Mest verdifulle offer / minst verdifulle angriper.
	 * 
	 * @param trekk1
	 * @param trekk2
	 * @return
	 */
	private boolean erBedreSlag(final short trekk1, final short trekk2) {
		final int utslaattBrikkesVerdiTrekk1 = BRIKKEVERDIER[brikkeSomSlaasITrekk(trekk1)];
		final int utslaattBrikkesVerdiTrekk2 = BRIKKEVERDIER[brikkeSomSlaasITrekk(trekk2)];

		if (utslaattBrikkesVerdiTrekk1 > utslaattBrikkesVerdiTrekk2) {
			return true;
		} else if (utslaattBrikkesVerdiTrekk1 < utslaattBrikkesVerdiTrekk2) {
			return false;
		}

		final int angripendeBrikkesVerdiTrekk1 = BRIKKEVERDIER[brikkeSomFlyttesITrekk(trekk1)];
		final int angripendeBrikkesVerdiTrekk2 = BRIKKEVERDIER[brikkeSomFlyttesITrekk(trekk2)];

		return angripendeBrikkesVerdiTrekk1 < angripendeBrikkesVerdiTrekk2;
	}

	/**
	 * Returnerer alle trekk som primitiver med sorterte slagtrekk først,
	 * deretter alle rolige trekk.
	 * 
	 * @return
	 */
	public short[] alleTrekkSortertPrimitiv() {
		final short[] utslagSortert = alleSlagtrekkPrimitivSortert();
		final short[] roligeTrekk = alleRoligeTrekkPrimitiv();
		final short[] alleTrekkSortert = new short[utslagSortert.length
				+ roligeTrekk.length];
		System.arraycopy(utslagSortert, 0, alleTrekkSortert, 0,
				utslagSortert.length);
		System.arraycopy(roligeTrekk, 0, alleTrekkSortert,
				utslagSortert.length, roligeTrekk.length);

		return alleTrekkSortert;
	}

	/**
	 * Returnerer alle trekk sortert, men med gitt trekk sortert først.
	 * 
	 * @param firstMove
	 * @return
	 */
	public short[] alleTrekkSortertPrimitiv(final short firstMove) {
		final short[] allMovesSorted = alleTrekkSortertPrimitiv();
		if (firstMove == Move.NO_MOVE || allMovesSorted.length <= 1) {
			return allMovesSorted;
		} else {
			short previousMove = allMovesSorted[0];
			allMovesSorted[0] = firstMove;
			for (int i = 1; i < allMovesSorted.length; i++) {
				if (previousMove == firstMove) {
					break;
				}
				final short currentMove = allMovesSorted[i];
				allMovesSorted[i] = previousMove;
				previousMove = currentMove;
			}
		}
		return allMovesSorted;
	}

	/**
	 * Returnerer brikken som slås i et trekk.
	 * 
	 * @param move
	 * @return
	 */
	private int brikkeSomSlaasITrekk(final short move) {
		return position.getPiece(Move.getToSqi(move));
	}

	/**
	 * Returnerer brikke som flyttes i et trekk.
	 * 
	 * @param move
	 * @return
	 */
	private int brikkeSomFlyttesITrekk(final short move) {
		return position.getPiece(Move.getFromSqi(move));
	}
}
