package enums;

import server.staticvalue.StaticState;

public enum PartieState {
	debut		(StaticState.debut),
	recherche	(StaticState.recherche),
	soumission	(StaticState.soumission),
	resultat	(StaticState.resultat);
		
	private String	name;
	private PartieState(String s) {
		name = s;
	}
	public String getValue() {
		return name;
	}
}