package maintest;

import bot.Anagramme;
import dictionnaire.Dico;
import enums.Lettre;

public class MainDico {

	public static void main(String[] args) {
		Dico d = new Dico();
		Anagramme ana=new Anagramme();
		for (String str : ana.getAnagramme("AAZERTUGHX")) {
			System.out.println(str+"\t"+d.isMotValide(Lettre.stringToArray(str)));
		}
	}

}
