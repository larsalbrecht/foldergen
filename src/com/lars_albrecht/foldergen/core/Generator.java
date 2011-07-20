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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.lars_albrecht.foldergen.core.generator.worker.CopyWorker;
import com.lars_albrecht.foldergen.core.generator.worker.DefaultContentWorker;
import com.lars_albrecht.foldergen.core.generator.worker.FileWorker;
import com.lars_albrecht.foldergen.core.generator.worker.FolderWorker;
import com.lars_albrecht.foldergen.core.generator.worker.interfaces.IWorker;
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
	private final static ArrayList<IWorker> fgcMapper = new ArrayList<IWorker>();
	private final static ArrayList<String> fileTypes = new ArrayList<String>();

	private Boolean isDebug = false;
	private File rootPath = null;
	private Boolean showConfirmation = false;
	private Boolean usePlugins = false;
	private List<CoreMarkerReplacer> pluginCollection = null;

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
	public Generator(final File rootPath, final File configFile, final Boolean isDebug, final Boolean showConfirmation, final Boolean usePlugins) {
		this.isDebug = isDebug;
		this.showConfirmation = showConfirmation;
		this.usePlugins = usePlugins;

		this.initGenerator();

		if (usePlugins) {
			this.loadPlugins();
		}

		this.rootPath = (rootPath != null ? rootPath : new File(configFile.getParent()));
		if (this.isDebug) {
			System.out.println(PropertiesReader.getInstance().getProperties("application.debug.choosedrootpath") + this.rootPath.getAbsolutePath() + "\n");
		}

		// If config file exists and is a file and parent != null
		if (configFile.exists() && configFile.isFile() && (configFile.getParent() != null)) {
			this.workFile(configFile);
		} else {
			System.out.println(PropertiesReader.getInstance().getProperties("application.output.wrongfile"));
		}
	}

	private void initGenerator() {
		Generator.fgcMapper.add(new FolderWorker());
		Generator.fgcMapper.add(new FileWorker());
		Generator.fgcMapper.add(new CopyWorker());
		Generator.fgcMapper.add(new DefaultContentWorker());

		for (IWorker w : Generator.fgcMapper) {
			if (w.getFileMarker() != null) {
				fileTypes.add(w.getFileMarker());
			}
		}
	}

	/**
	 * Works the file. Parse the given config file and generates the files and
	 * folders recursivly.
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
			while ((line = in.readLine()) != null) {
				// Split line
				String[] basicInfoArr = line.trim().split("\\s", 2);

				String typeStr = null;
				Matcher m = Pattern.compile("([\\s]{0,})([\\+\\-\\~]{1})([\\s]{1})").matcher(line);
				if (m.find() && (m.groupCount() > 0)) {
					typeStr = m.group(1);
				}

				if ((typeStr != null) && (basicInfoArr != null) && (basicInfoArr.length == 2) && (basicInfoArr[0] != null) && (basicInfoArr[1] != null)) {
					// No content
					for (IWorker w : Generator.fgcMapper) {
						if (!w.isContent() && basicInfoArr[0].trim().equalsIgnoreCase(w.getFileMarker())) {
							if (this.isDebug) {
								System.out.println("is " + w.getMarker() + " (" + w.getFileMarker() + ")");
							}
							layer = typeStr.split("\\s", -1).length - 1;
							if (layer.equals("")) {
								layer = 0;
							}
							HashMap<String, String> additionalInfo = new HashMap<String, String>();
							String itemTitle = null;

							additionalInfo.put("type", w.getMarker());
							if (w.isFolder()) {
								additionalInfo.put("folder", Boolean.toString(Boolean.TRUE));
							}

							itemTitle = w.getItemTitle(basicInfoArr);

							if (w.getAdditionlInfo(basicInfoArr) != null) {
								additionalInfo.putAll(w.getAdditionlInfo(basicInfoArr));
							}

							Object[] workedLine = this.workLine(layer, lastLayer, itemTitle, struct, lastItem, additionalInfo);
							struct = (Struct) workedLine[0];
							lastItem = (StructItem) workedLine[1];
							lastLayer = layer;
							break;
						}
					}
				} else {
					// content
					for (IWorker w : Generator.fgcMapper) {
						if (w.isContent() && basicInfoArr[0].trim().equalsIgnoreCase(w.getContentStartMarker())) {
							String s = null;
							String content = "";
							// read content
							while (((s = in.readLine()) != null) && !(s.trim().equals(w.getContentEndMarker()))) {
								content += s.trim();
								content += "\r\n";
							}
							HashMap<String, Object> workerMap = new HashMap<String, Object>();
							workerMap.put("content", content);
							workerMap.put("lastItem", lastItem);

							content = (String) w.doWork(workerMap).get("content");
							if (w.replaceMarker()) {
								content = this.replaceMarker(lastItem, content);
							}

							// add content to (last)item
							lastItem.getAdditionalData().put("content", content);
							break;
						}
					}
				}
			}
			if ((this.showConfirmation && this.confirmationWorker(struct, basicRootFolder)) || !this.showConfirmation) {
				this.workStruct(struct, basicRootFolder);
			}

			if (this.isDebug) {
				this.printStruct(struct, "", Boolean.TRUE);
			}

		} catch (IOException e) {
			if (isDebug) {
				System.out.println(e.getMessage());
			}
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
			while ((hasToWrite == null) && ((inputLine = br.readLine()) != null)) {
				if (inputLine.equals("") || inputLine.equalsIgnoreCase("n") || inputLine.equalsIgnoreCase("н")) {
					hasToWrite = false;
				} else if (inputLine.equalsIgnoreCase("y") || inputLine.equalsIgnoreCase("j") || inputLine.equalsIgnoreCase("o") || inputLine.equalsIgnoreCase("Д")) {
					hasToWrite = true;
				}
				if (this.isDebug) {
					System.out.println("\n" + PropertiesReader.getInstance().getProperties("application.debug.confirmation.writenotice") + hasToWrite);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		if (hasToWrite) {
			if (this.isDebug) {
				System.out.println("\n" + PropertiesReader.getInstance().getProperties("application.debug.struct.work"));
			}
			System.out.println(PropertiesReader.getInstance().getProperties("application.debug.struct.work.create"));
			this.workStruct(struct, basicRootFolder);
		} else if (this.isDebug) {
			System.out.println(PropertiesReader.getInstance().getProperties("application.debug.struct.nowork.nocreate"));
			System.out.println("\n" + PropertiesReader.getInstance().getProperties("application.debug.struct.nowork"));
		}
		return true;
	}

	/**
	 * Works a line. Add structItem to struct with care of the indent. Returns
	 * struct and lastItem in the result Object-Array.
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
	private Object[] workLine(final Integer layer, final Integer lastLayer, final String itemTitle, final Struct struct, final StructItem lastItem, final HashMap<String, String> additionalInfo) {
		Object[] mulitpleResult = { struct, lastItem };
		if (layer.equals(0)) { // zero / root layer
			mulitpleResult[0] = this.workLayerZero(itemTitle, struct, lastItem, additionalInfo);
			mulitpleResult[1] = struct.get(struct.size() - 1);
		} else if (layer > lastLayer) { // layer down
			mulitpleResult[1] = this.workLayerDown(itemTitle, struct, lastItem, additionalInfo);
		} else if (layer < lastLayer) { // layer up
			mulitpleResult[1] = this.workLayerUp(itemTitle, struct, lastItem, additionalInfo, lastLayer, layer);
		} else if (layer.equals(lastLayer)) { // same layer
			mulitpleResult[1] = this.workLayerSame(itemTitle, struct, lastItem, additionalInfo);
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
	private Struct workLayerZero(final String name, final Struct struct, final StructItem lastItem, final HashMap<String, String> additionalInfo) {
		StructItem tempStructItem = null;
		if (this.isDebug) {
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
	private StructItem workLayerDown(final String name, final Struct struct, final StructItem lastItem, final HashMap<String, String> additionalInfo) {
		StructItem tempStructItem = null;
		if (this.isDebug) {
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
	private StructItem workLayerUp(final String name, final Struct struct, StructItem lastItem, final HashMap<String, String> additionalInfo, final Integer lastLayer, final Integer layer) {
		StructItem tempStructItem = null;
		if (this.isDebug) {
			System.out.println(PropertiesReader.getInstance().getProperties("application.debug.up") + name);
		}
		tempStructItem = new StructItem(name.trim(), additionalInfo, null);
		for (int i = 0; i <= lastLayer - layer; i++) {
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
	private StructItem workLayerSame(final String name, final Struct struct, final StructItem lastItem, final HashMap<String, String> additionalInfo) {
		StructItem tempStructItem = null;
		if (this.isDebug) {
			System.out.println(PropertiesReader.getInstance().getProperties("application.debug.equals") + name);
		}
		tempStructItem = new StructItem(name.trim(), additionalInfo, lastItem.getParentStructItem());
		lastItem.getParentStructItem().getSubStruct().add(tempStructItem);
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
	 * <li>${date.formatcurrent([Format FORMAT])} - returns the current
	 * formatted date/time</li>
	 * <li>${func.counter(VAR)} - counter for a VAR</li>
	 * <li>${func.counter(VAR|STARTNUMBER)} - counter for a VAR with startnumber
	 * STARTNUMBER</li>
	 * <li>${func.getfilecontent(filepath)} - return the content of the given
	 * file</li>
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
			while (matcher.find()) {
				content = matcher.replaceAll(Boolean.toString(gc.isLeapYear(Integer.parseInt(matcher.group(1)))));
			}

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

		// function markers
		try {
			pattern = Pattern.compile("(\\$\\{func\\.counter\\(([a-zA-Z]+)?(\\|[0-9]+)?\\)\\})");
			matcher = pattern.matcher(content);
			while (matcher.find()) {
				int i = 0;
				if (matcher.group(3) != null) {
					i = Integer.parseInt(matcher.group(3).substring(1));
					content = content.replaceFirst("(\\$\\{func.counter\\((" + matcher.group(2) + ")\\|" + i + "\\)\\})", Integer.toString(i));
					i++;
				}
				while (content.indexOf("${func.counter(" + matcher.group(2) + ")}") != -1) {
					content = content.replaceFirst("(\\$\\{func.counter\\((" + matcher.group(2) + ")\\)\\})", Integer.toString(i));
					i++;
				}
			}
			pattern = Pattern.compile("(\\$\\{func\\.getfilecontent\\((.+)?\\)\\})");
			matcher = pattern.matcher(content);
			while (matcher.find()) {
				while (content.indexOf("${func.getfilecontent(" + matcher.group(2) + ")}") != -1) {
					content = content.replaceFirst("(\\$\\{func\\.getfilecontent\\((\\Q" + matcher.group(2) + "\\E)?\\)\\})", Utilities.getFileContent(new File(matcher.group(2))));
				}
			}
		} catch (IllegalArgumentException e) {
			if (this.isDebug) {
				e.printStackTrace();
			}
		} catch (IOException e) {
			if (this.isDebug) {
				e.printStackTrace();
			}
		}
		if (this.usePlugins && (this.pluginCollection != null)) {
			content = this.replacePluginMarkers(content);
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
		for (CoreMarkerReplacer plugin : this.pluginCollection) {
			plugin.setContent(resultStr);
			resultStr = plugin.replaceContent();
		}
		return resultStr;
	}

	/**
	 * Load plugins.
	 * 
	 */
	private void loadPlugins() {
		try {
			PluginFinder pluginFinder = new PluginFinder();
			pluginFinder.search(System.getProperty("user.dir"));
			this.pluginCollection = pluginFinder.getPluginCollection();
		} catch (Exception e) {
			if (this.isDebug) {
				e.printStackTrace();
			}
		}
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
				for (IWorker w : Generator.fgcMapper) {
					HashMap<String, Object> workerMap = new HashMap<String, Object>();
					workerMap.put("additionalData", tempAdditionalData);
					workerMap.put("rootFolder", rootFolder);
					workerMap.put("name", struct.get(i).getName());
					if (w.getMarker().equalsIgnoreCase(tempAdditionalData.get("type"))) {
						w.doWork(workerMap);
						break;
					}
				}
			}
			if (struct.get(i).getSubStruct() != null) {
				// read substructure
				this.workStruct(struct.get(i).getSubStruct(), new File(rootFolder + File.separator + struct.get(i).getName()));
			}
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
		for (int i = 0; i < struct.size(); i++) {
			System.out.println(seperator + i + " - " + struct.get(i).getName() + (showAll ? (struct.get(i).getAdditionalData() != null ? " - " + struct.get(i).getAdditionalData() : "") : ""));
			if (struct.get(i).getSubStruct() != null) {
				this.printStruct(struct.get(i).getSubStruct(), seperator + "\t", showAll);
			}
		}

	}

}
