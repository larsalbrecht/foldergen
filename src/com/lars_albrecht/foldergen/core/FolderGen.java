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
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.lars_albrecht.foldergen.core.helper.Struct;
import com.lars_albrecht.foldergen.core.helper.StructItem;

/**
 * If you have the .jar file on you filesystem, you need a .foldergenconf-File.
 * This file can be written in a default text editor like vim, notepad or
 * something like that. For more about the foldergenconf, look at the wiki-site.
 * java -jar foldergen.jar C:\<filename>.foldergenconf or java -jar
 * foldergen.jar /home/testuser/<filename>.foldergenconf
 * 
 * @see "http://code.google.com/p/foldergen/"
 * @author lalbrecht
 * @version 1.2.0.0
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
		if (args.length >= 1) {
			File f = null;
			Boolean isDebug = false;
			if ((args[0] != "") && args[0].endsWith(".foldergenconf") && (f = new File(args[0])).exists() && f.isFile()) {
				if ((args.length > 1) && (args[1] != null) && !args[1].equals("")) {
					isDebug = Boolean.parseBoolean(args[1].trim());
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
		if (rootFile.exists() && rootFile.isFile() && (rootFile.getParent() != null)) {
			try {
				// Buffered reader reads the file
				in = new BufferedReader(new FileReader(rootFile));
				// set "basicRootFolder" to the folder of the config-file.
				basicRootFolder = new File(rootFile.getParent());
				String line = null;
				Integer layer = 0;
				Integer lastLayer = 0;
				// Create struct for items
				Struct struct = new Struct();
				// Create structitem for the last created.
				StructItem lastItem = null;
				// every line in file
				while ((line = in.readLine()) != null) {
					String typeStr = null;
					String[] basicInfo = line.trim().split("\\s", 2);
					if (line.indexOf("+") != -1) {
						typeStr = line.substring(0, line.indexOf("+"));
					} else if (line.indexOf("-") != -1) {
						typeStr = line.substring(0, line.indexOf("-"));
					}
					if ((typeStr != null) || (line.indexOf("(((") != -1)) {
						if ((basicInfo != null) && (basicInfo.length == 2) && (basicInfo[0] != null) && (basicInfo[1] != null)) {
							layer = typeStr.split("\\s", -1).length - 1;
							HashMap<String, String> additionalInfo = new HashMap<String, String>();
							// if folder or file ...
							if (basicInfo[0].trim().equals(FolderGen.FOLDER) || basicInfo[0].trim().equals(FolderGen.FILE)) {
								additionalInfo.put("type", basicInfo[0].trim().equals(FolderGen.FOLDER) ? FolderGen.FOLDER_STR : FolderGen.FILE_STR);
								String name = basicInfo[1].trim();
								if (layer.equals(0)) { // zero / root layer
									struct = this.workLayerZero(name, struct, lastItem, additionalInfo);
									lastItem = struct.get(struct.size() - 1);
								} else if (layer > lastLayer) { // layer down
									lastItem = this.workLayerDown(name, struct, lastItem, additionalInfo);
								} else if (layer < lastLayer) { // layer up
									lastItem = this.workLayerUp(name, struct, lastItem, additionalInfo, lastLayer, layer);
								} else if (layer.equals(lastLayer)) { // same
									// layer
									lastItem = this.workLayerSame(name, struct, lastItem, additionalInfo);
								}
								lastLayer = layer;
							}
						} else if (basicInfo[0].trim().equals(FolderGen.CONTENT_START)) {
							this.workContent(in, lastItem);
						}
					}
				}
				this.workStruct(struct, basicRootFolder);
				if (this.isDebug) {
					this.debugPrint(struct, "");
				}
			} catch (IOException e) {
				System.out.println(e.getMessage());
			}
		} else {
			System.out.println("add config file with absolute parameter, eg: C:\\my.folderconf or /my.folderconf");
		}
	}

	/**
	 * Works the infos for the zero layer.
	 * 
	 * @param name
	 *            String
	 * @param struct
	 *            Struct
	 * @param lastItem
	 *            StructItem
	 * @param additionalInfo
	 *            HashMap<String, String>
	 * 
	 * @return Struct Struct
	 */
	private Struct workLayerZero(final String name, final Struct struct, final StructItem lastItem, final HashMap<String, String> additionalInfo) {
		StructItem tempStructItem = null;
		if (this.isDebug) {
			System.out.println("Zero(0): " + name);
		}
		tempStructItem = new StructItem(name.trim(), additionalInfo, null);
		struct.add(tempStructItem);
		return struct;
	}

	/**
	 * Works the infos for the lower layer.
	 * 
	 * @param name
	 *            String
	 * @param struct
	 *            Struct
	 * @param lastItem
	 *            StructItem
	 * @param additionalInfo
	 *            HashMap<String, String>
	 * 
	 * @return StructItem StructItem
	 */
	private StructItem workLayerDown(final String name, final Struct struct, final StructItem lastItem, final HashMap<String, String> additionalInfo) {
		StructItem tempStructItem = null;
		if (this.isDebug) {
			System.out.println("Down: " + name);
		}
		tempStructItem = new StructItem(name.trim(), additionalInfo, lastItem);
		lastItem.getSubStruct().add(tempStructItem);
		return tempStructItem;
	}

	/**
	 * Works the infos for the upper layer.
	 * 
	 * @param name
	 *            String
	 * @param struct
	 *            Struct
	 * @param lastItem
	 *            StructItem
	 * @param additionalInfo
	 *            HashMap<String, String>
	 * @param lastLayer
	 *            Integer
	 * @param layer
	 *            Integer
	 * 
	 * @return StructItem StructItem
	 */
	private StructItem workLayerUp(final String name, final Struct struct, StructItem lastItem, final HashMap<String, String> additionalInfo, final Integer lastLayer, final Integer layer) {
		StructItem tempStructItem = null;
		if (this.isDebug) {
			System.out.println("Up: " + name);
		}
		tempStructItem = new StructItem(name.trim(), additionalInfo, null);
		for (int i = 0; i <= lastLayer - layer; i++) {
			lastItem = lastItem.getParentStructItem();
		}
		lastItem.getSubStruct().add(tempStructItem);
		return tempStructItem;
	}

	/**
	 * Works the infos for the same layer.
	 * 
	 * @param name
	 *            String
	 * @param struct
	 *            Struct
	 * @param lastItem
	 *            StructItem
	 * @param additionalInfo
	 *            HashMap<String, String>
	 * 
	 * @return StructItem StructItem
	 */
	private StructItem workLayerSame(final String name, final Struct struct, final StructItem lastItem, final HashMap<String, String> additionalInfo) {
		StructItem tempStructItem = null;
		if (this.isDebug) {
			System.out.println("equals: " + name);
		}
		tempStructItem = new StructItem(name.trim(), additionalInfo, lastItem.getParentStructItem());
		lastItem.getParentStructItem().getSubStruct().add(tempStructItem);
		return tempStructItem;
	}

	/**
	 * Get the content for a specific file.
	 * 
	 * @param in
	 *            BufferedReader
	 * @param lastItem
	 *            StructItem
	 * @throws IOException
	 */
	private void workContent(final BufferedReader in, final StructItem lastItem) throws IOException {
		String s = null;
		Boolean first = true;
		String content = "";
		// read content
		while (((s = in.readLine()) != null) && !(s.trim().equals(FolderGen.CONTENT_END))) {
			if (first) {
				first = false;
			}
			content += s.trim();
			content += "\r\n";
		}

		content = this.replaceMarker(lastItem, content);

		// add content to (last)item
		lastItem.getAdditionalData().put("content", content);
	}

	/**
	 * Replace defined markers with items.<br />
	 * <ul>
	 * <li>${user.name} - returns the username</li>
	 * <li>${user.homedir} - returns the user home dir</li>
	 * <li>${system.os} - returns the operating system</li>
	 * <li>${file.currentdir} - returns the current dir</li>
	 * <li>${file.name} - returns the current filename</li>
	 * <li>${date.year} - returns the current year as decimal</li>
	 * <li>${date.month} - returns the current month as decimal</li>
	 * <li>${date.day} - returns the current day as decimal</li>
	 * <li>${date.hour} - returns the current hour as decimal</li>
	 * <li>${date.minute} - returns the current minute as decimal</li>
	 * <li>${date.second} - returns the current second as decimal</li>
	 * <li>${date.isleap(YEAR)} - returns true or false</li>
	 * <li>${date.formatcurrent([Format FORMAT])} - returns the current
	 * formatted date/time</li>
	 * <li>${func.counter(VAR)} - counter for a VAR</li>
	 * </ul>
	 * 
	 * @param lastItem
	 *            StructItem
	 * @param content
	 *            String
	 * @return String
	 */
	private String replaceMarker(final StructItem lastItem, String content) {
		GregorianCalendar gc = new GregorianCalendar();

		content = content.replaceAll("(\\$\\{user.name\\})", System.getProperty("user.name"));
		content = content.replaceAll("(\\$\\{user.homedir\\})", System.getProperty("user.home"));
		content = content.replaceAll("(\\$\\{system.os\\})", System.getProperty("os.name"));
		content = content.replaceAll("(\\$\\{file.currentdir\\})", System.getProperty("user.dir"));
		content = content.replaceAll("(\\$\\{file.name\\})", lastItem.getName());

		// date markers
		content = content.replaceAll("(\\$\\{date.year\\})", Integer.toString(gc.get(Calendar.YEAR)));
		content = content.replaceAll("(\\$\\{date.month\\})", Integer.toString(gc.get(Calendar.MONTH)));
		content = content.replaceAll("(\\$\\{date.day\\})", Integer.toString(gc.get(Calendar.DAY_OF_MONTH)));
		content = content.replaceAll("(\\$\\{date.hour\\})", Integer.toString(gc.get(Calendar.HOUR_OF_DAY)));
		content = content.replaceAll("(\\$\\{date.minute\\})", Integer.toString(gc.get(Calendar.MINUTE)));
		content = content.replaceAll("(\\$\\{date.second\\})", Integer.toString(gc.get(Calendar.SECOND)));

		Pattern pattern = null;
		Matcher matcher = null;
		try {
			pattern = Pattern.compile("\\$\\{date.isleap\\(([0-9]{4})\\)\\}");
			matcher = pattern.matcher(content);
			while (matcher.find()) {
				content = matcher.replaceAll(Boolean.toString(gc.isLeapYear(Integer.parseInt(matcher.group(1)))));
			}
		} catch (IllegalArgumentException e) {
			if (this.isDebug) {
				e.printStackTrace();
			}
		}

		try {
			pattern = Pattern.compile("\\$\\{date.formatcurrent\\((.+?)\\)\\}");
			matcher = pattern.matcher(content);
			while (matcher.find()) {
				content = matcher.replaceAll(new SimpleDateFormat(matcher.group(1)).format(new Date()));
			}
		} catch (IllegalArgumentException e) {
			if (this.isDebug) {
				e.printStackTrace();
			}
		}

		try {
			pattern = Pattern.compile("(\\$\\{func.counter\\(([a-zA-Z]{1})\\)\\})");
			matcher = pattern.matcher(content);
			while (matcher.find()) {
				int i = 0;
				while (content.indexOf("${func.counter(" + matcher.group(2) + ")}") != -1) {
					content = content.replaceFirst("\\$\\{func.counter\\((" + matcher.group(2) + ")\\)\\}", Integer.toString(i));
					i++;
				}
			}

		} catch (IllegalArgumentException e) {
			if (this.isDebug) {
				e.printStackTrace();
			}
		}

		return content;
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
			if (tempAdditionalData.containsKey("type") && (tempAdditionalData.get("type") != null)) {
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
				this.workStruct(struct.get(i).getSubStruct(), new File(rootFolder + File.separator + struct.get(i).getName()));
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
				if (tempAdditionalData.containsKey("content") && (tempAdditionalData.get("content") != null) && !tempAdditionalData.get("content").equals("")) {
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
				this.debugPrint(struct.get(i).getSubStruct(), seperator + "\t");
			}
		}

	}
}
