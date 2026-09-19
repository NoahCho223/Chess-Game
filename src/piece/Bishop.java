package piece;

import java.util.ArrayList;

public class Bishop extends Piece{
	public Bishop(ColorSquare color, int col, int row) {
		super(color, col, row);
		
		if(color == ColorSquare.WHITE) {
			image = getImage("/piece/w-bishop");
		}
		
		else {
			image = getImage("/piece/b-bishop");
		}
	}

	@Override
	public boolean canMove(int targetCol, int targetRow, ArrayList<Piece> simPieces) {
		if(isWithinBoard(targetCol, targetRow) && !isSameSquare(targetCol, targetRow)) {
			if(Math.abs(targetRow-preRow) == Math.abs(targetCol-preCol)) { //check if ratio 1:1
				if(isValidSquare(targetCol, targetRow, simPieces) && !pieceIsOnDiagonalLine(targetCol, targetRow, simPieces)) {
					return true;
				}
			}
		}
		return false;
	}
}
