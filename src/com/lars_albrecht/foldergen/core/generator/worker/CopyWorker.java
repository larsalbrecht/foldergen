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
package com.lars_albrecht.foldergen.core.generator.worker;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.ConnectException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

import com.lars_albrecht.foldergen.helper.Utilities;
import com.lars_albrecht.foldergen.plugin.classes.FolderGenPlugin;
import com.lars_albrecht.foldergen.plugin.interfaces.IFolderGenPlugin;

/**
 * Worker to copy files from filesystem or download from an url.
 * 
 * Title: CopyWorker<br>
 * Filemarker: ~<br>
 * Infomarker: copy<br>
 * 
 * @author lalbrecht
 * 
 */
public class CopyWorker extends FolderGenPlugin {

	private String name = null;

	public CopyWorker() {
		this.infoMap.put(IFolderGenPlugin.INFO_TITLE, "CopyWorker");
		this.infoMap.put(IFolderGenPlugin.INFO_FILEMARKER, "~");
		this.infoMap.put(IFolderGenPlugin.INFO_INFOMARKER, "copy");
		this.infoMap.put(IFolderGenPlugin.INFO_ADDITIONALKEYS, "src");
	}

	@SuppressWarnings("unchecked")
	@Override
	public HashMap<String, Object> doWork(final HashMap<String, Object> workerMap) {
		HashMap<String, String> tempAdditionalData = (HashMap<String, String>) workerMap.get("additionalData");

		File rootFolder = (File) workerMap.get("rootFolder");
		String name = (String) workerMap.get("name");
		this.workGetCopy(rootFolder, tempAdditionalData, name, (ArrayList<String>) workerMap.get("chain"));
		if(this.name != null) {
			workerMap.put("name", (name.trim().length() > 0 ? name.trim() : this.name));
			tempAdditionalData.put("src", rootFolder.getAbsolutePath() + File.separator
					+ (name.trim().length() > 0 ? name.trim() : this.name));

		}

		return null;
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
	private void workGetCopy(final File rootFolder, final HashMap<String, String> tempAdditionalData, final String name,
			final ArrayList<String> chain) {
		String sourceName = null;
		if(tempAdditionalData.get("src") != null) {
			if(tempAdditionalData.get("src").startsWith("http://") || tempAdditionalData.get("src").startsWith("https://")) {
				try {
					if((name.trim().length() == 0) || !new File(rootFolder.getAbsolutePath() + File.separator + name).exists()) {
						// Create URL
						URL source = new URL(tempAdditionalData.get("src"));
						sourceName = source.getFile().replaceAll("^([\\W])", "");
						sourceName = (source.getQuery() != null ?  source.getFile().substring(0, source.getFile().indexOf(source.getQuery()) - 1) : sourceName);
						// Create file
						File f = new File(rootFolder.getAbsolutePath() + File.separator
								+ (name.trim().length() > 0 ? name.trim() : sourceName));
						f.createNewFile();

						Utilities.getFileContentFromWeb(source, new FileOutputStream(f));

					}
				} catch(MalformedURLException e) {
				} catch(ConnectException e) {
				} catch(IOException e) {
				}
			} else {
				try {
					// file
					File source = new File(tempAdditionalData.get("src"));
					sourceName = source.getName();
					if(source.exists() && source.isFile()) {
						Utilities.copyFile(source, new File(rootFolder.getAbsolutePath() + File.separator
								+ (name.trim().length() > 0 ? name.trim() : sourceName)));
					} else if(source.exists() && source.isDirectory()) {
						Utilities.copyDir(source, new File(rootFolder.getAbsolutePath() + File.separator + name));
					}
				} catch(IOException e) {
				}
			}
			if((name.trim().length() == 0) && (sourceName.endsWith(".zip") || sourceName.endsWith(".jar"))) {
				this.name = sourceName;
				chain.add("ZipWorker");
				chain.add("DeleteWorker");
			}
		}

	}

	@Override
	public String getItemTitle(final HashMap<Integer, String> basicInfo) {
		return (basicInfo.get(IFolderGenPlugin.BASICINFO_FILETITLE).trim().indexOf("->") > -1) ? basicInfo.get(
				IFolderGenPlugin.BASICINFO_FILETITLE).trim().substring(0,
				basicInfo.get(IFolderGenPlugin.BASICINFO_FILETITLE).trim().indexOf("->")).trim() : "";
	}

	@Override
	public HashMap<String, String> getAdditionlInfo(final HashMap<Integer, String> basicInfo) {
		HashMap<String, String> tempMap = new HashMap<String, String>();
		tempMap.put("src", (basicInfo.get(IFolderGenPlugin.BASICINFO_FILETITLE).trim().indexOf("->") > -1) ? basicInfo.get(
				IFolderGenPlugin.BASICINFO_FILETITLE).trim().substring(
				basicInfo.get(IFolderGenPlugin.BASICINFO_FILETITLE).trim().indexOf("->") + 2).trim() : basicInfo.get(
				IFolderGenPlugin.BASICINFO_FILETITLE).trim());
		return tempMap;
	}

	@Override
	public Integer getPluginType() {
		return IFolderGenPlugin.PLUGINTYPE_CONFEXTENSION_FILE;
	}

	@Override
	public String replaceContent(final String content) {
		return null;
	}
}
