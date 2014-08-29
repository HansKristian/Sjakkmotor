package no.mesan.sjakk.ui;

import java.util.List;

import javax.swing.ComboBoxModel;
import javax.swing.event.EventListenerList;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

import no.mesan.sjakk.ui.eksternmotor.EksternSjakkmotor;
import no.mesan.sjakk.ui.eksternmotor.Motorbinge;
import no.mesan.sjakk.ui.eksternmotor.MotorbingeLytter;

public class MotorComboBoxModel implements ComboBoxModel<EksternSjakkmotor>,
		MotorbingeLytter {

	protected EventListenerList listenerList = new EventListenerList();
	private EksternSjakkmotor valgtEntity;
	private final Motorbinge motorbinge;
	private final boolean kunEksterneMotorer;

	public MotorComboBoxModel(final Motorbinge motorbinge,
			final boolean kunEksterneMotorer) {
		this.motorbinge = motorbinge;
		this.kunEksterneMotorer = kunEksterneMotorer;
		final EksternSjakkmotor elementAt = getElementAt(0);
		if (elementAt != null) {
			setSelectedItem(elementAt);
		}
		motorbinge.leggTilMotorbingeLytter(this);
	}

	private List<EksternSjakkmotor> motorliste() {
		return kunEksterneMotorer ? motorbinge.eksterneMotorer() : motorbinge
				.motorer();
	}

	@Override
	public EksternSjakkmotor getElementAt(final int index) {
		final List<EksternSjakkmotor> motorliste = motorliste();
		if (index >= motorliste.size())
			return null;
		return motorliste.get(index);
	}

	@Override
	public int getSize() {
		return motorliste().size();
	}

	@Override
	public EksternSjakkmotor getSelectedItem() {
		return valgtEntity;
	}

	@Override
	public void setSelectedItem(final Object anItem) {
		valgtEntity = (EksternSjakkmotor) anItem;
		fireContentsChanged(this, -1, -1);
	}

	@Override
	public void addListDataListener(final ListDataListener l) {
		listenerList.add(ListDataListener.class, l);
	}

	@Override
	public void removeListDataListener(final ListDataListener l) {
		listenerList.remove(ListDataListener.class, l);
	}

	protected void fireContentsChanged(final Object source, final int index0,
			final int index1) {
		final Object[] listeners = listenerList.getListenerList();
		ListDataEvent e = null;

		for (int i = listeners.length - 2; i >= 0; i -= 2) {
			if (listeners[i] == ListDataListener.class) {
				if (e == null) {
					e = new ListDataEvent(source,
							ListDataEvent.CONTENTS_CHANGED, index0, index1);
				}
				((ListDataListener) listeners[i + 1]).contentsChanged(e);
			}
		}
	}

	@Override
	public void bingeEndret() {
		fireContentsChanged(this, -1, -1);
	}
}
