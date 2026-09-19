package main;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.util.ArrayList;

import javax.swing.JPanel;

import piece.Bishop;
import piece.ColorSquare;
import piece.King;
import piece.Knight;
import piece.Pawn;
import piece.Piece;
import piece.Queen;
import piece.Rook;

public class GamePanel extends JPanel implements Runnable{
	public static final int WIDTH = 1100;
	public static final int HEIGHT = 800;
	final int FPS = 60;
	Thread gameThread;
	Board board;
	Mouse mouse;
	
	//current players turn
	ColorSquare currentColor;
	
	//Pieces
	private ArrayList<Piece> pieces = new ArrayList<>();
	public static ArrayList<Piece> simPieces = new ArrayList<>();
	ArrayList<Piece> whitePromoPieces = new ArrayList<>();
	ArrayList<Piece> blackPromoPieces = new ArrayList<>();
	Pawn promotionPiece = null;
	
	Piece activeP; //pieces that the player is currently holding
	
	//Booleans 
	boolean canMove;
	boolean validSquare;
	private boolean gameOver;
	private ColorSquare winner;
	
	public GamePanel() {
		setPreferredSize(new Dimension(WIDTH, HEIGHT));
		setBackground(Color.black);
		board = new Board();
		currentColor = ColorSquare.WHITE;
		
		mouse= new Mouse();
		addMouseMotionListener(mouse);
		addMouseListener(mouse);
		
		//setup the pieces
		setPieces();
		copyPieces(pieces, simPieces);
	}
	
	//start a new game thread and launch game
	public void launchGame() {
		gameThread=new Thread(this);
		gameThread.start();
	}
	
	public void setPieces() {
		
		//White pieces
		pieces.add(new Pawn(ColorSquare.WHITE, 0, 6));
		pieces.add(new Pawn(ColorSquare.WHITE, 1, 6));
		pieces.add(new Pawn(ColorSquare.WHITE, 2, 6));
		pieces.add(new Pawn(ColorSquare.WHITE, 3, 6));
		pieces.add(new Pawn(ColorSquare.WHITE, 4, 6));
		pieces.add(new Pawn(ColorSquare.WHITE, 5, 6));
		pieces.add(new Pawn(ColorSquare.WHITE, 6, 6));
		pieces.add(new Pawn(ColorSquare.WHITE, 7, 6));
		pieces.add(new Knight(ColorSquare.WHITE, 1, 7));
		pieces.add(new Knight(ColorSquare.WHITE, 6, 7));
		pieces.add(new Bishop(ColorSquare.WHITE, 2, 7));
		pieces.add(new Bishop(ColorSquare.WHITE, 5, 7));
		pieces.add(new Queen(ColorSquare.WHITE, 3, 7));
		pieces.add(new King(ColorSquare.WHITE, 4, 7));
		pieces.add(new Rook(ColorSquare.WHITE, 0, 7));
		pieces.add(new Rook(ColorSquare.WHITE, 7, 7));

		//White pieces
		pieces.add(new Pawn(ColorSquare.BLACK, 0, 1));
		pieces.add(new Pawn(ColorSquare.BLACK, 1, 1));
		pieces.add(new Pawn(ColorSquare.BLACK, 2, 1));
		pieces.add(new Pawn(ColorSquare.BLACK, 3, 1));
		pieces.add(new Pawn(ColorSquare.BLACK, 4, 1));
		pieces.add(new Pawn(ColorSquare.BLACK, 5, 1));
		pieces.add(new Pawn(ColorSquare.BLACK, 6, 1));
		pieces.add(new Pawn(ColorSquare.BLACK, 7, 1));
		pieces.add(new Knight(ColorSquare.BLACK, 1, 0));
		pieces.add(new Knight(ColorSquare.BLACK, 6, 0));
		pieces.add(new Bishop(ColorSquare.BLACK, 2, 0));
		pieces.add(new Bishop(ColorSquare.BLACK, 5, 0));
		pieces.add(new Queen(ColorSquare.BLACK, 3, 0));
		pieces.add(new King(ColorSquare.BLACK, 4, 0));
		pieces.add(new Rook(ColorSquare.BLACK, 0, 0));
		pieces.add(new Rook(ColorSquare.BLACK, 7, 0));
		
		whitePromoPieces.add(new Rook(ColorSquare.WHITE, 9 ,2));
		whitePromoPieces.add(new Knight(ColorSquare.WHITE, 9,3));
		whitePromoPieces.add(new Bishop(ColorSquare.WHITE, 9,4));
		whitePromoPieces.add(new Queen(ColorSquare.WHITE, 9,5));
		
		blackPromoPieces.add(new Rook(ColorSquare.BLACK, 9 ,2));
		blackPromoPieces.add(new Knight(ColorSquare.BLACK, 9,3));
		blackPromoPieces.add(new Bishop(ColorSquare.BLACK, 9,4));
		blackPromoPieces.add(new Queen(ColorSquare.BLACK, 9,5));
	}
	
