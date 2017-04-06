package dictionnaire;



import java.util.ArrayList;
import java.util.Collections;


public class THybrid implements ITHybrid {
	private ITHybrid	inf, eq, sup;
	private char		value;
	private boolean		finDeMot;

	public THybrid() {
		this.inf = null;
		this.sup = null;
		this.eq = null;
		this.value = ' ';
		this.finDeMot = false;
	}

	public THybrid(char value) {
		this.inf = null;
		this.eq = null;
		this.sup = null;
		this.value = value;
		this.finDeMot = false;
	}

	public THybrid(char value, boolean finDeMot) {
		this.inf = null;
		this.eq = null;
		this.sup = null;
		this.value = value;
		this.finDeMot = finDeMot;
	}

	public THybrid(ITHybrid inf, ITHybrid eq, ITHybrid sup, char value,
			boolean finDeMot) {
		super();
		this.inf = inf;
		this.eq = eq;
		this.sup = sup;
		this.value = value;
		this.finDeMot = finDeMot;
	}

	public ITHybrid getInf() {
		return inf;
	}

	public void setInf(ITHybrid inf) {
		this.inf = inf;
	}

	public ITHybrid getEq() {
		return eq;
	}

	public void setEq(ITHybrid eq) {
		this.eq = eq;
	}

	public ITHybrid getSup() {
		return sup;
	}

	public void setSup(ITHybrid sup) {
		this.sup = sup;
	}

	public char getValue() {
		return value;
	}

	public void setValue(char value) {
		this.value = value;
	}

	public boolean isFinDeMot() {
		return finDeMot;
	}

	public void setFinDeMot(boolean finDeMot) {
		this.finDeMot = finDeMot;
	}

	/**
	 * @param mot
	 *            à ajouter
	 * @return void modification de l'arbre en place
	 */
	public void insertion(String mot) {
		if (mot.isEmpty() || mot == null)
			return;
		if (this.noSon() && this.value == ' ' && this.finDeMot == false) {
			this.creerRacine(mot.charAt(0));
			if (mot.length() == 1)
				this.finDeMot = true;
			this.insertionRec(mot);
			return;
		}
		this.insertionRec(mot);
	}

	/**
	 * @param mot
	 *            à ajouter
	 * @return void modification de l'arbre en place fonction récursive
	 */
	private void insertionRec(String mot) {

		if (mot.isEmpty())
			return;

		char premier = mot.charAt(0);
		String reste = mot.substring(1);
		boolean fin = (mot.length() == 1);

		if (premier == this.value) {

			if (mot.length() == 1) {
				this.finDeMot = true;
				return;
			}
			if (this.eq == null)
				this.eq = new THybrid(mot.charAt(1));
			((THybrid) this.getEq()).insertionRec(reste);
			return;
		}

		if (premier < this.value) {
			if (this.inf == null)
				this.inf = new THybrid(premier, fin);
			((THybrid) this.getInf()).insertionRec(mot);
			return;
		}

		if (premier > this.value) {
			if (this.sup == null)
				this.sup = new THybrid(premier, fin);
			((THybrid) this.getSup()).insertionRec(mot);
			return;
		}

	}

	/** a utilise que dans le cas d'un arbre vide */
	public void creerRacine(char prem) {
		this.value = prem;
		this.inf = null;
		this.eq = null;
		this.sup = null;
		this.finDeMot = false;
	}

	@Override
	public int hauteur() {
		int res = 0;
		if (this.sup != null) {
			res = Math.max(res, this.sup.hauteur());
		}
		if (this.eq != null) {
			res = Math.max(res, this.eq.hauteur());
		}
		if (this.inf != null) {
			res = Math.max(res, this.inf.hauteur());
		}
		return res + 1;
	}

	
	public double profondeurMoyenne(){
	    ArrayList<Integer> res=new ArrayList<Integer>();
	    int sum=0;
	    
	    profondeurMoyenneRec(res,0);
	    
	    for (Integer integer : res) {
		sum+=integer;
	    }
	    
	    return (sum/(res.size()+0.));
	}

