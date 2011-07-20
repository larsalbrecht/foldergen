/**
 * 
 */
package com.lars_albrecht.foldergen.plugin.classes;

/**
 * @author lalbrecht
 * 
 */
public class CoreListModifier {

	protected String name;
	protected String listStr;

	/**
	 * Do action.
	 * 
	 * @return String
	 */
	public synchronized String doAction() {
		return null;
	}

	/**
	 * @return the name
	 */
	public synchronized final String getName() {
		return this.name;
	}

	/**
	 * @param name
	 *            the name to set
	 */
	public synchronized final void setName(final String name) {
		this.name = name;
	}

	/**
	 * @return the listStr
	 */
	public synchronized final String getListStr() {
		return this.listStr;
	}

	/**
	 * @param listStr
	 *            the listStr to set
	 */
	public synchronized final void setListStr(final String listStr) {
		this.listStr = listStr;
	}

}
