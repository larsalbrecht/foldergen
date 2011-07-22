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
package com.lars_albrecht.foldergen.core.generator.worker;

import java.util.HashMap;

import com.lars_albrecht.foldergen.plugin.classes.FolderGenPlugin;
import com.lars_albrecht.foldergen.plugin.interfaces.IFolderGenPlugin;

public class DefaultContentWorker extends FolderGenPlugin {

	public DefaultContentWorker() {
		this.infoMap.put(IFolderGenPlugin.INFO_TITLE, "DefaultContentWorker");
		this.infoMap.put(IFolderGenPlugin.INFO_CONTENTREPLACE, Boolean.TRUE);
		this.infoMap.put(IFolderGenPlugin.INFO_CONTENTSTARTMARKER, "(((");
		this.infoMap.put(IFolderGenPlugin.INFO_CONTENTENDMARKER, ")))");
	}

	@Override
	public HashMap<String, Object> doWork(final HashMap<String, Object> workerMap) {
		HashMap<String, Object> tempWorkerMap = new HashMap<String, Object>();
		tempWorkerMap.put("content", workerMap.get("content"));
		return tempWorkerMap;
	}

	@Override
	public HashMap<String, String> getAdditionlInfo(final String[] basicInfo) {
		return null;
	}

	@Override
	public String getItemTitle(final String[] basicInfo) {
		return null;
	}

	@Override
	public Integer getPluginType() {
		return IFolderGenPlugin.PLUGINTYPE_CONFEXTENSION_CONTENT;
	}

	@Override
	public String replaceContent(final String content) {
		return null;
	}

}
