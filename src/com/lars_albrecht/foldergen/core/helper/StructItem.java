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

import java.util.HashMap;

/**
 * StructItem is a special item for the Struct. The StructItem contains a name (String), a struct (Struct), additional data (Object) and a structItem (StructItem). The StructItem (parentStructItem) is
 * null or the parent item.
 * 
 * @author lalbrecht
 * @version 1.0.0.0
 */
public class StructItem {

	private String name = null;
	private Struct subStruct = null;
	private Object additionalData = null;
	private StructItem parentStructItem = null;

	/**
	 * Constructor with params "name" and "parentStructItem".
	 * 
	 * @param name
	 *            String
	 * @param parentStructItem
	 *            StructItem
	 */
	public StructItem(final String name, final StructItem parentStructItem) {
		this.name = name;
		this.subStruct = new Struct();
		this.parentStructItem = parentStructItem;
	}

	/**
	 * Constructor with params "name", "additionalData" and "parentStructItem".
	 * 
	 * @param name
	 *            String
	 * @param additionalData
	 *            Object
	 * @param parentStructItem
	 *            StructItem
	 */
	public StructItem(final String name, final Object additionalData, final StructItem parentStructItem) {
		this.name = name;
		this.additionalData = additionalData;
		this.subStruct = new Struct();
		this.parentStructItem = parentStructItem;
	}

	/**
	 * @return the name
	 */
	public synchronized final String getName() {
		return this.name;
	}

	/**
	 * @param name
	 *            the name to set
	 */
	public synchronized final void setName(final String name) {
		this.name = name;
	}

	/**
	 * @return the subStruct
	 */
	public synchronized final Struct getSubStruct() {
		return this.subStruct;
	}

	/**
	 * @param subStruct
	 *            the subStruct to set
	 */
	public synchronized final void setSubStruct(final Struct subStruct) {
		this.subStruct = subStruct;
	}

	/**
	 * @return the additionalData
	 */
	@SuppressWarnings("unchecked")
	public synchronized final HashMap<String, String> getAdditionalData() {
		return (HashMap<String, String>) this.additionalData;
	}

	/**
	 * @param additionalData
	 *            the additionalData to set
	 */
	public synchronized final void setAdditionalData(final Object additionalData) {
		this.additionalData = additionalData;
	}

	/**
	 * @return the parentStructItem
	 */
	public synchronized final StructItem getParentStructItem() {
		return this.parentStructItem;
	}

	/**
	 * @param parentStructItem
	 *            the parentStructItem to set
	 */
	public synchronized final void setParentStructItem(final StructItem parentStructItem) {
		this.parentStructItem = parentStructItem;
	}
}
