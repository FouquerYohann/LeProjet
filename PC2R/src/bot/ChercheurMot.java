package bot;

import java.util.ArrayList;

import enums.Lettre;
import scrabble.ScrabbleImpl;
import scrabble.ScrabbleParser;
import scrabble.ScrabbleService;


public class ChercheurMot {
	private ArrayList<String>	solutions	= new ArrayList<String>();
	private ScrabbleService		sc;
	private Anagramme			nana		= new Anagramme();
	private ClientIA			c;

	private Runnable			target		= new Runnable() {

												@Override public void run() {
													chercherMot();
													end = true;
												}
											};
	private Thread				t			= new Thread(target);

	private boolean				end			= false;

	public ChercheurMot(ClientIA c) {
		super();
		this.c = c;
	}

	private void chercherMot() {
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
				// System.out.println(string);
				Lettre[] mot = Lettre.stringToArray(string);
				try {
					newSc.placerMot(mot, 7, 7, true);
					if (sc.isValidPlacement(newSc.send())) {
						// System.out.println("\tplacerMot(" + string + " ," + 7
						// + " ," + 7 + " ,true);");
						add(newSc.send());
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
									add(newSc.send());
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
									add(newSc.send());
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
	}

	public void setSc(ScrabbleService sc) {
		t.interrupt();
		this.sc = sc;
		start();
	}

	public String getMot() {
		if (!solutions.isEmpty())
			return solutions.remove(0);
		if (end)
			return "";
		return null;
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

	private void add(String sol) {
		solutions.add(sol);
		synchronized (this) {
			System.out.println("notify");
			this.notify();
		}

	}

	public void start() {
		end = false;
		t = new Thread(target);
		t.start();
	}
}
