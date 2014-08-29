package no.mesan.sjakk.ui.parti;

import no.mesan.sjakk.motor.Farge;
import no.mesan.sjakk.motor.Posisjon;
import no.mesan.sjakk.motor.grensesnitt.Verktoy;
import no.mesan.sjakk.ui.eksternmotor.BesteTrekk;
import no.mesan.sjakk.ui.eksternmotor.EksternSjakkmotor;
import no.mesan.sjakk.ui.eksternmotor.SjakkmotorLytter;
import no.mesan.sjakk.ui.eksternmotor.Vurdering;
import chesspresso.move.Move;
import chesspresso.position.Position;

public class Parti implements SjakkmotorLytter {
	private enum PartiStatus {
		STOPPET, PAUSET, STARTET
	}

	private enum AvslutningsAarsak {
		ANTALL_TREKK, POSISJON, FEILTREKK, BRUKER;
	}

	private static final long TID_ANALYSE_AVGJORELSE = 1000;

	private final PartiLytter partiLytter;
	private final EksternSjakkmotor hvit;
	private final EksternSjakkmotor svart;
	private final EksternSjakkmotor analysemotor;
	private final Posisjon posisjon;
	private long tidPerTrekk;
	private long oppdatertTidPerTrekk;
	private PartiStatus status;
	private final int pauseEtterAntallTrekk;
	private Vurdering vurdering;

	public Parti(final PartiLytter partiLytter, final EksternSjakkmotor hvit,
			final EksternSjakkmotor svart,
			final EksternSjakkmotor analysemotor, final long tidPerTrekk,
			final int pauseEtterAntallTrekk) {

		this.partiLytter = partiLytter;
		this.hvit = hvit;
		this.svart = svart;
		this.analysemotor = analysemotor;
		this.tidPerTrekk = tidPerTrekk;
		this.oppdatertTidPerTrekk = tidPerTrekk;
		this.pauseEtterAntallTrekk = pauseEtterAntallTrekk;

		posisjon = new Posisjon(Position.createInitialPosition().getFEN());
		status = PartiStatus.STOPPET;

		svart.setSjakkmotorLytter(this);
		hvit.setSjakkmotorLytter(this);

		if (analysemotor != null) {
			analysemotor.setSjakkmotorLytter(this);
		}
	}

	public void pause() {
		status = PartiStatus.PAUSET;
		partiLytter.partiPauset();
	}

	public void stopp() {
		avsluttParti(AvslutningsAarsak.BRUKER);
	}

	public void start() {
		if (status != PartiStatus.PAUSET) {
			hvit.startMotor();
			svart.startMotor();
			if (analysemotor != null) {
				analysemotor.startMotor();
			}
		}

		if (status != PartiStatus.STARTET) {
			status = PartiStatus.STARTET;
			sendPosisjonTilNesteMotor();
		}
	}

	private void sendPosisjonTilNesteMotor() {
		if (analysemotor != null) {
			analysemotor.stoppKalkulering();
		}

		if (pauseEtterAntallTrekk() == (posisjon.position().getPlyNumber() / 2)) {
			avsluttParti(AvslutningsAarsak.ANTALL_TREKK);
		}

		if (posisjon.erPartietSlutt()) {
			avsluttParti(AvslutningsAarsak.POSISJON);
		} else {
			if (!(status == PartiStatus.PAUSET && posisjon.sideITrekket() == Farge.HVIT)) {
				motorForFarge(posisjon.sideITrekket()).startKalkulering(
						posisjon.position().getFEN(),
						tidForNesteTrekk(posisjon.sideITrekket()));
			}

			if (analysemotor != null) {
				analysemotor.startUendeligKalkulering(posisjon.position()
						.getFEN());
			}
		}
	}

	private long tidForNesteTrekk(final Farge fargeITrekket) {
		if (fargeITrekket == Farge.HVIT) {
			this.tidPerTrekk = oppdatertTidPerTrekk;
		}

		return tidPerTrekk;
	}

