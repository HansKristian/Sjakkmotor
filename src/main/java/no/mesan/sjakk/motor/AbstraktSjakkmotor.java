package no.mesan.sjakk.motor;

import java.util.concurrent.atomic.AtomicBoolean;

import no.mesan.sjakk.motor.grensesnitt.Grensenitthaandtering;

/**
 * Abstrakt klasse for sjakkmotorer. <br>
 * <br>
 * Ved søk etter et nytt trekk kalles først metoden
 * {@link #finnBesteTrekk(Posisjon)} med nåværende posisjon som input.
 * Implementasjoner må sette beste trekk ved å kalle metoden
 * {@link #settBesteTrekk(Trekk)}. <br>
 * <br>
 * Rammeverket vil beregne en passe tid til trekket i forhold til gjeldende
 * klokkesituasjon i partiet og videresende trekket som sist ble satt. Etter at
 * trekket er sendt, vil metoden {@link #erStoppet()} returnere
 * <code>true</code>. Dersom man har løkker eller rekursjoner som kan gå evig,
 * er det viktig å sjekke med denne metoden, og avlutte pågående arbeid når den
 * returnerer <code>true</code>. <br>
 * <br>
 * For å virke med rammeverket må subklasser inneholde en mainmetode hvor man
 * instansierer subklassen og kaller {@link #start()} på instansen. <br>
 * <br>
 * Eks:
 * 
 * <pre>
 * <code>
 * public class MinMotor extends AbstraktSjakkmotor {
 *  public static void main(final String[] args) {
 *   new MinMotor().start();
 *  }
 *  ...
 * }
 * </code>
 * </pre>
 */
public abstract class AbstraktSjakkmotor {

	private volatile Trekk besteTrekk;

	private final AtomicBoolean stoppet = new AtomicBoolean(true);

	/**
	 * Kalles for å sette i gang generering av neste trekk.<br>
	 * <br>
	 * Merk:<br>
	 * Metoden returnerer ikke trekket. Beste trekk må settes ved å kalle
	 * metoden {@link #settBesteTrekk(Trekk)}. Dette kan gjøres så mange ganger
	 * man ønsker. <br>
	 * <br>
	 * <b>Husk</b> også å avslutte eventuelle pågående beregninger når metoden
	 * {@link #erStoppet()} returnerer <code>true</code>!
	 * 
	 * @param posisjon
	 *            Posisjonen hvor neste trekk skal finnes.
	 */
	protected abstract void finnBesteTrekk(Posisjon posisjon);

	/**
	 * Hvem har laget motoren?
	 * 
	 * @return Navn på de(n) som har laget motoren.
	 */
	public abstract String lagetAv();

	/**
	 * Hva heter motoren?
	 * 
	 * @return Navn på motoren.
	 */
	public abstract String navn();

	/**
	 * Brukes for å sjekke om det er på tide å stoppe pågående leting etter
	 * beste trekk. Dersom metoden returnerer <code>true</code> har tiden for
	 * trekket løpt ut, og beste trekk er blitt hentet.
	 * 
	 * @return
	 */
	protected final boolean erStoppet() {
		return stoppet.get();
	}

	/**
	 * Brukes for å sette beste trekk. Kan kalles mange ganger!
	 * 
	 * @param trekk
	 */
	protected final void settBesteTrekk(final Trekk trekk) {
		besteTrekk = trekk;
	}

	/**
	 * Starter motoren i rammeverket slik at vi er klar for å ta i mot
	 * kommandoer fra stdin.
	 */
	public final void start() {
		new Grensenitthaandtering(this).run();
	}

	/**
	 * Starter søk etter beste trekk i en posisjon. Brukes av rammeverket.
	 * 
	 * @param posisjon
	 */
	public final void sok(final Posisjon posisjon) {
		if (!erStoppet()) {
			throw new IllegalStateException(
					"Nytt sok startet uten at gammelt søk er stoppet");
		}

		startSok();
		finnBesteTrekk(posisjon);
	}

	/**
	 * Henter beste trekk funnet i pågående søk. Brukes av rammeverket.
	 * 
	 * @return
	 */
	public final Trekk besteTrekk() {
		stoppSok();
		return besteTrekk;
	}

	private void startSok() {
		stoppet.set(false);
	}

	private void stoppSok() {
		stoppet.set(true);
	}
}
