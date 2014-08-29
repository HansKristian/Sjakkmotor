package no.mesan.sjakk.ui.eksternmotor;

public class BesteTrekk {
	private final String longAlgebraic;

	public BesteTrekk(final String longAlgebraic) {
		this.longAlgebraic = longAlgebraic;
	}

	public String longAlgebraic() {
		return longAlgebraic;
	}

	@Override
	public String toString() {
		return "BesteTrekk [longAlgebraic=" + longAlgebraic + "]";
	}
}
