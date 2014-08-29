package no.mesan.sjakk.motor.eksempler;

import java.util.List;

import no.mesan.sjakk.motor.AbstraktSjakkmotor;
import no.mesan.sjakk.motor.Posisjon;
import no.mesan.sjakk.motor.Trekk;

public class BesteSlagMotor extends AbstraktSjakkmotor {

	@Override
	protected void finnBesteTrekk(final Posisjon posisjon) {
		// Safer med et tilfeldit trekk først
		settTilfeldigTrekk(posisjon);

		// Rekurserer gjennom alle slagtrekk
		finnBesteSlag(posisjon, 0);
	}

	private int finnBesteSlag(final Posisjon posisjon, final int depth) {
		final List<Trekk> alleSlagtrekk = posisjon.alleSlagtrekk();

		// Vi har ingen slagtrekk. Returnerer materiell på brettet. (En løvnode
		// i rekursjonen).
		if (alleSlagtrekk.isEmpty()) {
			return posisjon.sumAvMateriellPaaBrettet();
		}

		int besteScore = Integer.MIN_VALUE;

		for (final Trekk slagtrekk : alleSlagtrekk) {
			posisjon.gjorTrekk(slagtrekk);

			// Snur fortegnet på verdien fra rekursjonen fordi det verste for
			// motstanderen er det beste for oss (og 0 er likevekt).
			final int score = -finnBesteSlag(posisjon, depth + 1);

			// Dersom score er bedre enn den den beste vi har funnet så langt,
			// oppdater beste score.
			if (score > besteScore) {
				besteScore = score;

				// Setter beste trekk dersom vi er i rotposisjonen
				if (depth == 0) {
					settBesteTrekk(slagtrekk);
				}
			}

			posisjon.taTilbakeSisteTrekk();
		}

		return besteScore;
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

	@Override
	public String lagetAv() {
		return "larse";
	}

	@Override
	public String navn() {
		return "Beste slagtrekk";
	}

	public static void main(final String[] args) {
		new BesteSlagMotor().start();
	}
}
