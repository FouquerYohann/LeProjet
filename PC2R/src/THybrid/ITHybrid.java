package THybrid;


import java.util.ArrayList;




public interface ITHybrid {
    //UTIL
    public ITHybrid getInf();
    public ITHybrid getSup();
    public ITHybrid getEq();
    public char getValue();
    public boolean isFinDeMot();
    public void setInf(ITHybrid inf);
    public void setEq(ITHybrid eq);
    public void setSup(ITHybrid sup);
    public void setValue(char value);
    public void setFinDeMot(boolean finDeMot);
    public boolean noSon();
    
    //PRIMITIVE
    public void insertion(String mot);
    public void remove(String mot);
    public boolean contient(String mot);
    public int compteMot();
    public ArrayList<String> listeMot();
    public int hauteur();
	public void supprimerRacine();
	public int comptageNil();
	public void reEquilibre();
	public double profondeurMoyenne();
}
