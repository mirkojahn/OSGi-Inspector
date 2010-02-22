package net.mjahn.inspector.reasoner;

public class ReasonerTaskCapability {
	private String name;
	private String description;
	private boolean mandatory;
	
	public ReasonerTaskCapability(String propertyName, String propertyDescription, boolean isMandatory) {
		name = propertyName;
		description = propertyDescription;
		mandatory = isMandatory;
	}

	public String getPropertyName(){
		return name;
	}
	
	public String getPropertyDescription(){
		return description;
	}
	
	public boolean isMandatory(){
		return mandatory;
	}
}
