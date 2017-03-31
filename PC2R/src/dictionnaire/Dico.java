package dictionnaire;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.util.Scanner;

import enums.Lettre;
import patricia.interfaces.INode;
import patricias.Node;

public class Dico implements IDico {
	private static final INode patTree = getDicoOnline();
	private static final String address = "http://www.pallier.org/ressources/dicofr/liste.de.mots.francais.frgut.txt";
	private static final String path = "dico/dic1.txt";

	public static String sansAccent(String s) {
		final String accents = "ÀÁÂÃÄÅÈÉÊËÎÏÍÌÓÒÔÖÕØŠÚÙÛÜÝŸŽ"; // A compléter...
		final String letters = "AAAAAAEEEEIIIIOOOOOOSUUUUYYZ"; // A compléter...

		StringBuffer buffer = null;
		for (int i = s.length() - 1; i >= 0; i--) {
			int index = accents.indexOf(s.charAt(i));
			if (index >= 0) {
				if (buffer == null) {
					buffer = new StringBuffer(s);
				}
				buffer.setCharAt(i, letters.charAt(index));
			}
		}
		return buffer == null ? s : buffer.toString();
	}

	private static INode getDicoOnline() {
		URL url;
		INode patTre = new Node();
		try {
			url = new URL(address);
			
			
			InputStream in = url.openStream();
			Scanner s = new Scanner(in);

			String line = null;
			while (s.hasNextLine()) {
				line = s.nextLine();
				if (line.length() < 16) {
					line = line.toUpperCase();
					line=sansAccent(line);
					patTre.insertion(line);
				}
			}
		} catch (Throwable e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return patTre;
	}

	private static INode getDico() {
		INode patTre = new Node();
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader("dico/dic.txt"));
			String line;
			while ((line = br.readLine()) != null)
				patTre.insertion(line);
		} catch (Exception e) {
			e.printStackTrace();

		} finally {
			try {
				br.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return patTre;
	}

	public boolean isMotValide(Lettre[] mot) {

		return patTree.contient(Lettre.toString(mot));
	}

}
