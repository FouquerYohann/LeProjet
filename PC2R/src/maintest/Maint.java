package maintest;

import java.io.IOException;
import java.util.Scanner;

import contract.ScrabbleContract;
import enums.Lettre;
import server.ScrabbleImpl;
import server.ScrabbleParser;
import service.ScrabbleService;

public class Maint {

	public static void main(String[] args) throws IOException {
		ScrabbleService sc = new ScrabbleImpl();

		sc = new ScrabbleContract(sc);
		sc.init();
		sc.tirerLettre(7);

		// System.out.println(sc);
		ScrabbleImpl sc2 = new ScrabbleImpl();
		Lettre[][] jghjnfg = ScrabbleParser
				.parseGrille("0000000000000000000000000000000000000000000000000000000000000000000000H000000000000J0UVAL000000000E0I0Z000000000CUIT0I00000000000000M00000000000000U000000000SIEENT00000000000000000000000000000000000000000000000000000000000000UEDGFKS");

		for (int i = 0; i < jghjnfg.length; i++) {
			for (int j = 0; j < jghjnfg.length; j++) {
				if (jghjnfg[i][j] == Lettre.VIDE)
					System.out.print(" ");
				else
					System.out.print(jghjnfg[i][j]);
			}
			System.out.println();
		}

		sc2.init(
				ScrabbleParser
						.parseGrille("0000000000000000000000000000000000000000000000000000000000000000000000H000000000000J0UVAL000000000E0I0Z000000000CUIT0I00000000000000M00000000000000U000000000SIEENT00000000000000000000000000000000000000000000000000000000000000UEDGFKS"),
				ScrabbleParser
						.parseTirage("0000000000000000000000000000000000000000000000000000000000000000000000H000000000000J0UVAL000000000E0I0Z000000000CUIT0I00000000000000M00000000000000U000000000SIEENT00000000000000000000000000000000000000000000000000000000000000UEDGFKS"));

		System.out.println(sc2);
		String sauvegarde = null;
		Scanner scan = new Scanner(System.in);
		String tok[];
		String str = null;
		do {
			try {
				sauvegarde = sc.send();
				System.out.println(sc);
				System.out.println("mot x y H :");
				str = scan.nextLine();
				if (str.equals("quit"))
					break;
				tok = str.split(" ");
				String mot = tok[0];
				int x = Integer.parseInt(tok[1]);
				int y = Integer.parseInt(tok[2]);
				boolean hor = (!tok[3].equals("H") ? true : false);

				sc.placerMot(Lettre.stringToArray(mot), x, y, hor);

				int nblettre = sc.getLettreTire().size();
				sc.tirerLettre(7 - nblettre);

				for (Lettre[] let : sc.getListeMot()) {
					for (int i = 0; i < let.length; i++) {
						System.out.print(let[i]);
					}
					System.out.println();
				}

			} catch (Error e) {
				e.printStackTrace();
				break;
			}

		} while (true);
		System.out.println(sauvegarde);
		System.out.println(sc.send());
		scan.close();

	}

}
