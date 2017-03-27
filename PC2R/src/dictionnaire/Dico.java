package dictionnaire;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import patricia.interfaces.INode;
import patricias.Node;
import ENUM.Lettre;

public class Dico implements IDico {
    private static final INode patTree=getDico();
    
    
    
    private static INode getDico(){
    INode patTre = new Node();
	BufferedReader br = null;
	try {
	    br = new BufferedReader(new FileReader("dico/dic.txt"));
	    String line;
	    while ((line = br.readLine()) != null)
		patTre.insertion(line);
	} catch (Exception e) {
	    e.printStackTrace();

	} finally {
	    try {
		br.close();
	    } catch (IOException e) {
		e.printStackTrace();
	    }
	}
	return patTre;
    }
    
    public boolean isMotValide(Lettre[] mot) {
	
	return patTree.contient(Lettre.toString(mot));
    }

}
