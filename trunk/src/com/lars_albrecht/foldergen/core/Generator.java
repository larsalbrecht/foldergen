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
package com.lars_albrecht.foldergen.core;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.lars_albrecht.foldergen.core.generator.helper.Struct;
import com.lars_albrecht.foldergen.core.generator.helper.StructItem;
import com.lars_albrecht.foldergen.core.generator.worker.CopyWorker;
import com.lars_albrecht.foldergen.core.generator.worker.DefaultContentWorker;
import com.lars_albrecht.foldergen.core.generator.worker.FileWorker;
import com.lars_albrecht.foldergen.core.generator.worker.FolderWorker;
import com.lars_albrecht.foldergen.core.generator.worker.ZipWorker;
import com.lars_albrecht.foldergen.core.helper.properies.PropertiesReader;
import com.lars_albrecht.foldergen.helper.Utilities;
import com.lars_albrecht.foldergen.plugin.classes.FolderGenPlugin;
import com.lars_albrecht.foldergen.plugin.finder.PluginFinder;
import com.lars_albrecht.foldergen.plugin.interfaces.IFolderGenPlugin;

/**
 * The Generator generates the folders and files using the given configfile.
 * 
 * @author lalbrecht
 * @version 1.5.4.1
 * 
 */
public class Generator {

	/**
	 * static variables for comparisons
	 */
	private final static ArrayList<FolderGenPlugin> fgpWorker = new ArrayList<FolderGenPlugin>();
	private final static ArrayList<FolderGenPlugin> fgpContentReplacer = new ArrayList<FolderGenPlugin>();

	private final static ArrayList<String> fileTypes = new ArrayList<String>();

	private Boolean isDebug = false;
	private File rootPath = null;
	private Boolean showConfirmation = false;
	private Boolean usePlugins = false;

	private Struct struct = null;
	private StructItem lastItem = null;
	private Integer lastLayer = null;
	private BufferedReader configFileReader = null;

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
		this.lastLayer = 0;

		this.initGenerator();

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
	 * Initilize Generator.
	 */
	private void initGenerator() {
		this.initWorker();
	}

