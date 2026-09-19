package piece;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import main.Board;
import main.GamePanel;

public abstract class Piece {
	public BufferedImage image;
	protected int x, y;
	protected int col, row, preCol, preRow;
	protected ColorSquare color;
	protected int pieceSizeX;
	protected int pieceSizeY;
	protected Piece hittingP;
	protected boolean moved;
	
	public Piece(ColorSquare color, int col, int row) {
		this.color = color;
		this.setCol(col);
		this.setRow(row);
		x = calculateX(col);
		y= calculateY(row);
		setPreCol(col);
		setPreRow(row);
		pieceSizeX=Board.SQUARE_SIZE;
		pieceSizeY=Board.SQUARE_SIZE;
	}
	
	public BufferedImage getImage(String imagePath) {
		BufferedImage image = null;
		
		try {
			image = ImageIO.read(getClass().getResourceAsStream(imagePath + ".png"));
			
		} catch(IOException e) {
			e.printStackTrace();
		}
		return image;
	}
	
	private int calculateX(int col) {
		return col * Board.SQUARE_SIZE;
	}
	
	public int calculateY(int row) {
		return row * Board.SQUARE_SIZE;
	}
	
	public int calculateCol(int x) {
		return (x+pieceSizeX/2)/Board.SQUARE_SIZE;
	}
	
	public int calculateRow(int y) {
		return (y+pieceSizeY/2)/Board.SQUARE_SIZE;
	}
	
	public void updatePosition() {
		x = calculateX(col);
		y = calculateY(row);
		preCol = calculateCol(x);
		preRow = calculateRow(y);
		moved = true;
	}
	
	//check if the square is attacked with respect to their color (for king checks and move legality)
	public static boolean isAttacked( int targetCol, int targetRow, ColorSquare color) {

		for(Piece piece : GamePanel.simPieces) {

			if(piece.getColor() == color) { //if same color piece, does not attack
				continue;
			}

			// Kings attack adjacent squares only
			// Castling is not an attack so check if king
			if(piece instanceof King) {

				int colDifference = Math.abs(piece.getPreCol() - targetCol);

				int rowDifference = Math.abs(piece.getPreRow() - targetRow);

				if(colDifference <= 1 &&
						rowDifference <= 1 &&
						!(colDifference == 0 && rowDifference == 0)) {

					return true;
				}
			}
			else if(piece.canMove(targetCol, targetRow, GamePanel.simPieces)) {
				return true;
			}
		}

		return false;
	}

	
	public abstract boolean canMove(int targetCol, int targetRow, ArrayList<Piece> simPieces);
	
	protected boolean isWithinBoard(int targetCol, int targetRow) {
		if(targetCol>=0 && targetCol <= 7 && targetRow>=0 && targetRow <=7) {
			return true;
		}
		return false;
	}
	
	public Piece calculateHittingP(int targetCol, int targetRow, ArrayList<Piece> simPieces) {
		for(Piece piece : simPieces) {
			if(piece.col == targetCol && piece.row == targetRow && piece != this) {
				return piece;
			}
		}
		return null;
	}
	
	public Piece getHittingP() {
		return hittingP;
	}
	
	//checks if the move will expose the king to danger
	public boolean moveExposeKing(int targetCol, int targetRow, King king) {
		
		boolean kingExposed;
		
		int tempCol = col;
		int tempRow = row;
		int tempPreCol = preCol;
		int tempPreRow = preRow;
	    
	    // Find a piece that would be captured
	    Piece capturedPiece = calculateHittingP(
	            targetCol,
	            targetRow,
	            GamePanel.simPieces
	    );

	    // Temporarily remove captured enemy piece
	    if(capturedPiece != null) {
	        GamePanel.simPieces.remove(capturedPiece);
	    }
	    
	    //simulate moving to the target
	    col=targetCol;
		row=targetRow;
	    preCol = targetCol;
	    preRow = targetRow;

		
	    //check if king will still be exposed to danger
		kingExposed = king.inCheck();
		
		//rollback the updates
		col = tempCol;
		row = tempRow;
		preCol = tempPreCol;
		preRow = tempPreRow;
		
		//restore captured piece
		if(capturedPiece != null) {
			GamePanel.simPieces.add(capturedPiece);
		}
		
		return kingExposed;
	}
	
	public boolean isValidSquare(int targetCol, int targetRow, ArrayList<Piece> simPieces) {
		
		//find any pieces that moving to the target hits
		hittingP = calculateHittingP(targetCol, targetRow, simPieces);
		
		if(hittingP == null) { //nothing in square, VACANT
			return true;
		}
		else { //square occupied
			if(hittingP.color != this.color) { //not same color, can capture piece
				return true;
			}
			else { //if is the same color then can't capture piece
				hittingP = null;
			}
		}
		return false;
	}
	
