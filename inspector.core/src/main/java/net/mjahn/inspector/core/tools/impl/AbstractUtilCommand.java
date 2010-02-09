package net.mjahn.inspector.core.tools.impl;

import java.io.PrintStream;

import net.mjahn.inspector.core.impl.Activator;

import org.osgi.framework.Bundle;

/**
 * Abstract class for all console commands to avoid duplication the majority of code.
 * 
 * @author Mirko Jahn
 * @version 1.0
 * @since 1.0
 */
public abstract class AbstractUtilCommand {

    /**
     * obtains the bundle by name and prints the result to the given PrintStream.
     * 
     * @param params a list of FQCNs
     * @param pStream a stream where to output the result.
     * @version 1.0
     * @since 1.0
     */
    public void bundleByName(final String[] params, final PrintStream pStream) {
        for (int i = 0; i < params.length; i++) {
            Bundle[] b = Activator.getFrameworkInspectorImpl().getBundleForClassName(params[i]);
            if (b.length < 1) {
                pStream.println("Class "
                    + params[i] + " was not found in any bundle.");
            } else {
                pStream.println("\nClass "
                    + params[i] + " is provided by:");
                for (int j = 0; j < b.length; j++) {
                    if (b[j] == null) {
                        // either the JVM or the App ClassLoader are able to produce that...
                        Class<?>[] clazzes = Activator.getFrameworkInspectorImpl().getClassesForName(params[i]);
                        // we can only have one possible class here
                        if (clazzes[0].getClassLoader() == null) {
                            // if no class loader is returned, it is loaded with the System CL
                            pStream
                                .println("\tNot provided by a Bundle, but by the JVM/ System ClassLoader.");
                        } else {
                            // here we have an Application ClassLoader providing the class
                            pStream
                                .println("\tNot provided by a Bundle, but by the Application ClassLoader.");
                        }

                    } else {
                        pStream.println("\tBundle: "
                            + b[j].getSymbolicName() + " [" + b[j].getBundleId() + "]");
                    }
                }
            }
        }
    }
}
