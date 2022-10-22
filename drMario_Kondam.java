/*
 * Siddharth Kondam 
 * This program simulates the game of Dr.Mario. Through a recursive function, all similar germs that are neighbors, 
 * neighbor's neighbors, etc are removed. Another method shifts the panels that are above the removed panels down until they reach a filled panel. 
 */

import javax.imageio.ImageIO;
import javax.swing.*;

import java.awt.*;
import java.util.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.*;

public class drMario_Kondam extends JFrame{

	private final String[] FILE_NAMES = {"red.jpg","blue.jpg","yellow.jpg","purple.jpg","green.jpg","pink.jpg"};
	private final int[] VERT_DISP = {-1,-1,-1,0,0,1,1,1};  //determines neighbors
	private final int[] HORZ_DISP = {-1,0,1,-1,1,-1,0,1};

	private PicPanel[][] germs;

	private int turnsLeft = 10;

	private JLabel message;

	private final int colorPanelLength = 485;
	private final int colorPanelHeight = 500;
	private int dim;							//dimensions for the grid (user input)

	public drMario_Kondam(){

		dim = Integer.parseInt(JOptionPane.showInputDialog(null,"What is the dimension of the matrix?"));

		germs = new PicPanel[dim][dim];

		setLayout(null);
		setSize(500,700);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		//stores all panels in a grid layout
		JPanel colorPanel = new JPanel();
		colorPanel.setLayout(new GridLayout(dim,dim,5,5));
		colorPanel.setBackground(Color.black);
		colorPanel.setBounds(1,0,colorPanelLength ,colorPanelHeight);
		colorPanel.setBorder(BorderFactory.createLineBorder(Color.black));

		//the message to be displayed
		JPanel messagePanel = new JPanel();
		messagePanel.setLayout(null);
		messagePanel.setBounds(0,500,500,200);
		messagePanel.setBackground(Color.white);

		message = new JLabel("Turns Left: "+turnsLeft);
		message.setFont(new Font("Comic Sans",Font.ITALIC,20));
		message.setBounds(175,-125,175,300);
		messagePanel.add(message);

		//randomly assign colors to and make the panels
		Random r = new Random();
		for(int row = 0; row < dim; row++){
			for(int col = 0; col < dim; col++){
				germs[row][col]= new PicPanel(row,col,FILE_NAMES[r.nextInt(FILE_NAMES.length)]);				
				colorPanel.add(germs[row][col]);
			}
		}

		add(colorPanel);
		add(messagePanel);
		setVisible(true);

	}

	//removes germ at spot r, c as long as it contains the file name represented by toRemove
	//recursively repeats for all neighbors and neighbor's neighbors.
	//updates the total points earned.

	private void removeGerms(int r, int c, String toRemove){
		toRemove = germs[r][c].fname;
		germs[r][c].clearPanel();

		//checks for each of the 8 positions for every grid value
		for(int i = 0; i < 8; i ++) {

			int horzVal = r + HORZ_DISP[i];
			int vertVal = c+ VERT_DISP[i];

			//checks if values are inside dimensions and potential moves are valid
			if((horzVal<dim && horzVal>=0) && (vertVal<dim && vertVal>=0) && 
					!germs[horzVal][vertVal].isEmpty() && germs[horzVal][vertVal].fname.equals(toRemove)) 
				removeGerms(r + HORZ_DISP[i],c + VERT_DISP[i], toRemove);
		}
	}

	//examines the entire grid and makes germs fall as necessary	
	private void fall() {

		for(int col = 0; col < dim; col ++) {

			int fall = dim - 1; 

			for(int row = dim - 1; row >= 0; row--) {

				if(germs[row][col].isEmpty()) {
					fall = row; 

					//checks till the lowest row value in a column
					while(row >= 0 && germs[row][col].isEmpty()) 
						row--; 

					//sets the image to the initial row and column that was examined empty
					if(row >= 0) {
						germs[fall][col].setImage(germs[row][col].fname);
						germs[row][col].clearPanel();
						fall--; 
					}
				}
				else
					fall--;
			}
		}
	}

	//type of panel that will accept mouse input
	public class PicPanel extends JPanel implements MouseListener{
		private int row;
		private int col;

		private String fname;
		private Image image;

		public PicPanel(int r, int c, String name){

			row = r;
			col = c;

			this.addMouseListener(this);
			setImage(name);
		}

		//places a germ based off the file name at the given panel
		public void setImage(String name) {
			fname = name;

			int newLength = (colorPanelLength-(dim+1)*5)/dim;
			int newHeight = (colorPanelHeight-(dim+1)*5)/dim;

			try {
				image = ImageIO.read(new File(fname));
				image = image.getScaledInstance(newLength, newHeight, Image.SCALE_DEFAULT);
			} catch (IOException ioe) {
				System.out.println("Can't open "+ fname);
				System.exit(-1);
			}		
			repaint();
		}

		// "blanks" out a panel
		public void clearPanel() {
			fname = null;
			image = null;
			repaint();
		}

		public boolean isEmpty() {
			return fname == null;
		}

		public void paintComponent(Graphics g){
			super.paintComponent(g);
			if(image != null)
				g.drawImage(image,0,0,this);
		}

		//the only method you will fill in this class
		public void mouseClicked(MouseEvent arg0) {

			if(!isEmpty() && turnsLeft!=0) {
				removeGerms(row,col,fname);
				fall();
				turnsLeft--; 
				message.setText("Turns Left " + turnsLeft);
			}
		}

		@Override
		public void mousePressed(MouseEvent e) {
			// TODO Auto-generated method stub

		}

		@Override
		public void mouseReleased(MouseEvent e) {
			// TODO Auto-generated method stub

		}

		@Override
		public void mouseEntered(MouseEvent e) {
			// TODO Auto-generated method stub

		}

		@Override
		public void mouseExited(MouseEvent e) {
			// TODO Auto-generated method stub

		}
	}


	public static void main(String[] args){
		new drMario_Kondam ();

	}
}
