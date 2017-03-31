package server.temps;

import java.util.Observable;

public class Chrono extends Observable {
	private double	time;

	private Thread t;
	public void start() {
		time = System.currentTimeMillis();
		t=new Thread() {
			public void run() {
				while (true) {

					try {
						sleep(1000);
					} catch (InterruptedException e) {
						return;
					}
					setChanged();
					notifyObservers();
				}
			};
		};
		t.start();
	}

	public int getTemps() {
		return (int) ((System.currentTimeMillis() - time) / 1000);
	}

	public void stop(){
		t.interrupt();
	}
	
}
