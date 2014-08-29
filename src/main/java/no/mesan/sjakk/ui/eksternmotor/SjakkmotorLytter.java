package no.mesan.sjakk.ui.eksternmotor;

public interface SjakkmotorLytter {
	void navnMottatt(String motorId, String navn);

	void lagetAvMottatt(String motorId, String lagetAv);

	void besteTrekkMottatt(String motorId, BesteTrekk besteTrekk);

	void vurderingMottatt(String motorId, Vurdering vurdering);

	void meldingMottat(String motorId, String melding);

	void meldingSendt(String motorId, String melding);

	void motorKlar(String motorId);
}
