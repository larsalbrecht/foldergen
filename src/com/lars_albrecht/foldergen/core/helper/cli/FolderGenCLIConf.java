/*
 * Copyright (c) 2011 Lars Chr. Albrecht
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:
 * Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
 * Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 */

/**
 * 
 */
package com.lars_albrecht.foldergen.core.helper.cli;

import java.awt.Component;
import java.io.File;
import java.util.Locale;

/**
 * Model-Class for command line parameters.
 * 
 * @author lalbrecht
 * @version 1.2.0.0
 * 
 */
public final class FolderGenCLIConf {

	public final static Integer OVERWRITE_OFF = 0;
	public final static Integer OVERWRITE_FORCE = 1;
	public final static Integer OVERWRITE_ASK = 2;

	private File rootPath = null;
	private File configFile = null;
	private Boolean isGui = null;
	private Boolean isDebug = null;
	private Boolean showConfirmation = null;
	private Locale locale = null;
	private Boolean usePlugins = null;
	private Boolean useProxy = null;
	private Integer overwrite = null;
	private Boolean createNew = null;
	private Boolean help = null;
	private Component mainGUIComponent = null;

	public FolderGenCLIConf() {
		this.isGui = Boolean.FALSE;
		this.isDebug = Boolean.FALSE;
		this.showConfirmation = Boolean.FALSE;
		this.locale = Locale.getDefault();
		this.usePlugins = Boolean.FALSE;
		this.useProxy = Boolean.FALSE;
		this.overwrite = FolderGenCLIConf.OVERWRITE_OFF;
		this.createNew = Boolean.FALSE;
		this.help = Boolean.FALSE;
		this.mainGUIComponent = null;
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

	/**
	 * @return the usePlugins
	 */
	public synchronized final Boolean getUsePlugins() {
		return this.usePlugins;
	}

	/**
	 * @param usePlugins
	 *            the usePlugins to set
	 */
	public synchronized final void setUsePlugins(final Boolean usePlugins) {
		this.usePlugins = usePlugins;
	}

	/**
	 * @return the useProxy
	 */
	public synchronized final Boolean getUseProxy() {
		return this.useProxy;
	}

	/**
	 * @param useProxy
	 *            the useProxy to set
	 */
	public synchronized final void setUseProxy(final Boolean useProxy) {
		this.useProxy = useProxy;
	}

	/**
	 * @return the overwrite
	 */
	public synchronized final Integer getOverwrite() {
		return this.overwrite;
	}

	/**
	 * @param overwrite
	 *            the overwrite to set
	 */
	public synchronized final void setOverwrite(final Integer overwrite) {
		this.overwrite = overwrite;
	}

	/**
	 * @return the createNew
	 */
	public synchronized final Boolean getCreateNew() {
		return this.createNew;
	}

	/**
	 * @param createNew
	 *            the createNew to set
	 */
	public synchronized final void setCreateNew(final Boolean createNew) {
		this.createNew = createNew;
	}

	/**
	 * @return the help
	 */
	public synchronized final Boolean getHelp() {
		return this.help;
	}

	/**
	 * @param help
	 *            the help to set
	 */
	public synchronized final void setHelp(final Boolean help) {
		this.help = help;
	}

	/**
	 * @return the mainGUIComponent
	 */
	public Component getMainGUIComponent() {
		return this.mainGUIComponent;
	}

	/**
	 * @param mainGUIComponent
	 *            the mainGUIComponent to set
	 */
	public void setMainGUIComponent(Component mainGUIComponent) {
		this.mainGUIComponent = mainGUIComponent;
	}

}
