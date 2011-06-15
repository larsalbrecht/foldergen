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
package com.lars_albrecht.foldergen.core;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

import com.lars_albrecht.foldergen.core.helper.Struct;
import com.lars_albrecht.foldergen.core.helper.StructItem;

/**
 * If you have the .jar file on you filesystem, you need a .foldergenconf-File.
 * This file can be written in a default text editor like vim, notepad or
 * something like that. For more about the foldergenconf, look at the wiki-site.<br />
 * java -jar foldergen.jar C:\<filename>.foldergenconf<br />
 * or<br />
 * java -jar foldergen.jar /home/testuser/<filename>.foldergenconf
 * 
 * @see "http://code.google.com/p/foldergen/"
 * @author lalbrecht
 * @version 1.0.0.0
 * 
 */
public class FolderGen {

	/**
	 * "main" to start the class.
	 * 
	 * @param args
	 *            String[]
	 */
	public static void main(String[] args) {
		if (args.length >= 1) {
			File f = null;
			Boolean isDebug = false;
			if (args[0] != "" && args[0].endsWith(".foldergenconf") && (f = new File(args[0])).exists() && f.isFile()) {
				if (args.length > 1 && args[1] != null && !args[1].equals("")) {
					isDebug = Boolean.getBoolean(args[1]);
				}
				new FolderGen(f, isDebug);
			}

		}
	}

	/**
	 * static variables for comparisons
	 */
	private final static String FOLDER = "+";
	private final static String FILE = "-";
	private final static String FOLDER_STR = "folder";
	private final static String FILE_STR = "file";
	private final static String CONTENT_START = "(((";
	private final static String CONTENT_END = ")))";
	private Boolean isDebug = false;

	/**
	 * Main "Class" with an argument "rootFile" and with an parameter "isDebug"
	 * for debug prints if needed.
	 * 
	 * @param rootFile
	 *            File
	 * @param isDebug
	 *            Boolean
	 */
	public FolderGen(final File rootFile, final Boolean isDebug) {
		this.isDebug = isDebug;
		BufferedReader in = null;
		File basicRootFolder = null;
		// If config file exists and is a file and parent != null
		if (rootFile.exists() && rootFile.isFile() && rootFile.getParent() != null) {
			try {
				// Buffered reader reads the file
				in = new BufferedReader(new FileReader(rootFile));
				// set "basicRootFolder" to the folder of the config-file.
				basicRootFolder = new File(rootFile.getParent());
				String s = null;
				Integer layer = 0;
				Integer lastLayer = 0;
				// Create struct for items
				Struct struct = new Struct();
				// Create structitem for the last created.
				StructItem lastItem = null;
				// every line in file
				while ((s = in.readLine()) != null) {
					String typeStr = null;
					String[] basicInfo = s.trim().split("\\s", 2);
					if (s.indexOf("+") != -1) {
						typeStr = s.substring(0, s.indexOf("+"));
					} else if (s.indexOf("-") != -1) {
						typeStr = s.substring(0, s.indexOf("-"));
					}
					if (typeStr != null || s.indexOf("(((") != -1) {
						if (basicInfo != null && basicInfo.length == 2 && basicInfo[0] != null && basicInfo[1] != null) {
							layer = typeStr.split("\\s", -1).length - 1;
							HashMap<String, String> additionalInfo = new HashMap<String, String>();
							// if folder or file ...
							if (basicInfo[0].trim().equals(FolderGen.FOLDER) || basicInfo[0].trim().equals(FolderGen.FILE)) {
								additionalInfo.put("type", basicInfo[0].trim().equals(FolderGen.FOLDER) ? FolderGen.FOLDER_STR : FolderGen.FILE_STR);
								StructItem tempStructItem = null;
								if (layer.equals(0)) { // zero / root layer
									if (this.isDebug)
										System.out.println("Zero(0): " + basicInfo[1]);
									tempStructItem = new StructItem(basicInfo[1].trim(), additionalInfo, null);
									struct.add(tempStructItem);
									lastItem = tempStructItem;
								} else if (layer > lastLayer) { // layer down
									if (this.isDebug)
										System.out.println("Down: " + basicInfo[1]);
									tempStructItem = new StructItem(basicInfo[1].trim(), additionalInfo, lastItem);
									lastItem.getSubStruct().add(tempStructItem);
									lastItem = tempStructItem;
								} else if (layer < lastLayer) { // layer up
									if (this.isDebug)
										System.out.println("Up: " + basicInfo[1]);
									tempStructItem = new StructItem(basicInfo[1].trim(), additionalInfo, null);
									for (int i = 0; i <= lastLayer - layer; i++) {
										lastItem = lastItem.getParentStructItem();
									}
									lastItem.getSubStruct().add(tempStructItem);
									lastItem = tempStructItem;
								} else if (layer.equals(lastLayer)) { // same
																		// layer
									if (this.isDebug)
										System.out.println("equals: " + basicInfo[1]);
									tempStructItem = new StructItem(basicInfo[1].trim(), additionalInfo, lastItem.getParentStructItem());
									lastItem.getParentStructItem().getSubStruct().add(tempStructItem);
									lastItem = tempStructItem;
								}
								lastLayer = layer;
							}
						} else if (basicInfo[0].trim().equals(FolderGen.CONTENT_START)) {
							workContent(in, lastItem);
						}
					}
				}
				workStruct(struct, basicRootFolder);
				if (this.isDebug)
					debugPrint(struct, "");
			} catch (IOException e) {
				System.out.println(e.getMessage());
			}
		} else {
			System.out.println("add config file with absolute parameter, eg: C:\\my.folderconf or /my.folderconf");
		}
	}

