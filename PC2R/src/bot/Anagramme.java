package bot;

import java.util.ArrayList;

public class Anagramme {

	private ArrayList<String> res = new ArrayList<String>();

	public ArrayList<String> getRes() {
		return res;
	}

	public ArrayList<String> getAnagramme(String pmot) {
		res.clear();
		char[] word;
		char[] solution;
		char[] TEMP = pmot.toCharArray();

		int taille_solution = 0;
		int long_anagram = TEMP.length;

		word = new char[long_anagram];
		solution = new char[long_anagram]; // L� ou seront stock�es les
		                                   // diff�rentes solutions
		for (int m = 0; m < long_anagram; m++) {
			word[m] = TEMP[m];
		}

		Arbre(word, 0, long_anagram, solution, taille_solution); // On rentre
		                                                         // dans le
		                                                         // premier
		                                                         // �tage de
		                                                         // l'arbre

		if (word != null) {
			word = null;
		}
		if (solution != null) {
			solution = null;
		}
		System.gc();
		return res;
	}

	void Arbre(char[] word, int n, int lg, char[] solution, int taille_solution) // Fonction
	                                                                             // recursive
	{
		char[] comb;
		int i, j, k, m;
		StringBuffer tmp;

		if (lg != 0) {
			comb = new char[lg];

			taille_solution++; // On est descendu d'un �tage dans l'arbre =>
			                   // taille de la solution +1
			for (i = 0; i < lg; i++) // Pour chaque lettre du nouveau mot on
			                         // r�alise autant d'arbre pour l'�tage
			                         // suivant
			{
				solution[n] = word[i]; // On ajoute la nouvelle lettre � la fin
				                       // de la solution (n = �tage)

				tmp = new StringBuffer();
				for (m = 0; m < taille_solution; m++) {
					tmp.append(solution[m]);
				}

				res.add(tmp.toString());
				// System.out.println(tmp); /// ########## Les diff�rentes
				/// combinaisons sortent ici !! BINGO
				/// ###########//

				k = 0; // ICI on d�finit la nouvelle combinaison COMB avec les
				       // lettres restantes
				for (j = 0; j < lg; j++) {
					if (j != i) {
						comb[k] = word[j]; // Donc comb prend toutes les lettres
						                   // restantes
						k++;
					}
				}

				Arbre(comb, n + 1, lg - 1, solution, taille_solution); // Puis
				                                                       // on
				                                                       // descend
				                                                       // d'un
				                                                       // �tage
				                                                       // dans
				                                                       // l'arbre
				                                                       // en
				                                                       // reinjectant
				                                                       // le
				                                                       // nouveau
				                                                       // comb
			}
		} else
			taille_solution--; // On remonte d'un �tage dans l'arbre
	}

	public static void main(String[] args) {

		Anagramme ana = new Anagramme();
		System.out.println(ana.getAnagramme("aeze"));

		System.out.println(ana.getAnagramme("rtte"));
	}

}