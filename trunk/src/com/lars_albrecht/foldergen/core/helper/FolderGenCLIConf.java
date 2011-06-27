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
package com.lars_albrecht.foldergen.core.helper;

import java.io.File;

/**
 * @author lalbrecht
 * @version 1.0.5.0
 * 
 */
public final class FolderGenCLIConf {

	private File file = null;
	private Boolean isGui = null;
	private Boolean isDebug = null;

	public FolderGenCLIConf() {
		this.file = null;
		this.isGui = Boolean.FALSE;
		this.isDebug = Boolean.FALSE;
	}

	/**
	 * @return the file
	 */
	public synchronized final File getFile() {
		return this.file;
	}

	/**
	 * @param file
	 *            the file to set
	 */
	public synchronized final void setFile(final File file) {
		this.file = file;
	}

	/**
	 * @return the isGui
	 */
	public synchronized final Boolean getIsGui() {
		return this.isGui;
	}

	/**
	 * @param isGui
	 *            the isGui to set
	 */
	public synchronized final void setIsGui(final Boolean isGui) {
		this.isGui = isGui;
	}

	/**
	 * @return the isDebug
	 */
	public synchronized final Boolean getIsDebug() {
		return this.isDebug;
	}

	/**
	 * @param isDebug
	 *            the isDebug to set
	 */
	public synchronized final void setIsDebug(final Boolean isDebug) {
		this.isDebug = isDebug;
	}

}
