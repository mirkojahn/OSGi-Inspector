package net.mjahn.inspector.core;

/**
 * This class describes OSGi R4 attributes.
 * 
 * The implementation is taking from the Apache Felix project, which is also under the Apache 2 License.
 * Thanks for the great work, so I don't have to reinvent the wheel!
 * 
 * @see org.apache.felix.framework.util.manifestparser.R4Attribute (Rev. 100644)
 *
 * @version 1.0
 */
public class Attribute
{
    private final String m_name;
    private final Object m_value;
    private final boolean m_isMandatory;
 
    public Attribute(String name, Object value, boolean isMandatory)
    {
        m_name = name;
        m_value = value;
        m_isMandatory = isMandatory;
    }
 
    public String getName()
    {
        return m_name;
    }
 
    public Object getValue()
    {
        return m_value;
    }
 
    public boolean isMandatory()
    {
        return m_isMandatory;
    }
    
    @Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		if (m_name != null) {
			builder.append("(Attribute: ");
			builder.append(m_name);
			builder.append("=");
		}
		if (m_value != null) {
			builder.append(m_value);
		}
		builder.append(" isMandatory=");
		builder.append(m_isMandatory);
		builder.append(") ");
		return builder.toString();
	}
}
