package contract;

public class ContractError extends Error {

    /**
     * 
     */
    private static final long serialVersionUID = 6617782425662361157L;

    public ContractError() {
	super();
	// TODO Auto-generated constructor stub
    }

    public ContractError(String message) {
	super("ContractError :"+message);
	// TODO Auto-generated constructor stub
    }

    
}
