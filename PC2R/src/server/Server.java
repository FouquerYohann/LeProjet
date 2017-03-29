package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

import enums.ClientState;
import enums.PartieState;
import server.staticvalue.StaticRequete;
import service.ScrabbleService;

public class Server implements Observer {
	private final static int		portDefault	= 2018;
	private ServerSocket			Ssock;
	private ArrayList<ClientThread>	listClient	= new ArrayList<ClientThread>();
	private ScrabbleService			partie		= new ScrabbleImpl();
	private Chrono					chronoTour	= new Chrono();
	private GameThread				gameThread	= new GameThread(this);
	private boolean					fini		= true;
	private int						tour		= 0;

	public Server(int port) throws IOException {
		gameThread.addObserver(this);
		Ssock = new ServerSocket(port);
		System.out.println("Server initialized on port " + port);
		partie.init();
	}

	public ArrayList<ClientThread> getListClient() {
		return listClient;
	}

	public static void main(String[] args) {
		Server server = null;
		try {
			if (args.length == 0)
				server = new Server(portDefault);
			else
				server = new Server(Integer.parseInt(args[0]));

			while (true) {
				Socket sock = server.Ssock.accept();
				ClientThread t = new ClientThread(sock, server);
				server.getListClient().add(t);
				t.start();
			}

		} catch (NumberFormatException | IOException e) {
			e.printStackTrace();
		}
	}

	public void disconnect(ClientThread clientThread) {
		System.out.println("disconnect " + clientThread.getNom());
		getListClient().remove(clientThread);
		if (clientThread.getClientState() == ClientState.playing)
			for (ClientThread ct : listClient) {
				if (ct.getClientState() == ClientState.playing) {
					String ret = StaticRequete.deconnexion + "/"
							+ clientThread.getNom() + "/";
					ct.write(ret);
				}
			}
	}

	public String retourConnection() {
		String ret = StaticRequete.bienvenue + "/" + partie.send() + "/";
		return ret;
	}

	public boolean alreadyExist(String nom) {
		if (nom == null)
			return true;
		for (ClientThread clientThread : listClient) {
			if (nom.equals(clientThread.getNom()))
				return true;
		}
		return false;
	}

	public PartieState getPartieState() {
		return gameThread.getPartieState();
	}

	public synchronized void setPartieState(PartieState partieState) {
		this.gameThread.setPartieState(partieState);
	}

	public int getTempsTour() {
		return chronoTour.getTemps();
	}

	public Chrono getChronoTour() {
		return chronoTour;
	}

	public void connecte(String string) {
		boolean premier=true;
		for (ClientThread cT : listClient) {
			if (cT.getClientState() == ClientState.playing) {
				if (cT.getNom().equals(string)) {
					continue;
				}
				premier=false;
				cT.write(StaticRequete.connecte + "/" + string + "/");
			}
		}
		if(premier)
			debutPartie();
		
	}

	public void debutPartie() {
		fini=false;
		System.out.println("Début partie");
		chronoTour.start();
		gameThread.start();
		for (ClientThread cT : listClient) {
			if (cT.getClientState() == ClientState.playing) {
				cT.setSc(partie);
				cT.write(StaticRequete.session + "/");
			}
		}
	}

	private void tour() {
		for (ClientThread cT : listClient) {
			if (cT.getClientState() == ClientState.playing) {
				cT.write(StaticRequete.tour + "/" + partie.send() + "/");
			}
		}
	}

	private void soumission() {
		for (ClientThread cT : listClient) {
			if (cT.getClientState() == ClientState.playing) {
				cT.write(StaticRequete.rfin + "/");
			}
		}
	}

	private void resultat() {
		for (ClientThread cT : listClient) {
			if (cT.getClientState() == ClientState.playing) {
				cT.write(StaticRequete.sfin + "/");
				cT.write(StaticRequete.bilan + "/" + bilanTour() + "/");
			}
		}
		if(partie.isFini()){
			fini=true;
			finPartie();
		}
	}

	private void finPartie(){
		for (ClientThread cT : listClient) {
			if (cT.getClientState() == ClientState.playing) {
				cT.write(StaticRequete.vainqueur + "/"+bilanPartie()+"/");
			}
		}
	}
	
	private String bilanPartie(){
		//TODO bouchon
		String bilan = "bah c'est fini";
		return bilan;
	}
	
	@Override
	public void update(Observable o, Object arg) {
		System.out.println("\t\tupdate!");
		switch (gameThread.getPartieState()) {
		case debut:
			throw new Error("euh pas normal");
		case recherche:
			tour();
			break;
		case soumission:
			soumission();
			break;
		case resultat:
			resultat();
			break;
		default:
			throw new Error("euh pas normal");

		}

	}

	public boolean isFini() {
		return fini;
	}

	private String score() {
		//TODO bouchon
		String scores = tour+"*";
		for (ClientThread cT : listClient) {
			if (cT.getClientState() == ClientState.playing) {
				scores+="*"+cT.getNom()+"*"+cT.getScore();
			}
		}
		return scores;
	}

	private String bilanTour() {
		//TODO bouchon
		String mot = "anticonstitutionnellement";
		String vainqueur = "justin";
		return mot + "/" + vainqueur + "/" + score() + "/";
	}

	
	public void setGameThread(GameThread gameThread) {
		this.gameThread.deleteObserver(this);
		this.gameThread = gameThread;
		this.gameThread.addObserver(this);
	}
	
	
}
