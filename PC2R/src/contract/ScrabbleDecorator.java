package contract;

import java.util.Collection;

import enums.Lettre;
import enums.PowerUp;
import service.ScrabbleService;

public class ScrabbleDecorator implements ScrabbleService {
    private ScrabbleService sc;
    
    
    
    public ScrabbleDecorator(ScrabbleService sc) {
	super();
	this.sc = sc;
    }



    public Collection<Lettre[]> getListeMot() {
	return sc.getListeMot();
    }



    public Lettre getLettre(int x, int y) {
	return sc.getLettre(x, y);
    }



    public int getPoints() {
	return sc.getPoints();
    }



    public Collection<Lettre> getLettreRestant() {
	return sc.getLettreRestant();
    }



    public Collection<Lettre> getLettreTire() {
	return sc.getLettreTire();
    }



    public PowerUp getBonus(int x, int y) {
	return sc.getBonus(x, y);
    }



    public void init() {
	sc.init();
    }



    public void init(Lettre[][] mots, Lettre[] tirage) {
	sc.init(mots,tirage);
    }



    public Collection<Lettre> tirerLettre(int i) {
	return sc.tirerLettre(i);
    }



    public void placerMot(Lettre[] mot, int x, int y, boolean horizontal) {
	sc.placerMot(mot, x, y, horizontal);
    }

    public String toString(){
	return sc.toString();
    }



    @Override
    public String send() {
	return sc.send();
    }
}
