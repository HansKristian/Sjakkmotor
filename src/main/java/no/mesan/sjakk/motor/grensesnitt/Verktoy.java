package no.mesan.sjakk.motor.grensesnitt;

import no.mesan.sjakk.motor.Farge;
import no.mesan.sjakk.motor.Posisjon;
import chesspresso.Chess;
import chesspresso.move.Move;

import com.fluxchess.jcpi.commands.EngineStartCalculatingCommand;
import com.fluxchess.jcpi.models.GenericChessman;
import com.fluxchess.jcpi.models.GenericColor;
import com.fluxchess.jcpi.models.GenericMove;
import com.fluxchess.jcpi.models.GenericPosition;
import com.fluxchess.jcpi.models.IllegalNotationException;

public final class Verktoy {
	private static final int MOVES_LEFT_SUDDEN_DEATH = 30;

	public static long kalkulerSoeketid(
			final EngineStartCalculatingCommand command, final Posisjon posisjon) {
		long sokeTid;

		if (command.getInfinite()) {
			sokeTid = (Long.MAX_VALUE >> 1);
		} else if (command.getMoveTime() != null) {
			sokeTid = command.getMoveTime();
		} else {
			final long tidIgjen = posisjon.sideITrekket() == Farge.HVIT ? command
					.getClock(GenericColor.WHITE) : command
					.getClock(GenericColor.BLACK);
			final long inkrement = posisjon.sideITrekket() == Farge.HVIT ? command
					.getClockIncrement(GenericColor.WHITE) : command
					.getClockIncrement(GenericColor.BLACK);
			final Integer gjenstaaendeTrekk = command.getMovesToGo() != null ? command
					.getMovesToGo() : MOVES_LEFT_SUDDEN_DEATH;
			sokeTid = (tidIgjen + gjenstaaendeTrekk * inkrement)
					/ gjenstaaendeTrekk;
		}
		return sokeTid;
	}

	public static short convertGenericMoveToMove(final GenericMove genericMove,
			final Posisjon position) {

		final String genericMoveStringUCI = genericMoveToUCIString(genericMove);

		for (final short move : position.alleLovligeTrekkPrimitiv()) {
			final String moveStringUCI = moveToUCIString(move);

			if (genericMoveStringUCI.equalsIgnoreCase(moveStringUCI)) {
				return move;
			}
		}
		throw new IllegalStateException("Illegal move:"+genericMoveStringUCI);
	}

	public static String moveToUCIString(final short move) {
		final String fromSquareString = Chess.sqiToStr(Move.getFromSqi(move));
		final String toSquareString = Chess.sqiToStr(Move.getToSqi(move));
		final String promotionPieceString = Character.toString(
				Chess.pieceToChar(Move.getPromotionPiece(move))).trim();

		final String moveString = fromSquareString + toSquareString
				+ promotionPieceString;

		return moveString.toLowerCase();
	}

	public static String fullMoveToUCIString(final Move move) {
		return moveToUCIString(move.getShortMoveDesc());
	}

	public static String genericMoveToUCIString(final GenericMove genericMove) {
		final String genericMoveString = genericMove.from.toString()
				+ genericMove.to.toString()
				+ (genericMove.promotion != null ? genericMove.promotion
						.toCharAlgebraic() + "" : "");
		return genericMoveString.toLowerCase();
	}

	public static GenericMove moveToGenericMove(final Move move) {
		final String fromSquare = Chess.sqiToStr(move.getFromSqi());
		final String toSquare = Chess.sqiToStr(move.getToSqi());

		if (move.isPromotion()) {
			final char promo = Chess.pieceToChar(move.getPromo());
			return new GenericMove(GenericPosition.valueOf(fromSquare),
					GenericPosition.valueOf(toSquare),
					GenericChessman.valueOfPromotion(promo));
		}

		return new GenericMove(GenericPosition.valueOf(fromSquare),
				GenericPosition.valueOf(toSquare));
	}

	public static short longAlgebraicToMovePrimitive(
			final String longAlgebraic, final Posisjon posisjon) {
		try {
			final GenericMove genericMove = new GenericMove(longAlgebraic);
			return convertGenericMoveToMove(genericMove, posisjon);
		} catch (final IllegalNotationException e) {
			e.printStackTrace();
			return 0;
		}
	}
}
