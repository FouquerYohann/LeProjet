package server;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Random;

import dictionnaire.Dico;
import dictionnaire.IDico;
import enums.Lettre;
import enums.PowerUp;
import enums.Raisons;
import service.ScrabbleService;

public class ScrabbleImpl implements ScrabbleService {
    private Lettre[][]         plateau;
    private PowerUp[][]        bonus;
    private static final int   size = 15;

    private Collection<Lettre> sacALettre;
    private Collection<Lettre> tirage;
    private final Random       rand = new Random();

    public ScrabbleImpl() {
	this.init();
    }

    public boolean isEmpty() {
	for (int i = 0; i < size; i++) {
	    for (int j = 0; j < size; j++) {
		if (plateau[i][j] != Lettre.VIDE)
		    return false;
	    }
	}
	return true;
    }

    public String toString() {
	String ret = "POINTS : " + this.getPoints() + "\n";
	for (int i = 0; i < size; i++) {
	    ret += i + ((i >= 10) ? "" : " ");
	}
	ret += "\n";
	for (int i = 0; i < size; i++) {
	    for (int j = 0; j < size; j++) {
		if (plateau[i][j] == Lettre.VIDE)
		    ret += "_" + " ";
		else
		    ret += plateau[i][j] + " ";
	    }
	    ret += " " + i + "\n";
	}
	ret += "\n";

	for (Lettre l : tirage) {
	    ret += l.toString();
	}

	ret += "\n\n";
	for (int i = 0; i < size; i++) {
	    for (int j = 0; j < size; j++) {
		switch (bonus[i][j]) {
		    case Lx2:
			ret += "L2 ";
			break;
		    case Lx3:
			ret += "L3 ";
			break;
		    case Mx2:
			ret += "M2 ";
			break;
		    case Mx3:
			ret += "M3 ";
			break;
		    case Normal:
			ret += "__ ";
			break;

		    default:
			break;
		}
	    }
	    ret += "\n";
	}

	return ret;
    }

    @Override
    public Collection<Lettre[]> getListeMot() {
	Collection<ArrayList<Lettre>> ret = new ArrayList<ArrayList<Lettre>>();
	ArrayList<Lettre> currH = new ArrayList<Lettre>();

	for (int i = 0; i < size; i++) {
	    for (int j = 0; j < size; j++) {
		if (plateau[i][j] == Lettre.VIDE) {
		    if (currH.isEmpty())
			continue;
		    if (currH.size() > 1)
			ret.add(currH);
		    currH = new ArrayList<Lettre>();
		} else {

		    currH.add(plateau[i][j]);
		}
	    }
	    if (!currH.isEmpty()) {
		ret.add(currH);
		currH = new ArrayList<Lettre>();
	    }
	}

	for (int i = 0; i < size; i++) {
	    for (int j = 0; j < size; j++) {
		if (plateau[j][i] == Lettre.VIDE) {
		    if (currH.isEmpty())
			continue;
		    if (currH.size() > 1) {
			ret.add(currH);
		    }
		    currH = new ArrayList<Lettre>();
		} else {
		    currH.add(plateau[j][i]);
		}
	    }
	    if (!currH.isEmpty()) {
		ret.add(currH);
		currH = new ArrayList<Lettre>();
	    }
	}
	Collection<Lettre[]> res = new ArrayList<Lettre[]>();
	for (ArrayList<Lettre> ar : ret) {
	    res.add(ar.toArray(new Lettre[ar.size()]));
	}

	return res;
    }

    @Override
    public Lettre getLettre(int x, int y) {
	return plateau[x][y];
    }

    @Override
    public int getPoints() {
	int sumTot = 0;
	int sumWord = 0;
	int sumMult = 1;

	for (int i = 0; i < size; i++) {
	    for (int j = 0; j < size; j++) {
		if (plateau[i][j] == Lettre.VIDE) {
		    sumTot += sumWord * sumMult;
		    sumWord = 0;
		    sumMult = 1;
		} else {
		    sumWord += plateau[i][j].getVal() * bonus[i][j].getMult();
		    sumMult *= bonus[i][j].multWorld();
		}
	    }
	    sumTot += sumWord * sumMult;
	    sumWord = 0;
	    sumMult = 1;

	}

	for (int i = 0; i < size; i++) {
	    for (int j = 0; j < size; j++) {
		if (plateau[j][i] == Lettre.VIDE) {
		    sumTot += sumWord * sumMult;
		    sumWord = 0;
		    sumMult = 1;
		} else {
		    sumWord += plateau[j][i].getVal() * bonus[j][i].getMult();
		    sumMult *= bonus[j][i].multWorld();
		}
	    }
	    sumTot += sumWord * sumMult;
	    sumWord = 0;
	    sumMult = 1;

	}
	return sumTot;
    }

    @Override
    public Collection<Lettre> getLettreRestant() {
	return sacALettre;
    }

    @Override
    public Collection<Lettre> getLettreTire() {
	return tirage;
    }

    @Override
    public PowerUp getBonus(int x, int y) {
	return bonus[x][y];
    }

