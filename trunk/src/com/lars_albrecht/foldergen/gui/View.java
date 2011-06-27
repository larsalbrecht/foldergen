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
package com.lars_albrecht.foldergen.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JRootPane;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import com.lars_albrecht.foldergen.FolderGen;
import com.lars_albrecht.foldergen.core.Generator;
import com.lars_albrecht.foldergen.core.helper.PropertiesReader;

/**
 * The view is a java JFrame to choose the file with a gui.
 * 
 * @author lalbrecht
 * @version 1.0.5.0
 */
@SuppressWarnings("serial")
public class View extends JFrame implements ActionListener {

	private JButton btnChooseFile = null;
	private JFileChooser fcChooser = null;
	private JRootPane rpPane = null;

	JPopupMenu pmTreeMenu = null;
	JMenuItem miAddFolder = null;
	JMenuItem miAddFile = null;

	private Boolean isDebug = false;

	/**
	 * 
	 * @param rootFile
	 * @param isDebug
	 */
	public View(final File rootFile, final Boolean isDebug) {
		super(PropertiesReader.getInstance().getProperties("application.name") + " - "
				+ PropertiesReader.getInstance().getProperties("application.version"));
		this.isDebug = isDebug;
		this.initComponents();
	}

	/**
	 * Adds the components to panes.
	 */
	private void addComponents() {
		this.rpPane.add(this.btnChooseFile, BorderLayout.NORTH);

		this.add(this.rpPane);
	}

	/**
	 * Set the look and feel to the system one.
	 */
	private void setSystemLookAndFeel() {
		try {
			if(this.isDebug) {
				System.out.println(PropertiesReader.getInstance().getProperties("application.debug.looknfeel")
						+ UIManager.getSystemLookAndFeelClassName());
			}
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch(UnsupportedLookAndFeelException e) {
			if(this.isDebug) {
				e.printStackTrace();
			}
		} catch(ClassNotFoundException e) {
			if(this.isDebug) {
				e.printStackTrace();
			}
		} catch(InstantiationException e) {
			if(this.isDebug) {
				e.printStackTrace();
			}
		} catch(IllegalAccessException e) {
			if(this.isDebug) {
				e.printStackTrace();
			}
		} catch(Exception e) {
			if(this.isDebug) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Initilize the components.
	 */
	private void initComponents() {
		this.setSystemLookAndFeel();

		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setLayout(new BorderLayout());

		final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		this.setBounds((screenSize.width - 75) / 2, (screenSize.height - 50) / 2, 75, 50);

		this.createComponents();
		this.configureComponents();
		this.addComponents();
		this.setVisible(true);
	}

	/**
	 * Configures the components.
	 */
	private void configureComponents() {
		this.rpPane.setLayout(new BorderLayout());
		this.btnChooseFile.setBounds(10, 10, 100, 25);
		this.btnChooseFile.addActionListener(this);
	}

	/**
	 * Creates the components for the view.
	 */
	private void createComponents() {
		this.rpPane = new JRootPane();
		this.btnChooseFile = new JButton(PropertiesReader.getInstance().getProperties("application.gui.filechooser.opener"));
	}

	/**
	 * @param e
	 *            ActionEvent
	 */
	@Override
	public void actionPerformed(final ActionEvent e) {
		if(e.getSource().equals(this.btnChooseFile)) {
			if(this.isDebug) {
				System.out.println(PropertiesReader.getInstance().getProperties("application.debug.filechooser.opener.click"));
			}
			this.fcChooser = new JFileChooser();
			this.fcChooser.setFileFilter(new FolderGenFileFilter());
			this.fcChooser.setDialogTitle(PropertiesReader.getInstance().getProperties("application.gui.filechooser.title"));
			Integer returnVal = this.fcChooser.showOpenDialog(this);
			if(returnVal == JFileChooser.APPROVE_OPTION) {
				File file = this.fcChooser.getSelectedFile();
				if(this.isDebug) {
					System.out.println(PropertiesReader.getInstance().getProperties("application.debug.filechooser.approved")
							+ file.getName());
				}
				if(file.isFile() && file.exists()) {
					new Generator(file, this.isDebug, FolderGen.IS_GUI);
				}
			} else {
				if(this.isDebug) {
					System.out.println(PropertiesReader.getInstance().getProperties("application.debug.filechooser.canceled"));
				}
			}
		}

	}

}
