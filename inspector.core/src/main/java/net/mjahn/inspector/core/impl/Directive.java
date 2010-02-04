package net.mjahn.inspector.core.impl;

/**
 * @see org.apache.felix.framework.util.manifestparser.R4Directive
 *
 * @version 1.0
 */
public class Directive {
	private final String m_name;
	private final String m_value;

	public Directive(String name, String value) {
		m_name = name;
		m_value = value;
	}

	public String getName() {
		return m_name;
	}

	public String getValue() {
		return m_value;
	}
}
