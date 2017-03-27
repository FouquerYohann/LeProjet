package server;

import ENUM.Lettre;

public class ScrabbleParser {

    public static Lettre[][] parseGrille(String s) {
	Lettre[][] ret = new Lettre[15][15];
	for (int i = 0; i < ret.length; i++) {
	    for (int j = 0; j < ret.length; j++) {
		char let = s.charAt(i * 15 + j);
		if (let == '0')
		    ret[i][j] = Lettre.VIDE;
		else {
		    int pos = let - 'A';
		    ret[i][j] = Lettre.values()[pos];
		}
	    }
	}
	return ret;
    }

    public static Lettre[] parseTirage(String s) {
	int deuxcentvingtcinq = 225;
	Lettre[] ret = new Lettre[7];
	for (int i = 0; i < 7; i++) {
	    char let = s.charAt(deuxcentvingtcinq + i);
	    if (let == '0')
		ret[i] = Lettre.VIDE;
	    else {
		int pos = let - 'A';
		ret[i] = Lettre.values()[pos];
	    }
	}
	return ret;
    }
}
