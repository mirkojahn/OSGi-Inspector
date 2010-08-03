package net.mjahn.inspector.core.reasoner.base;

import net.mjahn.inspector.core.reasoner.ReasonerResult;


public class SimpleReasonerResult implements ReasonerResult {

    private final boolean clue;
    private final float conf;
    private final String message;
    private final Throwable cause;
    private final String error;

    public SimpleReasonerResult(boolean isConfident, float confidence, String resultMessage, String errorCode) {
        this(isConfident, confidence, resultMessage, errorCode, null);
    }

    public SimpleReasonerResult(boolean isConfident, 
            float confidence,
            String resultMessage,
            String errorCode,
            Throwable t) {
        clue = isConfident;
        conf = confidence;
        message = resultMessage;
        error = errorCode;
        cause = t;
    }

    public boolean isConfident() {
        return true;
    }

    public float getConfidenceLevel() {
        return conf;
    }

    public String getResultMessage() {
        return message;
    }

    public String getErrorCode() {
        return error;
    }

    @Override
	public String toString() {
		StringBuilder s = new StringBuilder();
		s.append("AbstractReasonerResult [cause=")
                        .append(cause)
                        .append(", conf=")
                        .append(conf)
                        .append(", error=")
                        .append(error)
                        .append(", message=")
                        .append(message)
                        .append(", isConfident=")
                        .append(clue)
                        .append("]");
		return s.toString();
	}
}