	private void copyPieces(ArrayList<Piece> src, ArrayList<Piece> target) {
		target.clear();
		for(int i=0; i<src.size(); i++) {
			target.add(src.get(i));
		}
	}
	
	//create the game loop
	@Override
	public void run() {
		final double NANO_SECONDS_PER_SECOND = 1000000000;
		double drawInterval = NANO_SECONDS_PER_SECOND/FPS;
		double delta = 0;
		long lastTime=System.nanoTime();
		long currentTime;
		
		//keep running until thread is closed (game closed)
		while(gameThread != null) {
			currentTime = System.nanoTime();
			
			delta+= (currentTime-lastTime)/drawInterval; //find how many draw intervals passed since last runtime
			lastTime = currentTime;
			
			if(delta>=1) { //update if passed 1 frame in time
				if(!gameOver) {
					update();
				}
				repaint();
				delta--;
			}
		}
	}
	
	private void update() {
		if(promotionPiece != null) {
			promoting(promotionPiece.getColor() == ColorSquare.WHITE ? whitePromoPieces : blackPromoPieces);
		}
		else if(!gameOver){
			if(mouse.getPressed()) {
				if(activeP == null) {
					//check if mouse clicked on piece
					for(Piece piece : simPieces) {
						if(piece.getColor() == currentColor &&
								piece.getCol() == mouse.getX()/Board.SQUARE_SIZE &&
								piece.getRow() == mouse.getY()/Board.SQUARE_SIZE) {
							activeP= piece; //pick up the piece
						}
					}
				}
				else { //player holding piece
					simulate(); //if player is holding a piece, simulate the move and check
				}
			}
			
			//Mouse button released
			if(!mouse.getPressed()) {
				
				if(activeP != null) {
					
					if(validSquare) { //if the square is valid, then move the piece there
						
						//if hitting a piece, remove it from the list
						Piece hitPiece = activeP.getHittingP();
						if(hitPiece != null) {
							simPieces.remove(hitPiece);
							copyPieces(simPieces, pieces);
						}
						
						activeP.updatePosition();
						
						//check if the piece moved can promote after moving
						promotionPiece = canPromote(activeP);
						if(promotionPiece == null) { //only change player if no promotion happens
							
							changePlayer();
							
						    // Check whether the new player is checkmated
						    
							if(isCheckMate(currentColor)) { //if checkmate, then game over
								gameOver=true;
								
								if(currentColor == ColorSquare.WHITE) {
									winner = ColorSquare.BLACK;
								}
								else {
									winner = ColorSquare.WHITE;
								}
							}
							else if(isStalemate(currentColor)) {
								gameOver=true;
							}
						}
						
						activeP = null;
					}
					else {
						activeP.resetPosition();
						activeP = null;
					}
				}
				
			}
		}
	}
	