	private void workContent(final BufferedReader in, StructItem lastItem) throws IOException {
		String s = null;
		Boolean first = true;
		String content = "";
		// read content
		while ((s = in.readLine()) != null && !(s.trim().equals(FolderGen.CONTENT_END))) {
			if (first) {
				first = false;
			}
			content += s.trim();
			content += "\r\n";
		}
		// add content to (last)item
		lastItem.getAdditionalData().put("content", content);
	}

	/**
	 * Works the struct recursive and create folders and files (with content),
	 * if available.
	 * 
	 * @param struct
	 *            Struct
	 * @param rootFolder
	 *            File
	 */
	private void workStruct(final Struct struct, final File rootFolder) {
		for (int i = 0; i < struct.size(); i++) {
			// get additional data from structItem
			HashMap<String, String> tempAdditionalData = struct.get(i).getAdditionalData();
			if (tempAdditionalData.containsKey("type") && tempAdditionalData.get("type") != null) {
				// if valid ...
				if (tempAdditionalData.get("type").toLowerCase().equals(FolderGen.FILE_STR)) {
					// create file, if needed
					this.workFile(rootFolder, tempAdditionalData, struct.get(i).getName());
				} else if (tempAdditionalData.get("type").toLowerCase().equals(FolderGen.FOLDER_STR)) {
					// create folder, if needed
					this.workFolder(rootFolder, struct.get(i).getName());
				}
			}
			if (struct.get(i).getSubStruct() != null) {
				// read substructure
				workStruct(struct.get(i).getSubStruct(), new File(rootFolder + File.separator + struct.get(i).getName()));
			}
		}
	}

	/**
	 * If needed, creates a file and - if needed - insert content.
	 * 
	 * @param rootFolder
	 *            File
	 * @param tempAdditionalData
	 *            HashMap<String,String>
	 * @param name
	 *            String
	 */
	private void workFile(final File rootFolder, final HashMap<String, String> tempAdditionalData, final String name) {
		try {
			File f = new File(rootFolder.getAbsolutePath() + File.separator + name);
			// file not exists and format respectively filepath correct
			if (!f.exists() && new File(f.getParent()).isDirectory()) {
				f.createNewFile();
				if (tempAdditionalData.containsKey("content") && tempAdditionalData.get("content") != null && !tempAdditionalData.get("content").equals("")) {
					FileOutputStream fos = new FileOutputStream(f);
					for (int j = 0; j < tempAdditionalData.get("content").length(); j++) {
						fos.write((byte) tempAdditionalData.get("content").charAt(j));
					}
					fos.close();
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * If needed, creates a folder.
	 * 
	 * @param rootFolder
	 *            File
	 * @param name
	 *            String
	 */
	private void workFolder(final File rootFolder, final String name) {
		File f = new File(rootFolder.getAbsolutePath() + File.separator + name);
		// folder not exists and format respectively filepath correct
		if (!f.exists() && new File(f.getParent()).isDirectory()) {
			f.mkdir();
		}
	}

	/**
	 * Prints a given Struct "struct" in console with "seperator" to seperate.
	 * 
	 * @param struct
	 * @param seperator
	 */
	private void debugPrint(final Struct struct, final String seperator) {
		for (int i = 0; i < struct.size(); i++) {
			System.out.println(seperator + i + " - " + struct.get(i).getName() + (struct.get(i).getAdditionalData() != null ? " - " + struct.get(i).getAdditionalData() : ""));
			if (struct.get(i).getSubStruct() != null) {
				debugPrint(struct.get(i).getSubStruct(), seperator + "\t");
			}
		}

	}
}
