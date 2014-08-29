package no.mesan.sjakk.ui.imagemapper;

import java.awt.image.BufferedImage;

import no.mesan.sjakk.ui.chessboard.BrikkeUI;

public class BrikkeImageMapper {

	
	public static final BufferedImage getBildeForBrikke(BrikkeUI brikke, int size, BrikkeMapper mapper){
		return mapper.brikkeTilImage(brikke, size);
	}

}
