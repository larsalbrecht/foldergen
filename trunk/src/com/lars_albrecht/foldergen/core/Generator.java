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
import java.io.InputStreamReader;
import java.net.ConnectException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.lars_albrecht.foldergen.core.helper.Struct;
import com.lars_albrecht.foldergen.core.helper.StructItem;
import com.lars_albrecht.foldergen.core.helper.properies.PropertiesReader;
import com.lars_albrecht.foldergen.helper.Utilities;
import com.lars_albrecht.foldergen.plugin.classes.CoreMarkerReplacer;
import com.lars_albrecht.foldergen.plugin.finder.PluginFinder;

/**
 * The Generator generates the folders and files using the given configfile.
 * 
 * @author lalbrecht
 * @version 1.5.4.0
 * 
 */
public class Generator {

	/**
	 * static variables for comparisons
	 */
	private final static String FOLDER = "+";
	private final static String FILE = "-";
	private final static String COPY = "~";
	private final static String FOLDER_STR = "folder";
	private final static String FILE_STR = "file";
	private final static String COPY_STR = "copy";
	private final static String CONTENT_START = "(((";
	private final static String CONTENT_END = ")))";

	private Boolean isDebug = false;
	private File rootPath = null;
	private Boolean showConfirmation = false;
	private Boolean usePlugins = false;

	/**
	 * Generator constructor.
	 * 
	 * @param rootPath
	 *            File
	 * @param configFile
	 *            File
	 * @param isDebug
	 *            Boolean
	 * @param showConfirmation
	 *            Boolean
	 * @param usePlugins
	 *            Boolean
	 */
	public Generator(final File rootPath, final File configFile, final Boolean isDebug, final Boolean showConfirmation,
			final Boolean usePlugins) {
		this.isDebug = isDebug;
		this.showConfirmation = showConfirmation;
		this.usePlugins = usePlugins;

		this.rootPath = (rootPath != null ? rootPath : new File(configFile.getParent()));
		if(this.isDebug) {
			System.out.println(PropertiesReader.getInstance().getProperties("application.debug.choosedrootpath")
					+ this.rootPath.getAbsolutePath() + "\n");
		}

		// If config file exists and is a file and parent != null
		if(configFile.exists() && configFile.isFile() && (configFile.getParent() != null)) {
			this.workFile(configFile);
		} else {
			System.out.println(PropertiesReader.getInstance().getProperties("application.output.wrongfile"));
		}
	}

