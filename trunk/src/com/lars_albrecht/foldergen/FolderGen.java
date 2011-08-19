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
package com.lars_albrecht.foldergen;

import org.apache.commons.cli.ParseException;

import com.lars_albrecht.foldergen.core.Generator;
import com.lars_albrecht.foldergen.core.helper.cli.FolderGenCLIConf;
import com.lars_albrecht.foldergen.core.helper.cli.FolderGenCLIHelper;
import com.lars_albrecht.foldergen.core.helper.properies.PropertiesReader;
import com.lars_albrecht.foldergen.gui.View;

/**
 * If you have the .jar file on you filesystem, you need a .foldergenconf-File. This file can be written in a default text editor like vim, notepad or something like that. For more about the
 * foldergenconf, look at the wiki-site. java -jar foldergen.jar -c C:\<filename>.foldergenconf or java -jar foldergen.jar -c /home/testuser/<filename>.foldergenconf
 * 
 * @see "http://code.google.com/p/foldergen/"
 * @author lalbrecht
 * @version 1.5.0.1
 * 
 */
public class FolderGen {

	/**
	 * Static variable for the confirmation in the GUI (Show).
	 */
	public static final Boolean CONFIRMATION_SHOW = Boolean.TRUE;

	/**
	 * Static variable for the confirmation in the GUI (Hide).
	 */
	public static final Boolean CONFIRMATION_HIDE = Boolean.FALSE;

	/**
	 * Variable for the command line parameters
	 */
	private FolderGenCLIConf appConf = null;

	/**
	 * "main" to start the class.
	 * 
	 * @param args
	 *            String[]
	 */
	public static void main(final String[] args) {
		new FolderGen(args);
	}

	/**
	 * Main "Class" with arguments "args".
	 * 
	 * @param args
	 *            String[]
	 */
	public FolderGen(final String[] args) {
		// Variable for the config file
		try {
			// Saves parameter states in appConf
			this.appConf = FolderGenCLIHelper.parseArguments(args);

			// No args or no config file and no gui
			if((args.length < 1) || ((this.appConf.getConfigFile() == null) && !this.appConf.getIsGui())) {
				FolderGenCLIHelper.printUsage(PropertiesReader.getInstance().getProperties("application.name"),
						FolderGenCLIHelper.createOptions(), System.out);
			} else {
				// no gui and no config file or config file is not valid
				if(!this.appConf.getIsGui() && (!this.appConf.getConfigFile().exists() || !this.appConf.getConfigFile().isFile())) {
					FolderGenCLIHelper.printUsage(PropertiesReader.getInstance().getProperties("application.name"),
							FolderGenCLIHelper.createOptions(), System.out);
				} else {
					if(this.appConf.getLocale() != null) { // set locale if needed
						PropertiesReader.getInstance().setLocale(this.appConf.getLocale());
					}
					// Start gui or start generator directly
					this.startUp();
				}
			}
		} catch(ParseException e) {
			System.err.println(PropertiesReader.getInstance().getProperties("application.exception.parse"));
			FolderGenCLIHelper.printUsage(PropertiesReader.getInstance().getProperties("application.name"), FolderGenCLIHelper
					.createOptions(), System.out);
		}
	}

	/**
	 * Start FolderGen in gui or in cli mode
	 */
	private void startUp() {
		if(this.appConf.getUseProxy()) {
			try { // Try to get default proxy
				System.setProperty("java.net.useSystemProxies", "true");
			} catch(Exception e) {
				if(this.appConf.getIsDebug()) {
					e.printStackTrace();
				}
			}
		}
		if(this.appConf.getIsGui()) {
			new View(this.appConf.getRootPath(), this.appConf.getConfigFile(), this.appConf.getIsDebug(), this.appConf
					.getShowConfirmation(), this.appConf.getUsePlugins(), this.appConf.getOverwrite());
		} else {
			new Generator(this.appConf.getRootPath(), this.appConf.getConfigFile(), this.appConf.getIsDebug(), this.appConf
					.getShowConfirmation(), this.appConf.getUsePlugins(), this.appConf.getOverwrite());
		}
	}

}
