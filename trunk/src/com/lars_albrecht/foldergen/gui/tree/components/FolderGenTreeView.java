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
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;
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

	private FolderGenTreeController controller = null;

	private JMenuBar mbMenu = null;
	private JMenu menuFile = null;
	private JMenuItem miExportAll = null;

	public FolderGenTreeView(final FolderGenTreeController controller) {
		super(PropertiesReader.getInstance().getProperties("application.gui.tree.title"));
		this.controller = controller;

		this.initTreeView();

	}

	/**
	 * Initilize the tree view. Set the image and create the components with "createComponents()".
	 */
	private void initTreeView() {
		BufferedImage image = null;
		try {
			image = ImageIO.read(this.getClass()
					.getResource(PropertiesReader.getInstance().getProperties("application.iconpath")));
		} catch(IOException e) {
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

		this.createComponents();
	}

	/**
	 * Create the components and add them to view.
	 */
	private void createComponents() {
		this.mbMenu = new JMenuBar();
		this.menuFile = new JMenu(PropertiesReader.getInstance().getProperties("application.gui.tree.menu.file.title"));
		this.menuFile.setMnemonic(KeyEvent.VK_D);
		this.menuFile.getAccessibleContext().setAccessibleDescription(
				PropertiesReader.getInstance().getProperties("application.gui.tree.menu.file.description"));

		this.miExportAll = new JMenuItem(PropertiesReader.getInstance().getProperties(
				"application.gui.tree.menu.item.export.title"));
		this.miExportAll.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_1, ActionEvent.ALT_MASK));
		this.miExportAll.getAccessibleContext().setAccessibleDescription(
				PropertiesReader.getInstance().getProperties("application.gui.tree.menu.item.export.description"));

		this.miExportAll.addActionListener(this.controller);

		this.menuFile.add(this.miExportAll);
		this.menuFile.addSeparator();
		this.mbMenu.add(this.menuFile);
		this.setJMenuBar(this.mbMenu);
	}

	/**
	 * Shows the frame.
	 */
	public void showFrame() {
		this.pack();
		this.setVisible(true);
	}

	/**
	 * @return the mbMenu
	 */
	public synchronized final JMenuBar getMbMenu() {
		return this.mbMenu;
	}

	/**
	 * @param mbMenu
	 *            the mbMenu to set
	 */
	public synchronized final void setMbMenu(final JMenuBar mbMenu) {
		this.mbMenu = mbMenu;
	}

	/**
	 * @return the menuFile
	 */
	public synchronized final JMenu getMenuFile() {
		return this.menuFile;
	}

	/**
	 * @param menuFile
	 *            the menuFile to set
	 */
	public synchronized final void setMenuFile(final JMenu menuFile) {
		this.menuFile = menuFile;
	}

	/**
	 * @return the miExportAll
	 */
	public synchronized final JMenuItem getMiExportAll() {
		return this.miExportAll;
	}

	/**
	 * @param miExportAll
	 *            the miExportAll to set
	 */
	public synchronized final void setMiExportAll(final JMenuItem miExportAll) {
		this.miExportAll = miExportAll;
	}

}
