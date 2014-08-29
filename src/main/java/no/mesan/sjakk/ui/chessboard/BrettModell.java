package no.mesan.sjakk.ui.chessboard;

import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.swing.event.EventListenerList;

import no.mesan.sjakk.motor.Brikke;
import no.mesan.sjakk.motor.Farge;

import org.jdesktop.core.animation.timing.Animator;
import org.jdesktop.core.animation.timing.PropertySetter;
import org.jdesktop.core.animation.timing.TimingSource;
import org.jdesktop.core.animation.timing.TimingSource.PostTickListener;
import org.jdesktop.core.animation.timing.TimingTarget;
import org.jdesktop.swing.animation.timing.sources.SwingTimerTimingSource;


public class BrettModell {
	protected EventListenerList lyttere = new EventListenerList();

	private final Map<Integer, Rute> ruter = new HashMap<Integer, Rute>();

	private final List<BrikkeUI> hviteBrikker = new ArrayList<BrikkeUI>();
	private final List<BrikkeUI> svarteBrikker = new ArrayList<BrikkeUI>();

	public BrettModell() {
		init();
	}

//	public void uavgjort(String melding, String melding2){
//		Object[] lytter = lyttere.getListenerList();
//		for (int i = lytter.length - 2; i >= 0; i -= 2) {
//            if (lytter[i] == BrettLytter.class) {
//                ((BrettLytter)lytter[i+1]).uavgjort(melding, melding2);
//            }
//        }
//	}
//	public void partiVunnet(String melding, String melding2){
//		Object[] lytter = lyttere.getListenerList();
//		for (int i = lytter.length - 2; i >= 0; i -= 2) {
//            if (lytter[i] == BrettLytter.class) {
//                ((BrettLytter)lytter[i+1]).partiVunnet(melding, melding2);
//            }
//        }
//	}
	private void init() {
		hviteBrikker.clear();
		svarteBrikker.clear();

		for (int i = 0; i < 8; i++) {
			leggTilHvitBrikke(new BrikkeUI(Brikke.BONDE, Farge.HVIT, 8 + i));
		}
		leggTilHvitBrikke(new BrikkeUI(Brikke.TAARN, Farge.HVIT, 0));
		leggTilHvitBrikke(new BrikkeUI(Brikke.SPRINGER, Farge.HVIT, 1));
		leggTilHvitBrikke(new BrikkeUI(Brikke.LOEPER, Farge.HVIT, 2));
		leggTilHvitBrikke(new BrikkeUI(Brikke.DRONNING, Farge.HVIT, 3));
		leggTilHvitBrikke(new BrikkeUI(Brikke.KONGE, Farge.HVIT, 4));
		leggTilHvitBrikke(new BrikkeUI(Brikke.LOEPER, Farge.HVIT, 5));
		leggTilHvitBrikke(new BrikkeUI(Brikke.SPRINGER, Farge.HVIT, 6));
		leggTilHvitBrikke(new BrikkeUI(Brikke.TAARN, Farge.HVIT, 7));

		for (int i = 0; i < 8; i++) {
			leggTilSvartBrikke(new BrikkeUI(Brikke.BONDE, Farge.SVART, 48 + i));
		}
		leggTilSvartBrikke(new BrikkeUI(Brikke.TAARN, Farge.SVART, 56));
		leggTilSvartBrikke(new BrikkeUI(Brikke.SPRINGER, Farge.SVART, 57));
		leggTilSvartBrikke(new BrikkeUI(Brikke.LOEPER, Farge.SVART, 58));
		leggTilSvartBrikke(new BrikkeUI(Brikke.DRONNING, Farge.SVART, 59));
		leggTilSvartBrikke(new BrikkeUI(Brikke.KONGE, Farge.SVART, 60));
		leggTilSvartBrikke(new BrikkeUI(Brikke.LOEPER, Farge.SVART, 61));
		leggTilSvartBrikke(new BrikkeUI(Brikke.SPRINGER, Farge.SVART, 62));
		leggTilSvartBrikke(new BrikkeUI(Brikke.TAARN, Farge.SVART, 63));
	}

	public void leggTilHvitBrikke(final BrikkeUI brikke) {
		hviteBrikker.add(brikke);
	}

	public void leggTilSvartBrikke(final BrikkeUI brikke) {
		svarteBrikker.add(brikke);
	}

	public void drepBrikke(final BrikkeUI brikke) {
		if (hviteBrikker.contains(brikke)) {
			hviteBrikker.remove(brikke);
		}
		if (svarteBrikker.contains(brikke)) {
			svarteBrikker.remove(brikke);
		}
		fireBrikkeDrept(brikke);
	}

	public BrikkeUI hentBrikkePaaRute(final int rutekoordinat) {
		for (final BrikkeUI b : hviteBrikker) {
			if (b.getRutekoordinat() == rutekoordinat) {
				return b;
			}
		}
		for (final BrikkeUI b : svarteBrikker) {
			if (b.getRutekoordinat() == rutekoordinat) {
				return b;
			}
		}
		return null;
	}

	public void promoterBrikke(final BrikkeUI brikke, final BrikkeUI nyBrikke) {
		brikke.oppdaterBrikke(nyBrikke);
		fireModellOppdatert();
	}

