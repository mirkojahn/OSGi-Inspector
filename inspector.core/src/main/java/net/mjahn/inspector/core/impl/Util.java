package net.mjahn.inspector.core.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.mjahn.inspector.core.Attribute;
import net.mjahn.inspector.core.Directive;

import org.osgi.framework.Constants;

/**
 * @author "Mirko Jahn" <mirkojahn@gmail.com>
 * 
 * Generic parser utility, partially taken from the Apache Felix project (also under the Apache 2 License).
 * 
 * @see org.apache.felix.framework.util.manifestparser.ManifestParser (Rev. 100644)
 *
 * @version 1.0
 */
public class Util {
	public static final int CLAUSE_PATHS_INDEX = 0;
	public static final int CLAUSE_DIRECTIVES_INDEX = 1;
	public static final int CLAUSE_ATTRIBUTES_INDEX = 2;

	@SuppressWarnings("unchecked")
	static Object[][][] parseStandardHeader(String header) {
		Object[][][] clauses = null;

		if (header != null) {
			if (header.length() == 0) {
				throw new IllegalArgumentException(
						"A header cannot be an empty string.");
			}

			String[] clauseStrings = parseDelimitedString(header, ",");

			List completeList = new ArrayList();
			for (int i = 0; (clauseStrings != null)
					&& (i < clauseStrings.length); i++) {
				completeList.add(parseStandardHeaderClause(clauseStrings[i]));
			}
			clauses = (Object[][][]) completeList
					.toArray(new Object[completeList.size()][][]);
		}

		return (clauses == null) ? new Object[0][][] : clauses;
	}

	// Like this: path; path; dir1:=dirval1; dir2:=dirval2; attr1=attrval1;
	// attr2=attrval2
	@SuppressWarnings("unchecked")
	private static Object[][] parseStandardHeaderClause(String clauseString)
			throws IllegalArgumentException {
		// Break string into semi-colon delimited pieces.
		String[] pieces = parseDelimitedString(clauseString, ";");

		// Count the number of different paths; paths
		// will not have an '=' in their string. This assumes
		// that paths come first, before directives and
		// attributes.
		int pathCount = 0;
		for (int pieceIdx = 0; pieceIdx < pieces.length; pieceIdx++) {
			if (pieces[pieceIdx].indexOf('=') >= 0) {
				break;
			}
			pathCount++;
		}

		// Error if no paths were specified.
		if (pathCount == 0) {
			throw new IllegalArgumentException("No paths specified in header: "
					+ clauseString);
		}

		// Create an array of paths.
		String[] paths = new String[pathCount];
		System.arraycopy(pieces, 0, paths, 0, pathCount);

		// Parse the directives/attributes.
		Map dirsMap = new HashMap();
		Map attrsMap = new HashMap();
		int idx = -1;
		String sep = null;
		for (int pieceIdx = pathCount; pieceIdx < pieces.length; pieceIdx++) {
			// Check if it is a directive.
			if ((idx = pieces[pieceIdx].indexOf(":=")) >= 0) {
				sep = ":=";
			}
			// Check if it is an attribute.
			else if ((idx = pieces[pieceIdx].indexOf("=")) >= 0) {
				sep = "=";
			}
			// It is an error.
			else {
				throw new IllegalArgumentException(
						"Not a directive/attribute: " + clauseString);
			}

			String key = pieces[pieceIdx].substring(0, idx).trim();
			String value = pieces[pieceIdx].substring(idx + sep.length())
					.trim();

			// Remove quotes, if value is quoted.
			if (value.startsWith("\"") && value.endsWith("\"")) {
				value = value.substring(1, value.length() - 1);
			}

			// Save the directive/attribute in the appropriate array.
			if (sep.equals(":=")) {
				// Check for duplicates.
				if (dirsMap.get(key) != null) {
					throw new IllegalArgumentException("Duplicate directive: "
							+ key);
				}
				dirsMap.put(key, new Directive(key, value));
			} else {
				// Check for duplicates.
				if (attrsMap.get(key) != null) {
					throw new IllegalArgumentException("Duplicate attribute: "
							+ key);
				}
				attrsMap.put(key, new Attribute(key, value, false));
			}
		}

		// Create directive array.
		Directive[] dirs = (Directive[]) dirsMap.values().toArray(
				new Directive[dirsMap.size()]);

		// Create attribute array.
		Attribute[] attrs = (Attribute[]) attrsMap.values().toArray(
				new Attribute[attrsMap.size()]);

		// Create an array to hold the parsed paths, directives, and attributes.
		Object[][] clause = new Object[3][];
		clause[CLAUSE_PATHS_INDEX] = paths;
		clause[CLAUSE_DIRECTIVES_INDEX] = dirs;
		clause[CLAUSE_ATTRIBUTES_INDEX] = attrs;

		return clause;
	}