	/**
	 * Works the file. Parse the given config file and generates the files and folders recursivly.
	 * 
	 * @param configFile
	 *            File
	 * @return Boolean Boolean
	 */
	private Boolean workFile(final File configFile) {
		BufferedReader in = null;
		File basicRootFolder = null;
		try {
			// Buffered reader reads the file
			in = new BufferedReader(new FileReader(configFile));
			// set "basicRootFolder" to the folder of the config-file.
			basicRootFolder = this.rootPath;
			String line = null;
			Integer layer = 0;
			Integer lastLayer = 0;
			// Create struct for items
			Struct struct = new Struct();
			// Create structitem for the last created.
			StructItem lastItem = null;
			// every line in file
			while((line = in.readLine()) != null) {
				System.out.println("------------");
				String[] basicInfo = line.trim().split("\\s", 2);
				System.out.println(Arrays.toString(basicInfo));
				System.out.println("line: " + line);
				String typeStr = null;
				Matcher m = Pattern.compile("([\\s]{0,})([\\+\\-\\~]{1})([\\s]{1})").matcher(line);
				if(m.find() && (m.groupCount() > 0)) {
					typeStr = m.group(1);
				}
				if((typeStr != null) || (line.indexOf("(((") != -1)) {
					if((basicInfo != null) && (basicInfo.length == 2) && (basicInfo[0] != null) && (basicInfo[1] != null)) {
						layer = typeStr.split("\\s", -1).length - 1;
						if(layer.equals("")) {
							layer = 0;
						}
						HashMap<String, String> additionalInfo = new HashMap<String, String>();
						String itemTitle = null;
						// if folder or file ...
						if(basicInfo[0].trim().equals(Generator.FOLDER) || basicInfo[0].trim().equals(Generator.FILE)) {
							if(this.isDebug) {
								System.out.println("is file/folder");
							}
							additionalInfo.put("type", basicInfo[0].trim().equals(Generator.FOLDER) ? Generator.FOLDER_STR
									: Generator.FILE_STR);
							itemTitle = basicInfo[1].trim();

							// if copy
						} else if(basicInfo[0].trim().equals(Generator.COPY)) {
							if(this.isDebug) {
								System.out.println("is copy");
							}
							additionalInfo.put("type", Generator.COPY_STR);
							additionalInfo
									.put("src", basicInfo[1].trim().substring(basicInfo[1].trim().indexOf("->") + 2).trim());
							itemTitle = basicInfo[1].trim().substring(0, basicInfo[1].trim().indexOf("->")).trim();

						}
						if(basicInfo[0].trim().equals(Generator.FOLDER) || basicInfo[0].trim().equals(Generator.FILE)
								|| basicInfo[0].trim().equals(Generator.COPY)) {
							Object[] workedLine = this.workLine(layer, lastLayer, itemTitle, struct, lastItem, additionalInfo);
							struct = (Struct) workedLine[0];
							lastItem = (StructItem) workedLine[1];
							if((lastItem != null) && lastItem.getName().equals("test.css")) {
								this.printStruct(struct, " ", true);
							}
							System.out.println("LI: " + lastItem.getName());

						}
						lastLayer = layer;
					} else if(basicInfo[0].trim().equals(Generator.CONTENT_START)) {
						if(this.isDebug) {
							System.out.println("is Content");
						}
						this.workContent(in, lastItem);
					}
				}
			}
			if((this.showConfirmation && this.confirmationWorker(struct, basicRootFolder)) || !this.showConfirmation) {
				this.workStruct(struct, basicRootFolder);
			}

			if(this.isDebug) {
				this.printStruct(struct, "", Boolean.TRUE);
			}

		} catch(IOException e) {
			System.out.println(e.getMessage());
			return false;
		}
		return true;
	}

	/**
	 * 
	 * 
	 * @param struct
	 *            Struct
	 * @param basicRootFolder
	 *            File
	 * @return Boolean
	 */
	private Boolean confirmationWorker(final Struct struct, final File basicRootFolder) {
		this.printStruct(struct, "", Boolean.FALSE);
		System.out.println(PropertiesReader.getInstance().getProperties("application.output.confirmation.question"));
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		String inputLine = null;
		Boolean hasToWrite = null;
		try {
			while((hasToWrite == null) && ((inputLine = br.readLine()) != null)) {
				if(inputLine.equals("") || inputLine.equalsIgnoreCase("n") || inputLine.equalsIgnoreCase("н")) {
					hasToWrite = false;
				} else if(inputLine.equalsIgnoreCase("y") || inputLine.equalsIgnoreCase("j") || inputLine.equalsIgnoreCase("o")
						|| inputLine.equalsIgnoreCase("Д")) {
					hasToWrite = true;
				}
				if(this.isDebug) {
					System.out.println("\n"
							+ PropertiesReader.getInstance().getProperties("application.debug.confirmation.writenotice")
							+ hasToWrite);
				}
			}
		} catch(IOException e) {
			e.printStackTrace();
		}
		if(hasToWrite) {
			if(this.isDebug) {
				System.out.println("\n" + PropertiesReader.getInstance().getProperties("application.debug.struct.work"));
			}
			System.out.println(PropertiesReader.getInstance().getProperties("application.debug.struct.work.create"));
			this.workStruct(struct, basicRootFolder);
		} else if(this.isDebug) {
			System.out.println(PropertiesReader.getInstance().getProperties("application.debug.struct.nowork.nocreate"));
			System.out.println("\n" + PropertiesReader.getInstance().getProperties("application.debug.struct.nowork"));
		}
		return true;
	}

