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
package com.lars_albrecht.foldergen.gui.tree;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeModel;

import com.lars_albrecht.foldergen.core.Generator;
import com.lars_albrecht.foldergen.core.generator.helper.FileType;
import com.lars_albrecht.foldergen.core.generator.helper.Struct;
import com.lars_albrecht.foldergen.core.generator.helper.StructItem;
import com.lars_albrecht.foldergen.gui.View;
import com.lars_albrecht.foldergen.gui.helper.filesystem.FolderGenItem;
import com.lars_albrecht.foldergen.gui.tree.components.AdditionalInfoPanel;
import com.lars_albrecht.foldergen.gui.tree.components.FolderGenTreeView;
import com.lars_albrecht.foldergen.gui.tree.components.InfoPanel;
import com.lars_albrecht.foldergen.gui.tree.components.Tree;
import com.lars_albrecht.foldergen.gui.tree.helper.FolderGenMutableTreeNode;

/**
 * This controlls the view for the tree.
 * 
 * @author lalbrecht
 * @version 1.0.0.0
 */
public class FolderGenTreeController implements TreeSelectionListener, ActionListener, ItemListener {

	private static FolderGenTreeController instance = new FolderGenTreeController();

	private InfoPanel infoPanel = null;
	private AdditionalInfoPanel additionalInfoPanel = null;
	private Tree tree = null;
	private FolderGenTreeView view = null;

	private File configFile = null;
	private File rootPath = null;

	private Generator generator = null;

	private static View parentView = null;

	private final Struct struct = null;

	public FolderGenTreeController() {
		this.infoPanel = new InfoPanel(this);
		this.tree = new Tree(this);
		this.view = new FolderGenTreeView(this);
		this.additionalInfoPanel = new AdditionalInfoPanel(this);

		this.generator = new Generator(true, false, false);

		this.addComponentsToView();
		this.fillTypeComboBox();
	}

	/**
	 * Shows the frame.
	 */
	public void showFrame() {
		this.view.showFrame();
	}

	/**
	 * Fills the combo box on the info panel.
	 */
	public void fillTypeComboBox() {
		this.infoPanel.getCbTypeValueModel().addElement("");
		for(FileType type : Generator.getFiletypes()) {
			this.infoPanel.getCbTypeValueModel().addElement(type);
		}

	}

	/**
	 * Generates the struct and add the struct to tree.
	 */
	public void fillTree() {
		if(this.configFile != null) {
			// create struct
			this.generator.workFile(this.configFile);
			// clear before adding
			this.tree.getRootNode().removeAllChildren();
			this.tree.getDtmTreeModel().reload();
			// add now
			this.addStructToTree(this.generator.getStruct(), this.tree.getRootNode());
		}
	}

	/**
	 * Add sturct to tree.
	 * 
	 * @param struct
	 *            Struct
	 */
	private void addStructToTree(final Struct struct, final FolderGenMutableTreeNode node) {
		for(int len = struct.size(), i = 0; i < len; i++) {
			FolderGenMutableTreeNode subNode = new FolderGenMutableTreeNode(new FolderGenItem(struct.get(i).getName(), struct
					.get(i).getAdditionalData().get("filetype"), struct.get(i).getAdditionalData().get("type"), struct.get(i)
					.getAdditionalData()));
			if(struct.get(i).getAdditionalData().containsKey("folder")
					&& Boolean.parseBoolean(struct.get(i).getAdditionalData().get("folder"))) {
				subNode.setIsFolder(Boolean.TRUE);
			}
			this.tree.getDtmTreeModel().insertNodeInto(subNode, node, node.getChildCount());
			if((struct.get(i).getSubStruct() != null) && (struct.get(i).getSubStruct().size() > 0)) {
				subNode.setIsFolder(Boolean.TRUE);
				this.addStructToTree(struct.get(i).getSubStruct(), subNode);
			}
		}
	}

	private void getStructFromTree() {
		TreeModel model = this.tree.getDtmTreeModel();
		Object root;
		if(model != null) {
			root = model.getRoot();
			this.workTreeItem(model, root, " ");
		} else {
			System.out.println("Tree is empty.");
		}
	}

	private void workTreeItem(final TreeModel model, final Object o, final String seperator) {
		Integer childCount = model.getChildCount(o);
		for(int i = 0; i < childCount; i++) {
			FolderGenMutableTreeNode child = (FolderGenMutableTreeNode) model.getChild(o, i);
			if(model.isLeaf(child)) {
				this.struct.add(new StructItem(child.toString(), null));
				System.out.println(seperator + child);
			} else {
				System.out.println(seperator + child);
				this.workTreeItem(model, child, seperator + seperator);
			}
		}
	}

