package patricias;

import patricia.interfaces.IArrete;
import patricia.interfaces.INode;

public class Arrete implements IArrete,Comparable<Arrete> {
	public static final IArrete	END_OF_STRING	= new Arrete("");
	protected INode				next;
	protected String			value;

	public Arrete(INode fils, String value) {
		this.next = fils;
		this.value = value;
	}

	public Arrete(String value) {
		this.next = new Node();
		this.value = value;
	}

	public IArrete clone(){
	    if(this==Arrete.END_OF_STRING){
		return Arrete.END_OF_STRING;
	    }
	    return new Arrete(this.getNext().clone(),new String(this.getValue()));
	}
	
	@Override
	public INode getNext() {
		return next;
	}

	@Override
	public void setNext(INode fils) {
		this.next = fils;
	}

	@Override
	public String getValue() {
		return value;
	}

	@Override
	public void setValue(String value) {
		this.value = value;
	}

	@Override
	public String toString() {
	    if(this==END_OF_STRING)return "(EOS)";
	    return "(\""+value + "\" , " + next + ")";
	}

	@Override
	public int compareTo(Arrete o) {
		return value.compareTo(o.getValue());
	}
	
	@Override
	public boolean equals(Object o){
		if(this==o)
			return true;
		if (o instanceof Arrete) {
			Arrete new_arrete = (Arrete) o;
			if(this.value.equals(new_arrete.getValue())){
				return this.getNext().equals(new_arrete.getNext());
			}
		}
		return false;
	}

}
