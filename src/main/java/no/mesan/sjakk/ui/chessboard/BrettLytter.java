package no.mesan.sjakk.ui.chessboard;

import java.util.EventListener;

public interface BrettLytter extends EventListener {

	void modelUpdated();

	void pieceKilled(BrikkeUI brikke);
//	void uavgjort(String melding, String melding2);
//	void partiVunnet(String melding, String melding2);

	void resetBrett();
}
