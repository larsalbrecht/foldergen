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
import java.util.HashMap;

import com.lars_albrecht.foldergen.plugin.classes.FolderGenPlugin;
import com.lars_albrecht.foldergen.plugin.interfaces.IFolderGenPlugin;

/**
 * Worker to create folders.
 * 
 * Title: FolderWorker<br>
 * Filemarker: +<br>
 * Infomarker: folder<br>
 * 
 * @author lalbrecht
 * @version 1.0.0.0
 * 
 */
public class FolderWorker extends FolderGenPlugin {

	public FolderWorker() {
		this.infoMap.put(IFolderGenPlugin.INFO_TITLE, "FolderWorker");
		this.infoMap.put(IFolderGenPlugin.INFO_FILEMARKER, "+");
		this.infoMap.put(IFolderGenPlugin.INFO_INFOMARKER, "folder");
	}

	@Override
	public HashMap<String, Object> doWork(final HashMap<String, Object> workerMap) {
		File rootFolder = (File) workerMap.get("rootFolder");
		String name = (String) workerMap.get("name");
		this.workFolder(rootFolder, name);
		return null;
	}

	@Override
	public String getItemTitle(final HashMap<Integer, String> basicInfo) {
		return basicInfo.get(IFolderGenPlugin.BASICINFO_FILETITLE);
	}

	@Override
	public HashMap<String, String> getAdditionlInfo(final HashMap<Integer, String> basicInfo) {
		return null;
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

	@Override
	public Integer getPluginType() {
		return IFolderGenPlugin.PLUGINTYPE_CONFEXTENSION_FOLDER;
	}

	@Override
	public String replaceContent(final String content) {
		return null;
	}

}
