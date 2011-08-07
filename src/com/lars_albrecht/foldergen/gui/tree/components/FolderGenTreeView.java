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

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.WindowConstants;

import com.lars_albrecht.foldergen.core.helper.properies.PropertiesReader;
import com.lars_albrecht.foldergen.gui.tree.FolderGenTreeController;

/**
 * The extended JFrame to view the components. Controlled by "controller".
 * 
 * @author lalbrecht
 * @version 1.0.0.0
 */
@SuppressWarnings("serial")
public class FolderGenTreeView extends JFrame {

	@SuppressWarnings("unused")
	private FolderGenTreeController controller = null;

	public FolderGenTreeView(final FolderGenTreeController controller) {
		super(PropertiesReader.getInstance().getProperties("application.gui.tree.title"));
		this.controller = controller;

		BufferedImage image = null;
		try {
			image = ImageIO.read(this.getClass().getResource("/tp_deb_foldergen-32x32.png"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		this.setIconImage(image);

		this.setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
		final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		this.setBounds((screenSize.width - 900) / 2, (screenSize.height - 350) / 2, 900, 350);

		this.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(final WindowEvent e) {
				FolderGenTreeController.getParentView().setEnabled(true);
			}
		});

	}

	/**
	 * Shows the frame.
	 */
	public void showFrame() {
		this.pack();
		this.setVisible(true);
	}

	/**
	 * Fill tree with items.
	 */
	/**
	 * private void fillRootNode() { this.dtm.insertNodeInto(new
	 * FolderGenMutableTreeNode(new FolderGenItem("MyFolder1", "+", "folder"),
	 * Boolean.TRUE), this.rootNode, this.rootNode.getChildCount());
	 * 
	 * this.dtm.insertNodeInto(new FolderGenMutableTreeNode(new
	 * FolderGenItem("MyTitle", "-", "file", new HashMap<String, Object>() { {
	 * this.put("content", "this is my content"); } })), (MutableTreeNode)
	 * this.rootNode.getChildAt(0),
	 * this.rootNode.getChildAt(0).getChildCount());
	 * 
	 * this.dtm.insertNodeInto(new FolderGenMutableTreeNode(new
	 * FolderGenItem("MyFolder2", "+", "folder"), Boolean.TRUE), this.rootNode,
	 * this.rootNode.getChildCount());
	 * 
	 * this.dtm.insertNodeInto(new FolderGenMutableTreeNode(new
	 * FolderGenItem("MyFolder3", "+", "folder"), Boolean.TRUE), this.rootNode,
	 * this.rootNode.getChildCount());
	 * 
	 * this.dtm.insertNodeInto(new FolderGenMutableTreeNode(new
	 * FolderGenItem("MyTitle2", "~", "copy", new HashMap<String, Object>() { {
	 * this.put("src", "/home/my/file"); } })), (MutableTreeNode)
	 * this.rootNode.getChildAt(this.rootNode.getChildCount() - 1),
	 * this.rootNode.getChildAt( this.rootNode.getChildCount() -
	 * 1).getChildCount()); }
	 **/

	/**
	 * Create the GUI and show it. For thread safety, this method should be
	 * invoked from the event dispatch thread.
	 */

}
