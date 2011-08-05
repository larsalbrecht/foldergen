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
package com.lars_albrecht.foldergen.gui.tree.helper;

import javax.swing.tree.DefaultMutableTreeNode;

/**
 * @author lalbrecht
 * 
 */
@SuppressWarnings("serial")
public class FolderGenMutableTreeNode extends DefaultMutableTreeNode {

	protected Boolean isFolder = null;

	public FolderGenMutableTreeNode(final Object userObject) {
		super(userObject);
	}

	public FolderGenMutableTreeNode(final Object userObject, final Boolean isFolder) {
		super(userObject);
		this.isFolder = isFolder;
	}

	@Override
	public boolean isLeaf() {
		return this.isFolder == null ? super.isLeaf() : !this.isFolder;
	}

	/**
	 * @return the isFolder
	 */
	public synchronized final Boolean getIsFolder() {
		return this.isFolder;
	}

	/**
	 * @param isFolder
	 *            the isFolder to set
	 */
	public synchronized final void setIsFolder(final Boolean isFolder) {
		this.isFolder = isFolder;
	}

}
