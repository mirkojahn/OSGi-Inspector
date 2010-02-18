package net.mjahn.inspector.core;

/**
 * This class describes OSGi R4 directives.
 * 
 * The implementation is taking from the Apache Felix project, which is also under the Apache 2 License.
 * Thanks for the great work, so I don't have to reinvent the wheel!
 *
 * @see org.apache.felix.framework.util.manifestparser.R4Directive (Rev. 100644)
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

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		if (m_name != null) {
			builder.append("(Directive: ");
			builder.append(m_name);
			builder.append("=");
		}
		if (m_value != null) {
			builder.append(m_value);
		}
		builder.append(") ");
		return builder.toString();
	}
	
	/**
	 * Provides a JSON valid representation of this object.
	 * @since 1.0
	 * @return a valid JSON representation of this object.
	 */
	public String toJSON() {
		StringBuilder builder = new StringBuilder();
		builder.append("{ \"directive\": { ");

		builder.append("\"directiveName\": \"");
		if (m_name != null) {
			// only the value or noting if empty
			builder.append(m_name);
		}
		builder.append("\",");
		builder.append("\"directiveValue\": \"");
		if (m_value != null) {
			// only the value or noting if empty
			builder.append(m_value);
		}
		builder.append("\"}}");
		return builder.toString();
	}
}
