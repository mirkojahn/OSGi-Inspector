package net.mjahn.inspector.core.impl;

import java.util.ArrayList;

import net.mjahn.inspector.core.ExportedPackage;
import net.mjahn.inspector.core.OSGiRuntimeInfo;

import org.osgi.framework.Version;

public class OSGiRuntimeInfoImpl implements OSGiRuntimeInfo {
	private boolean fwStarted = false;

	public boolean hasBeenStarted() {
		return fwStarted;
	}

	public String toJSON() {
		// empty - for now!
		// FIXME: implement this meaningful
		return "{ \"osgiRuntime\":\"\"}";
	}

	void fwStartedEventFired(){
		fwStarted = true;
	}

	public Version getOSGiRuntimeVersion() {
		String prop =  Activator.getContext().getProperty("org.osgi.framework.version");
		if(prop == null || prop.length() == 0){
			return null;
		}else{
			return new Version(prop);
		}
	}
	
	public String getOSGiSpecificationVersion(){
		Version v = getOSGiRuntimeVersion();
		if(v.equals(new Version("1.3"))){
			return "4.0.0/4.0.1";
		} else if(v.equals(new Version("1.4"))){
			return "4.1.0";
		} else if(v.equals(new Version("1.5"))){
			return "4.2.0";
		} else if(v.equals(new Version("1.6"))){
			return "4.3.0";
		} else {
			return "unknown";
		}
	}

	public boolean supportsFragmets() {
		String prop = Activator.getContext().getProperty("org.osgi.supports.framework.fragment");
		if(prop == null || prop.length() < 4){
			return false;
		}
		return Boolean.parseBoolean(prop);
	}

	public String getFrameworkVendor() {
		String prop =  Activator.getContext().getProperty("org.osgi.framework.vendor");
		if(prop == null || prop.length() == 0){
			return "not specified";
		}else{
			return prop;
		}
	}

	public String[] getSupportedExecutionEnvironments() {
		String[] prop = Activator.getContext().getProperty("org.osgi.framework.executionenvironment").split(",");
		if(prop == null){
			return new String[0];
		}else{
			return prop;
		}
	}
	
	public String getFrameworkOsVersion() {
		String prop = Activator.getContext().getProperty("org.osgi.framework.os.version");
		if(prop == null || prop.length() == 0){
			return "not specified";
		}else{
			return prop;
		}
	}

	public String getFrameworkOsName() {
		String prop = Activator.getContext().getProperty("org.osgi.framework.os.name");
		if(prop == null || prop.length() == 0){
			return "not specified";
		}else{
			return prop;
		}
	}

	public boolean supportsFrameworkExtensions() {
		String prop = Activator.getContext().getProperty("org.osgi.supports.framework.extension");
		if(prop == null || prop.length() < 4){
			return false;
		}
		return Boolean.parseBoolean(prop);
	}

	public boolean supportsBootClassPathExtension() {
		String prop = Activator.getContext().getProperty("org.osgi.supports.bootclasspath.extension");
		if(prop == null || prop.length() < 4){
			return false;
		}
		return Boolean.parseBoolean(prop);
	}

	public boolean supportsRequireBundle() {
		String prop = Activator.getContext().getProperty("org.osgi.supports.framework.requirebundle");
		if(prop == null || prop.length() < 4){
			return false;
		}
		return Boolean.parseBoolean(prop);
	}

	private ArrayList<ExportedPackage> bootDelegation = null;
	public synchronized ArrayList<ExportedPackage> getBootDelegationPackages() {
		// lazy parsing
		if (bootDelegation != null) {
			return bootDelegation;
		}
		String bootDel = Activator.getContext().getProperty("org.osgi.framework.bootdelegation");
		bootDelegation = new ArrayList<ExportedPackage>();
		if(bootDel == null || bootDel.length()==0){
			return bootDelegation;
		}
		Object[][][] parsed = Util.parseStandardHeader(bootDel);
		for (int i = 0; i < parsed.length; i++) {
			bootDelegation.add(new ExportedPackageImpl(parsed[i],0));
		}
		return bootDelegation;
	}

	private ArrayList<ExportedPackage> systemPackages = null;
	public ArrayList<ExportedPackage> getSystemPackages() {
		// lazy parsing
		if (systemPackages != null) {
			return systemPackages;
		}
		String bootDel = Activator.getContext().getProperty("org.osgi.framework.system.packages");
		systemPackages = new ArrayList<ExportedPackage>();
		if(bootDel == null || bootDel.length()==0){
			return systemPackages;
		}
		Object[][][] parsed = Util.parseStandardHeader(bootDel);
		for (int i = 0; i < parsed.length; i++) {
			systemPackages.add(new ExportedPackageImpl(parsed[i],0));
		}
		return systemPackages;
	}
	
	
}
