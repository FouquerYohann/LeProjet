package enums;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Collection;
import java.util.Vector;

public enum Lettre {
    A(1), B(3), C(3), D(2), E(1), F(4), G(2), H(4), I(1), J(8), K(10), L(1), M(
	    2), N(1), O(1), P(3), Q(8), R(1), S(1), T(1), U(1), V(4), W(10), X(
	    10), Y(10), Z(10), VIDE(0);

    private int val;

    Lettre(int i) {
	val = i;
    }

    public int getVal() {
	return val;
    }

    public static final Collection<Lettre> Initial = initSacALettre();

    public static Collection<Lettre> initSacALettre() {
	Collection<Lettre> ret = new Vector<Lettre>(100);
	try {
	    BufferedReader br = new BufferedReader(new FileReader("InitTirage"));
	    String line;
	    while ((line = br.readLine()) != null) {
		String tok[] = line.split(" ");
		int nb = Integer.parseInt(tok[2]);
		char let = tok[0].charAt(0);
		int pos = let - 'A';
		for (int i = 0; i < nb; i++) {
		    ret.add(Lettre.values()[pos]);
		}
	    }
	    br.close();
	} catch (IOException e) {
	    e.printStackTrace();
	}

	return ret;
    }

    public static String toString(Lettre[] mot) {
	String m = "";
	for (int i = 0; i < mot.length; i++)
	    m += mot[i];
	return m;
    }
    
    public static Lettre[] stringToArray(String str){
	Lettre ret[]=new Lettre[str.length()];
	for (int i = 0; i < str.length(); i++) {
	    char let = str.charAt(i);
		int pos = let - 'A';
	    ret[i]=(Lettre.values()[pos]);
	}
	return ret;
	
    }

}
