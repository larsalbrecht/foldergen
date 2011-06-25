/**
 * 
 */
package com.lars_albrecht.foldergen.core.helper;

import java.io.File;

/**
 * @author lalbrecht
 * 
 */
public final class FolderGenCLIConf {

	private File file = null;
	private Boolean isGui = null;
	private Boolean isDebug = null;

	public FolderGenCLIConf() {
		this.file = null;
		this.isGui = Boolean.FALSE;
		this.isDebug = Boolean.FALSE;
	}

	/**
	 * @return the file
	 */
	public synchronized final File getFile() {
		return this.file;
	}

	/**
	 * @param file
	 *            the file to set
	 */
	public synchronized final void setFile(final File file) {
		this.file = file;
	}

	/**
	 * @return the isGui
	 */
	public synchronized final Boolean getIsGui() {
		return this.isGui;
	}

	/**
	 * @param isGui
	 *            the isGui to set
	 */
	public synchronized final void setIsGui(final Boolean isGui) {
		this.isGui = isGui;
	}

	/**
	 * @return the isDebug
	 */
	public synchronized final Boolean getIsDebug() {
		return this.isDebug;
	}

	/**
	 * @param isDebug
	 *            the isDebug to set
	 */
	public synchronized final void setIsDebug(final Boolean isDebug) {
		this.isDebug = isDebug;
	}

}
