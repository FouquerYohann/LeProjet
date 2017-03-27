package contract;

public class PreConditionError extends ContractError {

    /**
     * 
     */
    private static final long serialVersionUID = -8408111807253666465L;

    public PreConditionError(String message) {
	super("PreConditionError : "+message);
	// TODO Auto-generated constructor stub
    }

}
