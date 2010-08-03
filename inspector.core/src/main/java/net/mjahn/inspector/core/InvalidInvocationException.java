package net.mjahn.inspector.core;

/**
 *
 * @author mjahn
 */
public class InvalidInvocationException extends Exception {

    public InvalidInvocationException(String desc) {
        super(desc);
    }

    @Override
    public String toString() {
        return super.getMessage();
    }

}
