package piece;

import java.util.ArrayList;

import main.Board;
import main.GamePanel;

public class King extends Piece{

	public King(ColorSquare color, int col, int row) {
		super(color, col, row);

		if(color == ColorSquare.WHITE) {
			image = getImage("/piece/w-king");
		}

		else {
			image = getImage("/piece/b-king");
		}
	}

	@Override
	public boolean canMove(int targetCol, int targetRow, ArrayList<Piece> simPieces) {
		if(isWithinBoard(targetCol, targetRow)) {

			//Movement
			if(Math.abs(targetCol - preCol) + Math.abs(targetRow - preRow) == 1
					|| Math.abs(targetCol - preCol)*Math.abs(targetRow - preRow) == 1){
				if(isValidSquare(targetCol, targetRow, simPieces)) {
					return true;
				}
			}

			if(castlingPiece(targetCol, targetRow, simPieces) != null) {
				return true;
			}

		}
		return false;
	}

	public Piece castlingPiece(int targetCol, int targetRow, ArrayList<Piece> simPieces) {

		Piece rook = null;
		//can't castle when in check
		if(inCheck()) {
			return null;
		}


		if(moved) { //can only castle when not moved
			return null;
		}

		if(targetRow != preRow) {
			return null;
		}

		//queenside

		//Right castling
		if(targetCol == preCol +2) {

			//can't castle when movement is blocked off by attacking piece
			if(isAttacked(preCol + 1, preRow, color)) {
				return null;
			}

			if(isAttacked(preCol + 2, preRow, color)) {
				return null;
			}

			//find the corresponding rook
			for(Piece piece: simPieces) {
				//check if piece is rook on the right, then check if it moved yet
				if(piece instanceof Rook && piece.col == Board.MAX_COL-1 && piece.row == this.row && piece.color == this.color && piece.moved == false) {
					rook = piece;
				}
			}
		}

		if(rook!=null && !pieceIsOnStraightLine(rook.preCol, rook.preRow, simPieces)) {  //check if theres any pieces between rook and king

			return rook;
		}

		//Left castling
		if(targetCol == preCol -2) {
			//can't castle when movement is blocked off by attacking piece
			if(isAttacked(preCol - 1, preRow, color)) {
				return null;
			}

			if(isAttacked(preCol - 2, preRow, color)) {
				return null;
			}

			//find the corresponding rook
			for(Piece piece: simPieces) {
				//check if piece is rook on the left, then check if it moved yet
				if(piece instanceof Rook && piece.col == Board.MIN_COL && piece.row == this.row && piece.color == this.color && piece.moved == false) { 
					rook = piece;
				}
			}
		}

		if(rook != null && !pieceIsOnStraightLine(rook.preCol, rook.preRow, simPieces)) {  //check if theres any pieces between rook and king

			return rook;
		}

		return null;
	}

	public void updatePosition() {
		//check if castling occured, and move the rook as well if so
		Piece rook = castlingPiece(col, row, GamePanel.simPieces);
		super.updatePosition();
		if(rook != null) { //if possible to castle, then move rook
			if(rook.col == Board.MAX_COL-1) { //right side rook castling
				rook.col = this.col-1; //castle one space to the left of king
			}
			else { //left side rook castling
				rook.col = this.col + 1;
			}
			rook.updatePosition();
		}
	}

	public boolean inCheck() {
		return isAttacked(getPreCol(), getPreRow(), color);
	}

}