	/**
	 * Add components (tree and infoPanel) in a splitPane to view.
	 */
	private void addComponentsToView() {
		final JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		final GridBagConstraints gbc = new GridBagConstraints();
		JPanel basePanel = new JPanel(new GridBagLayout());

		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.anchor = GridBagConstraints.NORTHWEST;
		basePanel.add(this.infoPanel, gbc);

		gbc.anchor = GridBagConstraints.NORTHEAST;
		gbc.insets = new Insets(0, 10, 0, 0);
		gbc.gridx = 1;
		basePanel.add(this.additionalInfoPanel, gbc);

		// create scroll panes
		JScrollPane treeViewScrollPane = new JScrollPane(this.tree);
		JScrollPane scrollPaneView = new JScrollPane(basePanel);

		// order split pane components
		splitPane.setLeftComponent(treeViewScrollPane);
		splitPane.setRightComponent(scrollPaneView);

		// set minimum size
		treeViewScrollPane.setMinimumSize(new Dimension(200, 50));
		scrollPaneView.setMinimumSize(new Dimension(355, 50));

		// set size and divider location
		splitPane.setDividerLocation(200);
		splitPane.setPreferredSize(new Dimension(900, 350));

		// add to view
		this.view.add(splitPane);
	}

	/**
	 * Fills the additional info panel with information.
	 * 
	 * @param additionalData
	 *            HashMap<String, String>
	 */
	private void fillAddtionalInfoPanel(final HashMap<String, String> additionalData) {
		this.additionalInfoPanel.getpInformation().removeAll();
		if(additionalData != null) {
			final GridBagConstraints gbc = new GridBagConstraints();
			gbc.fill = GridBagConstraints.HORIZONTAL;
			gbc.gridwidth = 1;
			JTextField valTextField = new JTextField(), keyTextField = new JTextField();

			valTextField.setPreferredSize(new Dimension(250, 20));
			keyTextField.setPreferredSize(new Dimension(75, 20));

			int i = 0;
			for(Map.Entry<String, String> e : additionalData.entrySet()) {
				if((e.getKey() != "content") && (e.getKey() != "filetype") && (e.getKey() != "type") && (e.getKey() != "folder")) {
					gbc.gridy = i;
					gbc.gridx = 0;
					keyTextField.setText(e.getKey());
					gbc.insets = new Insets(0, 0, 10, 10);
					this.additionalInfoPanel.getpInformation().add(keyTextField, gbc);
					gbc.gridx = 1;
					valTextField.setText(e.getValue());
					gbc.insets = new Insets(0, 0, 10, 0);
					this.additionalInfoPanel.getpInformation().add(valTextField, gbc);
					i++;
				}
			}
			this.additionalInfoPanel.revalidate();
		}
	}

	/**
	 * Eventhandler for the tree.
	 * 
	 * @param treeSelectionEvent
	 *            TreeSelectionEvent
	 */
	public void valueChanged(final TreeSelectionEvent treeSelectionEvent) {

		// returns the selected treenode
		final DefaultMutableTreeNode node = (DefaultMutableTreeNode) this.tree.getLastSelectedPathComponent();
		if(node == null) {
			return;
		}

		// Set to ""
		this.infoPanel.getCbTypeValue().setSelectedItem("");

		// create new node object
		final Object nodeInfo = node.getUserObject();
		this.additionalInfoPanel.getpInformation().removeAll();

		// if not class (so it is a file (isLeaf) or folder (!isLeaf), no root)
		if(nodeInfo.getClass() != String.class) {
			this.infoPanel.getCbTypeValue().setEnabled(Boolean.TRUE);
			this.infoPanel.getTfTitleValue().setEnabled(Boolean.TRUE);
			this.infoPanel.getTaContent().setEnabled(Boolean.TRUE);
			// create new item to read out informations
			final FolderGenItem folderGenItem = (FolderGenItem) nodeInfo;

			// set the title type and content (if exists)
			this.infoPanel.getTfTitleValue().setText(folderGenItem.getTitle());
			this.infoPanel.getCbTypeValue().getModel().setSelectedItem(folderGenItem.getInfomarker());
			if((folderGenItem.getAdditionalData().containsKey("folder") && Boolean.parseBoolean(folderGenItem.getAdditionalData()
					.get("folder")))) {
				this.infoPanel.getTaContent().setText("");
				this.infoPanel.getTaContent().setEnabled(Boolean.FALSE);
				this.infoPanel.getCbTypeValue().setEnabled(Boolean.FALSE);
			} else {
				this.infoPanel.getCbTypeValue().setEnabled(Boolean.TRUE);
				this.infoPanel.getTaContent().setEnabled(Boolean.TRUE);
				this.infoPanel.getTaContent()
						.setText(
								((folderGenItem.getAdditionalData() != null)
										&& folderGenItem.getAdditionalData().containsKey("content") ? folderGenItem
										.getAdditionalData().get("content") : ""));

			}

			this.fillAddtionalInfoPanel(folderGenItem.getAdditionalData());
		} else {
			this.infoPanel.getTfTitleValue().setText("");
			this.infoPanel.getCbTypeValue().setSelectedItem("");
			this.infoPanel.getTaContent().setText("");
			this.infoPanel.getCbTypeValue().setEnabled(Boolean.FALSE);
			this.infoPanel.getTfTitleValue().setEnabled(Boolean.FALSE);
			this.infoPanel.getTaContent().setEnabled(Boolean.FALSE);

		}
	}

