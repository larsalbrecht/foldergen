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
package com.lars_albrecht.foldergen.plugin.classes;

import java.util.HashMap;

import com.lars_albrecht.foldergen.plugin.interfaces.IFolderGenPlugin;

/**
 * FolderGenPlugin is the base class for all plugins (workers and replacers).
 * 
 * @author lalbrecht
 * @version 1.0.0.0
 */
public abstract class FolderGenPlugin implements IFolderGenPlugin {

	/**
	 * Fill this array to return plugin informations. The integer is a static var of the IFolderGenPlugin-Interface.
	 */
	protected HashMap<Integer, Object> infoMap = new HashMap<Integer, Object>();

	/**
	 * Returns the infoMap.
	 * 
	 * @return HashMap<Integer, Object>
	 */
	public HashMap<Integer, Object> getInfoMap() {
		return this.infoMap;
	}

	/**
	 * Returns a value from infoMap as object. The key is is a static var of the IFolderGenPlugin-Interface.
	 * 
	 * @param key
	 *            Integer
	 * @return Object
	 */
	public Object getInfoMapValue(final Integer key) {
		return this.infoMap.get(key);
	}
}
