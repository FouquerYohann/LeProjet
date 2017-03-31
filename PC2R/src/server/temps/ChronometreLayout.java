package server.temps;

import java.awt.Font;
import java.awt.Graphics;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class ChronometreLayout extends JPanel implements Observer{

	private static final long	serialVersionUID	= 3161800316683029596L;
	private Chrono temps;
	private int value;
	private int x,y;
	private JFrame jf = new JFrame();
	
	public ChronometreLayout(Chrono temps) {
		super();
		this.temps = temps;
		value=temps.getTemps();
		x=0;
		y=120;
		temps.addObserver(this);
		jf.getContentPane().add(this);
		jf.setSize(250, 180);
		jf.setResizable(false);
		jf.setVisible(true);

	}
	
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		Font timeFont = new Font("SansSerif", Font.BOLD, 120);
		g.setFont(timeFont);
		g.drawString(value+"", x, y);
	}

	@Override
	public void update(Observable o, Object arg) {
		value=temps.getTemps();
		refresh();
	}
	
	
	public void close(){
		jf.setVisible(false);
		jf.dispose();
	}
	
	public void refresh(){

		this.revalidate();
		this.repaint();
	}
	
	public static void main(String[] args)throws Throwable {
		Chrono c = new Chrono();
		
		
		c.start();
		Thread.sleep(1000);
		ChronometreLayout cl=new ChronometreLayout(c);
		Thread.sleep(2500);
//		c.stop();
		
		Thread.sleep(2000);
		cl.close();
		c.start();
		Thread.sleep(1200);
		cl=new ChronometreLayout(c);
		
	}
}
