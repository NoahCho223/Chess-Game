package piece;

import java.util.ArrayList;

import main.GamePanel;

public class Pawn extends Piece {
	public final int BLACKSTARTINGRANK = 1;
	public final int WHITESTARTINGRANK = 6;
	private boolean twoStepped;
	private int twoSteppedCounter=0;

	public Pawn(ColorSquare color, int col, int row) {
		super(color, col, row);
		
		if(color == ColorSquare.WHITE) {
			image = getImage("/piece/w-pawn");
		}
		
		else {
			image = getImage("/piece/b-pawn");
		}
	}

	@Override
	public boolean canMove(int targetCol, int targetRow, ArrayList<Piece> simPieces) {
		if(isWithinBoard(targetCol, targetRow) && !isSameSquare(targetCol, targetRow)) {
			//move based on the color of the piece
			int moveValue=1;
			
			//move value magnitude 2 when on the starting rank, 1 otherwise
			
			if(color == ColorSquare.BLACK) { //black pawn
				if(!moved) {
					moveValue = 2;
				}
			}
			else { //white pawn
				if(!moved) {
					moveValue = 2;
				}
				moveValue = -moveValue;
			}
			
			//check the hitting piece
			hittingP = calculateHittingP(targetCol, targetRow, simPieces);
			
			if(targetCol == preCol && (targetRow == preRow + moveValue || targetRow == preRow + Integer.signum(moveValue)) 
					&& hittingP == null
					&& !pieceIsOnStraightLine(targetCol, targetRow, simPieces)) {
				return true;
			}
			
			//Diagonal movement captures
			if(Math.abs(targetCol - preCol) == 1 && targetRow == preRow + Integer.signum(moveValue) && hittingP != null &&
					hittingP.getColor() != color) {
				return true;
			}
			
			//En Passant
			if(Math.abs(targetCol - preCol) == 1 && targetRow == preRow + Integer.signum(moveValue)) {
				for(Piece piece : GamePanel.simPieces) {
					if(piece instanceof Pawn && ((Pawn) piece).getTwoStepped() && piece.getPreCol() == targetCol
							&& piece.getPreRow() == preRow && piece.getColor() != color) {
						hittingP = piece;
						return true;
					}
				}
			}
		}
		return false;
	}
	
	//pawn overridden updatePosition() that checks for en passent
	public void updatePosition() {
		
		//Check for en passant
		if(Math.abs(row-preRow) == 2) {
			twoStepped = true;
		}
		
		super.updatePosition();
	}
	
	public boolean getTwoStepped() {
		return twoStepped;
		
	}
	
	public int getTwoSteppedCounter() {
		return twoSteppedCounter;
	}
	
	public int setTwoSteppedCounter(int twoSteppedCounter) {
		return this.twoSteppedCounter=twoSteppedCounter;
	}

	public void setTwoStepped(boolean twoStepped) {
		this.twoStepped=twoStepped;
		
	}

}
