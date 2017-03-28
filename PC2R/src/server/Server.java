package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

import enums.ClientState;
import enums.PartieState;
import server.staticvalue.StaticRequete;
import service.ScrabbleService;

public class Server {
	private final static int		portDefault	= 2018;
	private ServerSocket			Ssock;
	private ArrayList<ClientThread>	listClient	= new ArrayList<ClientThread>();
	private ScrabbleService			partie		= new ScrabbleImpl();
	private PartieState				partieState	= PartieState.debut;
	private Chrono					chronoTour	= new Chrono();

	public Server(int port) throws IOException {
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
		return partieState;
	}

	public void setPartieState(PartieState partieState) {
		this.partieState = partieState;
	}

	public int getChronoTour() {
		return chronoTour.getTemps();
	}

	public void connecte(String string) {
		boolean premier=true;
		for (ClientThread cT : listClient) {
			if (cT.getClientState() == ClientState.playing) {
				if (cT.getNom().equals(string))
					continue;
				cT.write(StaticRequete.connecte + "/" + string + "/");
				premier=false;
			}
		}
		if(premier)debutPartie();
		
	}
	
	public void debutPartie(){
		partieState = PartieState.recherche;
		chronoTour.start();
		for (ClientThread cT : listClient) {
			if (cT.getClientState() == ClientState.playing) {
				cT.setSc(partie);
				cT.write(StaticRequete.session+"/");
			}
		}
	}

}
