package server;

import java.io.IOException;
import java.util.Observable;

import enums.PartieState;

public class GameThread extends Observable implements Runnable {
	private Server				server;
	private PartieState			partieState	= PartieState.debut;
	private static final long	troisMin	= 100000;//1800000;
	private static final long	cinqMin		= 120000;//3000000;
	

	public GameThread(Server server) {
		this.server = server;
	}

	public PartieState getPartieState() {
		return partieState;
	}

	public void setPartieState(PartieState partieState) {
		this.partieState = partieState;
	}

	private void debutRecherche() throws InterruptedException {
		System.out.println("recherche");
		partieState = PartieState.recherche;
		notifyObservers();
		wait(cinqMin);
	}

	private void debutSoumission() throws InterruptedException {
		System.out.println("soumission");
		partieState = PartieState.soumission;
		notifyObservers();
		wait(troisMin);
	}

	private void debutResultat() throws IOException, InterruptedException {
		System.out.println("resultat");
		partieState = PartieState.resultat;
		notifyObservers();
		wait();
	}

	@Override
	public void run() {
		try {
			while (!server.isFini()) {
				debutRecherche();
				debutSoumission();
				debutResultat();
			}
		} catch (InterruptedException | IOException e) {
			e.printStackTrace();
		}
	}
	
	public void start(){
		new Thread(this).start();
	}

}
