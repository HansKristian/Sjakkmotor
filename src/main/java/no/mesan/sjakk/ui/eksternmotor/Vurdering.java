package no.mesan.sjakk.ui.eksternmotor;

public class Vurdering {
	private final boolean erMatt;
	private final int verdi;

	public Vurdering(final boolean erMatt, final int verdi) {
		this.erMatt = erMatt;
		this.verdi = verdi;
	}

	public boolean erMatt() {
		return erMatt;
	}

	public int verdi() {
		return verdi;
	}

	@Override
	public String toString() {
		return "Vurdering [erMatt=" + erMatt + ", verdi=" + verdi + "]";
	}
}
