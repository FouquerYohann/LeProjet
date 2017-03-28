package server;

import java.io.IOException;
import java.util.Observable;

import enums.PartieState;

public class GameThread extends Observable implements Runnable {
	private Server				server;
	private PartieState			partieState	= PartieState.debut;
	private static final long	troisMin	= 1800000;
	private static final long	cinqMin		= 3000000;

	public GameThread(Server server) {
		this.server = server;
	}

	public PartieState getPartieState() {
		return partieState;
	}

	public void setPartieState(PartieState partieState) {
		this.partieState = partieState;
	}

	public void debutRecherche() throws InterruptedException {
		partieState = PartieState.recherche;
		notifyObservers();
		wait(cinqMin);
	}

	public void debutSoumission() throws InterruptedException {
		partieState = PartieState.soumission;
		notifyObservers();
		wait(troisMin);
	}

	public void debutResultat() throws IOException {
		partieState = PartieState.resultat;
		notifyObservers();
		server.resultat();
	}
	@Override
	public void run() {
		try {
			debutRecherche();
			debutSoumission();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
