package bot;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

import enums.Lettre;
import enums.PartieState;
import scrabble.ScrabbleImpl;
import scrabble.ScrabbleParser;
import scrabble.ScrabbleService;
import server.staticvalue.StaticRequete;
import server.staticvalue.StaticState;

public class ClientIA implements Runnable {
	private static final int	port	= 2018;
	private Thread				t;
	private ScrabbleService		sc;
	private PartieState			state	= PartieState.debut;
	private BufferedReader		inBR	= null;
	private BufferedWriter		outBW	= null;
	private String				ip;
	private String				nom;
	private boolean				trouve	= false;
	private Random				r		= new Random(System.currentTimeMillis());
	private ChercheurMot		cher	= new ChercheurMot(this);

	public ClientIA(String ip, String nom) {
		this.ip = ip;
		this.nom = nom;
	}

	public ClientIA(String nom) {
		this("127.0.0.1", nom);
	}

	@Override public void run() {
		try {
			Socket connect = new Socket(ip, port);
			outBW = new BufferedWriter(new OutputStreamWriter(connect.getOutputStream()));
			inBR = new BufferedReader(new InputStreamReader(connect.getInputStream()));
			connexion(nom);
			bienvenu(readLine().split("/"));

			System.out.println("playing...");
			playing();

			sort();
			connect.close();
		} catch (Exception e) {
			e.printStackTrace(System.err);
		}
	}

	public void start() {
		t = new Thread(this);
		t.start();
	}

	private void connexion(String user) throws IOException {
		write(StaticRequete.connexion + "/" + user + "/");
		new Thread() {
			@Override public void run() {
				while (true) {
					try {
						Messages.sendMessage(user, outBW);
					} catch (IOException e) {
					}
				}
			}
		}.start();
	}

	public void write(String str) throws IOException {
		System.out.println(str);
		outBW.write(str + "\n");
		outBW.flush();
	}

	public String readLine() throws IOException {
		String str = inBR.readLine();
		System.out.println("\t" + str);
		return str;
	}

	private void bienvenu(String tok[]) {
		if (tok.length != 6)
			return;
		if (!tok[0].equals(StaticRequete.bienvenue))
			return;

		sc = new ScrabbleImpl();
		sc.init(ScrabbleParser.parseGrille(tok[1]), ScrabbleParser.parseTirage(tok[2]));

		if (tok[4].equals(StaticState.debut))
			state = PartieState.debut;
		else if (tok[4].equals(StaticState.soumission))
			state = PartieState.soumission;
		else if (tok[4].equals(StaticState.resultat))
			state = PartieState.resultat;
		else if (tok[4].equals(StaticState.recherche))
			state = PartieState.recherche;

	}

	private void sort() throws IOException {
		write(StaticRequete.sort + "/" + nom + "/");
	}

	private void playing() throws IOException {
		while (true) {
			String rl = readLine();
			String tok[] = rl.split("/");
			for (int i = 0; i < tok.length; i++) {
				if (StaticRequete.tour.equals(tok[i])) {
					sc = new ScrabbleImpl();
					sc.init(ScrabbleParser.parseGrille(tok[i + 1]), ScrabbleParser.parseTirage(tok[i + 2]));
					state = PartieState.recherche;
					cher.setSc(sc);
				} else if (StaticRequete.rfin.equals(tok[i])) {
					state = PartieState.soumission;
				} else if (StaticRequete.sfin.equals(tok[i])) {
					state = PartieState.resultat;
				} else if (StaticRequete.bilan.equals(tok[i])) {
					state = PartieState.debut;
				} else if (StaticRequete.rinvalide.equals(tok[i])) {
					trouve = false;
					System.err.println("Invalide");
				} else if (StaticRequete.sinvalide.equals(tok[i])) {
					trouve = false;
					System.err.println("Invalide");
				}
			}

			switch (state) {
				case debut:
					// try {
					// Thread.sleep(1000);
					// } catch (InterruptedException e) {
					// }
					break;
				case recherche:
					trouve();
					break;
				case soumission:
					if (!trouve)
						trouve();
					break;
				case resultat:
					break;

			}
			// try {
			// Thread.sleep(1000 * r.nextInt(20));
			// } catch (InterruptedException e) {
			// }
		}
	}

	private void trouve() throws IOException {
		String placement = cher.getMot();

		if (placement == null) {
			synchronized (cher) {

				try {
					System.out.println("waiting");
					cher.wait();
					System.out.println("nouvelle solution");
				} catch (InterruptedException e) {
				}
				placement = cher.getMot();
			}
		} else if (placement.isEmpty())
			return;
		System.out.println(placement);

		write(StaticRequete.trouve + "/" + placement.split("/")[0] + "/");
	}

	public static void main(String[] args) throws Exception {
		System.out.println("Enter bot name : ");
		Scanner scan = new Scanner(System.in);
		String str = scan.nextLine();
		System.out.println("Starting bot " + str);
		new ClientIA(str).start();
		scan.close();
	}
}