	public void profondeurMoyenneRec(ArrayList<Integer> res, int i) {
	    
	    if(this.eq==null || this.inf ==null || this.sup==null)
		res.add(i);
	    THybrid tree=this;
	    
	    if(this.inf!=null)
		((THybrid) tree.getInf()).profondeurMoyenneRec(res,i+1);
	    if(this.eq!=null)
		((THybrid) tree.getEq()).profondeurMoyenneRec(res,i+1);
	    if(this.sup!=null)
		((THybrid) tree.getSup()).profondeurMoyenneRec(res,i+1);
	    
	}

	/**
	 * supprime la racine la remplacant par sont fils ayant la plus grande
	 * hauteur
	 */
	@Override
	public void supprimerRacine() {
		int tailleInf = (this.inf == null) ? 0 : this.inf.hauteur();
		int tailleSup = (this.sup == null) ? 0 : this.sup.hauteur();
		if (tailleInf == 0 && tailleSup == 0)
			return;

		if (tailleInf > tailleSup) {
			ITHybrid Is = this.inf.getSup();
			ITHybrid I = this.inf;
			this.inf = I.getInf();
			this.value = I.getValue();
			this.eq = I.getEq();
			this.finDeMot = I.isFinDeMot();

			if (this.sup != null) {
				ITHybrid previterSi = this.sup;
				ITHybrid iterSi = this.sup;
				while (iterSi != null) {
					previterSi = iterSi;
					iterSi = iterSi.getInf();
				}
				previterSi.setInf(Is);
			} else {
				this.sup = Is;
			}

		}

		if (tailleInf <= tailleSup) {
			ITHybrid Si = this.sup.getInf();
			ITHybrid S = this.sup;
			this.sup = S.getSup();
			this.value = S.getValue();
			this.eq = S.getEq();
			this.finDeMot = S.isFinDeMot();

			if (this.inf != null) {
				ITHybrid previterIs = this.inf;
				ITHybrid iterIs = this.inf;
				while (iterIs != null) {
					previterIs = iterIs;
					iterIs = iterIs.getSup();
				}
				previterIs.setInf(Si);
			} else {
				this.sup = Si;
			}
		}

	}

	/**
	 * @param mot
	 *            a retirer de l'arbre
	 */
	@Override
	public void remove(String mot) {
		/**
		 * cas d'arrets : le mot est vide
		 */
		if (mot.isEmpty())
			return;
		char premier = mot.charAt(0);
		String reste = mot.substring(1);
		boolean fin = (mot.length() == 1);

		if (premier == this.value) {
			if (fin)
				this.finDeMot = false;

			if (this.eq == null) {

				return;
			}
			this.eq.remove(reste);
			/**
			 * si seulement le mot à supprimer passait par ce noeud on appel
			 * supprimer racine pour le supprimer
			 */
			if (this.eq.noSon() && !this.eq.isFinDeMot()) {
				this.eq = null;
				this.supprimerRacine();
			}

			return;
		}

		if (premier < this.value) {
			if (this.inf == null) {
				this.inf = new THybrid(premier, fin);
			}
			this.inf.remove(mot);
			if (this.inf.noSon() && !this.inf.isFinDeMot())
				this.inf = null;
			return;
		}

		if (premier > this.value) {
			if (this.sup == null) {
				this.sup = new THybrid(premier, fin);
			}
			this.sup.remove(mot);

			if (this.sup.noSon() && !this.sup.isFinDeMot())
				this.sup = null;
			return;
		}

	}