	private void avsluttParti(final AvslutningsAarsak avslutningsAarsak) {
		status = PartiStatus.STOPPET;

		if (posisjon.erPatt()) {
			partiLytter.partiUavgjort("Uavgjort på patt");
		} else if (posisjon.erTreGangerRepetisjon()) {
			partiLytter.partiUavgjort("Uavgjort på trekkrepetisjon");
		} else if (posisjon.erUavgjortPaa50trekkregel()) {
			partiLytter.partiUavgjort("Uavgjort på 50-trekks regel");
		} else if (posisjon.erSjakkMatt()) {
			final Farge vinner = posisjon.sideITrekket() == Farge.HVIT ? Farge.SVART
					: Farge.HVIT;
			partiLytter.partiAvgjort(vinner, "Sjakk matt!");
		} else if (avslutningsAarsak == AvslutningsAarsak.ANTALL_TREKK) {
			avgjorPartiMedAnalysemotor();
		} else if (avslutningsAarsak == AvslutningsAarsak.FEILTREKK) {
			final Farge vinner = posisjon.sideITrekket() == Farge.HVIT ? Farge.SVART
					: Farge.HVIT;
			partiLytter.partiAvgjort(vinner, "Motstander gjorde et feiltrekk!");
		}

		hvit.stoppMotor();
		svart.stoppMotor();
		if (analysemotor != null) {
			analysemotor.stoppMotor();
		}
	}

	private void avgjorPartiMedAnalysemotor() {
		if (analysemotor != null) {
			analysemotor.startUendeligKalkulering(posisjon.position().getFEN());
			try {
				Thread.sleep(TID_ANALYSE_AVGJORELSE);
			} catch (final InterruptedException e) {
				e.printStackTrace();
			}
			analysemotor.stoppKalkulering();

			final Farge sideITrekket = posisjon.sideITrekket();
			final int verdi = sideITrekket == Farge.HVIT ? vurdering.verdi()
					: -vurdering.verdi();

			if (verdi == 0) {
				partiLytter.partiUavgjort("Uavgjort!");
			} else if (verdi > 0) {
				partiLytter.partiAvgjort(Farge.HVIT, "Avgjørelse på analyse!");
			} else {
				partiLytter.partiAvgjort(Farge.SVART, "Avgjørelse på analyse!");
			}
		}
	}

	public void oppdaterTidPerTrekk(final long millis) {
		this.oppdatertTidPerTrekk = millis;
	}

	@Override
	public void navnMottatt(final String motorId, final String navn) {
		// TODO Auto-generated method stub

	}

	@Override
	public void lagetAvMottatt(final String motorId, final String lagetAv) {
		// TODO Auto-generated method stub

	}

	@Override
	public void besteTrekkMottatt(final String motorId,
			final BesteTrekk besteTrekk) {

		if (motorIdForFarge(posisjon.sideITrekket()) == motorId) {
			short movePrimitive = 0;
			try {
				movePrimitive = Verktoy.longAlgebraicToMovePrimitive(
						besteTrekk.longAlgebraic(), posisjon);
			} catch (final Exception e) {
				avsluttParti(AvslutningsAarsak.FEILTREKK);
				e.printStackTrace();
				return;
			}
			posisjon.gjorTrekk(movePrimitive);
			partiLytter.brikkeFlyttet(Move.getFromSqi(movePrimitive),
					Move.getToSqi(movePrimitive), besteTrekk.longAlgebraic());

			sendPosisjonTilNesteMotor();
		} else if (analysemotor == null
				|| !motorId.equals(analysemotor.motorId())) {
			System.out.println("Mottatt trek fra feil motor");
		}
	}

	private String motorIdForFarge(final Farge farge) {
		return farge == Farge.HVIT ? hvit.motorId() : svart.motorId();
	}

	@Override
	public void vurderingMottatt(final String motorId, final Vurdering vurdering) {
		this.vurdering = vurdering;
		if (motorId.equals(analysemotor.motorId())) {
			partiLytter.nyVurdering(posisjon.sideITrekket(), vurdering);
		}
	}

	@Override
	public void meldingMottat(final String motorId, final String melding) {
		partiLytter.motorLogg(motorForId(motorId).navn() + " -> " + melding);
	}

	private EksternSjakkmotor motorForId(final String motorId) {
		if (analysemotor != null) {
			if (motorId.equals(analysemotor.motorId()))
				return analysemotor;
		}
		return hvit.motorId().equals(motorId) ? hvit : svart;
	}

	@Override
	public void meldingSendt(final String motorId, final String melding) {
		partiLytter.motorLogg(motorForId(motorId).navn() + " <- " + melding);
	}

	@Override
	public void motorKlar(final String motorId) {
		// TODO Auto-generated method stub

	}

	private EksternSjakkmotor motorForFarge(final Farge farge) {
		return farge == Farge.HVIT ? hvit : svart;
	}

	public boolean erPauset() {
		return status == PartiStatus.PAUSET;
	}

	public int trekkNummer() {
		final int trekk = posisjon.position().getPlyNumber() / 2;
		return trekk;
	}

	public int pauseEtterAntallTrekk() {
		return pauseEtterAntallTrekk;
	}
}
