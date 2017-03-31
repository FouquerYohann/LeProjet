package contract;

import java.awt.Point;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

import service.ScrabbleService;
import dictionnaire.Dico;
import dictionnaire.IDico;
import enums.Lettre;
import enums.PowerUp;
import enums.Raisons;

public class ScrabbleContract extends ScrabbleDecorator {

    private Collection<Point> departPossible = new ArrayList<Point>();

    public ScrabbleContract(ScrabbleService sc) {
	super(sc);
	// TODO Auto-generated constructor stub
    }

    public void checkInvariant() {
	if (!(getPoints() >= 0)) {
	    throw new InvariantError("Points inferieur a 0");
	}
	IDico dick = new Dico();
	Collection<Lettre[]> cMot = getListeMot();
	for (Lettre[] mot : cMot) {
	    if (!(dick.isMotValide(mot))) {
		throw new InvariantError("mot : " + Lettre.toString(mot)
		        + " Non valide");
	    }
	}
	ArrayList<Lettre> lettreUtilise = new ArrayList<Lettre>(
	        getLettreRestant());
	lettreUtilise.addAll(getLettreTire());

	for (int i = 0; i < 15; i++) {
	    for (int j = 0; j < 15; j++) {
		if (getLettre(i, j) != Lettre.VIDE) {
		    lettreUtilise.add(getLettre(i, j));
		}
	    }
	}

	Collections.sort(lettreUtilise);

	if (!(Arrays.equals(lettreUtilise.toArray(), Lettre.initSacALettre()
	        .toArray()))) {
	    throw new InvariantError("Il manque des lettre ");

	}
	if (!(getLettreTire().size() <= 7))
	    throw new InvariantError("Les lettre tire sont superieur a 7");
    }

    @Override
    public Collection<Lettre[]> getListeMot() {
	return super.getListeMot();
    }

    @Override
    public Lettre getLettre(int x, int y) {

	if (!(x >= 0 && x < 15))
	    throw new PreConditionError("x doit etre compris entre 0 et 14");
	if (!(y >= 0 && y < 15))
	    throw new PreConditionError("y doit etre compris entre 0 et 14");

	return super.getLettre(x, y);

    }

    @Override
    public int getPoints() {

	return super.getPoints();

    }

    @Override
    public Collection<Lettre> getLettreRestant() {
	return super.getLettreRestant();
    }

    @Override
    public Collection<Lettre> getLettreTire() {
	return super.getLettreTire();
    }

    @Override
    public PowerUp getBonus(int x, int y) {

	if (!(x >= 0 && x < 15))
	    throw new PreConditionError("x doit etre compris entre 0 et 14");
	if (!(y >= 0 && y < 15))
	    throw new PreConditionError("y doit etre compris entre 0 et 14");

	return super.getBonus(x, y);
    }

    @Override
    public void init() {
	checkInvariant();
	departPossible.add(new Point(7, 7));

	super.init();

	for (int i = 0; i < 15; i++) {
	    for (int j = 0; j < 15; j++) {
		if (!(getLettre(i, j) == Lettre.VIDE))
		    throw new PostConditionError("case : " + i + "," + j
			    + "n'est pas vide");
	    }
	}
	if (!(getPoints() == 0))
	    throw new PostConditionError("Points different de 0");
	if (!(getLettreRestant().size() == Lettre.Initial.size()))
	    throw new PostConditionError(
		    "les Lettres dans le sac ne sont pas au nombre de 100");
	checkInvariant();

    }

    @Override
    public void init(Lettre[][] mots, Lettre[] tirage) {

	if (!(tirage.length == 7))
	    throw new PreConditionError("le tirage doit etre de taille 7");
	if (!(mots.length == 15))
	    throw new PreConditionError("le plateau ne fait pas 15 par 15");
	for (int i = 0; i < mots.length; i++) {
	    if (!(mots[i].length == 15))
		throw new PreConditionError("le plateau ne fait pas 15 par 15");

	}
	checkInvariant();

	super.init(mots, tirage);
	checkInvariant();
	for (int i = 0; i < 15; i++) {
	    for (int j = 0; j < 15; j++) {
		if (!(getLettre(i, j).equals(mots[i][j])))
		    throw new PostConditionError("case : " + i + "," + j
			    + "n'est pas vide");
	    }
	}
	for (int i = 0; i < tirage.length; i++) {
	    if (!(tirage[i] == getLettreTire().toArray(
		    new Lettre[tirage.length])[i]))
		throw new PostConditionError("LE tirage n'est pas le meme");
	}
    }

