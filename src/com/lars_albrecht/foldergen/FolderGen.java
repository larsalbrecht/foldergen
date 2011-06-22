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

import com.lars_albrecht.foldergen.core.Generator;
import com.lars_albrecht.foldergen.core.helper.Utilities;
import com.lars_albrecht.foldergen.gui.View;

/**
 * If you have the .jar file on you filesystem, you need a .foldergenconf-File. This file can be written in a default text editor like vim, notepad or something like that. For more about the
 * foldergenconf, look at the wiki-site. java -jar foldergen.jar C:\<filename>.foldergenconf or java -jar foldergen.jar /home/testuser/<filename>.foldergenconf
 * 
 * @see http://code.google.com/p/foldergen/
 * @author lalbrecht
 * @version 1.3.0.0
 * 
 */
public class FolderGen {

	/**
	 * "main" to start the class.
	 * 
	 * @param args
	 *            String[]
	 */
	public static void main(final String[] args) {
		if(args.length >= 1) {
			File f = null;
			Boolean isDebug = false;
			Boolean isGui = false;
			if(args[0] != "") {
				if(args[0].endsWith(".foldergenconf") && (f = new File(args[0])).exists() && f.isFile()) {
					if((args.length > 1) && (args[1] != "") && Utilities.isBoolean(args[1])) {
						isGui = Boolean.parseBoolean(args[1].trim());
					}
					if((args.length > 2) && (args[2] != "") && (Utilities.isBoolean(args[2]))) {
						isDebug = Boolean.parseBoolean(args[2].trim());
					}
				} else if(Utilities.isBoolean(args[0])) {
					isGui = Boolean.parseBoolean(args[0].trim());
					if(isGui) {
						if((args.length > 1) && (args[1] != "") && Utilities.isBoolean(args[1])) {
							isDebug = Boolean.parseBoolean(args[1].trim());
						}
					} else {
						FolderGen.exitOnStartup();
					}
				} else {
					FolderGen.exitOnStartup();
				}
				new FolderGen(f, isGui, isDebug);
			}

			if((args[0] != "") && args[0].endsWith(".foldergenconf") && (f = new File(args[0])).exists() && f.isFile()) {
				if((args.length > 1) && (args[1] != null) && !args[1].equals("")) {
					isGui = Boolean.parseBoolean(args[1].trim());
				}
				if((args.length > 2) && (args[2] != null) && !args[2].equals("")) {
					isDebug = Boolean.parseBoolean(args[2].trim());
				}
				new FolderGen(f, isGui, isDebug);
			}
		}
	}

	/**
	 * Prints text to console and exit the application.
	 */
	public static void exitOnStartup() {
		System.out.println("Given parameters are not valid.");
		System.out.println("Following parameters are available:");
		System.out.println("[file isGui isDebug]|[isGui isDebug]");
		System.out.println("--------------------------------------");
		System.out.println("Examples:");
		System.out.println("Start with gui and file:");
		System.out.println("/myFile.foldergenconf true");
		System.out.println("Start without gui and with file:");
		System.out.println("/myFile.foldergenconf");
		System.out.println("Start without gui and with file and debug:");
		System.out.println("/myFile.foldergenconf false true");
		System.out.println("--------------------------------------");
		System.out.println("For more examples, see wiki @ http://code.google.com/p/foldergen/");
		System.exit(-1);
	}

	/**
	 * Main "Class" with an argument "rootFile" and with an parameter "isDebug" for debug prints if needed.
	 * 
	 * @param rootFile
	 *            File
	 * @param isDebug
	 *            Boolean
	 */
	public FolderGen(final File rootFile, final Boolean isGui, final Boolean isDebug) {
		if(isGui) {
			new View(rootFile, isDebug);
		} else {
			new Generator(rootFile, isDebug);
		}

	}
}