	private void simulate() {
		
		canMove = false;
		validSquare = false;
		
		//if piece is held, update its position
		activeP.setX(mouse.getX() - activeP.getPieceSizeX()/2); //center the piece on mouse
		activeP.setY(mouse.getY() - activeP.getPieceSizeY()/2);
		activeP.setCol(activeP.calculateCol(activeP.getX()));
		activeP.setRow(activeP.calculateRow(activeP.getY()));
		
		//Check if the piece is hovering over a reachable square
		if(activeP.canMove(activeP.getCol(), activeP.getRow(), simPieces)) {
			/*
			 * Four conditions:
			 * 1. Cannot move king into check
			 * 2. Can only move to eliminate check if in check
			 * 3. Cannot move a pinned piece
			 * 4. Cannot be in checkmate
			 */
			King king = findKing(currentColor);
			
			//check if the move will still expose the king
			if(!activeP.moveExposeKing(activeP.getCol(), activeP.getRow(), king)) { //if not still in check even after simulated move
				canMove = true;
				
				validSquare = true;
			}
		}
	}
	
	private King findKing(ColorSquare currentColor) {
		King king = null;
		for(Piece piece : simPieces) {
			if(piece instanceof King && piece.getColor() == currentColor) {
				king = (King)piece;
				break;
			}
		}
		return king;
	}
	
	private void changePlayer() {
		//increment all of the pawn counters
		for(Piece piece : simPieces) {
			Pawn p;
			if(piece instanceof Pawn && ((p = (Pawn) piece)).getTwoStepped() == true) {
				if(p.getTwoSteppedCounter() < 1) {
					p.setTwoSteppedCounter(p.getTwoSteppedCounter()+1);
				}
				else {
					//reset the counter, not allowed to en passant anymore
					p.setTwoSteppedCounter(0);
					p.setTwoStepped(false);
				}
			}
		}
		if(currentColor == ColorSquare.WHITE) {
			currentColor = ColorSquare.BLACK;
		}
		else {
			currentColor = ColorSquare.WHITE;
		}
	}
	
