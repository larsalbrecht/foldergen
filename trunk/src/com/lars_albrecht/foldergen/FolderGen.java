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
package com.lars_albrecht.foldergen;

import java.io.File;

import org.apache.commons.cli.ParseException;

import com.lars_albrecht.foldergen.core.Generator;
import com.lars_albrecht.foldergen.core.helper.FolderGenCLIConf;
import com.lars_albrecht.foldergen.core.helper.FolderGenCLIHelper;
import com.lars_albrecht.foldergen.core.helper.PropertiesReader;
import com.lars_albrecht.foldergen.gui.View;

/**
 * If you have the .jar file on you filesystem, you need a .foldergenconf-File. This file can be written in a default text editor like vim, notepad or something like that. For more about the
 * foldergenconf, look at the wiki-site. java -jar foldergen.jar -c C:\<filename>.foldergenconf or java -jar foldergen.jar -c /home/testuser/<filename>.foldergenconf
 * 
 * @see http://code.google.com/p/foldergen/
 * @author lalbrecht
 * @version 1.4.5.0
 * 
 */
public class FolderGen {

	public static Boolean IS_GUI = true;
	public static Boolean IS_CONSOLE = false;

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
	 * Main "Class" with an argument "args".
	 * 
	 * @param args
	 *            String[]
	 */
	public FolderGen(final String[] args) {
		File f = null;
		FolderGenCLIConf appConf = null;
		try {
			appConf = FolderGenCLIHelper.parseArguments(args);
		} catch(ParseException e) {
			System.err.println(PropertiesReader.getInstance().getProperties("application.exception.parse") + e.getMessage());
		}
		if((args.length < 1) || ((appConf.getFile() == null) && !appConf.getIsGui())) {
			FolderGenCLIHelper.printUsage(PropertiesReader.getInstance().getProperties("application.name"), FolderGenCLIHelper
					.createOptions(), System.out);
		} else {
			if(appConf.getFile() != null) {
				f = appConf.getFile();
			}
			if(!appConf.getIsGui() && (!f.exists() || !f.isFile())) {
				FolderGenCLIHelper.printUsage(PropertiesReader.getInstance().getProperties("application.name"),
						FolderGenCLIHelper.createOptions(), System.out);
			} else {
				this.startUp(f, appConf.getIsGui(), appConf.getIsDebug());
			}
		}
	}

	/**
	 * Start FolderGen. "isGui" loads the gui if true, "isDebug" prints debug prints - if true, and configFile contains the start config file if not null.
	 * 
	 * @param configFile
	 *            File
	 * @param isGui
	 *            Boolean
	 * @param isDebug
	 *            Boolean
	 */
	private void startUp(final File configFile, final Boolean isGui, final Boolean isDebug) {
		if(isGui) {
			new View(configFile, isDebug);
		} else {
			new Generator(configFile, isDebug, FolderGen.IS_CONSOLE);
		}
	}

}
