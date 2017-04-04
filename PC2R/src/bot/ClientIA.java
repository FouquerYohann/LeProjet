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
import server.ScrabbleImpl;
import server.ScrabbleParser;
import server.staticvalue.StaticRequete;
import server.staticvalue.StaticState;
import service.ScrabbleService;

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
	private Anagramme			nana	= new Anagramme();

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
		new Thread(){
			@Override public void run() {
				while(true){
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
		String placement = trouverMot().send();
		placement = placement.substring(0, placement.length() - 8);

		write(StaticRequete.trouve + "/" + placement + "/");
	}

	private ScrabbleService trouverMot() {
		String strs[] = sc.send().split("/");
		ScrabbleService newSc = new ScrabbleImpl();
		newSc.init(ScrabbleParser.parseGrille(strs[0]), ScrabbleParser.parseTirage(strs[1]));
		ArrayList<String> ana;
		String tirage = strs[1];

		ArrayList<String> lMot = new ArrayList<String>();
		for (Lettre[] lettre : sc.getListeMot()) {
			lMot.add(Lettre.toString(lettre));
		}

		if (sc.isEmpty()) {
			ana = nana.getAnagramme(tirage);
			for (String string : ana) {
				System.out.println(string);
				Lettre[] mot = Lettre.stringToArray(string);
				try {
					newSc.placerMot(mot, 7, 7, true);
					if (sc.isValidPlacement(newSc.send())) {
						// System.out.println("\tplacerMot(" + string + " ," + 7
						// + " ," + 7 + " ,true);");
						return newSc;
					}
				} catch (Error e) {
					// System.out.println("placerMot(" + string + " ," + 7 + "
					// ," + 7 + " ,true);");
				}
				String tok[] = sc.send().split("/");
				newSc = new ScrabbleImpl();
				newSc.init(ScrabbleParser.parseGrille(tok[0]), ScrabbleParser.parseTirage(tok[1]));

			}
		} else {

			for (int i = 0; i < ScrabbleImpl.size; i++) {
				char[] lettres = getLettreLigne(i);
				for (int j = 0; j < lettres.length; j++) {
					char l = lettres[j];
					tirage += l;
					ana = nana.getAnagramme(tirage);
					for (String str : ana) {
						for (int k = 0; k < ScrabbleImpl.size; k++) {
							Lettre[] mot = Lettre.stringToArray(str);

							if (lMot.contains(str))
								continue;

							try {
								newSc.placerMot(mot, j, k, true);
								if (sc.isValidPlacement(newSc.send())) {
									// System.out.println("\tplacerMot(" + str +
									// " ," + j + " ," + k + " ,true);");
									return newSc;
								}
							} catch (Error e) {
								// System.out.println("placerMot(" + str + " ,"
								// + j + " ," + k + " ,true);");
							}
							String tok[] = sc.send().split("/");
							newSc = new ScrabbleImpl();
							newSc.init(ScrabbleParser.parseGrille(tok[0]), ScrabbleParser.parseTirage(tok[1]));
						}

					}

				}

			}

			for (int i = 0; i < ScrabbleImpl.size; i++) {
				char[] lettres = getLettreColonne(i);
				for (int j = 0; j < lettres.length; j++) {
					char l = lettres[j];
					tirage += l;
					ana = nana.getAnagramme(tirage);
					for (String str : ana) {
						for (int k = 0; k < ScrabbleImpl.size; k++) {
							Lettre[] mot = Lettre.stringToArray(str);
							if (lMot.contains(str))
								continue;
							try {
								newSc.placerMot(mot, k, j, false);
								System.out.println("\tplacerMot(" + str + " ," + j + " ," + k + " ,false);");
								if (sc.isValidPlacement(newSc.send())) {
									return newSc;
								}
							} catch (Error e) {
								System.out.println("placerMot(" + str + " ," + j + " ," + k + " ,false);");
							}
							String tok[] = sc.send().split("/");
							newSc = new ScrabbleImpl();
							newSc.init(ScrabbleParser.parseGrille(tok[0]), ScrabbleParser.parseTirage(tok[1]));
						}

					}

				}

			}
		}
		return sc;
	}

	private char[] getLettreLigne(int x) {
		String ret = "";
		for (int i = 0; i < ScrabbleImpl.size; i++) {
			if (sc.getLettre(x, i) != Lettre.VIDE) {
				ret += sc.getLettre(x, i).toString();
			}
		}
		return ret.toCharArray();
	}

	private char[] getLettreColonne(int y) {
		String ret = "";
		for (int i = 0; i < ScrabbleImpl.size; i++) {
			if (sc.getLettre(i, y) != Lettre.VIDE) {
				ret += sc.getLettre(i, y).toString();
			}
		}
		return ret.toCharArray();
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
