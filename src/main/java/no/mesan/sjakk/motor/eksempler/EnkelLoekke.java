package no.mesan.sjakk.motor.eksempler;

import no.mesan.sjakk.motor.AbstraktSjakkmotor;
import no.mesan.sjakk.motor.Posisjon;
import no.mesan.sjakk.motor.Trekk;

public class EnkelLoekke extends AbstraktSjakkmotor {

	@Override
	protected void finnBesteTrekk(final Posisjon posisjon) {
		int minBesteScore = Integer.MAX_VALUE;
		for (final Trekk mittTrekk : posisjon.alleTrekk()) {
			posisjon.gjorTrekk(mittTrekk);
			final int score = posisjon.sumAvMateriellPaaBrettet();
			posisjon.taTilbakeSisteTrekk();
			if (minBesteScore > score) {
				minBesteScore = score;
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
		return "Enkel loekke";
	}

	public static void main(final String[] args) {
		new EnkelLoekke().start();
	}
}
