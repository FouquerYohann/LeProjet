package patricias;

import java.util.ArrayList;
import java.util.Collections;

import patricia.interfaces.IArrete;
import patricia.interfaces.INode;

public class Node implements INode {
    protected ArrayList<IArrete> fils;

    public Node() {
	fils = new ArrayList<IArrete>();
    }

    public Node(ArrayList<IArrete> fils) {
	super();
	this.fils = fils;
    }

    public INode clone() {
	ArrayList<IArrete> new_fils = new ArrayList<IArrete>();
	for (IArrete iArrete : this.getFils()) {
	    if (iArrete == Arrete.END_OF_STRING)
		new_fils.add(Arrete.END_OF_STRING);
	    else
		new_fils.add(iArrete.clone());
	}
	Node new_node = new Node(new_fils);
	for (int i = 0; i < new_fils.size(); i++) {

	    new_node.getFils().get(i)
		    .setNext(this.getFils().get(i).getNext().clone());
	}
	return new_node;
    }

    @Override
    public String toString() {
	return " " + fils + " ";
    }

    @Override
    public void insertion(String mot) {
	this.insertionRec(mot);
	this.fils.remove(Arrete.END_OF_STRING);
    }

    public void insertionRec(String mot) {
	if (mot == null || mot.isEmpty() || mot.equals(" ")) {
	    return;
	}
	if (this.noSon()) {
	    fils.add(new Arrete(mot));
	    this.addFils(Arrete.END_OF_STRING);
	    return;
	}

	for (IArrete iArrete : fils) {
	    if (iArrete == Arrete.END_OF_STRING)
		continue;
	    String value = iArrete.getValue();
	    int i = 0;

	    if (value.equals(mot)) {
		if (iArrete.getNext().noSon()
			|| iArrete.getNext().getFils()
				.contains(Arrete.END_OF_STRING))
		    return;
		iArrete.getNext().addFils(Arrete.END_OF_STRING);
		return;
	    }
	    String str_commun, ancien, nouveaux;
	    while (true) {
		// mot plus court separation arrete
		if (i >= mot.length()) {
		    str_commun = mot.substring(0, i);
		    ancien = value.substring(i);
		    iArrete.setValue(str_commun);
		    INode petiFils = iArrete.getNext();
		    INode nouveauxfils = new Node();
		    nouveauxfils.addFils(Arrete.END_OF_STRING);
		    nouveauxfils.addFils(new Arrete(petiFils, ancien));
		    iArrete.setNext(nouveauxfils);
		    return;
		}
		// appel recursif sur la suite prefixe commun et existant dans
		// l'arbre
		if (i >= value.length()) {
		    nouveaux = mot.substring(i);
		    ((Node) iArrete.getNext()).insertionRec(nouveaux);
		    return;
		}
		// rajout d'une arrete mot inexistant prefixe en partie existant
		if (value.charAt(i) != mot.charAt(i)) {
		    if (i == 0) {
			break;
		    }
		    str_commun = mot.substring(0, i);
		    ancien = mot.substring(i);
		    nouveaux = value.substring(i);
		    iArrete.setValue(str_commun);
		    INode petiFils = iArrete.getNext();
		    IArrete nouvelleArrete = new Arrete(petiFils, nouveaux);
		    iArrete.setNext(new Node());
		    iArrete.getNext().addFils(nouvelleArrete);
		    IArrete arreteAncien = new Arrete(ancien);
		    iArrete.getNext().addFils(arreteAncien);
		    arreteAncien.getNext().addFils(Arrete.END_OF_STRING);
		    return;
		}
		i++;
	    }
	}
	this.addFils(new Arrete(mot));

    }

    public void setFils(ArrayList<IArrete> fils) {
	this.fils = fils;
    }

    @Override
    public void remove(String mot) {
	this.removeRec(mot);
	this.verifArbre();
    }

    public void removeRec(String mot) {
	IArrete asupprimer = null;
	if (this.noSon()) {
	    return;
	}

	for (IArrete iArrete : fils) {
	    int ar_length = iArrete.getValue().length();
	    if (iArrete == Arrete.END_OF_STRING && mot.equals("")) {
		asupprimer = iArrete;
		break;
	    }
	    if (mot.equals(iArrete.getValue())
		    && iArrete.getNext().getFils().isEmpty()) {
		asupprimer = iArrete;
		break;
	    }
	    try {
		String deb_mot = mot.substring(0, ar_length);
		String fin_mot = mot.substring(ar_length);
		if (deb_mot.equals(iArrete.getValue())) {
		    ((Node) (iArrete.getNext())).removeRec(fin_mot);
		}
	    } catch (IndexOutOfBoundsException e) {
		// si la value de l'arrete est plus grande que le mot rechercher
		continue;
	    }
	}
	if (asupprimer == null) {
	    return;
	}
	this.fils.remove(asupprimer);
	return;
    }

