package patricia.interfaces;
import java.util.ArrayList;




public interface INode {
    public void insertion(String mot);
    public void remove(String mot);
    public boolean contient(String mot);
    public boolean noSon();
    public ArrayList<IArrete> getFils();
    public void addFils(IArrete fils);
    public void setFils(ArrayList<IArrete> fils);
    public void verifArbre();
    public int compteMot();
    public int comptageNil();
    public int hauteur();
    public double profondeurMoyenne();
    public int prefixe(String mot);
    public ArrayList<String> listeMot();
    public INode fusion(INode n1);
    public INode clone();
}
