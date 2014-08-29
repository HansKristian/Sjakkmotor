package no.mesan.sjakk.motor.eksempler;

import java.util.List;

import no.mesan.sjakk.motor.AbstraktSjakkmotor;
import no.mesan.sjakk.motor.Posisjon;
import no.mesan.sjakk.motor.Trekk;

public class TilfeldigMotor extends AbstraktSjakkmotor {

	@Override
	public void finnBesteTrekk(final Posisjon posisjon) {
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
		return "Tilfeldig trekkplukker";
	}

	public static void main(final String[] args) {
		new TilfeldigMotor().start();
	}
}
