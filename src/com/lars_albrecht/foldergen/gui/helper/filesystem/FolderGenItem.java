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
package com.lars_albrecht.foldergen.gui.helper.filesystem;

import java.util.HashMap;

/**
 * Represents an item for FolderGen. See it as a model.
 * 
 * @author lalbrecht
 * @version 1.0.0.0
 * 
 */
public class FolderGenItem {

	private String title = null;
	private String filemarker = null;
	private String infomarker = null;
	private HashMap<String, String> additionalData = null;

	public FolderGenItem(final String title, final String filemarker, final String infomarker,
			final HashMap<String, String> hashMap) {
		super();
		this.title = title;
		this.filemarker = filemarker;
		this.infomarker = infomarker;
		this.additionalData = hashMap;
	}

	public FolderGenItem(final String title, final String filemarker, final String infomarker) {
		super();
		this.title = title;
		this.filemarker = filemarker;
		this.infomarker = infomarker;
	}

	/**
	 * @return the title
	 */
	public synchronized final String getTitle() {
		return this.title;
	}

	/**
	 * @param title
	 *            the title to set
	 */
	public synchronized final void setTitle(final String title) {
		this.title = title;
	}

	/**
	 * @return the filemarker
	 */
	public synchronized final String getFilemarker() {
		return this.filemarker;
	}

	/**
	 * @param filemarker
	 *            the filemarker to set
	 */
	public synchronized final void setFilemarker(final String filemarker) {
		this.filemarker = filemarker;
	}

	/**
	 * @return the infomarker
	 */
	public synchronized final String getInfomarker() {
		return this.infomarker;
	}

	/**
	 * @param infomarker
	 *            the infomarker to set
	 */
	public synchronized final void setInfomarker(final String infomarker) {
		this.infomarker = infomarker;
	}

	/**
	 * @return the additionalData
	 */
	public synchronized final HashMap<String, String> getAdditionalData() {
		return this.additionalData;
	}

	/**
	 * @param additionalData
	 *            the additionalData to set
	 */
	public synchronized final void setAdditionalData(final HashMap<String, String> additionalData) {
		this.additionalData = additionalData;
	}

	@Override
	public String toString() {
		return this.title;
	}

}