	public void flyttBrikke(final BrikkeUI brikke, final int tilKoordinat) {
		fjernValgAvAlleRuter();
		final Rute fraRute = hentRutePaKoordinat(brikke.getRutekoordinat());
		final Rute tilRute = hentRutePaKoordinat(tilKoordinat);
		fraRute.setValgtFra(true);

		final SwingTimerTimingSource animationTimer = new SwingTimerTimingSource();
		animationTimer.init();
		animationTimer.addPostTickListener(new PostTickListener() {
			@Override
			public void timingSourcePostTick(final TimingSource source,
					final long nanoTime) {
				BrettModell.this.fireModellOppdatert();
				tilRute.setValgtTil(true);
			}
		});

		final BrikkeUI brikkePaaRute = hentBrikkePaaRute(tilKoordinat);
		Animator.setDefaultTimingSource(animationTimer);

		final Point tilPunkt = getSenterAvRute(tilKoordinat);
		final Point fraPunkt = getSenterAvRute(brikke.getRutekoordinat());
		final double avstandIPx = tilPunkt.distance(fraPunkt);
		//final int animasjonslengde = (int) (avstandIPx);
		final int animasjonslengde = 1;
		final TimingTarget ttBrikke = PropertySetter.getTarget(brikke,
				"center", fraPunkt, tilPunkt);
		final Animator animatorBrikke = new Animator.Builder()
				.setDuration(animasjonslengde, TimeUnit.MILLISECONDS)
				.setDisposeTimingSource(true).addTarget(ttBrikke).build();

		animatorBrikke.start();
		animatorBrikke.addTarget(new TimingTarget() {
			@Override
			public void begin(final Animator source) {
				brikke.setRutekoordinat(tilKoordinat);
			}

			@Override
			public void end(final Animator source) {
				if (brikkePaaRute != null) {
					drepBrikke(brikkePaaRute);
				}
				
			}

			@Override
			public void repeat(final Animator source) {
			}

			@Override
			public void reverse(final Animator source) {
			}

			@Override
			public void timingEvent(final Animator source, final double fraction) {
			}
		});
	}

	public BrikkeUI[] getHviteBrikker() {
		return hviteBrikker.toArray(new BrikkeUI[] {});
	}

	public BrikkeUI[] getSvarteBrikker() {
		return svarteBrikker.toArray(new BrikkeUI[] {});
	}

	public Rute[] getRuter() {
		return ruter.values().toArray(new Rute[] {});
	}

	public Rute getRutePaPosisjon(final int x, final int y) {
		for (final Rute r : getRuter()) {
			if (r.inneholder(x, y)) {
				return r;
			}
		}
		return null;
	}

	public Point getSenterAvRute(final int rutekoordinat) {
		final Rute rute = ruter.get(rutekoordinat);
		if (rute != null) {
			final int x = rute.getX();
			final int y = rute.getY();
			final int size = rute.getSize();
			final Point p = new Point(x + (size / 2), y + (size / 2));
			return p;
		}
		return null;
	}

	public void velgRute(final Rute rute) {
		fjernValgAvAlleRuter();
		rute.setValgt(true);
		fireModellOppdatert();
	}

	public void fjernValgAvAlleRuter() {
		for (final Rute r : getRuter()) {
			r.fjernValg();
		}
	}

	public void leggTilEllerOppdaterRuter(final List<Rute> ruter) {
		for (final Rute r : ruter) {
			leggTilEllerOppdaterRute(r);
		}
		fireModellOppdatert();
	}

	public void leggTilEllerOppdaterRute(final Rute rute) {
		final Rute ruteToUpdate = hentRutePaKoordinat(rute.hentRutekoordinat());
		if (ruteToUpdate == null) {
			ruter.put(rute.hentRutekoordinat(), rute);
		} else {
			ruteToUpdate.oppdater(rute);
		}
	}

	private Rute hentRutePaKoordinat(final int rutekoordinat) {
		final Rute ruteToUpdate = ruter.get(rutekoordinat);
		return ruteToUpdate;
	}

	private void fireModellOppdatert() {
		final Object[] lytter = lyttere.getListenerList();
		for (int i = lytter.length - 2; i >= 0; i -= 2) {
			if (lytter[i] == BrettLytter.class) {
				((BrettLytter) lytter[i + 1]).modelUpdated();
			}
		}
	}

	private void fireBrikkeDrept(final BrikkeUI brikke) {
		final Object[] lytter = lyttere.getListenerList();
		for (int i = lytter.length - 2; i >= 0; i -= 2) {
			if (lytter[i] == BrettLytter.class) {
				((BrettLytter) lytter[i + 1]).pieceKilled(brikke);
			}
		}
	}

	private void fireResetBrett() {
		final Object[] lytter = lyttere.getListenerList();
		for (int i = lytter.length - 2; i >= 0; i -= 2) {
			if (lytter[i] == BrettLytter.class) {
				((BrettLytter) lytter[i + 1]).resetBrett();
			}
		}
	}

	public void leggTilBrettLytter(final BrettLytter lytter) {
		System.out.println("Lytter lagt til:"+lytter);
		lyttere.add(BrettLytter.class, lytter);
	}

	public void fjernBrettLytter(final BrettLytter lytter) {
		lyttere.remove(BrettLytter.class, lytter);
	}

	public BrettLytter[] getBrettLyttere() {
		return lyttere.getListeners(BrettLytter.class);
	}

	public void nyttParti() {
		init();
		fireResetBrett();
	}
}