    public void verifArbre() {

	for (IArrete iArrete : fils) {
	    ArrayList<IArrete> petitfils = iArrete.getNext().getFils();
	    if (petitfils.size() == 1) {
		String value = petitfils.get(0).getValue();
		INode petitfils_next = petitfils.get(0).getNext();
		iArrete.setValue(iArrete.getValue().concat(value));
		iArrete.setNext(petitfils_next);
	    }
	    iArrete.getNext().verifArbre();
	}

    }

    @Override
    public boolean contient(String mot) {
	// cas d'arret fin du mot dans l'arbre
	if (this.noSon()) {
	    // renvoi VRAI si la string est également finis FAUX sinon
	    return mot.length() == 0;
	}
	for (IArrete iArrete : fils) {
	    int ar_length = iArrete.getValue().length();
	    if (iArrete == Arrete.END_OF_STRING) {
		return true;
	    }
	    try {
		String deb_mot = mot.substring(0, ar_length);
		String fin_mot = mot.substring(ar_length);
		if (deb_mot.equals(iArrete.getValue())) {
		    return iArrete.getNext().contient(fin_mot);
		}
	    } catch (IndexOutOfBoundsException e) {
		// si la value de l'arrete est plus grande que le mot rechercher
		continue;
	    }

	}
	return false;
    }

    @Override
    public boolean noSon() {
	return fils.isEmpty();
    }

    @Override
    public ArrayList<IArrete> getFils() {
	return fils;
    }

    @Override
    public void addFils(IArrete fils) {
	this.fils.add(fils);
    }

    @Override
    public boolean equals(Object o) {
	if (this == o)
	    return true;
	if (o instanceof Node) {
	    Node node = (Node) o;
	    node.getFils().sort(null);
	    this.getFils().sort(null);
	    if (this.fils.size() != node.fils.size())
		return false;
	    for (int i = 0; i < this.fils.size(); i++) {
		IArrete nodeI = node.getFils().get(i);
		IArrete thisI = this.getFils().get(i);

		if (!nodeI.equals(thisI)) {
		    return false;
		}
	    }
	    return true;
	}
	return false;
    }

    @Override
    public int compteMot() {
	int res = 0;
	if (this.noSon()) {
	    return 1;
	}
	for (IArrete iArrete : fils) {
	    if (iArrete == Arrete.END_OF_STRING) {
		res++;
		continue;
	    }
	    res += iArrete.getNext().compteMot();
	}
	return res;
    }

    @Override
    public int comptageNil() {
	int res = 0;
	if (this.noSon()) {
	    return 0;
	}
	for (IArrete iArrete : fils) {
	    if (iArrete == Arrete.END_OF_STRING) {
		res++;
		continue;
	    }
	    res += iArrete.getNext().comptageNil();
	}
	return res;
    }

    @Override
    public int hauteur() {
	int res = 0;
	if (this.noSon())
	    return 0;

	for (IArrete iArrete : fils) {
	    if (iArrete == Arrete.END_OF_STRING)
		continue;
	    res = Math.max(iArrete.getNext().hauteur() + 1, res);
	}
	return res;
    }

    @Override
    public double profondeurMoyenne() {
	ArrayList<Integer> res = new ArrayList<Integer>();
	int current = 0;
	profondeurMoyenneRec(res, current);
	int sum = 0;
	for (Integer integer : res) {
	    sum += integer;
	}

	return (sum / (res.size() + 0.));
    }

    public void profondeurMoyenneRec(ArrayList<Integer> res, int current) {

	if (this.noSon())
	    res.add(current);

	for (IArrete iArrete : fils) {
	    if (iArrete == Arrete.END_OF_STRING)
		continue;
	    ((Node) iArrete.getNext()).profondeurMoyenneRec(res, current + 1);
	}
	return;
    }

    @Override
    public int prefixe(String mot) {
	if (mot.isEmpty())
	    return this.compteMot();
	for (IArrete iArrete : fils) {
	    int i = 0;
	    String value = iArrete.getValue();
	    while (true) {
		if (i >= value.length()) {
		    String new_string = mot.substring(i);
		    return iArrete.getNext().prefixe(new_string);
		}
		if (i >= mot.length()) {
		    return iArrete.getNext().compteMot();
		}
		if (value.charAt(i) != mot.charAt(i)) {
		    break;
		}
		i++;
	    }
	    if (i > 1)
		break;
	}
	return 0;
    }

    @Override
    public ArrayList<String> listeMot() {
	ArrayList<String> res = new ArrayList<String>();
	((Node) this).listeMotRec(res, "");
	res.remove("");
	Collections.sort(res);
	return res;
    }

