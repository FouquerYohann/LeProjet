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
	private ServerSocket			Ssock;
	private ArrayList<ClientThread>	listClient	= new ArrayList<ClientThread>();
	private ScrabbleService			partie		= new ScrabbleImpl();
	private PartieState				partieState	= PartieState.debut;
	private Chrono					chronoTour	= new Chrono();

	public Server(int port) throws IOException {
		Ssock = new ServerSocket(port);
		partie.init();
	}

	public ArrayList<ClientThread> getListClient() {
		return listClient;
	}

	public static void main(String[] args) {

		try {
			Server server = new Server(Integer.parseInt(args[0]));

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
					try {
						String ret = StaticRequete.deconnexion + "/"
								+ clientThread.getNom() + "/";
						ct.getOutBW().write(ret);
						ct.getOutBW().flush();
					} catch (IOException e) {
						System.err.println("j'ai pas le droit");
					}
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

}
