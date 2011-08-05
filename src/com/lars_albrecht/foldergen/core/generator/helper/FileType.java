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
package com.lars_albrecht.foldergen.core.generator.helper;

import java.util.ArrayList;

/**
 * @author lalbrecht
 * @version 1.0.0.0
 * 
 */
public class FileType {

	private String filemarker = null;
	private String infomarker = null;
	private ArrayList<String> additionalKeys = null;

	public FileType(final String filemarker, final String infomarker, final ArrayList<String> additionalKeys) {
		super();
		this.filemarker = filemarker;
		this.infomarker = infomarker;
		this.additionalKeys = additionalKeys;
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

	@Override
	public String toString() {
		return this.getInfomarker();
	}

	/**
	 * @return the additionalKeys
	 */
	public synchronized final ArrayList<String> getAdditionalKeys() {
		return this.additionalKeys;
	}

	/**
	 * @param additionalKeys
	 *            the additionalKeys to set
	 */
	public synchronized final void setAdditionalKeys(final ArrayList<String> additionalKeys) {
		this.additionalKeys = additionalKeys;
	}

}