	/**
	 * This method is taken from the Apache Felix project!
	 * 
	 * @see org.apache.felix.framework.util.manifestparser.ManifestParser#parseDelimitedString(String,
	 *      String)
	 * 
	 *      Parses delimited string and returns an array containing the tokens.
	 *      This parser obeys quotes, so the delimiter character will be ignored
	 *      if it is inside of a quote. This method assumes that the quote
	 *      character is not included in the set of delimiter characters.
	 * @param value
	 *            the delimited string to parse.
	 * @param delim
	 *            the characters delimiting the tokens.
	 * @return an array of string tokens or null if there were no tokens.
	 **/
	@SuppressWarnings("unchecked")
	public static String[] parseDelimitedString(String value, String delim) {
		if (value == null) {
			value = "";
		}

		List list = new ArrayList();

		int CHAR = 1;
		int DELIMITER = 2;
		int STARTQUOTE = 4;
		int ENDQUOTE = 8;

		StringBuffer sb = new StringBuffer();

		int expecting = (CHAR | DELIMITER | STARTQUOTE);

		for (int i = 0; i < value.length(); i++) {
			char c = value.charAt(i);

			boolean isDelimiter = (delim.indexOf(c) >= 0);
			boolean isQuote = (c == '"');

			if (isDelimiter && ((expecting & DELIMITER) > 0)) {
				list.add(sb.toString().trim());
				sb.delete(0, sb.length());
				expecting = (CHAR | DELIMITER | STARTQUOTE);
			} else if (isQuote && ((expecting & STARTQUOTE) > 0)) {
				sb.append(c);
				expecting = CHAR | ENDQUOTE;
			} else if (isQuote && ((expecting & ENDQUOTE) > 0)) {
				sb.append(c);
				expecting = (CHAR | STARTQUOTE | DELIMITER);
			} else if ((expecting & CHAR) > 0) {
				sb.append(c);
			} else {
				throw new IllegalArgumentException("Invalid delimited string: "
						+ value);
			}
		}

		if (sb.length() > 0) {
			list.add(sb.toString().trim());
		}

		return (String[]) list.toArray(new String[list.size()]);
	}
	
	public static ArrayList<Directive> parseDirectives(Object[] objDirectives){
		ArrayList<Directive> directives = new ArrayList<Directive>(objDirectives.length);
		if(objDirectives.length > 0){
			// check each attribute if it is a version. 
			for(int i=0;i<objDirectives.length;i++){
				directives.add((Directive)objDirectives[i]);
			}
		}
		return directives;
	}
	
	public static boolean isOptional(ArrayList<Directive> directives){
		Iterator<Directive> dirIter = directives.iterator();
		while(dirIter.hasNext()){
			Directive dir = dirIter.next();
			if(dir.getName().equalsIgnoreCase(Constants.RESOLUTION_DIRECTIVE)){
				return (((String)dir.getValue()).equalsIgnoreCase(Constants.RESOLUTION_OPTIONAL)?true:false);
			}
		}
		return false;
	}
	
	public static ArrayList<Attribute> parseAttributes(Object[] objAttributes){
		ArrayList<Attribute> attributes = new ArrayList<Attribute>(objAttributes.length);
		if(objAttributes.length > 0){
			// check each attribute if it is a version.
			for(int i=0;i<objAttributes.length;i++){
				Attribute att = (Attribute)objAttributes[i];
				attributes.add(att);
			}
		}
		return attributes;
	}
	
	public static String getVersionString(ArrayList<Attribute> attributes){
		Iterator<Attribute> attIter = attributes.iterator();
		while(attIter.hasNext()){
			Attribute att = attIter.next();
			if(att.getName().equalsIgnoreCase(Constants.VERSION_ATTRIBUTE)){
				return (String)att.getValue();
			}
		}
		return "0.0.0";
	}
}
