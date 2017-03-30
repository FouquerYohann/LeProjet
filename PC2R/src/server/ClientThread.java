package server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

import enums.ClientState;
import enums.PartieState;
import enums.Raisons;
import server.staticvalue.StaticRequete;
import service.ScrabbleService;

public class ClientThread extends Thread {
    private String          nom         = null;
    private Socket          sock;
    private ScrabbleService sc          = null;
    private ClientState     clientState = ClientState.connecting;
    private Server          server;
    private BufferedReader  inBR        = null;
    private BufferedWriter  outBW       = null;
    private int             score       = 0;
    private ScrabbleService scJoue      = null;
    private int             scoreTour   = 0;

    public ClientThread(Socket sock, Server server) {
	super();
	System.out.println("client connected " + sock.getInetAddress());
	this.sock = sock;
	this.server = server;
	try {
	    inBR = new BufferedReader(new InputStreamReader(
		    sock.getInputStream()));
	    outBW = new BufferedWriter(new OutputStreamWriter(
		    sock.getOutputStream()));
	} catch (IOException e) {
	    disconnect();
	}
    }

    @Override
    public void run() {
	try {
	    handle(sock);
	} catch (Throwable e) {
	    e.printStackTrace();
	    System.err.println(e.getMessage());
	    disconnect();
	}
    }

    public void handle(Socket sock) throws IOException {

	while (true) {
	    String received = inBR.readLine();
	    String[] tok = received.split("/");
	    System.out.println("recu [" + tok.length + "]" + received);

	    if (tok.length == 0) {
		System.out.println("error");
		continue;
	    }
	    String requete = tok[0];
	    // CONNEXION
	    if (requete.equals(StaticRequete.connexion) && tok.length == 2) {
		try {
		    setNom(tok[1]);
		} catch (Exception e) {
		    refus();
		}
		clientState = ClientState.playing;
		write(server.retourConnection() + score + "/"
		        + server.getPartieState().getValue() + "/"
		        + server.getTempsTour() + "/");
		server.connecte(tok[1]);

	    } else if (requete.equals(StaticRequete.sort) && tok.length == 2) {
		if (tok[1].equals(nom)) {
		    throw new Error("disconnect");
		}
	    } else if (requete.equals(StaticRequete.trouve) && tok.length == 2) {
		trouve(tok[1]);

	    } else {
		// DEFAULT
		error();

	    }

	}

    }

    public void refus() {
	System.out.println(StaticRequete.refus);
	write(StaticRequete.refus + "/");
	throw new Error("refus");
    }

    private void trouve(String grille) {
	ScrabbleService capture = new ScrabbleImpl();
	Raisons raisonsRefus;
	if (sc.raisonValide(grille, capture) == Raisons.none) {

	    int scortemp = capture.getPoints() - sc.getPoints();
	    if (scortemp > scoreTour) {
		scoreTour = scortemp;
		scJoue = capture;

		if (server.getPartieState() == PartieState.recherche) {
		    write(StaticRequete.rvalide + "/");
		} else if (server.getPartieState() == PartieState.soumission) {
		    write(StaticRequete.svalide + "/");
		}
		return;
	    } else {
		raisonsRefus = Raisons.inf;
	    }
	} else {
	    raisonsRefus = sc.raisonValide(grille, new ScrabbleImpl());
	}

	if (server.getPartieState() == PartieState.recherche) {
	    write(StaticRequete.rinvalide + "/" + raisonsRefus + "/");
	} else if (server.getPartieState() == PartieState.soumission) {
	    write(StaticRequete.sinvalide + "/" + raisonsRefus + "/");
	}

    }

    public void error() {
	System.out.println("requete inconnu");
	write(StaticRequete.error + "/");
    }

    public void write(String str) {
	System.out.println("\t" + this.getNom() + "[" + this.getName() + "] "
	        + str);
	try {
	    outBW.write(str);
	    outBW.flush();
	} catch (IOException e) {
	    System.err.println("Error write");
	}
    }

    public void setNom(String nom) throws Exception {
	if (server.alreadyExist(nom))
	    throw new Exception();
	if (this.nom == null) {
	    System.out.println("set nom : " + nom);
	    this.nom = nom;
	}
    }

    public String getNom() {
	return nom;
    }

    public ScrabbleService getSc() {
	return sc;
    }

    public void setSc(ScrabbleService sc) {
	this.sc = sc;
    }

    public BufferedWriter getOutBW() {
	return outBW;
    }

    public Socket getSock() {
	return sock;
    }

    public ClientState getClientState() {
	return clientState;
    }

    public int getScore() {
	return score;
    }

    public int getScoreTour() {
	return scoreTour;
    }

    public void disconnect() {
	try {
	    server.disconnect(this);
	    if (inBR != null) {
		inBR.close();
	    }
	    if (outBW != null) {
		outBW.close();
	    }
	    if (sock != null)
		sock.close();
	} catch (IOException e) {
	    System.err.println("disconnect Thread");
	}

    }

    public ScrabbleService getScJoue() {
	return scJoue;
    }

    public void ResetScoreTour() {
	sc = server.getPartie();
	scoreTour = 0;
	scJoue = null;
    }

    public void updateScore() {
	score += scoreTour;
    }
}
