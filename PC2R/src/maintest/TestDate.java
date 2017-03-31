package maintest;

import java.io.File;

import server.save.Profil;
import server.save.SaveProfil;

public class TestDate {
	public static void main(String[] args) throws Throwable{
		
		SaveProfil sp=new SaveProfil();
		
		sp.add(new Profil("toto"));
		sp.add(new Profil("tata"));
		sp.add(new Profil("tagf"));
		sp.add(new Profil("atat"));
		sp.add(new Profil("tqsd"));
		Thread.sleep(4544);
		sp.get("tata").connexion();
		System.out.println(sp.get("tata").getLastConnexion());
		System.out.println(sp.get("toto").getLastConnexion());
		sp.saveCSV(new File("save"));
		
		
	}
}
