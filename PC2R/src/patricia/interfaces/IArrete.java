package patricia.interfaces;

public interface IArrete {
    public String getValue();
    public void   setValue(String value);
    public INode  getNext();
    public void   setNext(INode fils);
    public IArrete clone();   
}
