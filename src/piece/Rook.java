package piece;

import java.util.ArrayList;

public class Rook extends Piece{
	
	public Rook(ColorSquare color, int col, int row) {
		super(color, col, row);
		
		if(color == ColorSquare.WHITE) {
			image = getImage("/piece/w-rook");
		}
		
		else {
			image = getImage("/piece/b-rook");
		}
	}

	@Override
	public boolean canMove(int targetCol, int targetRow, ArrayList<Piece> simPieces) {
		if(isWithinBoard(targetCol, targetRow) && !isSameSquare(targetCol, targetRow)) {
			if((targetCol == preCol || targetRow == preRow)) {
				if(isValidSquare(targetCol, targetRow, simPieces) && !pieceIsOnStraightLine(targetCol, targetRow, simPieces)) {
					return true;
				}
			}
		}
		return false;
	}
}