    @Override
    public void init() {

	sacALettre = Lettre.initSacALettre();
	tirage = new ArrayList<Lettre>();
	

	this.plateau = new Lettre[size][size];
	this.bonus = new PowerUp[size][size];

	for (int i = 0; i < size; i++) {
	    for (int j = 0; j < size; j++) {
		plateau[i][j] = Lettre.VIDE;
		bonus[i][j] = PowerUp.Normal;
	    }
	}

	//
	// for (int i = 0; i < size; i++) {
	// for (int j = 0; j < size; j++) {
	// bonus[i][j] = PowerUp.Normal;
	// }
	// }

//	Random rand = new Random();
//	for (int i = 0; i < 10; i++) {
//	    bonus[rand.nextInt(size)][rand.nextInt(size)] = PowerUp.Lx2;
//	}
//	for (int i = 0; i < 5; i++) {
//	    bonus[rand.nextInt(size)][rand.nextInt(size)] = PowerUp.Lx3;
//	}
//	for (int i = 0; i < 8; i++) {
//	    bonus[rand.nextInt(size)][rand.nextInt(size)] = PowerUp.Mx2;
//	}
//	for (int i = 0; i < 10; i++) {
//	    bonus[rand.nextInt(size)][rand.nextInt(size)] = PowerUp.Mx3;
//	}

    }

    @Override
    public Collection<Lettre> tirerLettre(int i) {
	Collection<Lettre> ret = new ArrayList<Lettre>();
	for (int j = 0; j < i; j++) {
	    int r = rand.nextInt(sacALettre.size());
	    Lettre e = sacALettre.toArray(new Lettre[sacALettre.size()])[r];
	    sacALettre.remove(e);
	    ret.add(e);
	    tirage.add(e);
	}
	return ret;
    }

    @Override
    public void placerMot(Lettre[] mot, int x, int y, boolean horizontal) {

	int k = x;
	int l = y;
	for (int i = 0; i < mot.length; i++) {
	    if (plateau[k][l] != mot[i]) {
		plateau[k][l] = mot[i];
		tirage.remove(mot[i]);

	    }
	    if (horizontal)
		k++;
	    else
		l++;
	}

    }

    @Override
    public void init(Lettre[][] mots, Lettre[] tirage) {
	this.init();

	for (int i = 0; i < mots.length; i++) {
	    Lettre[] lettres = mots[i];
	    for (int j = 0; j < lettres.length; j++) {
		plateau[i][j] = lettres[j];
		sacALettre.remove(lettres[j]);
	    }
	}
	for (int i = 0; i < tirage.length; i++) {
	    this.tirage.add(tirage[i]);
	    sacALettre.remove(tirage[i]);
	}
	tirerLettre(7-tirage.length);

    }


    @Override
    public String send() {
	String ret = "";
	for (int i = 0; i < size; i++) {
	    for (int j = 0; j < size; j++) {
		if (plateau[i][j] != Lettre.VIDE)
		    ret += plateau[i][j];
		else {
		    ret += "0";
		}
	    }
	}
	ret += "/";
	for (Lettre l : tirage) {
	    ret += l;
	}
	for (int i = 0; i < 7 - tirage.size(); i++) {
	    ret += "0";
	}
	return ret;
    }

    @Override
    public boolean isFini() {
	return sacALettre.isEmpty();
    }

    public Raisons raisonValide(String placement, ScrabbleService scrabblePlayer) {
	int curr = 0;
	ArrayList<Lettre> more = new ArrayList<Lettre>();
	for (int i = 0; i < size; i++) {
	    for (int j = 0; j < size; j++) {
		char let = placement.charAt(curr++);
		int pos = 26;
		if (let != '0')
		    pos = let - 'A';
		if (Lettre.values()[pos] != plateau[i][j]) {
		    System.out.println("i :" + i + " j :" + j);
		    more.add(Lettre.values()[pos]);
		}
	    }
	}

	if (more.size() > 7) {

	    return Raisons.tricheur;
	}
	ArrayList<Lettre> copieLettreTire = new ArrayList<Lettre>(
	        getLettreTire());
	System.out.println(copieLettreTire);
	for (Lettre lettre : more) {
	    if (!copieLettreTire.remove(lettre)) {
		return Raisons.tricheur;
	    }
	}
	System.out.println(copieLettreTire);
	ScrabbleImpl tmp = new ScrabbleImpl();
	tmp.init(ScrabbleParser.parseGrille(placement),
	        copieLettreTire.toArray(new Lettre[copieLettreTire.size()]));

	IDico dick = new Dico();
	for (Lettre[] lettre : tmp.getListeMot()) {
	    if (!dick.isMotValide(lettre)) {
		return Raisons.dic;
	    }
	}
	if (this.getPoints() > tmp.getPoints())
	    return Raisons.inf;

	scrabblePlayer.init(tmp.plateau,
	        tmp.tirage.toArray(new Lettre[tirage.size()]));
	;
	return Raisons.none;

    }

    public boolean isValidPlacement(String placement) {
	return raisonValide(placement, new ScrabbleImpl()) == Raisons.none;
    }
    
    public void reTire(){
	int latailledutiragearefaire=tirage.size();
	sacALettre.addAll(tirage);
	tirage.clear();
	tirerLettre(latailledutiragearefaire);
    }
    
    
}
