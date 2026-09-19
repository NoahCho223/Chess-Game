package main;

import javax.swing.JFrame;

//TODO: implement threefold repetition

public class Main {
	public static void main(String args[]) {
		JFrame window = new JFrame("Chess Game");
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		window.setResizable(false);
		
		//add the Gamepanel to the window
		GamePanel gp = new GamePanel();
		window.add(gp);
		window.pack();
		
		
		window.setLocationRelativeTo(null);
		window.setVisible(true);
		
		gp.launchGame();
		
	}
}
