package dictionnaire;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Scanner;

import enums.Lettre;

public class Dico implements IDico {
	private static final ITHybrid thyb = getDicoOnlineTHybrid();
	private static final String address = "http://www.pallier.org/ressources/dicofr/liste.de.mots.francais.frgut.txt";
	private static final String path = "dico/dic1.txt";

	public static String sansAccent(String s) {
		final String accents = "ÀÁÂÃÄÅÈÉÊËÎÏÍÌÓÒÔÖÕØŠÚÙÛÜÝŸŽ"; // A compl�ter...
		final String letters = "AAAAAAEEEEIIIIOOOOOOSUUUUYYZ"; // A compl�ter...

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

	private static THybrid getDicoOnlineTHybrid() {
		URL url;
		THybrid thyb=new THybrid();
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
					thyb.insertion(line);
				}
			}
			s.close();
		} catch (Throwable e) {
			
			return getDicoTHybrid();
		}
		return thyb;
	}
	
	
	private static THybrid getDicoTHybrid() {
		THybrid thyb=new THybrid();
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(path));
			String line;
			while ((line = br.readLine()) != null)
				thyb.insertion(line);
		} catch (Exception e) {
			e.printStackTrace();

		} finally {
			try {
				br.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return thyb;
	}

	public boolean isMotValide(Lettre[] mot) {

		return thyb.contient(Lettre.toString(mot));
	}

}
