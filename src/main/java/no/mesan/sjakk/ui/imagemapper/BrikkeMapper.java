package no.mesan.sjakk.ui.imagemapper;

import java.awt.image.BufferedImage;

import no.mesan.sjakk.ui.chessboard.BrikkeUI;

public interface BrikkeMapper {

	BufferedImage brikkeTilImage(BrikkeUI brikke, int size);

}
