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
package com.lars_albrecht.foldergen.gui.tree.components;

import javax.swing.JTree;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeSelectionModel;

import com.lars_albrecht.foldergen.core.helper.properies.PropertiesReader;
import com.lars_albrecht.foldergen.gui.tree.FolderGenTreeController;
import com.lars_albrecht.foldergen.gui.tree.helper.FolderGenMutableTreeNode;

/**
 * @author lalbrecht
 * 
 */
@SuppressWarnings("serial")
public class Tree extends JTree {

	private FolderGenTreeController controller = null;

	private DefaultTreeModel dtmTreeModel = null;
	private FolderGenMutableTreeNode rootNode = null;

	/**
	 * Constructor of Tree.
	 * 
	 * @param controller
	 *            FolderGenTreeController
	 */
	public Tree(final FolderGenTreeController controller) {
		super();
		this.controller = controller;
		this.rootNode = new FolderGenMutableTreeNode(PropertiesReader.getInstance()
				.getProperties("application.gui.tree.rootnode"), Boolean.TRUE);

		this.dtmTreeModel = new DefaultTreeModel(this.rootNode);

		this.setModel(this.dtmTreeModel);
		this.setEditable(Boolean.FALSE);

		this.setRootVisible(Boolean.TRUE);
		this.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		this.addTreeSelectionListener(this.controller);

	}

	/**
	 * @return the dtmTreeModel
	 */
	public synchronized final DefaultTreeModel getDtmTreeModel() {
		return this.dtmTreeModel;
	}

	/**
	 * @param dtmTreeModel
	 *            the dtmTreeModel to set
	 */
	public synchronized final void setDtmTreeModel(final DefaultTreeModel dtmTreeModel) {
		this.dtmTreeModel = dtmTreeModel;
	}

	/**
	 * @return the rootNode
	 */
	public synchronized final FolderGenMutableTreeNode getRootNode() {
		return this.rootNode;
	}

	/**
	 * @param rootNode
	 *            the rootNode to set
	 */
	public synchronized final void setRootNode(final FolderGenMutableTreeNode rootNode) {
		this.rootNode = rootNode;
	}

}