	/**
	 * Works a line. Add structItem to struct with care of the indent. Returns struct and lastItem in the result Object-Array.
	 * 
	 * @param layer
	 *            Integer
	 * @param lastLayer
	 *            Integer
	 * @param itemTitle
	 *            String
	 * @param struct
	 *            Struct
	 * @param lastItem
	 *            StructItem
	 * @param additionalInfo
	 *            HashMap<String, String>
	 * @return Object{struct, lastItem}
	 */
	private Object[] workLine(final Integer layer, final Integer lastLayer, final String itemTitle, final Struct struct,
			final StructItem lastItem, final HashMap<String, String> additionalInfo) {
		Object[] mulitpleResult = { struct, lastItem };
		System.out.println("layer: " + layer);
		System.out.println("lastLayer: " + lastLayer);
		System.out.println("Last item: " + (lastItem != null ? lastItem.getName() : "none"));
		if(layer.equals(0)) { // zero / root layer
			mulitpleResult[0] = this.workLayerZero(itemTitle, struct, lastItem, additionalInfo);
			mulitpleResult[1] = struct.get(struct.size() - 1);
		} else if(layer > lastLayer) { // layer down
			mulitpleResult[1] = this.workLayerDown(itemTitle, struct, lastItem, additionalInfo);
		} else if(layer < lastLayer) { // layer up
			mulitpleResult[1] = this.workLayerUp(itemTitle, struct, lastItem, additionalInfo, lastLayer, layer);
		} else if(layer.equals(lastLayer)) { // same layer
			mulitpleResult[1] = this.workLayerSame(itemTitle, struct, lastItem, additionalInfo);
		}
		if(((StructItem) mulitpleResult[1]).getName().equals("general.css")) {
			System.out.println("SpecialLastItem: " + ((StructItem) mulitpleResult[1]).getParentStructItem().getName());
		}
		return mulitpleResult;
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
	private Struct workLayerZero(final String name, final Struct struct, final StructItem lastItem,
			final HashMap<String, String> additionalInfo) {
		StructItem tempStructItem = null;
		if(this.isDebug) {
			System.out.println(PropertiesReader.getInstance().getProperties("application.debug.zero") + name);
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
	private StructItem workLayerDown(final String name, final Struct struct, final StructItem lastItem,
			final HashMap<String, String> additionalInfo) {
		StructItem tempStructItem = null;
		if(this.isDebug) {
			System.out.println(PropertiesReader.getInstance().getProperties("application.debug.down") + name);
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
	private StructItem workLayerUp(final String name, final Struct struct, StructItem lastItem,
			final HashMap<String, String> additionalInfo, final Integer lastLayer, final Integer layer) {
		StructItem tempStructItem = null;
		if(this.isDebug) {
			System.out.println(PropertiesReader.getInstance().getProperties("application.debug.up") + name);
		}
		tempStructItem = new StructItem(name.trim(), additionalInfo, null);
		for(int i = 0; i <= lastLayer - layer; i++) {
			lastItem = lastItem.getParentStructItem();
		}
		tempStructItem.setParentStructItem(lastItem);
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
	private StructItem workLayerSame(final String name, final Struct struct, final StructItem lastItem,
			final HashMap<String, String> additionalInfo) {
		StructItem tempStructItem = null;
		if(this.isDebug) {
			System.out.println(PropertiesReader.getInstance().getProperties("application.debug.equals") + name);
		}
		System.out.println("MyLastItem is: " + (lastItem != null ? lastItem.getParentStructItem().getName() : "none"));
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
		String content = "";
		// read content
		while(((s = in.readLine()) != null) && !(s.trim().equals(Generator.CONTENT_END))) {
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
	 * <li>${date.formatcurrent([Format FORMAT])} - returns the current formatted date/time</li>
	 * <li>${func.counter(VAR)} - counter for a VAR</li>
	 * <li>${func.counter(VAR|STARTNUMBER)} - counter for a VAR with startnumber STARTNUMBER</li>
	 * <li>${func.getfilecontent(filepath)} - return the content of the given file</li>
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
		// user markers
		content = content.replaceAll("(\\$\\{user.name\\})", System.getProperty("user.name"));
		content = content.replaceAll("(\\$\\{user.homedir\\})", System.getProperty("user.home"));

		// system markers
		content = content.replaceAll("(\\$\\{system.os\\})", System.getProperty("os.name"));

		// file markers
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
			while(matcher.find()) {
				content = matcher.replaceAll(Boolean.toString(gc.isLeapYear(Integer.parseInt(matcher.group(1)))));
			}

			pattern = Pattern.compile("\\$\\{date.formatcurrent\\((.+?)\\)\\}");
			matcher = pattern.matcher(content);
			while(matcher.find()) {
				content = matcher.replaceAll(new SimpleDateFormat(matcher.group(1)).format(new Date()));
			}

		} catch(IllegalArgumentException e) {
			if(this.isDebug) {
				e.printStackTrace();
			}
		}

		// function markers
		try {
			pattern = Pattern.compile("(\\$\\{func\\.counter\\(([a-zA-Z]+)?(\\|[0-9]+)?\\)\\})");
			matcher = pattern.matcher(content);
			while(matcher.find()) {
				int i = 0;
				if(matcher.group(3) != null) {
					i = Integer.parseInt(matcher.group(3).substring(1));
					content = content.replaceFirst("(\\$\\{func.counter\\((" + matcher.group(2) + ")\\|" + i + "\\)\\})", Integer
							.toString(i));
					i++;
				}
				while(content.indexOf("${func.counter(" + matcher.group(2) + ")}") != -1) {
					content = content
							.replaceFirst("(\\$\\{func.counter\\((" + matcher.group(2) + ")\\)\\})", Integer.toString(i));
					i++;
				}
			}
			pattern = Pattern.compile("(\\$\\{func\\.getfilecontent\\((.+)?\\)\\})");
			matcher = pattern.matcher(content);
			while(matcher.find()) {
				while(content.indexOf("${func.getfilecontent(" + matcher.group(2) + ")}") != -1) {
					content = content.replaceFirst("(\\$\\{func\\.getfilecontent\\((\\Q" + matcher.group(2) + "\\E)?\\)\\})",
							Utilities.getFileContent(new File(matcher.group(2))));
				}
			}
		} catch(IllegalArgumentException e) {
			if(this.isDebug) {
				e.printStackTrace();
			}
		} catch(IOException e) {
			if(this.isDebug) {
				e.printStackTrace();
			}
		}
		if(this.usePlugins) {
			content = this.getPluginResult(content);
		}

		return content;
	}

	/**
	 * Returns the result of the plugin replacements.
	 * 
	 * @param content
	 *            String
	 * @return String
	 */
	private String getPluginResult(final String content) {
		String resultStr = content;
		try {
			PluginFinder pluginFinder = new PluginFinder();
			pluginFinder.search(System.getProperty("user.dir"));
			List<CoreMarkerReplacer> pluginCollection = pluginFinder.getPluginCollection();
			for(CoreMarkerReplacer plugin : pluginCollection) {
				plugin.setContent(resultStr);
				resultStr = plugin.replaceContent();
			}
		} catch(Exception e) {
			if(this.isDebug) {
				e.printStackTrace();
			}
		}

		return resultStr;
	}

	/**
	 * Works the struct recursive and create folders and files (with content), if available.
	 * 
	 * @param struct
	 *            Struct
	 * @param rootFolder
	 *            File
	 */
	private void workStruct(final Struct struct, final File rootFolder) {
		for(int i = 0; i < struct.size(); i++) {
			// get additional data from structItem
			HashMap<String, String> tempAdditionalData = struct.get(i).getAdditionalData();
			if(tempAdditionalData.containsKey("type") && (tempAdditionalData.get("type") != null)) {
				// if valid ...
				if(tempAdditionalData.get("type").toLowerCase().equals(Generator.FILE_STR)) {
					// create file, if needed
					this.workFile(rootFolder, tempAdditionalData, struct.get(i).getName());
				} else if(tempAdditionalData.get("type").toLowerCase().equals(Generator.FOLDER_STR)) {
					// create folder, if needed
					this.workFolder(rootFolder, struct.get(i).getName());
				} else if(tempAdditionalData.get("type").toLowerCase().equals(Generator.COPY_STR)) {
					// create copy, if needed
					this.workGetCopy(rootFolder, tempAdditionalData, struct.get(i).getName());
				}
			}
			if(struct.get(i).getSubStruct() != null) {
				// read substructure
				this.workStruct(struct.get(i).getSubStruct(), new File(rootFolder + File.separator + struct.get(i).getName()));
			}
		}
	}

	/**
	 * If needed, creates a copy of a file or a file from a HTTP-url.
	 * 
	 * @param rootFolder
	 *            File
	 * @param tempAdditionalData
	 *            HashMap<String,String>
	 * @param name
	 *            String
	 */
	private void workGetCopy(final File rootFolder, final HashMap<String, String> tempAdditionalData, final String name) {
		if(tempAdditionalData.get("src") != null) {
			if(tempAdditionalData.get("src").startsWith("http://") || tempAdditionalData.get("src").startsWith("https://")) {
				try {
					// url
					File f = new File(rootFolder.getAbsolutePath() + File.separator + name);
					if(!f.exists()) {
						URL source = new URL(tempAdditionalData.get("src"));

						String content = Utilities.getFileContentFromWeb(source);
						f.createNewFile();
						FileOutputStream fos = new FileOutputStream(f);
						for(int j = 0; j < content.length(); j++) {
							fos.write((byte) content.charAt(j));
						}
						fos.close();
					}
				} catch(MalformedURLException e) {
					if(this.isDebug) {
						e.printStackTrace();
					}
				} catch(ConnectException e) {
					if(this.isDebug) {
						e.printStackTrace();
					}
				} catch(IOException e) {
					if(this.isDebug) {
						e.printStackTrace();
					}
				}
			} else {
				try {
					// file
					File source = new File(tempAdditionalData.get("src"));
					if(source.exists() && source.isFile()) {
						Utilities.copyFile(source, new File(rootFolder.getAbsolutePath() + File.separator + name));
					} else if(source.exists() && source.isDirectory()) {
						Utilities.copyDir(source, new File(rootFolder.getAbsolutePath() + File.separator + name));
					}
				} catch(IOException e) {
					if(this.isDebug) {
						e.printStackTrace();
					}
				}
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
			if(!f.exists() && new File(f.getParent()).isDirectory()) {
				f.createNewFile();
				if(tempAdditionalData.containsKey("content") && (tempAdditionalData.get("content") != null)
						&& !tempAdditionalData.get("content").equals("")) {
					FileOutputStream fos = new FileOutputStream(f);
					for(int j = 0; j < tempAdditionalData.get("content").length(); j++) {
						fos.write((byte) tempAdditionalData.get("content").charAt(j));
					}
					fos.close();
				}
			}
		} catch(IOException e) {
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
		if(!f.exists() && new File(f.getParent()).isDirectory()) {
			f.mkdir();
		}
	}

	/**
	 * Prints a given Struct "struct" in console with "seperator" to seperate.
	 * 
	 * @param struct
	 *            Struct
	 * @param seperator
	 *            String
	 * @param showAll
	 *            Boolean
	 */
	private void printStruct(final Struct struct, final String seperator, final Boolean showAll) {
		for(int i = 0; i < struct.size(); i++) {
			System.out.println(seperator
					+ i
					+ " - "
					+ struct.get(i).getName()
					+ (showAll ? (struct.get(i).getAdditionalData() != null ? " - " + struct.get(i).getAdditionalData() : "")
							: ""));
			if(struct.get(i).getSubStruct() != null) {
				this.printStruct(struct.get(i).getSubStruct(), seperator + "\t", showAll);
			}
		}

	}

}
