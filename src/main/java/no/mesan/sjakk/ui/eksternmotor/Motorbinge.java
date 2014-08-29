package no.mesan.sjakk.ui.eksternmotor;

import java.util.ArrayList;
import java.util.List;

import no.mesan.sjakk.motor.AbstraktSjakkmotor;

import com.openpojo.reflection.PojoClass;
import com.openpojo.reflection.impl.PojoClassFactory;

public class Motorbinge {
	private final List<EksternSjakkmotor> motorer = new ArrayList<>();
	private final List<MotorbingeLytter> lyttere = new ArrayList<>();

	public Motorbinge() {
		initMotorerPaaClasspath();
		initBundledeMotorer();
	}

	private void initMotorerPaaClasspath() {
		final List<PojoClass> motorklasser = PojoClassFactory
				.enumerateClassesByExtendingType("no.mesan",
						AbstraktSjakkmotor.class, null);
		for (final PojoClass pojoClass : motorklasser) {
			leggTilMotor(pojoClass.getClazz().getName(), true);
		}
	}

	private void initBundledeMotorer() {
		final String os = System.getProperty("os.name");
		String analyse = null;
		final String path = System.getProperty("user.dir");
		if (os.toLowerCase().contains("windows")) {
			analyse = path + "/sjakkmotorer/stockfish_win_64.exe";
		} else if (os.toLowerCase().contains("linux")) {
			analyse = path + "/sjakkmotorer/stockfish_linux_32";
		} else {
			analyse = path + "/sjakkmotorer/stockfish_ios_64";
		}

		if (analyse != null) {
			leggTilMotor(analyse, false);
		}
	}

	public void leggTilMotorbingeLytter(final MotorbingeLytter motorbingeLytter) {
		lyttere.add(motorbingeLytter);
	}

	public void fjernMotorbingeLytter(final MotorbingeLytter motorbingeLytter) {
		lyttere.remove(motorbingeLytter);
	}

	public void leggTilMotor(final String command, final boolean lokalKlasse) {
		final EksternSjakkmotor eksternSjakkmotor = new EksternSjakkmotor(
				command, lokalKlasse);

		eksternSjakkmotor.setSjakkmotorLytter(new SjakkmotorLytter() {

			@Override
			public void vurderingMottatt(final String motorId,
					final Vurdering vurdering) {
			}

			@Override
			public void navnMottatt(final String motorId, final String navn) {
			}

			@Override
			public void motorKlar(final String motorId) {
				eksternSjakkmotor.stoppMotor();
				fyrOppdatering();
			}

			@Override
			public void meldingSendt(final String motorId, final String melding) {
			}

			@Override
			public void meldingMottat(final String motorId, final String melding) {
			}

			@Override
			public void lagetAvMottatt(final String motorId,
					final String lagetAv) {
			}

			@Override
			public void besteTrekkMottatt(final String motorId,
					final BesteTrekk besteTrekk) {
			}
		});

		eksternSjakkmotor.startMotor();

		motorer.add(eksternSjakkmotor);

		fyrOppdatering();
	}

	private void fyrOppdatering() {
		for (final MotorbingeLytter lytter : lyttere) {
			lytter.bingeEndret();
		}
	}

	public List<EksternSjakkmotor> motorer() {
		return motorer;
	}

	public List<EksternSjakkmotor> eksterneMotorer() {
		final List<EksternSjakkmotor> list = new ArrayList<>();
		for (final EksternSjakkmotor eksternSjakkmotor : motorer) {
			if (!eksternSjakkmotor.erLokalKlasse()) {
				list.add(eksternSjakkmotor);
			}
		}
		return list;
	}
}
