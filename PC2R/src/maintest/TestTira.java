package maintest;

import server.ScrabbleImpl;
import service.ScrabbleService;

public class TestTira {

	public static void main(String[] args) {

		ScrabbleService sc = new ScrabbleImpl();
		sc.init();
		
		System.out.println("lala");
	}

}