    public void listeMotRec(ArrayList<String> resultat, String current) {
	if (this.noSon()) {
	    resultat.add(current);
	    return;
	}
	for (IArrete iArrete : fils) {
	    if (iArrete == Arrete.END_OF_STRING) {
		resultat.add(current);
		continue;
	    }
	    String new_string = current + iArrete.getValue();
	    ((Node) iArrete.getNext()).listeMotRec(resultat, new_string);
	}
	return;
    }

    public INode fusion(INode n1) {

	INode n2 = this;
	if (n1.noSon()
		|| (n1.getFils().size() == 1 && n1.getFils().get(0) == Arrete.END_OF_STRING)) {
	    if (!n2.getFils().contains(Arrete.END_OF_STRING)) {
		n2.addFils(Arrete.END_OF_STRING);
	    }
	    return n2.clone();
	}
	if (n2.noSon()
		|| (n2.getFils().size() == 1 && n2.getFils().get(0) == Arrete.END_OF_STRING)) {
	    if (!n1.getFils().contains(Arrete.END_OF_STRING)) {
		n1.addFils(Arrete.END_OF_STRING);
	    }
	    return n1.clone();
	}

	INode res = new Node();

	ArrayList<IArrete> l1 = new ArrayList<IArrete>();
	ArrayList<IArrete> l2 = new ArrayList<IArrete>();
	for (IArrete iArrete : n1.getFils())
	    l1.add(iArrete.clone());

	for (IArrete iArrete : n2.getFils())
	    l2.add(iArrete.clone());

	if (l1.remove(Arrete.END_OF_STRING))
	    res.addFils(Arrete.END_OF_STRING);
	if (l2.remove(Arrete.END_OF_STRING))
	    if (!res.getFils().contains(Arrete.END_OF_STRING))
		res.addFils(Arrete.END_OF_STRING);

	l1.sort(null);
	l2.sort(null);

	IArrete iter1 = l1.remove(0);
	IArrete iter2 = l2.remove(0);

	while (true) {

	    char prem1 = iter1.getValue().charAt(0);
	    char prem2 = iter2.getValue().charAt(0);

	    if (prem1 == prem2) {
		int i = 0;
		String str1 = iter1.getValue();
		String str2 = iter2.getValue();
		if (str1.equals(str2)) {
		    INode node1 = iter1.getNext();
		    INode node2 = iter2.getNext();

		    iter1.setNext(node1.fusion(node2));
		    res.addFils(iter1);

		} else {
		    while (true) {
			if (str1.charAt(i) != str2.charAt(i)) {
			    String com_str = str1.substring(0, i);
			    String sub_str1 = str1.substring(i);
			    String sub_str2 = str2.substring(i);

			    INode new_node = new Node();
			    new_node.addFils(new Arrete(iter1.getNext(),
				    sub_str1));
			    new_node.addFils(new Arrete(iter2.getNext(),
				    sub_str2));
			    IArrete com_ar = new Arrete(new_node, com_str);
			    res.getFils().add(com_ar);
			    break;

			}
			if (i == str1.length() - 1) {
			    String sub_str = str2.substring(i + 1);
			    INode cur1 = iter1.getNext();
			    INode cur2 = iter2.getNext();

			    IArrete sub_ar = new Arrete(cur2, sub_str);
			    INode fus2 = new Node();

			    fus2.addFils(sub_ar);
			    iter1.setNext(cur1.fusion(fus2));

			    res.getFils().add(iter1);
			    break;

			}
			if (i == str2.length() - 1) {
			    String sub_str = str1.substring(i + 1);
			    INode cur1 = iter1.getNext();
			    INode cur2 = iter2.getNext();

			    IArrete sub_ar = new Arrete(cur1, sub_str);
			    INode fus1 = new Node();

			    fus1.addFils(sub_ar);
			    iter2.setNext(cur2.fusion(fus1));

			    res.getFils().add(iter2);
			    break;
			}

			i++;
		    }
		}

		if (l1.isEmpty()) {
		    res.getFils().addAll(l2);
		    return res;
		}
		if (l2.isEmpty()) {
		    res.getFils().addAll(l1);
		    return res;
		}

		iter1 = l1.remove(0);
		iter2 = l2.remove(0);
	    }

	    if (prem1 > prem2) {
		res.getFils().add(iter2);
		if (l2.isEmpty()) {
		    res.getFils().addAll(l1);
		    res.addFils(iter1);
		    return res;
		}
		iter2 = l2.remove(0);
	    }
	    if (prem1 < prem2) {
		res.getFils().add(iter1);
		if (l1.isEmpty()) {
		    res.getFils().addAll(l2);
		    res.addFils(iter2);
		    return res;
		}
		iter1 = l1.remove(0);
	    }
	}

    }

}
