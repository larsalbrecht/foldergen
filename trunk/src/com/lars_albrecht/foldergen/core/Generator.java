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
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.lars_albrecht.foldergen.core.generator.helper.FileType;
import com.lars_albrecht.foldergen.core.generator.helper.Struct;
import com.lars_albrecht.foldergen.core.generator.helper.StructItem;
import com.lars_albrecht.foldergen.core.generator.worker.CopyWorker;
import com.lars_albrecht.foldergen.core.generator.worker.DefaultContentWorker;
import com.lars_albrecht.foldergen.core.generator.worker.DeleteWorker;
import com.lars_albrecht.foldergen.core.generator.worker.FileWorker;
import com.lars_albrecht.foldergen.core.generator.worker.FolderWorker;
import com.lars_albrecht.foldergen.core.generator.worker.ZipWorker;
import com.lars_albrecht.foldergen.core.helper.cli.FolderGenCLIConf;
import com.lars_albrecht.foldergen.core.helper.properies.PropertiesReader;
import com.lars_albrecht.foldergen.gui.helper.filesystem.FolderGenItem;
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
	private final static HashMap<FolderGenPlugin, Boolean> fgpWorker = new HashMap<FolderGenPlugin, Boolean>();
	private final static ArrayList<FolderGenPlugin> fgpContentReplacer = new ArrayList<FolderGenPlugin>();

	private final static ArrayList<FileType> fileTypes = new ArrayList<FileType>();

	private FolderGenCLIConf appConf = null;

	private Struct struct = null;
	private StructItem lastItem = null;
	private Integer lastLayer = null;
	private BufferedReader configFileReader = null;

	/**
	 * Generator constructor.
	 * 
	 * @param appConf
	 *            FolderGenCLIConf
	 */
	public Generator(final FolderGenCLIConf appConf) {
		this.appConf = appConf;
		this.appConf.setRootPath(this.appConf.getRootPath() != null ? this.appConf.getRootPath() : new File(this.appConf
				.getConfigFile().getParent()));

		if(this.appConf.getCreateNew()) {
			if((this.appConf.getConfigFile() != null) && (this.appConf.getRootPath() != null)
					&& !this.appConf.getConfigFile().exists() && this.appConf.getRootPath().exists()
					&& this.appConf.getRootPath().isDirectory()) {
				File file = this.appConf.getConfigFile();
				try {
					file.createNewFile();
				} catch(IOException e1) {
					e1.printStackTrace();
				}
				if(file.isFile() && file.exists()) {
					BufferedWriter bw = null;
					try {
						bw = new BufferedWriter(new FileWriter(file));
						bw.write(Generator.getStringFromStruct(Generator.getStructFromFilesystem(this.appConf.getRootPath()), "",
								""));
						bw.close();
						System.out.println("\n"
								+ PropertiesReader.getInstance().getProperties(
										"application.gui.messagedialog.configexported.message"));
					} catch(IOException ex) {
						System.out.println("\n"
								+ PropertiesReader.getInstance().getProperties(
										"application.gui.messagedialog.configexportederror.message"));
					}
				}

			}
		} else {

			this.lastLayer = 0;
			this.initGenerator();
			if(this.appConf.getIsDebug()) {
				System.out.println(PropertiesReader.getInstance().getProperties("application.debug.choosedrootpath")
						+ this.appConf.getRootPath().getAbsolutePath() + "\n");
			}

			// If config file exists and is a file and parent != null
			if(this.appConf.getConfigFile().exists()
					&& this.appConf.getConfigFile().isFile()
					&& (this.appConf.getConfigFile().getParent() != null)
					&& this.workFile(this.appConf.getConfigFile())
					&& ((this.appConf.getShowConfirmation() && this.confirmationWorker(this.appConf.getRootPath())) || !this.appConf
							.getShowConfirmation())) {
				if(this.appConf.getIsDebug()) {
					this.printStruct(this.struct, "", Boolean.TRUE);
				}
				this.workStruct(this.struct, this.appConf.getRootPath());
			} else {
				System.out.println(PropertiesReader.getInstance().getProperties("application.output.wrongfile"));
			}
		}
	}

	/**
	 * 
	 * Generator constructor.
	 * 
	 * @param struct
	 *            Struct
	 * @param appConf
	 *            FolderGenCLIConf
	 */
	public Generator(final Struct struct, final FolderGenCLIConf appConf) {
		this.struct = struct;
		this.appConf = appConf;

		this.lastLayer = 0;

		this.initGenerator();

		if((this.appConf.getShowConfirmation() && this.confirmationWorker(this.appConf.getRootPath()))
				|| !this.appConf.getShowConfirmation()) {
			if(this.appConf.getIsDebug()) {
				this.printStruct(this.struct, "", Boolean.TRUE);
			}
			this.workStruct(this.struct, this.appConf.getRootPath());
		}
	}

	/**
	 * Generator constructor.
	 * 
	 * @param isDebug
	 *            Boolean
	 * @param showConfirmation
	 *            Boolean
	 * @param usePlugins
	 *            Boolean
	 */
	public Generator(final Boolean isDebug, final Boolean showConfirmation, final Boolean usePlugins) {
		this.appConf = new FolderGenCLIConf();
		this.appConf.setShowConfirmation(showConfirmation);
		this.appConf.setUsePlugins(usePlugins);

		this.lastLayer = 0;
		this.initGenerator();
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
		Generator.fgpWorker.put(new FolderWorker(), true);
		Generator.fgpWorker.put(new FileWorker(), true);
		Generator.fgpWorker.put(new CopyWorker(), true);
		Generator.fgpWorker.put(new DefaultContentWorker(), true);
		Generator.fgpWorker.put(new ZipWorker(), true);
		Generator.fgpWorker.put(new DeleteWorker(), false);

		// Load plugins if needed
		if(this.appConf.getUsePlugins()) {
			this.loadPlugins();
		}

		// Add fileTypes

		for(Map.Entry<FolderGenPlugin, Boolean> item : Generator.fgpWorker.entrySet()) {
			FolderGenPlugin plugin = item.getKey();
			if(item.getValue() && (plugin.getInfoMapValue(IFolderGenPlugin.INFO_FILEMARKER) != null)) {
				if(this.appConf.getIsDebug()) {
					System.out.println(PropertiesReader.getInstance().getProperties("application.debug.filetype.added")
							+ plugin.getInfoMapValue(IFolderGenPlugin.INFO_FILEMARKER));
					System.out.println("");
					for(Map.Entry<Integer, Object> debugItem : plugin.getInfoMap().entrySet()) {
						System.out.println("plugin.getInfoMap() debugItem: " + debugItem.getKey() + " - " + debugItem.getValue());
					}
				}

				Generator.fileTypes.add(new FileType((String) plugin.getInfoMapValue(IFolderGenPlugin.INFO_FILEMARKER),
						(String) plugin.getInfoMapValue(IFolderGenPlugin.INFO_INFOMARKER), plugin.getInfoMap().containsKey(
								IFolderGenPlugin.INFO_ADDITIONALKEYS) ? new ArrayList<String>(Arrays.asList(((String) plugin
								.getInfoMapValue(IFolderGenPlugin.INFO_ADDITIONALKEYS)).split(";"))) : new ArrayList<String>()));
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
	public Boolean workFile(final File configFile) {
		try {
			// Buffered reader reads the file
			this.configFileReader = new BufferedReader(new FileReader(configFile));
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
		} catch(IOException e) {
			if(this.appConf.getIsDebug()) {
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
		for(Map.Entry<FolderGenPlugin, Boolean> item : Generator.fgpWorker.entrySet()) {
			FolderGenPlugin plugin = item.getKey();
			if(item.getValue()
					&& (plugin.getPluginType() != IFolderGenPlugin.PLUGINTYPE_CONFEXTENSION_CONTENT)
					&& basicInfo.get(IFolderGenPlugin.BASICINFO_FILEMARKER).trim().equalsIgnoreCase(
							(String) plugin.getInfoMapValue(IFolderGenPlugin.INFO_FILEMARKER))) {

				if(this.appConf.getIsDebug()) {
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
				additionalInfo.put("filetype", (String) plugin.getInfoMapValue(IFolderGenPlugin.INFO_FILEMARKER));
				if(plugin.getPluginType() == IFolderGenPlugin.PLUGINTYPE_CONFEXTENSION_FOLDER) {
					additionalInfo.put("folder", Boolean.toString(Boolean.TRUE));
				}

				if(plugin.getAdditionlInfo(basicInfo) != null) {
					additionalInfo.putAll(plugin.getAdditionlInfo(basicInfo));
				}

				this.workLine(layer, plugin.getItemTitle(basicInfo), additionalInfo);
				this.lastLayer = layer;
				break;
			} else if(this.appConf.getIsDebug()) {
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
		for(Map.Entry<FolderGenPlugin, Boolean> item : Generator.fgpWorker.entrySet()) {
			FolderGenPlugin plugin = item.getKey();
			if(item.getValue()
					&& (plugin.getPluginType() == IFolderGenPlugin.PLUGINTYPE_CONFEXTENSION_CONTENT)
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
			} else if(this.appConf.getIsDebug()) {
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
		for(int len = Generator.fileTypes.size(), i = 0; i < len; i++) {
			regexpTypeStr += "\\" + Generator.fileTypes.get(i).getFilemarker();
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

					if(this.appConf.getIsDebug()) {
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
		if(this.appConf.getIsDebug()) {
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
		if(this.appConf.getIsDebug()) {
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
		if(this.appConf.getIsDebug()) {
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
		if(this.appConf.getIsDebug()) {
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
				if(this.appConf.getIsDebug()) {
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
				if(this.appConf.getIsDebug()) {
					e.printStackTrace();
				}
			} catch(IOException e) {
				if(this.appConf.getIsDebug()) {
					e.printStackTrace();
				}
			}
			if(this.appConf.getUsePlugins() && (Generator.fgpContentReplacer != null)) {
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
				if(this.appConf.getIsDebug()) {
					System.out.println("Plugin: " + plugin.getInfoMapValue(IFolderGenPlugin.INFO_TITLE));
					System.out.println("PluginType: " + plugin.getPluginType());
				}
				if(plugin.getPluginType() != null) {
					if((plugin.getPluginType() >= 200) && (plugin.getPluginType() < 300)) {
						Generator.fgpWorker.put(plugin, true);
					} else if((plugin.getPluginType() >= 100) && (plugin.getPluginType() < 200)) {
						Generator.fgpContentReplacer.add(plugin);
					}
				} else {
					if(this.appConf.getIsDebug()) {
						System.out.println("No plugin type");
					}
				}
			}

		} catch(Exception e) {
			if(this.appConf.getIsDebug()) {
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
	@SuppressWarnings("unchecked")
	private void workStruct(final Struct struct, final File rootFolder) {
		for(int len = struct.size(), i = 0; i < len; i++) {
			// get additional data from structItem
			HashMap<String, String> tempAdditionalData = struct.get(i).getAdditionalData();
			if(tempAdditionalData.containsKey("type") && (tempAdditionalData.get("type") != null)) {
				for(Map.Entry<FolderGenPlugin, Boolean> item : Generator.fgpWorker.entrySet()) {
					FolderGenPlugin plugin = item.getKey();
					if(item.getValue()
							&& (plugin.getInfoMap() != null)
							&& (((String) plugin.getInfoMapValue(IFolderGenPlugin.INFO_INFOMARKER)) != null)
							&& ((String) plugin.getInfoMapValue(IFolderGenPlugin.INFO_INFOMARKER))
									.equalsIgnoreCase(tempAdditionalData.get("type"))) {

						if(((plugin.getInfoMap() != null) && (plugin.getInfoMapValue(IFolderGenPlugin.INFO_CONTENTREPLACE) != null))
								&& (Boolean) plugin.getInfoMapValue(IFolderGenPlugin.INFO_CONTENTREPLACE)) {
							tempAdditionalData.put("content", this
									.replaceMarker(struct.get(i), tempAdditionalData.get("content")));
						}
						ArrayList<String> workerChain = new ArrayList<String>();
						workerChain.add(plugin.getClass().getSimpleName());

						HashMap<String, Object> workerMap = new HashMap<String, Object>();
						workerMap.put("additionalData", tempAdditionalData);
						workerMap.put("rootFolder", rootFolder);
						workerMap.put("name", struct.get(i).getName());
						workerMap.put("chain", workerChain);

						// no for each - with for-loop you can modify the workerChain
						for(int j = 0; j < workerChain.size(); j++) {
							try {
								FolderGenPlugin p = null;
								for(Map.Entry<FolderGenPlugin, Boolean> myItem : Generator.fgpWorker.entrySet()) {
									if(myItem.getKey().getClass().getSimpleName().equalsIgnoreCase(workerChain.get(j))) {
										Class<FolderGenPlugin> c = (Class<FolderGenPlugin>) Class.forName(myItem.getKey()
												.getClass().getCanonicalName());
										p = c.newInstance();
										break;
									}
								}

								if(this.appConf.getIsDebug()) {
									System.out.println("chainitem: " + p.getInfoMap().get(IFolderGenPlugin.INFO_TITLE));
									for(Map.Entry<String, Object> debugItem : workerMap.entrySet()) {
										System.out
												.println("item-in-chaine: " + debugItem.getKey() + " - " + debugItem.getValue());
									}
								}

								p.doWork(workerMap);
							} catch(ClassNotFoundException e) {
								e.printStackTrace();
							} catch(java.lang.IllegalAccessException e) {
							} catch(java.lang.InstantiationException e) {
							}

						}
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
	public void printStruct(final Struct struct, final String seperator, final Boolean showAll) {
		for(int len = struct.size(), i = 0; i < len; i++) {
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

	/**
	 * Returns a string that represents a foldergenconf-file.
	 * 
	 * @param struct
	 *            Struct
	 * @param seperator
	 *            String
	 * @param structStr
	 *            String
	 * @return String
	 */
	public static String getStringFromStruct(final Struct struct, final String seperator, String structStr) {
		for(int len = struct.size(), i = 0; i < len; i++) {
			String itemName = struct.get(i).getName();
			if(itemName.equals("") && (struct.get(i).getAdditionalData().containsKey("src"))
					&& !struct.get(i).getAdditionalData().get("src").equals("")) {
				itemName = struct.get(i).getAdditionalData().get("src");
			} else if((struct.get(i).getAdditionalData().containsKey("src"))
					&& !struct.get(i).getAdditionalData().get("src").equals("")) {
				itemName = itemName + " -> " + struct.get(i).getAdditionalData().get("src");
			}
			// add struct to string
			structStr = structStr + "\n" + seperator + struct.get(i).getAdditionalData().get("filetype") + " " + itemName;

			// read substructs
			if((struct.get(i).getSubStruct() != null) && (struct.get(i).getSubStruct().size() > 0)) {
				structStr = Generator.getStringFromStruct(struct.get(i).getSubStruct(), seperator + "\t", structStr);
			}
		}
		return structStr;
	}

	/**
	 * Get struct from filesystem and returns new struct.
	 * 
	 * @param directory
	 *            File
	 * @return Struct
	 */
	public static Struct getStructFromFilesystem(final File directory) {
		if(directory.exists() && directory.isDirectory()) {
			return Generator.workFileItem(directory, null);
		}
		return null;
	}

	/**
	 * Returns a struct with the new created struct from JTree.
	 * 
	 * @param path
	 *            File
	 * @param lastItem
	 *            StructItem
	 * @return Struct
	 */
	private static Struct workFileItem(final File path, final StructItem lastItem) {
		Struct tempStruct = new Struct();
		HashMap<String, String> tempAdditionalInfo = new HashMap<String, String>();
		FolderGenItem tempItem = new FolderGenItem(path.getName(), tempAdditionalInfo);
		StructItem tempStructItem = new StructItem(tempItem.getTitle(), tempItem.getAdditionalData(), lastItem);

		if(path.isDirectory()) {
			tempItem.getAdditionalData().put("filetype", "+");
			tempItem.getAdditionalData().put("type", "folder");
			File[] files = path.listFiles();
			if(files != null) {
				for(File file : files) {
					tempStructItem.getSubStruct().addAll(Generator.workFileItem(file, tempStructItem)); // ruft sich selbst auf
				}
			}
		} else {
			tempItem.getAdditionalData().put("filetype", "-");
			tempItem.getAdditionalData().put("type", "file");
		}
		tempStruct.add(tempStructItem);
		return tempStruct;
	}

	/**
	 * @return the appConf
	 */
	public synchronized final FolderGenCLIConf getAppConf() {
		return this.appConf;
	}

	/**
	 * @param appConf
	 *            the appConf to set
	 */
	public synchronized final void setAppConf(final FolderGenCLIConf appConf) {
		this.appConf = appConf;
	}

	/**
	 * @return the struct
	 */
	public synchronized final Struct getStruct() {
		return this.struct;
	}

	/**
	 * @return the filetypes
	 */
	public static synchronized final ArrayList<FileType> getFiletypes() {
		return Generator.fileTypes;
	}

}
