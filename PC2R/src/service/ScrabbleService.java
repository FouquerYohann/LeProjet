package service;

import java.util.Collection;

import enums.Lettre;
import enums.PowerUp;
import enums.Raisons;

public interface ScrabbleService {

    // OBSERVATOR
    /**
     * @return tout les mots de la grille
     */
    Collection<Lettre[]> getListeMot();

    /**
     * @require x>=0 && x<15 && y>=0 && y<15
     * @param x
     *            colonne
     * @param y
     *            ligne
     * @return la lettre de la y eme ligne et la x eme colonne
     */
    Lettre getLettre(int x, int y);

    /**
     * @return le nombre de point de la grille
     */
    int getPoints();

    /**
     * @return les lettres restantes à tirer
     */
    Collection<Lettre> getLettreRestant();

    /**
     * @return les lettres du tirage
     */
    Collection<Lettre> getLettreTire();

    /**
     * @require x>=0 && x<15 && y>=0 && y<15
     * @param x
     *            colonne
     * @param y
     *            ligne
     * @return le bonus de la y eme ligne et la x eme colonne
     */
    PowerUp getBonus(int x, int y);

    // INVARIANT
    // getPoint()>=0
    // /forall mot in getListeMot() Mot is valide
    // getLettreRestant().addAll(getLettreTire()).addAll(Lettre in
    // plateau).equals(LettreInitial)
    // getLettreTire().size <=7

    // CONSTRUCTOR
    /**
     * @post /forall (x,y) in (0..14,0..14) getLettre(x,y)=Lettre.VIDE
     * @post getPoint()==0
     * @post getLettreRestant().size()==102
     */
    void init();

    /**
     * @param mots
     *            mots à ajouter dans la grille
     * @post /forall (x,y) in (0..14,0..14) getLettre(x,y) = mots[x][y]
     */
    void init(Lettre[][] mots, Lettre[] tirage);

    // OPERATOR
    /**
     * @require i<= getLettreRestant().size() && i+getLettreTire() == 7
     * @post getLettreTire()==@pre getLettreTire().addAll(tirerLettre())
     * @post getLettreRestant()==@pre
     *       getLettreRestant().removeAll(getTirerLettre())
     * @post tirerLettre().size()==7
     * @param i
     *            nb lettre à tirer
     * @return les lettres tiré
     */
    public Collection<Lettre> tirerLettre(int i);

    /**
     * @require x>=0 && x<15 && y>=0 && y<15
     * @require mot.length > 1 && mot.length < 16
     * @require if(horizontal) x+mot.lenght<15
     * @require if(!horizontal) y+mot.lenght<15
     * 
     * @post mot /isIn getMot()
     * @post if(horizontal) i \in (x..x+mot.size()) getLettre(i,y)==mot[i-x]
     * @post if(!horizontal) i \in (y..y+mot.size()) getLettre(x,i)==mot[i-y]
     * @post getPoints()>@pre getPoints()
     * @param mot
     *            mot à placer
     * @param x
     *            colonne
     * @param y
     *            ligne
     * @param horizontal
     *            sens (true if horizontal)
     */
    public void placerMot(Lettre[] mot, int x, int y, boolean horizontal);

    /**
     * @return une chaine de 232 caractères contenant les 225 case de la grille
     *         et les 7 lettres a jouer
     * */

    public String send();

    public boolean isFini();
    
    public boolean isValidPlacement(String placement);

    public Raisons raisonValide(String placement,ScrabbleService scrabblePlayer);
    
    public void reTire();
    
    public String plusLong(ScrabbleService old);
    	  
    
    public boolean isEmpty();
}
