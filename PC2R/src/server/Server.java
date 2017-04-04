package server;

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.TimeUnit;

import contract.ScrabbleContract;
import enums.ClientState;
import enums.PartieState;
import server.save.SaveProfil;
import server.staticvalue.StaticRequete;
import server.temps.Chrono;
import server.temps.ChronometreLayout;
import service.ScrabbleService;

public class Server implements Observer {
	private final String			saveFic			= "save";
	private final static int		portDefault		= 2018;
	private ServerSocket			Ssock;
	private ArrayList<ClientThread>	listClient		= new ArrayList<ClientThread>();
	private ScrabbleService			partie			= new ScrabbleContract(new ScrabbleImpl());
	private Chrono					chronoTour		= new Chrono();
	private GameThread				gameThread		= new GameThread(this);
	private boolean					fini			= true;
	private int						tour			= 0;
	private ChronometreLayout		cml;
	private int						bilanTourInt	= 0;
	private String					bilanTour		= "";
	private SaveProfil				save			= new SaveProfil(new File(saveFic));

	public Server(int port) throws IOException {
		gameThread.addObserver(this);
		Ssock = new ServerSocket(port);
		System.out.println("Server initialized on port " + port);
		partie.init();
		partie.tirerLettre(7);
	}

	public ArrayList<ClientThread> getListClient() {
		return listClient;
	}

	public void disconnect(ClientThread clientThread) {
		save.deconnecte(clientThread.getNom(), clientThread.getScore());
		save.saveCSV(new File(saveFic));
		System.out.println("disconnect " + clientThread.getNom());
		boolean dernier = true;
		getListClient().remove(clientThread);
		if (clientThread.getClientState() == ClientState.playing)
			for (ClientThread ct : listClient) {
				if (ct.getClientState() == ClientState.playing) {
					String ret = StaticRequete.deconnexion + "/" + clientThread.getNom() + "/";
					ct.write(ret);
					dernier = false;
				}
			}
		if (dernier) {
			System.out.println("dernier");
			finPartie();
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

	public int getTempsTour() {
		return chronoTour.getTemps();
	}

	public Chrono getChronoTour() {
		return chronoTour;
	}

	public void connecte(String string) {
		boolean premier = true;
		save.connecte(string);
		save.saveCSV(new File(saveFic));
		for (ClientThread cT : listClient) {
			if (cT.getClientState() == ClientState.playing) {
				if (cT.getNom().equals(string)) {
					cT.setSc(partie);
					continue;
				}
				premier = false;
				cT.write(StaticRequete.connecte + "/" + string + "/");
			}
		}
		if (premier) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
			}
			debutPartie();

		}
	}

	public void debutPartie() {
		fini = false;
		gameThread.start();
		System.out.println("Début partie");
		tour = 0;
		for (ClientThread cT : listClient) {
			if (cT.getClientState() == ClientState.playing) {
				cT.write(StaticRequete.session + "/");
			}
		}
	}

	private void tour() {
		tour++;
		chronoTour.start();
		cml = new ChronometreLayout(chronoTour);

		for (ClientThread cT : listClient) {
			if (cT.getClientState() == ClientState.playing) {
				cT.write(StaticRequete.tour + "/" + partie.send() + "/");
				cT.ResetScoreTour();
			}
		}
	}

	private void soumission() {
		chronoTour.start();
		for (ClientThread cT : listClient) {
			if (cT.getClientState() == ClientState.playing) {
				cT.write(StaticRequete.rfin + "/");
			}
		}
	}

	private void resultat() {
		cml.close();
		chronoTour.stop();
		for (ClientThread cT : listClient) {
			if (cT.getClientState() == ClientState.playing) {
				cT.write(StaticRequete.sfin + "/");
				try {
					TimeUnit.MILLISECONDS.sleep(500);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				cT.write(StaticRequete.bilan + "/" + bilanTour() + "/");
			}
		}
		if (partie.isFini()) {
			fini = true;
			finPartie();
		}
	}

	private void finPartie() {
		fini = true;
		for (ClientThread cT : listClient) {
			if (cT.getClientState() == ClientState.playing) {
				cT.write(StaticRequete.vainqueur + "/" + bilanPartie() + "/");
			}
		}
		chronoTour.stop();
		cml.close();
		gameThread.deleteObserver(this);
		gameThread.stop();
		gameThread = new GameThread(this);
		gameThread.addObserver(this);
		partie = new ScrabbleImpl();
		partie.init();
		partie.tirerLettre(7);

	}

	private String bilanPartie() {
		// TODO bouchon
		String bilan = "bah c'est fini";
		return bilan;
	}

	@Override public void update(Observable o, Object arg) {
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
		String scores = tour + "";
		for (ClientThread cT : listClient) {
			if (cT.getClientState() == ClientState.playing) {
				scores += "*" + cT.getNom() + "*" + cT.getScore();
			}
		}
		return scores;
	}

public void retourMessage(String name,String message) {
		for (ClientThread cT : listClient) {
			if (cT.getClientState() == ClientState.playing) {
				cT.write(StaticRequete.retMessage + "/"+name+"/"+message);
			}
		}
	}

	private String bilanTour() {
		if (bilanTourInt == tour)
			return bilanTour;

		String mot = "anticonstitutionnellement";
		String vainqueur = "personne";
		int bestScore = 0;
		ScrabbleService best = partie;
		for (ClientThread cT : listClient) {
			if (cT.getClientState() == ClientState.playing) {
				cT.updateScore();
				int scoreIt = cT.getScoreTour();
				if (scoreIt > bestScore) {
					bestScore = scoreIt;
					best = cT.getScJoue();
					vainqueur = cT.getNom();
					if (cT != null)
						mot = cT.getScJoue().plusLong(partie);
				}
			}
		}
		if (best == partie)
			partie.reTire();
		partie = best;
		bilanTour = mot + "/" + vainqueur + "/" + score();
		bilanTourInt++;
		return bilanTour;
	}

	public void setGameThread(GameThread gameThread) {
		this.gameThread.deleteObserver(this);
		this.gameThread = gameThread;
		this.gameThread.addObserver(this);
	}

	public ScrabbleService getPartie() {
		return partie;
	}

public void best_player() {
		
		String best=null;
		int best_score=-1;
		for (ClientThread cT : listClient) {
			if (cT.getClientState() == ClientState.playing) {
				int tmp=cT.getScoreTour();
				if(tmp>best_score){
					best_score=tmp;
					best=cT.getNom();
				}
			}
		}
		
		
		for (ClientThread cT : listClient) {
			if (cT.getClientState() == ClientState.playing) {
				int i=(cT.getNom().equals(best))?1:0;
				cT.write(StaticRequete.meilleur + "/"+i+"/");
			}
		}
		
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




}
