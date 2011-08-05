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

	public CopyWorker() {
		this.infoMap.put(IFolderGenPlugin.INFO_TITLE, "CopyWorker");
		this.infoMap.put(IFolderGenPlugin.INFO_FILEMARKER, "~");
		this.infoMap.put(IFolderGenPlugin.INFO_INFOMARKER, "copy");
		this.infoMap.put(IFolderGenPlugin.INFO_ADDITIONALKEYS, "src");
	}

	@Override
	public HashMap<String, Object> doWork(final HashMap<String, Object> workerMap) {
		@SuppressWarnings("unchecked")
		HashMap<String, String> tempAdditionalData = (HashMap<String, String>) workerMap.get("additionalData");
		File rootFolder = (File) workerMap.get("rootFolder");
		String name = (String) workerMap.get("name");
		this.workGetCopy(rootFolder, tempAdditionalData, name);

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
	private void workGetCopy(final File rootFolder, final HashMap<String, String> tempAdditionalData, final String name) {
		if(tempAdditionalData.get("src") != null) {
			if(tempAdditionalData.get("src").startsWith("http://") || tempAdditionalData.get("src").startsWith("https://")) {
				try {
					File f = new File(rootFolder.getAbsolutePath() + File.separator + name);
					if(!f.exists()) {
						URL source = new URL(tempAdditionalData.get("src"));

						String content = Utilities.getFileContentFromWeb(source);
						f.createNewFile();
						FileOutputStream fos = new FileOutputStream(f);
						for(int len = content.length(), j = 0; j < len; j++) {
							fos.write((byte) content.charAt(j));
						}
						fos.close();
					}
				} catch(MalformedURLException e) {
				} catch(ConnectException e) {
				} catch(IOException e) {
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
				}
			}
		}

	}

	@Override
	public String getItemTitle(final HashMap<Integer, String> basicInfo) {
		return basicInfo.get(IFolderGenPlugin.BASICINFO_FILETITLE).trim().substring(0,
				basicInfo.get(IFolderGenPlugin.BASICINFO_FILETITLE).trim().indexOf("->")).trim();
	}

	@Override
	public HashMap<String, String> getAdditionlInfo(final HashMap<Integer, String> basicInfo) {
		HashMap<String, String> tempMap = new HashMap<String, String>();
		tempMap.put("src", basicInfo.get(IFolderGenPlugin.BASICINFO_FILETITLE).trim().substring(
				basicInfo.get(IFolderGenPlugin.BASICINFO_FILETITLE).trim().indexOf("->") + 2).trim());
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
