/**
 * 
 */
package com.lars_albrecht.foldergen.core.helper;

/**
 * Some helper functions.
 * 
 * @author lalbrecht
 * @version 1.0.0.0
 */
public class Utilities {

	/**
	 * Returns the number of appearces of "character" in "string".
	 * 
	 * @param string
	 *            String
	 * @param String
	 *            findStr
	 * @return Integer
	 */
	public static Integer countChars(final String string, final String findStr) {
		String strCopy = new String(string);
		return strCopy.replaceAll("[" + findStr + "]", "").length();
	}

	/**
	 * Returns true is the given String is a boolean.
	 * 
	 * @param strBool
	 * @return Boolean
	 */
	public static Boolean isBoolean(final String strBool) {
		return strBool.equalsIgnoreCase("false") || strBool.equalsIgnoreCase("true");
	}
}