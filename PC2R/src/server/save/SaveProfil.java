package server.save;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

public class SaveProfil implements Collection<Profil> {
	private ArrayList<Profil>	profils;

	public SaveProfil(File f) {
		if (f.exists())
			profils = csvLoad(f);
		else
			profils = new ArrayList<Profil>();
	}

	public SaveProfil() {
		profils = new ArrayList<Profil>();
	}

	private static ArrayList<Profil> csvLoad(File f) {
		ArrayList<Profil> ret = new ArrayList<Profil>();
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(
					new FileInputStream(f)));
			String line;
			while ((null != (line = br.readLine()))) {
				ret.add(Profil.fromCSV(line));
			}

			br.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return ret;
	}

	public void saveCSV(File f) {
		try {
			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream(f)));
			for (Profil profil : profils) {
				bw.write(profil.toCSV());
			}
			bw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public Profil get(String name) {
		for (Profil profil : profils) {
			if (profil.getName().equals(name))
				return profil;
		}
		return null;
	}

	public boolean contain(String name) {
		return get(name) != null;
	}

	public void connecte(String name){
		if(contain(name))
			get(name).connexion();
		else
			add(new Profil(name));
	}
	
	public void deconnecte(String name,int score){
		if(contain(name))
			get(name).deconnexion(score);
		else
			System.err.println("joueur inconnu : " +name);
	}
	
	@Override
	public int size() {
		return profils.size();
	}

	@Override
	public boolean isEmpty() {
		return profils.isEmpty();
	}

	@Override
	public boolean contains(Object o) {
		return profils.contains(o);
	}

	@Override
	public Iterator<Profil> iterator() {
		return profils.iterator();
	}

	@Override
	public Object[] toArray() {
		return profils.toArray();
	}

	@Override
	public <T> T[] toArray(T[] a) {
		return profils.toArray(a);
	}

	@Override
	public boolean add(Profil e) {
		return profils.add(e);
	}

	@Override
	public boolean remove(Object o) {
		return profils.remove(o);
	}

	@Override
	public boolean containsAll(Collection<?> c) {
		return profils.containsAll(c);
	}

	@Override
	public boolean addAll(Collection<? extends Profil> c) {
		return profils.addAll(c);
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		return profils.removeAll(c);
	}

	@Override
	public boolean retainAll(Collection<?> c) {
		return profils.retainAll(c);
	}

	@Override
	public void clear() {
		profils.clear();
	}

}
