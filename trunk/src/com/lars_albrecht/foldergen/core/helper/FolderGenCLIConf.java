/*
 * Copyright (c) 2011 Lars Chr. Albrecht
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 * 
 * Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 * 
 * Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 * 
 * Neither the name of the project's author nor the names of its
 * contributors may be used to endorse or promote products derived from
 * this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS
 * FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
 * HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED
 * TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 */

/**
 * 
 */
package com.lars_albrecht.foldergen.core.helper;

import java.io.File;
import java.util.Locale;

/**
 * @author lalbrecht
 * @version 1.1.0.0
 * 
 */
public final class FolderGenCLIConf {

	private File rootPath = null;
	private File configFile = null;
	private Boolean isGui = null;
	private Boolean isDebug = null;
	private Boolean showConfirmation = null;
	private Locale locale = null;

	public FolderGenCLIConf() {
		this.isGui = Boolean.FALSE;
		this.isDebug = Boolean.FALSE;
		this.showConfirmation = Boolean.FALSE;
		this.locale = Locale.getDefault();
	}

	/**
	 * @return the showConfirmation
	 */
	public synchronized final Boolean getShowConfirmation() {
		return this.showConfirmation;
	}

	/**
	 * @param showConfirmation
	 *            the showConfirmation to set
	 */
	public synchronized final void setShowConfirmation(final Boolean showConfirmation) {
		this.showConfirmation = showConfirmation;
	}

	/**
	 * @return the rootPath
	 */
	public synchronized final File getRootPath() {
		return this.rootPath;
	}

	/**
	 * @param rootPath
	 *            the rootPath to set
	 */
	public synchronized final void setRootPath(final File rootPath) {
		this.rootPath = rootPath;
	}

	/**
	 * @return the configFile
	 */
	public synchronized final File getConfigFile() {
		return this.configFile;
	}

	/**
	 * @param configFile
	 *            the configFile to set
	 */
	public synchronized final void setConfigFile(final File configFile) {
		this.configFile = configFile;
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

	/**
	 * @return the locale
	 */
	public synchronized final Locale getLocale() {
		return this.locale;
	}

	/**
	 * @param locale
	 *            the locale to set
	 */
	public synchronized final void setLocale(final Locale locale) {
		this.locale = locale;
	}

}