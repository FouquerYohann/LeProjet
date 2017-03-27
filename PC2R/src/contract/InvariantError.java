package contract;

public class InvariantError extends ContractError {

    /**
     * 
     */
    private static final long serialVersionUID = -4518736470227542584L;


    public InvariantError(String message) {
	super("InvariantError : "+message);
	// TODO Auto-generated constructor stub
    }

}
