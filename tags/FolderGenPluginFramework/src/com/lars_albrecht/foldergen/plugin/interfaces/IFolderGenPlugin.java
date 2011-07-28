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
package com.lars_albrecht.foldergen.plugin.interfaces;

import java.util.HashMap;

/**
 * @author lalbrecht
 * @version 1.0.0.0
 */
public interface IFolderGenPlugin {

	/**
	 * Replace markers in files.
	 */
	final static Integer PLUGINTYPE_CONTENTEXTENSION_REPLACER = 100;

	/**
	 * Creats a file.
	 */
	final static Integer PLUGINTYPE_CONFEXTENSION_FILE = 200;

	/**
	 * Creats a folder.
	 */
	final static Integer PLUGINTYPE_CONFEXTENSION_FOLDER = 201;

	/**
	 * Creats a file with content.
	 */
	final static Integer PLUGINTYPE_CONFEXTENSION_CONTENT = 202;

	/**
	 * Plugintitle
	 */
	final static Integer INFO_TITLE = 0;

	/**
	 * Marker for the plugin in the foldergenconf (e.g.: "+", "-" [...]).
	 */
	final static Integer INFO_FILEMARKER = 1;

	/**
	 * Internal marker. Defines in a single word the function.
	 */
	final static Integer INFO_INFOMARKER = 2;

	/**
	 * In contentplugins, this is the start of file.
	 */
	final static Integer INFO_CONTENTSTARTMARKER = 3;

	/**
	 * In contentplugins, this is the end of file.
	 */
	final static Integer INFO_CONTENTENDMARKER = 4;

	/**
	 * If the content will be replaced in plugintype PLUGINTYPE_CONFEXTENSION_CONTENT.
	 */
	final static Integer INFO_CONTENTREPLACE = 5;

	/**
	 * The filetitle. Can be more than a title, but usually this is only the title.
	 */
	final static Integer BASICINFO_FILETITLE = 1;

	/**
	 * The filemarker. e.g.: +
	 */
	final static Integer BASICINFO_FILEMARKER = 2;

	/**
	 * CONFEXTENSION: You only need to return null. You became a HashMap with "additionalData" (HashMap), "rootFolder" (File) and "name" (String)<br>
	 * CONTENTEXTENSION: you need to return a HashMap with key "content" and became a HashMap with "content".
	 * 
	 * @param workerMap
	 * @return HashMap<String, Object>
	 */
	HashMap<String, Object> doWork(final HashMap<String, Object> workerMap);

	/**
	 * Returns additional info if return not null. basicInfo is a HashMap<Integer, String>. It contains the BASICINFO_* statics.
	 * 
	 * @param basicInfo
	 * @return HashMap<String, String>
	 */
	HashMap<String, String> getAdditionlInfo(final HashMap<Integer, String> basicInfo);

	/**
	 * Replaces the content for CONTENTEXTENSION-Plugins.
	 * 
	 * @param content
	 * @return String
	 */
	String replaceContent(final String content);

	/**
	 * Returns the title for the item in file. In basic plugins you return basicInfo.get(IFolderGenPlugin.BASICINFO_FILETITLE).trim();
	 * 
	 * @param basicInfo
	 * @return String
	 */
	String getItemTitle(final HashMap<Integer, String> basicInfo);

	/**
	 * Returns the plugin type. This is necessaray to load the plugin. You need to return one of the PLUGINTYPE... statics.
	 * 
	 * @return Integer
	 */
	Integer getPluginType();
}
