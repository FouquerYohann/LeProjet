package maintest;

import java.awt.Font;
import java.awt.Graphics;
import java.util.Observable;
import java.util.Observer;

import server.temps.Chrono;

import javax.swing.JPanel;

public class ChronometreLayout extends JPanel implements Observer{

	private static final long	serialVersionUID	= 3161800316683029596L;
	private Chrono temps;
	private int value=0;
	
	public ChronometreLayout(Chrono temps) {
		super();
		this.temps = temps;
	}
	
	public void paintComponent(Graphics g) {
		Font titleFont = new Font("SansSerif", Font.BOLD, 20);
	}

	@Override
	public void update(Observable o, Object arg) {
		value=temps.getTemps();
		refresh();
	}
	
	
	public void refresh(){
//		this.remove(pane);
//		pane=paintDot(grille.getX(),grille.getY());
//
//		this.add(pane);
//		this.revalidate();
//		this.repaint();
	}
}