    @Override
    public Collection<Lettre> tirerLettre(int i) {
	checkInvariant();

	if (!(i <= getLettreRestant().size()))
	    throw new PreConditionError(
		    "Il n'y as pas assez de lettre dans le sac");

	if (!(i + getLettreTire().size() == 7))
	    throw new PreConditionError(
		    "Le nimbre de lettre tirer plus le nombre de lettre tirer ne vaut pas 7");

	Collection<Lettre> pre = new ArrayList<Lettre>(getLettreTire());
	Collection<Lettre> pre2 = new ArrayList<Lettre>(getLettreRestant());

	Collection<Lettre> ret = super.tirerLettre(i);

	pre.addAll(ret);

	if (!(Arrays.equals(pre.toArray(), getLettreTire().toArray())))
	    throw new PostConditionError(
		    "Probleme d'ajout des lettre tirer dans les lettre pour jouer");
	for (Lettre lettre : ret) {
	    pre2.remove(lettre);
	}
	if (!(Arrays.equals(pre2.toArray(), getLettreRestant().toArray())))
	    throw new PostConditionError(
		    "Probleme des lettres retire des lettres restant");
	if (!(getLettreTire().size() == 7))
	    throw new PostConditionError(
		    "le nombre de lettre jouable n'est pas 7");

	return ret;
    }

    @Override
    public void placerMot(Lettre[] mot, int x, int y, boolean horizontal) {
	checkInvariant();

	if (!(x >= 0 && x < 15))
	    throw new PreConditionError("x doit etre compris entre 0 et 14");
	if (!(y >= 0 && y < 15))
	    throw new PreConditionError("y doit etre compris entre 0 et 14");
	if (!(mot.length > 1 && mot.length < 16))
	    throw new PreConditionError(
		    "le taille du mot a placer doit etre compris entre 2 et 15");
	if (horizontal)
	    if (!(x + mot.length < 15))
		throw new PreConditionError("le mot depasse du plateau");
	if (!horizontal)
	    if (!(y + mot.length < 15))
		throw new PreConditionError("le mot depasse du plateau");

	boolean ok = false;
	int k = x;
	int l = y;
	for (int i = 0; i < mot.length; i++) {
	    if (isStart(k, l)) {
		ok = true;
	    }

	    if (getLettre(k, l) != Lettre.VIDE && getLettre(k, l) != mot[i]) {
		throw new PreConditionError("La lettre  " + mot[i]
		        + " ne peut pas etre placer");
	    }
	    if (getLettre(k, l) == Lettre.VIDE) {
		if (!getLettreTire().contains(mot[i]))
		    throw new PreConditionError("Lettre " + mot[i]
			    + " non presente dans le tirage");
	    }
	    if (horizontal)
		k++;
	    else
		l++;
	}
	if (!ok)
	    throw new PreConditionError("Mauvais Depart !! ");

	int prePoint = getPoints();
	super.placerMot(mot, x, y, horizontal);

	checkInvariant();

	k = x;
	l = y;
	for (int i = 0; i < mot.length; i++) {
	    departPossible.add(new Point(k, l));
	    if (horizontal)
		k++;
	    else
		l++;
	}

	boolean throwPost = true;
	for (Lettre[] lettre : getListeMot()) {
	    ok = true;
	    for (int i = 0; i < lettre.length; i++) {
		if (!(lettre[i].equals(mot[i]))) {
		    ok = false;
		    break;
		}
	    }
	    if (ok) {
		throwPost = false;
		break;
	    }
	}

	if (throwPost)
	    throw new PostConditionError("Le mot n'a pas ete placer");

	if (horizontal)
	    for (int i = x; i < x + mot.length; i++)
		if (!(getLettre(i, y).equals(mot[i - x])))
		    throw new PostConditionError(
			    "Probleme avec le placement du mot");
	if (!horizontal)
	    for (int i = y; i < y + mot.length; i++)
		if (!(getLettre(x, i).equals(mot[i - y])))
		    throw new PostConditionError(
			    "Probleme avec le placement du mot");
	if (!(getPoints() > prePoint)) {

	    throw new PostConditionError("POINT : " + getPoints() + " <="
		    + prePoint);
	}
    }

    public boolean isStart(int x, int y) {
	for (Point2D p : departPossible) {
	    if (p.equals(new Point(x, y)))
		return true;
	}
	return false;
    }

    public boolean isValidPlacement(String placement) {

	checkInvariant();

	if (!(placement.length() == 225))
	    throw new PreConditionError(
		    "le placement ne fait pas 225 character");

	boolean retour = super.isValidPlacement(placement);

	checkInvariant();

	return retour;

    }

    public Raisons raisonValide(String placement, ScrabbleService scra) {
	checkInvariant();

	if (!(placement.length() == 225))
	    throw new PreConditionError(
		    "le placement ne fait pas 225 character");

	Raisons retour = super.raisonValide(placement, scra);

	checkInvariant();

	return retour;

    }

    public void reTire() {
	checkInvariant();
	super.reTire();
	checkInvariant();
    }
}
