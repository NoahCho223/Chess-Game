package piece;

import java.util.ArrayList;

public class Knight extends Piece{

	public Knight(ColorSquare color, int col, int row) {
		super(color, col, row);
		
		if(color == ColorSquare.WHITE) {
			image = getImage("/piece/w-knight");
		}
		
		else {
			image = getImage("/piece/b-knight");
		}
	}

	@Override
	public boolean canMove(int targetCol, int targetRow, ArrayList<Piece> simPieces) {
		if(isWithinBoard(targetCol, targetRow)) {
			//knights movement ratio of col and row is 1:2 or 2:1
			if((Math.abs(targetCol - preCol)*Math.abs(targetRow-preRow) == 2)) {
				if(isValidSquare(targetCol, targetRow, simPieces)) {
					return true;
				}
			}
		}
		return false;
	}
	
}
