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
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
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
import com.lars_albrecht.foldergen.core.helper.cli.FolderGenCLIConf;
import com.lars_albrecht.foldergen.core.helper.properies.PropertiesReader;
import com.lars_albrecht.foldergen.gui.View;
import com.lars_albrecht.foldergen.gui.helper.filesystem.FolderGenFileFilter;
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

	private JFileChooser fcChooser = null;

	private InfoPanel infoPanel = null;
	private AdditionalInfoPanel additionalInfoPanel = null;
	private Tree tree = null;
	private FolderGenTreeView view = null;

	private File configFile = null;
	private File rootPath = null;

	private Generator generator = null;

	private static View parentView = null;
	private FolderGenCLIConf appConf = null;

	private Struct struct = new Struct();

	private final HashMap<JTextField, JTextField> additionalInfoMap = new HashMap<JTextField, JTextField>();

	/**
	 * Constructor of FolderGenTreeController.
	 * 
	 * @param parentView
	 *            View
	 * @param appConf
	 *            FolderGenCLIConf
	 */
	public FolderGenTreeController(final View parentView, final FolderGenCLIConf appConf) {
		FolderGenTreeController.parentView = parentView;
		this.appConf = appConf;

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
			this.struct = this.generator.getStruct();
			// add now
			this.addStructToTree(this.struct, this.tree.getRootNode());
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
					.get(i).getAdditionalData()));
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

	/**
	 * Get struct from tree and save new struct in this.struct.
	 */
	private void setStructFromTree() {
		TreeModel model = this.tree.getDtmTreeModel();
		if(model != null) {
			this.struct.clear();
			this.struct = this.workTreeItem((FolderGenMutableTreeNode) model.getChild(model.getRoot(), 0), null);
		} else if(this.appConf.getIsDebug()) {
			System.out.println("Tree is empty.");
		}
		if(this.appConf.getIsDebug()) {
			this.generator.printStruct(this.struct, "", true);
		}
	}

	/**
	 * Returns a struct with the new created struct from JTree.
	 * 
	 * @param node
	 *            FolderGenMutableTreeNode
	 * @param lastItem
	 *            StructItem
	 * @return Struct
	 */
	private Struct workTreeItem(final FolderGenMutableTreeNode node, final StructItem lastItem) {
		Struct tempStruct = new Struct();
		FolderGenItem tempItem = (FolderGenItem) node.getUserObject();
		StructItem tempStructItem = new StructItem(tempItem.getTitle(), tempItem.getAdditionalData(), lastItem);

		if(!node.isLeaf()) {
			for(int i = 0; i < node.getChildCount(); i++) {
				tempStructItem.getSubStruct().addAll(
						this.workTreeItem((FolderGenMutableTreeNode) node.getChildAt(i), tempStructItem));
			}
		}
		tempStruct.add(tempStructItem);
		return tempStruct;
	}

	/**
	 * Add components (tree and infoPanel) in a splitPane to view.
	 */
	private void addComponentsToView() {
		final JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		final GridBagConstraints gbc = new GridBagConstraints();
		JPanel basePanel = new JPanel(new GridBagLayout());
		gbc.weightx = .6;
		gbc.weighty = 1;
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.fill = GridBagConstraints.BOTH;
		gbc.insets = new Insets(10, 10, 0, 0);
		gbc.anchor = GridBagConstraints.FIRST_LINE_START;
		basePanel.add(this.infoPanel, gbc);

		gbc.weightx = .4;
		gbc.anchor = GridBagConstraints.FIRST_LINE_START;
		gbc.insets = new Insets(10, 10, 0, 0);
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
			gbc.anchor = GridBagConstraints.FIRST_LINE_START;
			gbc.gridwidth = 1;
			gbc.weighty = 1;
			JTextField valTextField = new JTextField(), keyTextField = new JTextField();

			valTextField.setPreferredSize(new Dimension(250, 20));
			keyTextField.setPreferredSize(new Dimension(75, 20));

			int i = 0;
			this.additionalInfoMap.clear();
			for(Map.Entry<String, String> e : additionalData.entrySet()) {
				if((e.getKey() != "content") && (e.getKey() != "filetype") && (e.getKey() != "type") && (e.getKey() != "folder")) {
					this.additionalInfoMap.put(keyTextField, valTextField);
					gbc.gridy = i;
					gbc.gridx = 0;
					keyTextField.setText(e.getKey());
					gbc.insets = new Insets(i > 0 ? 10 : 0, 0, 10, 10);
					this.additionalInfoPanel.getpInformation().add(keyTextField, gbc);

					gbc.weightx = 0.1;
					gbc.gridx = 1;
					valTextField.setText(e.getValue());
					gbc.insets = new Insets(i > 0 ? 10 : 0, 0, 10, 0);
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
	@Override
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
			this.infoPanel.getCbTypeValue().getModel().setSelectedItem(folderGenItem.getAdditionalData().get("type"));
			this.infoPanel.getCbTypeValue().setEnabled(Boolean.TRUE);
			this.infoPanel.getTaContent().setEnabled(Boolean.TRUE);
			this.infoPanel.getTaContent()
					.setText(
							((folderGenItem.getAdditionalData() != null)
									&& folderGenItem.getAdditionalData().containsKey("content") ? folderGenItem
									.getAdditionalData().get("content") : ""));

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
		this.fcChooser = new JFileChooser();
		if(this.appConf.getIsDebug()) {
			System.out.println("ActionEvent e.source: " + e.getSource());
		}
		if(e.getSource() == this.infoPanel.getbSave()) {
			// Save settings
			DefaultMutableTreeNode node = (DefaultMutableTreeNode) this.tree.getLastSelectedPathComponent();
			if(node == null) {
				return;
			}

			// create new node object and read out
			final Object nodeInfo = node.getUserObject();

			// if not class (so it is a file (isLeaf) or folder (!isLeaf), no
			// root)
			if(nodeInfo.getClass() != String.class) {
				// create new item to read out informations
				final FolderGenItem folderGenItem = (FolderGenItem) nodeInfo;
				folderGenItem.setTitle(this.infoPanel.getTfTitleValue().getText());

				// clear old map
				folderGenItem.getAdditionalData().clear();

				// fill addtionalData with new basic infos
				for(FileType type : Generator.getFiletypes()) {
					if(type == this.infoPanel.getCbTypeValueModel().getSelectedItem()) {
						folderGenItem.getAdditionalData().put("filetype",
								((FileType) this.infoPanel.getCbTypeValueModel().getSelectedItem()).getFilemarker());

						folderGenItem.getAdditionalData().put("type",
								((FileType) this.infoPanel.getCbTypeValueModel().getSelectedItem()).getInfomarker());
						break;
					}
				}

				// fill map with new entries
				for(Map.Entry<JTextField, JTextField> entry : this.additionalInfoMap.entrySet()) {
					folderGenItem.getAdditionalData().put(entry.getKey().getText(), entry.getValue().getText());
				}

				// set user object to new folderGenItem
				node.setUserObject(folderGenItem);

				// reload node
				this.tree.getDtmTreeModel().reload(node);

				// set new struct
				this.setStructFromTree();
			}
		} else if(e.getSource() == this.view.getMiExportAll()) {
			// Export the JTree-Entries to a file
			this.fcChooser.setFileFilter(new FolderGenFileFilter());
			this.fcChooser.setDialogTitle(PropertiesReader.getInstance().getProperties("application.gui.filechooser.title"));
			this.fcChooser.setCurrentDirectory((this.rootPath != null ? this.rootPath : (this.configFile != null ? new File(
					this.configFile.getParent()) : new File(System.getProperty("user.dir")))));
			Integer returnVal = this.fcChooser.showSaveDialog(this.view);
			if(returnVal == JFileChooser.APPROVE_OPTION) {
				File file = this.fcChooser.getSelectedFile();
				try {
					file.createNewFile();
				} catch(IOException e1) {
					e1.printStackTrace();
				}
				if(file.isFile() && file.exists()) {
					BufferedWriter bw = null;
					try {
						bw = new BufferedWriter(new FileWriter(file));
						bw.write(Generator.getStringFromStruct(this.struct, "", ""));
						bw.close();
						this.configFile = file;
						JOptionPane.showMessageDialog(this.view, PropertiesReader.getInstance().getProperties(
								"application.gui.messagedialog.configexported.message"), PropertiesReader.getInstance()
								.getProperties("application.gui.messagedialog.configexported.title"),
								JOptionPane.INFORMATION_MESSAGE);
					} catch(IOException ex) {
						JOptionPane.showMessageDialog(this.view, PropertiesReader.getInstance().getProperties(
								"application.gui.messagedialog.configexportederror.message"), PropertiesReader.getInstance()
								.getProperties("application.gui.messagedialog.configexportederror.title"),
								JOptionPane.ERROR_MESSAGE);
					}
				}
			}
		} else if(e.getSource() == this.view.getMiExit()) {
			// set struct, set parent view enabled and dispose treeview
			FolderGenTreeController.getParentView().setStruct(this.getStruct());
			FolderGenTreeController.getParentView().setEnabled(true);
			this.view.setVisible(false);
			this.view.dispose();
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

	/**
	 * @return the struct
	 */
	public synchronized final Struct getStruct() {
		return this.struct;
	}

	/**
	 * @param struct
	 *            the struct to set
	 */
	public synchronized final void setStruct(final Struct struct) {
		this.struct = struct;
	}

	/**
	 * @return the appConf
	 */
	public synchronized final FolderGenCLIConf getAppConf() {
		return this.appConf;
	}

	/**
	 * @param appConf
	 *            the appConf to set
	 */
	public synchronized final void setAppConf(final FolderGenCLIConf appConf) {
		this.appConf = appConf;
	}

}
