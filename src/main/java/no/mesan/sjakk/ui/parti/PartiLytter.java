package no.mesan.sjakk.ui.parti;

import no.mesan.sjakk.motor.Farge;
import no.mesan.sjakk.ui.eksternmotor.Vurdering;

public interface PartiLytter {
	void brikkeFlyttet(int fraRute, int tilRute, String trekk);

	void nyVurdering(Farge sideITrekket, Vurdering vurdering);

	void partiAvgjort(Farge vinner, String melding);

	void partiUavgjort(String melding);

	void motorLogg(String logglinje);

	void partiPauset();
}