	public boolean isSameSquare(int targetCol, int targetRow) {
		if(targetCol == preCol && targetRow == preRow) {
			return true;
		}
		return false;
	}
	/*Precondition: Either the target row or column is the same as the piece and not both the same
	checks if there is a target piece on the line of the source piece to the target
	*/
	public boolean pieceIsOnStraightLine(int targetCol, int targetRow, ArrayList<Piece> simPieces) {
		
		//check if the the target row is the same
		if(targetRow==preRow) {
			//check if to the left or the right
			if(targetCol>preCol) { //to the right
				for(int c=preCol+1; c<targetCol; c++) {
					for(Piece piece : simPieces) {
						if(piece.getPreRow() == targetRow && piece.getPreCol() == c) {
							System.out.println(
								    "BLOCKING PIECE FOUND: " +
								    piece.getClass().getSimpleName() +
								    " at col=" + piece.getPreCol() +
								    ", row=" + piece.getPreRow()
								);
							return true;
						}
					}
				}
			}
			else { //to the left
				for(int c=preCol-1; c>targetCol; c--) {
					for(Piece piece : simPieces) {
						if(piece.getPreRow() == targetRow && piece.getPreCol() == c) {
							System.out.println(
								    "BLOCKING PIECE FOUND: " +
								    piece.getClass().getSimpleName() +
								    " at col=" + piece.getPreCol() +
								    ", row=" + piece.getPreRow()
								);
							return true;
						}
					}
				}
			}
		}
		else { //target column is same
			if(targetRow>preRow) {
				for(int c=preRow+1; c<targetRow; c++) {
					for(Piece piece : simPieces) {
						if(piece.getPreCol() == targetCol && piece.getPreRow() == c) {
							System.out.println(
								    "BLOCKING PIECE FOUND: " +
								    piece.getClass().getSimpleName() +
								    " at col=" + piece.getPreCol() +
								    ", row=" + piece.getPreRow()
								);
							return true;
						}
					}
				}
			}
			else { //to the left
				for(int c=preRow-1; c>targetRow; c--) {
					for(Piece piece : simPieces) {
						if(piece.getPreCol() == targetCol && piece.getPreRow() == c) {
							System.out.println(
								    "BLOCKING PIECE FOUND: " +
								    piece.getClass().getSimpleName() +
								    " at col=" + piece.getPreCol() +
								    ", row=" + piece.getPreRow()
								);
							return true;
						}
					}
				}
			}
		}
		return false; //if nothing blocking return false
	}
	
	//Precondition: target piece is on the same diagonal as piece
	public boolean pieceIsOnDiagonalLine(int targetCol, int targetRow, ArrayList<Piece> simPieces) {
		
		if(targetRow < preRow) { //up
			
			if(targetCol < preCol) {//up left
				for(int c = preCol-1, r = preRow - 1; c>targetCol; c--, r--) {
					for(Piece piece : simPieces) {
						if(piece.getRow() == r && piece.getCol() == c) {
							return true;
						}
					}
				}
			}
			else { //up right
				for(int c = preCol+1, r = preRow - 1; c<targetCol; c++, r--) {
					for(Piece piece : simPieces) {
						if(piece.getRow() == r && piece.getCol() == c) {
							return true;
						}
					}
				}
			}
		}
		else { //down
			if(targetCol < preCol) {//down left
				for(int c = preCol-1, r = preRow + 1; c>targetCol; c--, r++) {
					for(Piece piece : simPieces) {
						if(piece.getRow() == r && piece.getCol() == c) {
							return true;
						}
					}
				}
			}
			else { //down right
				for(int c = preCol+1, r = preRow + 1; c<targetCol; c++, r++) {
					for(Piece piece : simPieces) {
						if(piece.getRow() == r && piece.getCol() == c) {
							return true;
						}
					}
				}
			}
		}
		return false;
	}
	
	public void resetPosition() {
		col = preCol;
		row = preRow;
		x = calculateX(col);
		y = calculateY(row);
	}
	
	public ColorSquare getColor() {
		return this.color;
	}
	
	public int getX() {
		return x;
	}
	
	public int getY() {
		return y;
	}
	
	public int setX(int x) {
		return this.x=x;
	}
	
	public int setY(int y) {
		return this.y=y;
	}

	public int getPieceSizeX() {
		return pieceSizeX;
	}

	public int getPieceSizeY() {
		return pieceSizeY;
	}
	
	public void draw(Graphics2D g2) {
		g2.drawImage(image, x, y, pieceSizeX, pieceSizeY, null);
	}

	public int getCol() {
		return col;
	}

	public void setCol(int col) {
		this.col = col;
	}

	public int getRow() {
		return row;
	}

	public void setRow(int row) {
		this.row = row;
	}

	public int getPreCol() {
		return preCol;
	}

	public void setPreCol(int preCol) {
		this.preCol = preCol;
	}

	public int getPreRow() {
		return preRow;
	}

	public void setPreRow(int preRow) {
		this.preRow = preRow;
	}
}
