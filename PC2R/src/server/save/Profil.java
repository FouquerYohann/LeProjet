package server.save;

import java.util.Date;

public class Profil {
	private String	name;
	private int		score;
	private Date	lastConnexion;
	private int		nbConnexion;

	public Profil(String name, int score, Date lastConnexion, int nbConnexion) {
		super();
		this.name = name;
		this.score = score;
		this.lastConnexion = lastConnexion;
		this.nbConnexion = nbConnexion;
	}

	public Profil(String name) {
		this(name, 0, new Date(), 1);
	}

	public void connexion() {
		lastConnexion = new Date();
		nbConnexion++;
	}

	public void deconnexion(int score) {
		this.score += score;
	}

	public String getName() {
		return name;
	}

	public int getScore() {
		return score;
	}

	public Date getLastConnexion() {
		return lastConnexion;
	}

	public int getNbConnexion() {
		return nbConnexion;
	}

	public String toCSV() {
		return name + ";" + score + ";" + lastConnexion.getTime() + ";"
				+ nbConnexion + "\n";
	}

	public static Profil fromCSV(String line) {
		Profil ret = null;
		try {
			String tok[] = new String[4];
			tok=line.split(";");
			ret = new Profil(tok[0], Integer.parseInt(tok[1]), new Date(
					Long.parseLong(tok[2])), Integer.parseInt(tok[3]));
		} catch (Error e) {
			System.err.println("format error :" + line);
		}
		return ret;
	}
}