	@Override
	public boolean contient(String mot) {
		if (mot.isEmpty())
			return false;

		char premier = mot.charAt(0);
		String reste = mot.substring(1);
		if (premier == this.value) {
			if (mot.length() == 1)
				return this.finDeMot;
			if (this.eq == null)
				return false;
			return this.eq.contient(reste);
		}
		if (premier < this.value) {
			if (this.inf == null)
				return false;
			return this.inf.contient(mot);
		}
		if (premier > this.value) {
			if (this.sup == null)
				return false;
			return this.sup.contient(mot);
		}
		return false;
	}

	public int compteMot() {
		int res = this.finDeMot ? 1 : 0;
		if (this.sup != null) {
			res += this.sup.compteMot();
		}
		if (this.eq != null) {
			res += this.eq.compteMot();
		}
		if (this.inf != null) {
			res += this.inf.compteMot();
		}
		return res;
	}

	public ArrayList<String> listeMot() {
		ArrayList<String> res = new ArrayList<String>();
		String cur = "";
		res = listeMotRec(res, cur);

		Collections.sort(res);
		return res;
	}

	public ArrayList<String> listeMotRec(ArrayList<String> res, String cur) {

		if (this.finDeMot) {
			String str = cur + this.value;
			res.add(str);
		}

		if (this.sup != null) {
			((THybrid) this.getSup()).listeMotRec(res, cur);
		}
		if (this.eq != null) {
			String str = cur + this.value;
			((THybrid) this.getEq()).listeMotRec(res, str);
		}
		if (this.inf != null) {
			((THybrid) this.getInf()).listeMotRec(res, cur);
		}

		return res;
	}

	@Override
	public boolean noSon() {
		return inf == null && eq == null && sup == null;
	}

	@Override
	public String toString() {
		return "[ '" + value + "'," + finDeMot + ", inf="
				+ (inf == null ? "" : inf) + ", eq=" + (eq == null ? "" : eq)
				+ ", sup=" + (sup == null ? "" : sup) + "]";
	}

	@Override
	public int comptageNil() {
		int res = 0;
		if (this.eq == null)
			res++;
		else
			res += this.eq.comptageNil();

		if (this.inf == null)
			res++;
		else
			res += this.inf.comptageNil();

		if (this.sup == null)
			res++;
		else
			res += this.sup.comptageNil();

		return res;
	}

	public int prefixe(String mot) {

		if (mot.length() == 0) {
			return this.compteMot();
		}

		char prem = mot.charAt(0);
		String rest = mot.substring(1);

		if (this.value == prem) {
			if (this.eq == null)
				return 0;
			return ((THybrid) this.getEq()).prefixe(rest);
		}
		if (this.value < prem) {
			if (this.sup == null)
				return 0;
			return ((THybrid) this.getSup()).prefixe(mot);
		}
		if (this.value > prem) {
			if (this.inf == null)
				return 0;
			return ((THybrid) this.getInf()).prefixe(mot);
		}

		return 0;
	}


	/**
	 * @param une
	 *            arrayList à remplir remplis l'arrayList passé en argument
	 */
	public void parcoursRec(ArrayList<ArrayList<Object>> deg) {

		if (this.inf != null) {
			ITHybrid cur = this.inf;
			deg.add(((THybrid) cur).motEndessous());
			((THybrid) cur).parcoursRec(deg);
		}

		if (this.sup != null) {
			ITHybrid cur = this.sup;
			deg.add(((THybrid) cur).motEndessous());
			((THybrid) cur).parcoursRec(deg);
		}
		return;

	}