	/**
	 * Eventhandler for the save button.
	 * 
	 * @param e
	 *            ActionEvent
	 */
	@Override
	public void actionPerformed(final ActionEvent e) {
		if(e.getSource() == this.infoPanel.getbSave()) {
			// Save settings
			DefaultMutableTreeNode node = (DefaultMutableTreeNode) this.tree.getLastSelectedPathComponent();
			if(node == null) {
				return;
			}

			// create new node object
			final Object nodeInfo = node.getUserObject();

			// if not class (so it is a file (isLeaf) or folder (!isLeaf), no root)
			if(nodeInfo.getClass() != String.class) {
				// create new item to read out informations
				final FolderGenItem folderGenItem = (FolderGenItem) nodeInfo;
				folderGenItem.setTitle(this.infoPanel.getTfTitleValue().getText());

				for(FileType type : Generator.getFiletypes()) {
					if(type == this.infoPanel.getCbTypeValueModel().getSelectedItem()) {
						folderGenItem.setFilemarker(((FileType) this.infoPanel.getCbTypeValueModel().getSelectedItem())
								.getFilemarker());
						folderGenItem.setInfomarker(((FileType) this.infoPanel.getCbTypeValueModel().getSelectedItem())
								.getInfomarker());
						break;
					}
				}

				this.tree.getDtmTreeModel().reload(node);
				// this.tree.revalidate();
				this.getStructFromTree();
			}
		}
	}

	/**
	 * Eventhandler for the type combo box.
	 * 
	 * @param e
	 *            ItemEvent
	 */
	@Override
	public void itemStateChanged(final ItemEvent e) {
		if((e.getSource() == this.infoPanel.getCbTypeValue())
				&& e.getItem().getClass().getSimpleName().equalsIgnoreCase("filetype")
				&& (e.getStateChange() == ItemEvent.SELECTED)) {
			HashMap<String, String> addtionalInformations = new HashMap<String, String>();
			for(String key : ((FileType) e.getItem()).getAdditionalKeys()) {
				addtionalInformations.put(key, "");
			}
			this.fillAddtionalInfoPanel(addtionalInformations);
		}

	}

	public static FolderGenTreeController getInstance(final View view) {
		FolderGenTreeController.parentView = view;
		return FolderGenTreeController.instance;
	}

	/**
	 * @return the infoPanel
	 */
	public synchronized final InfoPanel getInfoPanel() {
		return this.infoPanel;
	}

	/**
	 * @return the tree
	 */
	public synchronized final Tree getTree() {
		return this.tree;
	}

	/**
	 * @return the rootPath
	 */
	public synchronized final File getRootPath() {
		return this.rootPath;
	}

	/**
	 * @param rootPath
	 *            the rootPath to set
	 */
	public synchronized final void setRootPath(final File rootPath) {
		this.rootPath = rootPath;
	}

	/**
	 * @return the configFile
	 */
	public synchronized final File getConfigFile() {
		return this.configFile;
	}

	/**
	 * @param configFile
	 *            the configFile to set
	 */
	public synchronized final void setConfigFile(final File configFile) {
		this.configFile = configFile;
	}

	/**
	 * @return the additionalInfoPanel
	 */
	public synchronized final AdditionalInfoPanel getAdditionalInfoPanel() {
		return this.additionalInfoPanel;
	}

	/**
	 * @return the parentView
	 */
	public static synchronized final View getParentView() {
		return FolderGenTreeController.parentView;
	}

	/**
	 * @param parentView
	 *            the parentView to set
	 */
	public static synchronized final void setParentView(final View parentView) {
		FolderGenTreeController.parentView = parentView;
	}

}
