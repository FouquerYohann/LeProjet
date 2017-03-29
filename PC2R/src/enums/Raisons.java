package enums;

import server.staticvalue.StaticRaisons;

public enum Raisons {
	pos		(StaticRaisons.pos), 
	dic		(StaticRaisons.dic), 
	inf		(StaticRaisons.inf), 
	tricheur(StaticRaisons.tricheur), 
	none	("");

	private String	name;

	private Raisons(String s) {
		name = s;
	}

	public String getValue() {
		return name;
	}
}
