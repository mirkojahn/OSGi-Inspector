package net.mjahn.inspector.core.tools.impl;

import java.io.PrintStream;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.osgi.framework.console.CommandInterpreter;
import org.eclipse.osgi.framework.console.CommandProvider;

/**
 * Eclipse console extension implementing the bbn command.
 * <p>
 * When started in Eclipse, register an instance of this object as a service with the Interface
 * {@code org.eclipse.osgi.framework.console.CommandProvider}. It will then be registered in the
 * Eclipse console. Hit help in the console to see further information on how to use the command
 * properly.
 * </p>
 * 
 * @author Mirko Jahn
 * @version 1.0
 * @since 1.0
 */
public class EclipseCommands extends AbstractUtilCommand implements CommandProvider {

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.osgi.framework.console.CommandProvider#getHelp()
     * @version 1.0
     * @since 1.0
     */
    public String getHelp() {
        StringBuffer buffer = new StringBuffer();
        buffer.append("---Handy tools for the OSGi runtime---\n");
        buffer.append("\tbbn - (short for get >>Bundle By Name<< of one of its classes) "
            + "returns the bundle(s) for a given full qualified class name.\n");
        return buffer.toString();
    }

    /**
     * @param ci object providing the given parameters and an interface to interact with the
     *        console.
     * @throws Exception you never know...
     * @version 1.0
     * @since 1.0
     */
    public void _bbn(final CommandInterpreter ci) throws Exception {
        // FIXME: allow more than one parameter
        Set<String> fqcns = new HashSet<String>();
        String fqcn = null;
        do {
            fqcn = ci.nextArgument();
            if (fqcn != null) {
                fqcns.add(fqcn);
            }
        } while (fqcn != null);
        if (fqcns.isEmpty()) {
            System.out
                .println("ERROR: The command requires a full qualified class name like: net.mjahn.tools.inspector.IBundleUtils");
        } else {
            bundleByName(fqcns.toArray(new String[fqcns.size()]), new PrintStream(System.out));
        }
    }
}
