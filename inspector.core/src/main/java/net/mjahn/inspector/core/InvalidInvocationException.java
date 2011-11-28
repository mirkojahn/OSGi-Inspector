package net.mjahn.inspector.core;

/**
 *
 * @author mjahn
 */
public class InvalidInvocationException extends Exception {

    /**
	 * default serial Id
	 */
	private static final long serialVersionUID = 7446637675620215054L;

	public InvalidInvocationException(String desc) {
        super(desc);
    }

    @Override
    public String toString() {
        return super.getMessage();
    }

}
