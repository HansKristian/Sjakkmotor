package no.mesan.sjakk.motor.eksempler;

import java.util.List;

import no.mesan.sjakk.motor.AbstraktSjakkmotor;
import no.mesan.sjakk.motor.Posisjon;
import no.mesan.sjakk.motor.Trekk;

public class TilfeldigMorder extends AbstraktSjakkmotor {

	@Override
	public void finnBesteTrekk(final Posisjon posisjon) {
		final List<Trekk> alleLovligeTrekk = posisjon.alleTrekk();
	
		final int index = (int) (alleLovligeTrekk.size() * Math.random());
		settBesteTrekk(alleLovligeTrekk.get(index));
		for (Trekk trekk:alleLovligeTrekk){
			if (trekk.erSlag()){
				settBesteTrekk(trekk);
				return;
			}
			if (erStoppet()){
				return ;
			}
		}
		
	}

	@Override
	public String lagetAv() {
		return "Harald Alexander Kulø";
	}

	@Override
	public String navn() {
		return "Tilfeldig morder";
	}

	public static void main(final String[] args) {
		new TilfeldigMorder().start();
	}

}
