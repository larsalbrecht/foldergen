/**
 * 
 */
package com.lars_albrecht.foldergen.core.helper;

/**
 * @author lalbrecht
 * 
 */
public abstract class ConfMarker {

	protected String fileMarker = null;
	protected String marker = null;

	/**
	 * Do work.
	 */
	protected void doWork() {

	}

	/**
	 * @return the fileMarker
	 */
	public synchronized final String getFileMarker() {
		return this.fileMarker;
	}

	/**
	 * @param fileMarker
	 *            the fileMarker to set
	 */
	public synchronized final void setFileMarker(final String fileMarker) {
		this.fileMarker = fileMarker;
	}

	/**
	 * @return the marker
	 */
	public synchronized final String getMarker() {
		return this.marker;
	}

	/**
	 * @param marker
	 *            the marker to set
	 */
	public synchronized final void setMarker(final String marker) {
		this.marker = marker;
	}

}