	/**
	 * @return renvois une arrayListe contenant 3 éléments : - la String de
	 *         l'arete à inserer dans le patricia - le noeud apres tout les
	 *         noeuds contenant les caractère de cette String - un objet
	 *         initialisé si c'est une fin de mot null sinon
	 */
	public ArrayList<Object> motEndessous() {
		String concat = "" + this.getValue();
		ArrayList<Object> tmp = new ArrayList<Object>(3);

		if (this.isFinDeMot()) {
			tmp.add(concat);
			tmp.add(this.getEq());
			tmp.add(new Object());
			return tmp;
		}

		if (this.eq != null) {
			ITHybrid cur = this.getEq();

			while (cur != null) {

				if (cur.getInf() != null || cur.getSup() != null) {
					tmp.add(concat);
					tmp.add(cur);
					tmp.add(null);
					return tmp;

				}

				if (cur.getEq() != null && cur.isFinDeMot()) {
					tmp.add(concat + cur.getValue());
					tmp.add(cur.getEq());
					tmp.add(new Object());
					return tmp;

				}

				concat += cur.getValue();
				cur = cur.getEq();
			}
		}

		tmp.add(concat);
		tmp.add(null);
		tmp.add(null);
		return tmp;
	}

	/** calcul la hauteur sans prendre en compte les fils eq */
	private int hauteurEquilibre() {
		int res = 0;
		if (this.sup != null) {
			res = Math.max(res, this.sup.hauteur());
		}
		if (this.inf != null) {
			res = Math.max(res, this.inf.hauteur());
		}
		return res + 1;
	}

	private void rotaDroite() {
		if (this.inf == null) {
			System.out.println("inf null :" + this);
			return;
		}
		THybrid inf = (THybrid) this.getInf();
		THybrid sup = (THybrid) this.getSup();
		THybrid iS = (inf == null ? null : (THybrid) this.getInf().getSup());

		ITHybrid eqCurr = eq;
		char valueCurr = value;
		boolean finDeMotCurr = finDeMot;

		this.value = inf.getValue();
		this.eq = inf.getEq();
		this.finDeMot = inf.isFinDeMot();
		this.inf = inf.getInf();

		THybrid newR = new THybrid(valueCurr, finDeMotCurr);
		newR.setEq(eqCurr);
		newR.setInf(iS);
		newR.setSup(sup);

		this.setSup(newR);

	}

	private void rotaGauche() {
		if (this.sup == null) {
			System.out.println("sup null :" + this);
			return;
		}
		THybrid inf = (THybrid) this.getInf();
		THybrid sup = (THybrid) this.getSup();
		THybrid sI = (sup == null ? null : (THybrid) this.getSup().getInf());

		ITHybrid eqCurr = eq;
		char valueCurr = value;
		boolean finDeMotCurr = finDeMot;

		this.value = sup.getValue();
		this.eq = sup.getEq();
		this.finDeMot = sup.isFinDeMot();
		this.sup = sup.getSup();

		THybrid newR = new THybrid(valueCurr, finDeMotCurr);
		newR.setEq(eqCurr);
		newR.setSup(sI);
		newR.setInf(inf);

		this.setInf(newR);
	}

	/** rééquilibre l'arbre en place */
	@Override
	public void reEquilibre() {
		if (this.noSon())
			return;

		if (this.inf != null)
			this.inf.reEquilibre();

		if (this.sup != null)
			this.sup.reEquilibre();

		THybrid inf = (THybrid) this.getInf();
		THybrid sup = (THybrid) this.getSup();

		int hSup = (sup == null) ? 0 : sup.hauteurEquilibre();
		int hInf = (inf == null) ? 0 : inf.hauteurEquilibre();

		int difference = hInf - hSup;

		if (difference >= 2) {
			THybrid infInf = (THybrid) inf.getInf();
			THybrid infSup = (THybrid) inf.getSup();

			if (((infInf == null) ? 0 : infInf.hauteurEquilibre()) < ((infSup == null) ? 0
					: infSup.hauteurEquilibre()))
				inf.rotaGauche();
			this.rotaDroite();
		}
		if (difference <= -2) {
			THybrid supSup = (THybrid) sup.getSup();
			THybrid supInf = (THybrid) sup.getInf();
			if (((supSup == null) ? 0 : supSup.hauteurEquilibre()) < ((supInf == null) ? 0
					: supInf.hauteurEquilibre()))
				sup.rotaDroite();
			this.rotaGauche();
		}

	}
}