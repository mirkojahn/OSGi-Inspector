package net.mjahn.inspector.core.reasoner.base;

public class Utils {
	
	public static String[] splitPackageAndClassForSearch(String fullQualifiedClassName){
		String fqcn = fullQualifiedClassName.replace(".", "/");
		String[] temp = fqcn.split("/");
		
		// get the className
		String className = temp[temp.length-1];
		if(!className.endsWith("class")){
			// add ".class" to be able to find it
			className+=".class";
			fqcn+=".class";
		}
		
		// get the packageName
		String packageName = "";
		if(temp.length >0){
			int i = fqcn.indexOf(temp[temp.length-1]);
			if(i>0){
				// only in case a package was found (this should always be true due to the previous check
				packageName = fqcn.substring(0, i-1); // don't include the "/"
			}
		}
		return new String[] {packageName, className, fqcn, fullQualifiedClassName};
	}

}
