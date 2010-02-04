package net.mjahn.inspector.core.impl;

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
}