	//inherited method for drawing graphics
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D)g;
		
		//Draw board
		board.draw(g2);
		
		//Draw pieces
		for(Piece p : simPieces) {
			p.draw(g2);
		}
		
		if(activeP != null) {
			if(canMove) {
				g2.setColor(Color.white);
				g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.7f));
				g2.fillRect(activeP.getCol()*Board.SQUARE_SIZE, 
						activeP.getRow()*Board.SQUARE_SIZE, 
						Board.SQUARE_SIZE, Board.SQUARE_SIZE);
				g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
			}
			
			activeP.draw(g2); //draw the piece again to be above the highlight
		}
		
		//draw status messages
		g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		g2.setFont(new Font("Book Antiqua", Font.BOLD, 40));
		g2.setColor(Color.white);
		
		if(promotionPiece != null) {
			
			ArrayList<Piece> promoPieces;
			
			if(promotionPiece.getColor() == ColorSquare.WHITE) {
				 promoPieces = whitePromoPieces;
				 g2.drawString("Promote to:", Board.BOARD_SIDE_LENGTH+40, HEIGHT/5);
			}
			else {
				promoPieces = blackPromoPieces;
				g2.drawString("Promote to:", Board.BOARD_SIDE_LENGTH+40, 4*HEIGHT/5);
			}
	
			for(Piece piece : promoPieces) {
				g2.drawImage(piece.image, piece.getX(), piece.getY(),
						Board.SQUARE_SIZE, Board.SQUARE_SIZE, null);
			}
		}
		
		if(gameOver) {
			if(winner != null) {
				g2.setFont(new Font("Arial", Font.BOLD, 40));
				g2.setColor(Color.green);
				g2.drawString("Checkmate!", Board.BOARD_SIDE_LENGTH+40, HEIGHT/2);
				g2.drawString(winner.toString() + " wins!", Board.BOARD_SIDE_LENGTH+40, 2*HEIGHT/3);
			}
			else { //No winner. Stalemate occured
				g2.drawString("Stalemate!", Board.BOARD_SIDE_LENGTH+40, HEIGHT/2);
			}
		}
		else if(currentColor == ColorSquare.WHITE) {
			g2.drawString("White's turn", Board.BOARD_SIDE_LENGTH+40, 4*HEIGHT/5);
		}
		else {
			g2.drawString("Black's turn", Board.BOARD_SIDE_LENGTH+40, HEIGHT/5);
		}
	}
	
	private Pawn canPromote(Piece piece) {
		if(piece instanceof Pawn) {
			if((piece.getColor() == ColorSquare.WHITE 
					&& piece.getPreRow() == Board.MIN_ROW)
					|| (piece.getColor() == ColorSquare.BLACK
					&& piece.getPreRow() == Board.MAX_ROW - 1)) {
				return (Pawn) piece;
			}
		}
		return null;
	}
	
	public void testPromotion() {
		pieces.add(new Pawn(ColorSquare.WHITE, 0, 1));
		pieces.add(new Pawn(ColorSquare.BLACK, 7, 6));
		pieces.add(new King(ColorSquare.WHITE, 0, 2));
		pieces.add(new King(ColorSquare.BLACK, 7, 5));
		
		whitePromoPieces.add(new Rook(ColorSquare.WHITE, 9 ,2));
		whitePromoPieces.add(new Knight(ColorSquare.WHITE, 9,3));
		whitePromoPieces.add(new Bishop(ColorSquare.WHITE, 9,4));
		whitePromoPieces.add(new Queen(ColorSquare.WHITE, 9,5));
		
		blackPromoPieces.add(new Rook(ColorSquare.BLACK, 9 ,2));
		blackPromoPieces.add(new Knight(ColorSquare.BLACK, 9,3));
		blackPromoPieces.add(new Bishop(ColorSquare.BLACK, 9,4));
		blackPromoPieces.add(new Queen(ColorSquare.BLACK, 9,5));
	}
	public void promoting(ArrayList<Piece> promoPieces) {
		
		if(mouse.getPressed()) {
			for(Piece piece : promoPieces) {
				if(piece.getPreCol() == mouse.getX()/Board.SQUARE_SIZE
						&& piece.getPreRow() == mouse.getY()/Board.SQUARE_SIZE) {
					//select the piece that the mouse is pointing at
					if(piece instanceof Rook) {
						simPieces.add(new Rook(promotionPiece.getColor(), promotionPiece.getPreCol(), promotionPiece.getPreRow()));
					}
					else if(piece instanceof Knight) {
						simPieces.add(new Knight(promotionPiece.getColor(), promotionPiece.getPreCol(), promotionPiece.getPreRow()));
					}
					else if(piece instanceof Queen) {
						simPieces.add(new Queen(promotionPiece.getColor(), promotionPiece.getPreCol(), promotionPiece.getPreRow()));
					}
					else if (piece instanceof Bishop) {
						simPieces.add(new Bishop(promotionPiece.getColor(), promotionPiece.getPreCol(), promotionPiece.getPreRow()));
					}
					simPieces.remove(promotionPiece);
					copyPieces(simPieces, pieces);
					promotionPiece = null;
					activeP = null;
					changePlayer();
				}
			}
		}
	}
	
	private boolean isCheckMate(ColorSquare color) {

		King king = findKing(color);

		// Can't be checkmate unless the king is actually in check
		if(!king.inCheck()) {
			return false;
		}

		// King is in check and there are no legal moves, then it is in checkmate
		return noLegalMoves(color, king);
	}
	
	//checks whether or not the king has any legal moves or not
	private boolean noLegalMoves(ColorSquare color, King king) {
		// Check every piece belonging to the player
				for(Piece piece : new ArrayList<>(simPieces)) {

					if(piece.getColor() != color) {
						continue;
					}

					// Try every square on the board
					for(int col = Board.MIN_COL; col < Board.MAX_COL; col++) {

						for(int row = Board.MIN_ROW; row < Board.MAX_ROW; row++) {

							// Can the piece physically move there?
							if(piece.canMove(col, row, simPieces)) {

								// Would that move leave/expose the king to check?
								if(!piece.moveExposeKing(col, row, king)) {

									// At least one legal move exists
									return false;
								}
							}
						}
					}
				}
				return true;
	}
	
	public boolean isStalemate(ColorSquare color) {
		if(simPieces.size() <=2) { //only two kings left then stalemate occured
			return true;
		}
		
		//threefold repetition
		
		
		King king = findKing(color);
		if(!king.inCheck()) {
			return noLegalMoves(color, king);
		}
		return false;
	}
	
	public ArrayList<Piece> getSimPieces() {
		return simPieces;
	}
	
	public ArrayList<Piece> getPieces() {
		return pieces;
	}
	
}
 