package server.temps;

public class ThreadInterrupteur extends Thread {
	private Thread	t;
	private long	temps;

	public ThreadInterrupteur(Thread t, long temps) {
		super();
		this.t = t;
		this.temps = temps;
	}

	@Override
	public void run() {
		try {
			sleep(temps -1);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		t.interrupt();

	}

}
