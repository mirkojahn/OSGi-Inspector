/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.mjahn.inspector.core.reasoner.base;

import net.mjahn.inspector.core.reasoner.ReasonerResult;

/**
 *
 * @author mjahn
 */
public class NoClueReasonerResult implements ReasonerResult {

    public final static String NO_CLUE = "net.mjahn.inspector.core.error.noclue";

    public boolean isConfident() {
        return false;
    }

    public float getConfidenceLevel() {
        return 0;
    }

    public String getResultMessage() {
        return "no clue";
    }

    public String getErrorCode() {
        return NO_CLUE;
    }

}
