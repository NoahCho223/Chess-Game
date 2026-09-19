package main;

import java.awt.Color;
import java.awt.Graphics2D;

import piece.ColorSquare;

public class Board {
	public final static int MAX_COL = 8;
	public final static int MAX_ROW = 8;
	public final static int MIN_COL = 0;
	public final static int MIN_ROW = 0;
	public static final int SQUARE_SIZE = 100;
	public static final int HALF_SQUARE_SIZE = SQUARE_SIZE/2;
	public static final int BOARD_SIDE_LENGTH = SQUARE_SIZE * MAX_COL;
	
	public void draw(Graphics2D g2) {
		
		//draw the chess board
		for(int row = 0; row < MAX_ROW; row++) {
			
			for(int col = 0; col < MAX_COL; col++) {
				
				//alternate the colors drawn
				if((row+col)%2 == 0) { //white square
					g2.setColor(Color.decode(ColorSquare.WHITE.colorCode));
				}
				else { //black square
					g2.setColor(Color.decode(ColorSquare.BLACK.colorCode));
				}
				
				g2.fillRect(col*SQUARE_SIZE, row*SQUARE_SIZE, SQUARE_SIZE, SQUARE_SIZE);
			}
			
		}
	}
}
