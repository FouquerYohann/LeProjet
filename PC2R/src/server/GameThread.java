package server;

import java.io.IOException;
import java.util.Observable;

import enums.PartieState;

public class GameThread extends Observable implements Runnable {
    private Server           server;
    private PartieState      partieState = PartieState.debut;
    public static final long troisMin    = 100000;            // 180000;
    public static final long cinqMin     = 120000;            // 300000;

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
	setChanged();
	notifyObservers();
	synchronized (server) {
	    server.wait(cinqMin);
	}

    }

    private void debutSoumission() throws InterruptedException {
	System.out.println("soumission");
	partieState = PartieState.soumission;
	setChanged();
	notifyObservers();
	synchronized (server) {
	    server.wait(troisMin);
	}
    }

    private void debutResultat() throws IOException, InterruptedException {
	System.out.println("resultat");
	partieState = PartieState.resultat;
	setChanged();
	notifyObservers();
	// synchronized (server) {
	// server.wait();
	// }
    }

    @Override
    public void run() {
	try {
	    while (!server.isFini()) {
		debutRecherche();
		debutSoumission();
		debutResultat();
		System.out.println("TOUR");
	    }
	} catch (InterruptedException | IOException e) {
	    e.printStackTrace();
	}
    }

    public void start() {
	new Thread(this).start();
    }

}