	/**
	 * Loads worker and plugins (if needed) and add file types to list ("fileTypes").
	 */
	private void initWorker() {
		Generator.fgpWorker.add(new FolderWorker());
		Generator.fgpWorker.add(new FileWorker());
		Generator.fgpWorker.add(new CopyWorker());
		Generator.fgpWorker.add(new DefaultContentWorker());
		Generator.fgpWorker.add(new ZipWorker());

		// Load plugins if needed
		if(this.usePlugins) {
			this.loadPlugins();
		}

		// Add fileTypes
		for(FolderGenPlugin plugin : Generator.fgpWorker) {
			if(plugin.getInfoMapValue(IFolderGenPlugin.INFO_FILEMARKER) != null) {
				if(this.isDebug) {
					System.out.println(PropertiesReader.getInstance().getProperties("application.debug.filetype.added")
							+ plugin.getInfoMapValue(IFolderGenPlugin.INFO_FILEMARKER));
				}
				Generator.fileTypes.add((String) plugin.getInfoMapValue(IFolderGenPlugin.INFO_FILEMARKER));
			}
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
		File basicRootFolder = null;
		try {
			// Buffered reader reads the file
			this.configFileReader = new BufferedReader(new FileReader(configFile));
			// set "basicRootFolder" to the folder of the config-file.
			basicRootFolder = this.rootPath;
			String line = null;
			// Create struct for items
			this.struct = new Struct();
			// every line in file
			while((line = this.configFileReader.readLine()) != null) {
				// Split line
				HashMap<Integer, String> basicInfo = this.getBasicInfoMapFromConfigLine(line);
				String typeStr = this.getTypeFromConfigLine(line);
				if((typeStr != null) && (basicInfo.size() > 0) && (basicInfo.containsKey(IFolderGenPlugin.BASICINFO_FILETITLE))
						&& (basicInfo.containsKey(IFolderGenPlugin.BASICINFO_FILETITLE))) {
					// No content
					this.workNonContentWorker(basicInfo, typeStr);
				} else {
					// content
					this.workContentWorker(basicInfo);
				}
			}
			if((this.showConfirmation && this.confirmationWorker(basicRootFolder)) || !this.showConfirmation) {
				if(this.isDebug) {
					this.printStruct(this.struct, "", Boolean.TRUE);
				}
				this.workStruct(this.struct, basicRootFolder);
			}

		} catch(IOException e) {
			if(this.isDebug) {
				System.out.println(e.getMessage());
			}
			return false;
		}
		return true;
	}

	/**
	 * Work non content worker to create struct.
	 * 
	 * @param basicInfo
	 *            HashMap<Integer, String>
	 * @param typeStr
	 *            String
	 */
	private void workNonContentWorker(final HashMap<Integer, String> basicInfo, final String typeStr) {
		Integer layer = null;
		for(FolderGenPlugin plugin : Generator.fgpWorker) {
			if((plugin.getPluginType() != IFolderGenPlugin.PLUGINTYPE_CONFEXTENSION_CONTENT)
					&& basicInfo.get(IFolderGenPlugin.BASICINFO_FILEMARKER).trim().equalsIgnoreCase(
							(String) plugin.getInfoMapValue(IFolderGenPlugin.INFO_FILEMARKER))) {
				if(this.isDebug) {
					for(Entry<Integer, Object> e : plugin.getInfoMap().entrySet()) {
						System.out.println(e.getKey() + " - " + e.getValue());
					}
					System.out.println(PropertiesReader.getInstance().getProperties(
							"application.debug.workfile.plugin.plugintype")
							+ plugin.getPluginType());
					System.out.println(PropertiesReader.getInstance().getProperties("application.debug.is")
							+ (String) plugin.getInfoMapValue(IFolderGenPlugin.INFO_INFOMARKER) + " ("
							+ (String) plugin.getInfoMapValue(IFolderGenPlugin.INFO_FILEMARKER) + ")");
				}
				layer = typeStr.split("\\s", -1).length - 1;
				if(layer.equals("")) {
					layer = 0;
				}
				HashMap<String, String> additionalInfo = new HashMap<String, String>();

				additionalInfo.put("type", (String) plugin.getInfoMapValue(IFolderGenPlugin.INFO_INFOMARKER));
				if(plugin.getPluginType() == IFolderGenPlugin.PLUGINTYPE_CONFEXTENSION_FOLDER) {
					additionalInfo.put("folder", Boolean.toString(Boolean.TRUE));
				}

				if(plugin.getAdditionlInfo(basicInfo) != null) {
					additionalInfo.putAll(plugin.getAdditionlInfo(basicInfo));
				}

				this.workLine(layer, plugin.getItemTitle(basicInfo), additionalInfo);
				this.lastLayer = layer;
				break;
			} else if(this.isDebug) {
				System.out.println(PropertiesReader.getInstance().getProperties("application.debug.notequals"));
			}
		}
	}

	/**
	 * Work content worker to fill items with content.
	 * 
	 * @param basicInfo
	 *            HashMap<Integer, String>
	 * @throws IOException
	 */
	private void workContentWorker(final HashMap<Integer, String> basicInfo) throws IOException {
		for(FolderGenPlugin plugin : Generator.fgpWorker) {
			if((plugin.getPluginType() == IFolderGenPlugin.PLUGINTYPE_CONFEXTENSION_CONTENT)
					&& basicInfo.get(IFolderGenPlugin.BASICINFO_FILEMARKER).trim().equalsIgnoreCase(
							(String) plugin.getInfoMapValue(IFolderGenPlugin.INFO_CONTENTSTARTMARKER))) {
				String contentLine = null;
				String content = "";
				// read content
				while(((contentLine = this.configFileReader.readLine()) != null)
						&& !(contentLine.trim().equals(plugin.getInfoMapValue(IFolderGenPlugin.INFO_CONTENTENDMARKER)))) {
					content += contentLine.trim();
					content += "\r\n";
				}
				HashMap<String, Object> workerMap = new HashMap<String, Object>();
				workerMap.put("content", content);
				workerMap.put("lastItem", this.lastItem);

				content = (String) plugin.doWork(workerMap).get("content");
				if((Boolean) plugin.getInfoMapValue(IFolderGenPlugin.INFO_CONTENTREPLACE)) {
					content = this.replaceMarker(this.lastItem, content);
				}

				// add content to (last)item
				this.lastItem.getAdditionalData().put("content", content);
				break;
			} else if(this.isDebug) {
				System.out.println(PropertiesReader.getInstance().getProperties("application.debug.notequals"));
			}
		}
	}

	/**
	 * Returns basicInfoMap from cofiguration line.
	 * 
	 * @param line
	 *            String
	 * @return HashMap<String, String>
	 */
	private HashMap<Integer, String> getBasicInfoMapFromConfigLine(final String line) {
		HashMap<Integer, String> tempResult = new HashMap<Integer, String>();
		String[] basicInfoArr = line.trim().split("\\s", 2);

		switch(basicInfoArr.length) {
		case 2:
			tempResult.put(IFolderGenPlugin.BASICINFO_FILETITLE, basicInfoArr[1]);
		case 1:
			tempResult.put(IFolderGenPlugin.BASICINFO_FILEMARKER, basicInfoArr[0]);
			break;

		}

		return tempResult;
	}

	/**
	 * Returns type from configuration line.
	 * 
	 * @param line
	 *            String
	 * @return String
	 */
	private String getTypeFromConfigLine(final String line) {
		String regexpTypeStr = "";
		for(int i = 0; i < Generator.fileTypes.size(); i++) {
			regexpTypeStr += "\\" + Generator.fileTypes.get(i);
		}
		Matcher m = Pattern.compile("([\\s]{0,})([" + regexpTypeStr + "]{1})([\\s]{1})").matcher(line);
		if(m.find() && (m.groupCount() > 0)) {
			return m.group(1);
		} else {
			return null;
		}
	}

	/**
	 * 
	 * @param basicRootFolder
	 *            File
	 * @return Boolean
	 */
	private Boolean confirmationWorker(final File basicRootFolder) {
		this.printStruct(this.struct, "", Boolean.FALSE);
		System.out.println(PropertiesReader.getInstance().getProperties("application.output.confirmation.question"));
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		String inputLine = null;
		try {
			while(((inputLine = br.readLine()) != null)) {
				if(inputLine.equals("") || inputLine.equalsIgnoreCase("n") || inputLine.equalsIgnoreCase("н")) {
					System.out.println(PropertiesReader.getInstance().getProperties("application.debug.struct.nowork.nocreate"));
					System.out.println("\n" + PropertiesReader.getInstance().getProperties("application.debug.struct.nowork"));
					return Boolean.FALSE;
				} else if(inputLine.equalsIgnoreCase("y") || inputLine.equalsIgnoreCase("j") || inputLine.equalsIgnoreCase("o")
						|| inputLine.equalsIgnoreCase("Д")) {

					if(this.isDebug) {
						System.out.println("\n" + PropertiesReader.getInstance().getProperties("application.debug.struct.work"));
					}
					System.out.println(PropertiesReader.getInstance().getProperties("application.debug.struct.work.create"));
					return Boolean.TRUE;
				}
			}
		} catch(IOException e) {
			e.printStackTrace();
		}
		return Boolean.FALSE;
	}

	/**
	 * Works a line. Add structItem to struct with care of the indent.
	 * 
	 * @param layer
	 *            Integer
	 * @param itemTitle
	 *            String
	 * @param additionalInfo
	 *            HashMap<String, String>
	 */
	private void workLine(final Integer layer, String itemTitle, final HashMap<String, String> additionalInfo) {
		itemTitle = (itemTitle == null ? "" : itemTitle);
		if(layer.equals(0)) { // zero / root layer
			this.struct = this.workLayerZero(itemTitle, additionalInfo);
			this.lastItem = this.struct.get(this.struct.size() - 1);
		} else if(layer > this.lastLayer) { // layer down
			this.lastItem = this.workLayerDown(itemTitle, additionalInfo);
		} else if(layer < this.lastLayer) { // layer up
			this.lastItem = this.workLayerUp(itemTitle, additionalInfo, this.lastLayer, layer);
		} else if(layer.equals(this.lastLayer)) { // same layer
			this.lastItem = this.workLayerSame(itemTitle, additionalInfo);
		}
	}

	/**
	 * Works the infos for the zero layer.
	 * 
	 * @param name
	 *            String
	 * @param additionalInfo
	 *            HashMap<String, String>
	 * 
	 * @return Struct Struct
	 */
	private Struct workLayerZero(final String name, final HashMap<String, String> additionalInfo) {
		StructItem tempStructItem = null;
		if(this.isDebug) {
			System.out.println(PropertiesReader.getInstance().getProperties("application.debug.zero") + name);
		}
		tempStructItem = new StructItem(name.trim(), additionalInfo, null);
		this.struct.add(tempStructItem);
		return this.struct;
	}

	/**
	 * Works the infos for the lower layer.
	 * 
	 * @param name
	 *            String
	 * @param additionalInfo
	 *            HashMap<String, String>
	 * 
	 * @return StructItem StructItem
	 */
	private StructItem workLayerDown(final String name, final HashMap<String, String> additionalInfo) {
		StructItem tempStructItem = null;
		if(this.isDebug) {
			System.out.println(PropertiesReader.getInstance().getProperties("application.debug.down") + name);
		}
		tempStructItem = new StructItem(name.trim(), additionalInfo, this.lastItem);
		this.lastItem.getSubStruct().add(tempStructItem);
		return tempStructItem;
	}

	/**
	 * Works the infos for the upper layer.
	 * 
	 * @param name
	 *            String
	 * @param additionalInfo
	 *            HashMap<String, String>
	 * @param lastLayer
	 *            Integer
	 * @param layer
	 *            Integer
	 * 
	 * @return StructItem StructItem
	 */
	private StructItem workLayerUp(final String name, final HashMap<String, String> additionalInfo, final Integer lastLayer,
			final Integer layer) {
		StructItem tempStructItem = null;
		if(this.isDebug) {
			System.out.println(PropertiesReader.getInstance().getProperties("application.debug.up") + name);
		}
		tempStructItem = new StructItem(name.trim(), additionalInfo, null);
		for(int i = 0; i <= lastLayer - layer; i++) {
			this.lastItem = this.lastItem.getParentStructItem();
		}
		tempStructItem.setParentStructItem(this.lastItem);
		this.lastItem.getSubStruct().add(tempStructItem);
		return tempStructItem;
	}

	/**
	 * Works the infos for the same layer.
	 * 
	 * @param name
	 *            String
	 * @param additionalInfo
	 *            HashMap<String, String>
	 * 
	 * @return StructItem StructItem
	 */
	private StructItem workLayerSame(final String name, final HashMap<String, String> additionalInfo) {
		StructItem tempStructItem = null;
		if(this.isDebug) {
			System.out.println(PropertiesReader.getInstance().getProperties("application.debug.equals") + name);
		}
		tempStructItem = new StructItem(name.trim(), additionalInfo, this.lastItem.getParentStructItem());
		this.lastItem.getParentStructItem().getSubStruct().add(tempStructItem);
		return tempStructItem;
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
		if((content != null) && (lastItem != null)) {
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
						content = content.replaceFirst("(\\$\\{func.counter\\((" + matcher.group(2) + ")\\|" + i + "\\)\\})",
								Integer.toString(i));
						i++;
					}
					while(content.indexOf("${func.counter(" + matcher.group(2) + ")}") != -1) {
						content = content.replaceFirst("(\\$\\{func.counter\\((" + matcher.group(2) + ")\\)\\})", Integer
								.toString(i));
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
			if(this.usePlugins && (Generator.fgpContentReplacer != null)) {
				content = this.replacePluginMarkers(content);
			}
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
	private String replacePluginMarkers(final String content) {
		String resultStr = content;
		for(FolderGenPlugin plugin : Generator.fgpContentReplacer) {
			resultStr = plugin.replaceContent(resultStr);
		}
		return resultStr;
	}

	/**
	 * Load plugins.
	 * 
	 */
	private void loadPlugins() {
		List<FolderGenPlugin> pluginCollection = null;

		try {
			PluginFinder pluginFinder = new PluginFinder();
			pluginFinder.search(System.getProperty("user.dir"));
			pluginCollection = pluginFinder.getPluginCollection();

			for(FolderGenPlugin plugin : pluginCollection) {
				if(this.isDebug) {
					System.out.println("Plugin: " + plugin.getInfoMapValue(IFolderGenPlugin.INFO_TITLE));
					System.out.println("PluginType: " + plugin.getPluginType());
				}
				if(plugin.getPluginType() != null) {
					if((plugin.getPluginType() >= 200) && (plugin.getPluginType() < 300)) {
						Generator.fgpWorker.add(plugin);
					} else if((plugin.getPluginType() >= 100) && (plugin.getPluginType() < 200)) {
						Generator.fgpContentReplacer.add(plugin);
					}
				} else {
					if(this.isDebug) {
						System.out.println("No plugin type");
					}
				}
			}

		} catch(Exception e) {
			if(this.isDebug) {
				e.printStackTrace();
			}
		}
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
				for(FolderGenPlugin plugin : Generator.fgpWorker) {
					if((plugin.getInfoMap() != null)
							&& (((String) plugin.getInfoMapValue(IFolderGenPlugin.INFO_INFOMARKER)) != null)
							&& ((String) plugin.getInfoMapValue(IFolderGenPlugin.INFO_INFOMARKER))
									.equalsIgnoreCase(tempAdditionalData.get("type"))) {

						if(((plugin.getInfoMap() != null) && (plugin.getInfoMapValue(IFolderGenPlugin.INFO_CONTENTREPLACE) != null))
								&& (Boolean) plugin.getInfoMapValue(IFolderGenPlugin.INFO_CONTENTREPLACE)) {
							tempAdditionalData.put("content", this
									.replaceMarker(struct.get(i), tempAdditionalData.get("content")));
						}

						HashMap<String, Object> workerMap = new HashMap<String, Object>();
						workerMap.put("additionalData", tempAdditionalData);
						workerMap.put("rootFolder", rootFolder);
						workerMap.put("name", struct.get(i).getName());
						plugin.doWork(workerMap);
						break;
					}
				}
			}
			if(struct.get(i).getSubStruct() != null) {
				// read substructure
				this.workStruct(struct.get(i).getSubStruct(), new File(rootFolder + File.separator + struct.get(i).getName()));
			}
		}
	}

	/**
	 * Prints a given Struct "struct" in console with "seperator" to seperate recursive.
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
