package no.mesan.sjakk.motor.eksempler;

import java.util.List;

import no.mesan.sjakk.motor.AbstraktSjakkmotor;
import no.mesan.sjakk.motor.Posisjon;
import no.mesan.sjakk.motor.Trekk;

public class DobbelLoekke extends AbstraktSjakkmotor {

	@Override
	protected void finnBesteTrekk(final Posisjon posisjon) {
		settTilfeldigTrekk(posisjon);

		int minBesteScore = Integer.MIN_VALUE;
		for (final Trekk mittTrekk : posisjon.alleTrekk()) {
			posisjon.gjorTrekk(mittTrekk);
			int motstandersBesteScore = Integer.MAX_VALUE;
			for (final Trekk motstandersTrekk : posisjon.alleTrekk()) {
				posisjon.gjorTrekk(motstandersTrekk);
				motstandersBesteScore = Math.min(motstandersBesteScore,
						posisjon.sumAvMateriellPaaBrettet());
				posisjon.taTilbakeSisteTrekk();
			}
			posisjon.taTilbakeSisteTrekk();
			if (minBesteScore < motstandersBesteScore) {
				minBesteScore = motstandersBesteScore;
				settBesteTrekk(mittTrekk);
			}
		}
	}

	@Override
	public String lagetAv() {
		return "larse";
	}

	@Override
	public String navn() {
		return "Dobbel loekke";
	}

	/**
	 * Setter et tilfeldig trekk blant alle lovlige i posisjonen.
	 * 
	 * @param posisjon
	 */
	private void settTilfeldigTrekk(final Posisjon posisjon) {
		final List<Trekk> alleLovligeTrekk = posisjon.alleTrekk();
		final int index = (int) (alleLovligeTrekk.size() * Math.random());
		settBesteTrekk(alleLovligeTrekk.get(index));
	}

	public static void main(final String[] args) {
		new DobbelLoekke().start();
	}
}
