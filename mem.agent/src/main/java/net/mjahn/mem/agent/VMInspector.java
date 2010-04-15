/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.mjahn.mem.agent;

/**
 *
 * @author mjahn
 */
public class VMInspector {
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        System.out.println("Loading inspector VM agent.");
        VMInspector insp = new VMInspector();
        System.out.println("This agent used "
                + insp.getSizeOfAllLoadedObjectsBy(
                        insp.getClass().getClassLoader())
                + " bytes of memory.");
    }

    public native long getSizeOfAllLoadedObjectsBy(ClassLoader cl);

    public native Object[] getAllLoadedObjectsOf(ClassLoader cl);

    public native Object[] getAllClassLoadersOfType(String classLoaderName);

    public native ClassLoader[] getAllClassLoaders();
    
}
