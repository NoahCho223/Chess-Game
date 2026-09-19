package piece;

import java.util.ArrayList;

public class Queen extends Piece{
	
	public Queen(ColorSquare color, int col, int row) {
		super(color, col, row);
		
		if(color == ColorSquare.WHITE) {
			image = getImage("/piece/w-queen");
		}
		
		else {
			image = getImage("/piece/b-queen");
		}
	}

	@Override
	public boolean canMove(int targetCol, int targetRow, ArrayList<Piece> simPieces) {
		if(isWithinBoard(targetCol, targetRow) && !isSameSquare(targetCol, targetRow)) {
				if(isValidSquare(targetCol, targetRow, simPieces)) {
					
					//check vertical and horizontal
					if((targetCol == preCol || targetRow == preRow) && !pieceIsOnStraightLine(targetCol, targetRow, simPieces)) {
						return true;
					}
					//check if movement is diagonal
					if(Math.abs(targetRow-preRow) == Math.abs(targetCol-preCol) && !pieceIsOnDiagonalLine(targetCol, targetRow, simPieces)) {
						return true;
					}
				}
		}
		return false;
	}
}
