package maintest;

import server.temps.ThreadInterrupteur;

public class TestThread {

	public static void main(String[] args) throws Throwable {
		Thread t = new Thread() {
			@Override
			public void run() {
				double temps = System.currentTimeMillis();

				try {
					sleep(20000);
				} catch (InterruptedException e) {
					System.out.println("Reveillé au bout de "
							+ (System.currentTimeMillis() - temps));
				}
			}
		};

		t.start();
		new ThreadInterrupteur(t,1000).start();

	}

}