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
package com.lars_albrecht.foldergen.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.HeadlessException;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import com.lars_albrecht.foldergen.FolderGen;
import com.lars_albrecht.foldergen.core.Generator;
import com.lars_albrecht.foldergen.core.generator.helper.Struct;
import com.lars_albrecht.foldergen.core.helper.cli.FolderGenCLIConf;
import com.lars_albrecht.foldergen.core.helper.properies.PropertiesReader;
import com.lars_albrecht.foldergen.gui.helper.filesystem.FolderGenFileFilter;
import com.lars_albrecht.foldergen.gui.tree.FolderGenTreeController;

/**
 * The view is a java JFrame to choose the file with a gui.
 * 
 * @author lalbrecht
 * @version 1.5.3.0
 */
@SuppressWarnings("serial")
public class View extends JFrame implements ActionListener, ItemListener {

	private JButton btnChooseFile = null;
	private JButton btnChooseRootFolder = null;
	private JButton btnStart = null;
	private JButton btnShow = null;
	private JFileChooser fcChooser = null;
	private JPanel rpPane = null;

	private JCheckBox cbConfirmation = null;
	private JCheckBox cbPlugins = null;
	private JCheckBox cbCreate = null;

	private FolderGenCLIConf appConf = null;

	private Struct struct = null;

	/**
	 * View constructor.
	 * 
	 * @param appConf
	 *            FolderGenCLIConf
	 */
	public View(final FolderGenCLIConf appConf) {
		super(PropertiesReader.getInstance().getProperties("application.name") + " - "
				+ PropertiesReader.getInstance().getProperties("application.version"));
		this.appConf = appConf;

		BufferedImage image = null;
		try {
			image = ImageIO.read(this.getClass()
					.getResource(PropertiesReader.getInstance().getProperties("application.iconpath")));
		} catch(IOException e) {
			e.printStackTrace();
		}
		this.setIconImage(image);

		this.initComponents();
	}

	/**
	 * Set the look and feel to the system one.
	 */
	private void setSystemLookAndFeel() {
		try {
			if(this.appConf.getIsDebug()) {
				System.out.println(PropertiesReader.getInstance().getProperties("application.debug.looknfeel")
						+ UIManager.getSystemLookAndFeelClassName());
			}
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch(UnsupportedLookAndFeelException e) {
			if(this.appConf.getIsDebug()) {
				e.printStackTrace();
			}
		} catch(ClassNotFoundException e) {
			if(this.appConf.getIsDebug()) {
				e.printStackTrace();
			}
		} catch(InstantiationException e) {
			if(this.appConf.getIsDebug()) {
				e.printStackTrace();
			}
		} catch(IllegalAccessException e) {
			if(this.appConf.getIsDebug()) {
				e.printStackTrace();
			}
		} catch(Exception e) {
			if(this.appConf.getIsDebug()) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Initilize the components.
	 */
	private void initComponents() {
		this.setSystemLookAndFeel();
		this.setLayout(new BorderLayout());
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		this.setBounds((screenSize.width - 400) / 2, (screenSize.height - 130) / 2, 400, 130);
		this.setResizable(Boolean.FALSE);

		this.createComponents();
		this.configureComponents();
		this.setVisible(true);
	}

	/**
	 * Configures the components.
	 */
	private void configureComponents() {
		this.rpPane.setLayout(new GridBagLayout());
		final GridBagConstraints gbc = new GridBagConstraints();

		gbc.fill = GridBagConstraints.HORIZONTAL;

		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.insets = new Insets(10, 0, 0, 10);
		this.btnChooseFile.addActionListener(this);
		this.rpPane.add(this.btnChooseFile, gbc);

		gbc.gridx = 1;
		gbc.gridy = 0;
		gbc.insets = new Insets(10, 0, 0, 10);
		this.btnChooseRootFolder.addActionListener(this);
		this.rpPane.add(this.btnChooseRootFolder, gbc);
		gbc.gridwidth = 1;

		gbc.gridx = 2;
		gbc.gridy = 0;
		gbc.insets = new Insets(10, 0, 0, 0);
		this.btnShow.addActionListener(this);
		this.rpPane.add(this.btnShow, gbc);

		gbc.gridx = 0;
		gbc.gridy = 1;
		gbc.insets = new Insets(10, 0, 0, 10);
		this.cbConfirmation.addItemListener(this);
		this.cbConfirmation.setSelected(this.appConf.getShowConfirmation());
		this.rpPane.add(this.cbConfirmation, gbc);

		gbc.gridx = 1;
		gbc.gridy = 1;
		gbc.insets = new Insets(10, 0, 0, 0);
		this.cbPlugins.addItemListener(this);
		this.cbPlugins.setSelected(this.appConf.getUsePlugins());
		this.rpPane.add(this.cbPlugins, gbc);

		gbc.gridx = 2;
		gbc.gridy = 1;
		this.cbCreate.addItemListener(this);
		this.cbCreate.setSelected(this.appConf.getCreateNew());
		this.rpPane.add(this.cbCreate, gbc);

		gbc.gridx = 0;
		gbc.gridy = 2;
		gbc.gridwidth = 3;
		this.btnStart.addActionListener(this);
		this.rpPane.add(this.btnStart, gbc);

		this.add(this.rpPane);

	}

	/**
	 * Creates the components for the view.
	 */
	private void createComponents() {
		this.rpPane = new JPanel();
		this.btnChooseFile = new JButton(PropertiesReader.getInstance().getProperties("application.gui.filechooser.opener"));
		this.btnChooseRootFolder = new JButton(PropertiesReader.getInstance().getProperties(
				"application.gui.rootfolderchooser.opener"));
		this.btnStart = new JButton(PropertiesReader.getInstance().getProperties("application.gui.button.start"));
		this.btnShow = new JButton(PropertiesReader.getInstance().getProperties("application.gui.button.show"));

		this.cbConfirmation = new JCheckBox(PropertiesReader.getInstance().getProperties("application.gui.checkbox.confirmation"));
		this.cbPlugins = new JCheckBox(PropertiesReader.getInstance().getProperties("application.gui.checkbox.plugins"));
		this.cbCreate = new JCheckBox(PropertiesReader.getInstance().getProperties("application.gui.checkbox.create"));
	}

	/**
	 * Action events.
	 * 
	 * @param e
	 *            ActionEvent
	 */
	@Override
	public void actionPerformed(final ActionEvent e) {
		if(e.getSource().equals(this.btnChooseFile)) {
			this.fcChooser = new JFileChooser();
			this.fcChooser.setCurrentDirectory((this.appConf.getRootPath() != null ? this.appConf.getRootPath() : (this.appConf
					.getConfigFile() != null ? new File(this.appConf.getConfigFile().getParent()) : new File(System
					.getProperty("user.dir")))));
			this.fcChooser.setFileFilter(new FolderGenFileFilter());
			this.fcChooser.setDialogTitle(PropertiesReader.getInstance().getProperties("application.gui.filechooser.title"));
			if(this.cbCreate.isSelected()) {
				Integer returnVal = this.fcChooser.showSaveDialog(this);
				if(returnVal == JFileChooser.APPROVE_OPTION) {
					File file = this.fcChooser.getSelectedFile();
					this.appConf.setConfigFile(file);
				}
			} else {
				if(this.appConf.getIsDebug()) {
					System.out
							.println(PropertiesReader.getInstance().getProperties("application.debug.filechooser.opener.click"));
				}

				Integer returnVal = this.fcChooser.showOpenDialog(this);
				if(returnVal == JFileChooser.APPROVE_OPTION) {
					File file = this.fcChooser.getSelectedFile();
					if(this.appConf.getIsDebug()) {
						System.out.println(PropertiesReader.getInstance().getProperties("application.debug.filechooser.approved")
								+ file.getName());
					}
					if(file.isFile() && file.exists()) {
						this.appConf.setConfigFile(file);
					}
				} else {
					if(this.appConf.getIsDebug()) {
						System.out
								.println(PropertiesReader.getInstance().getProperties("application.debug.filechooser.canceled"));
					}
				}
			}
		} else if(e.getSource() == this.btnChooseRootFolder) {
			if(this.appConf.getIsDebug()) {
				System.out.println(PropertiesReader.getInstance().getProperties("application.debug.filechooser.opener.click"));
			}
			this.fcChooser = new JFileChooser();
			this.fcChooser.setCurrentDirectory((this.appConf.getRootPath() != null ? this.appConf.getRootPath() : (this.appConf
					.getConfigFile() != null ? new File(this.appConf.getConfigFile().getParent()) : new File(System
					.getProperty("user.dir")))));
			this.fcChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			this.fcChooser
					.setDialogTitle(PropertiesReader.getInstance().getProperties("application.gui.rootfolderchooser.title"));
			this.fcChooser.setAcceptAllFileFilterUsed(false);
			Integer returnVal = this.fcChooser.showOpenDialog(this);

			if(returnVal == JFileChooser.APPROVE_OPTION) {
				File file = this.fcChooser.getSelectedFile() != null ? this.fcChooser.getSelectedFile() : this.fcChooser
						.getCurrentDirectory();
				if(this.appConf.getIsDebug()) {
					System.out.println(PropertiesReader.getInstance().getProperties("application.debug.filechooser.approved")
							+ file.getName());
				}
				if(file.isDirectory() && file.exists()) {
					this.appConf.setRootPath(file);
				}
			} else {
				if(this.appConf.getIsDebug()) {
					System.out.println(PropertiesReader.getInstance().getProperties("application.debug.filechooser.canceled"));
				}
			}
		} else if(e.getSource() == this.btnStart) {
			if(this.cbCreate.isSelected()) {
				try {
					if(this.appConf.getConfigFile().exists()
							|| (this.appConf.getConfigFile().createNewFile() && this.appConf.getConfigFile().exists() && this.appConf
									.getConfigFile().isFile())) {
						BufferedWriter bw = null;
						try {
							bw = new BufferedWriter(new FileWriter(this.appConf.getConfigFile()));
							bw.write(Generator.getStringFromStruct(Generator.getStructFromFilesystem(this.appConf.getRootPath()),
									"", ""));
							bw.close();

							JOptionPane.showMessageDialog(this, PropertiesReader.getInstance().getProperties(
									"application.gui.messagedialog.configcreated.message"), PropertiesReader.getInstance()
									.getProperties("application.gui.messagedialog.configcreated.title"),
									JOptionPane.INFORMATION_MESSAGE);
						} catch(IOException ex) {
							JOptionPane.showMessageDialog(this, PropertiesReader.getInstance().getProperties(
									"application.gui.messagedialog.configcreatederror.message"), PropertiesReader.getInstance()
									.getProperties("application.gui.messagedialog.configcreatederror.title"),
									JOptionPane.ERROR_MESSAGE);
						}
					}
				} catch(HeadlessException e1) {
					e1.printStackTrace();
				} catch(IOException e1) {
					e1.printStackTrace();
				}
			} else {

			}

			if(this.appConf.getShowConfirmation()) {
				if(JOptionPane.showConfirmDialog(this, PropertiesReader.getInstance().getProperties(
						"application.gui.messagedialog.confirmation.message"), PropertiesReader.getInstance().getProperties(
						"application.gui.messagedialog.confirmation.title"), JOptionPane.YES_NO_OPTION,
						JOptionPane.INFORMATION_MESSAGE) == 0) {
					if(this.struct != null) {
						new Generator(this.struct, this.appConf);
					} else {
						this.appConf.setShowConfirmation(FolderGen.CONFIRMATION_HIDE);
						new Generator(this.appConf);
					}

				}
			} else {
				if(this.struct != null) {
					new Generator(this.struct, this.appConf);
				} else {
					this.appConf.setShowConfirmation(FolderGen.CONFIRMATION_HIDE);
					new Generator(this.appConf);
				}
			}
		} else if(e.getSource() == this.btnShow) {
			if((this.appConf.getConfigFile() == null) || !this.appConf.getConfigFile().exists()) {
				JOptionPane.showMessageDialog(this, PropertiesReader.getInstance().getProperties(
						"application.gui.messagedialog.noconfig.message"), PropertiesReader.getInstance().getProperties(
						"application.gui.messagedialog.noconfig.title"), JOptionPane.INFORMATION_MESSAGE);
			} else {
				FolderGenTreeController treeC = new FolderGenTreeController(this, this.appConf);
				this.setEnabled(Boolean.FALSE);
				treeC.setConfigFile(this.appConf.getConfigFile());
				treeC.setRootPath(this.appConf.getRootPath());
				treeC.fillTree();
				treeC.showFrame();
			}

		}

	}

	/**
	 * Item events.
	 * 
	 * @param ie
	 *            ItemEvent
	 */
	@Override
	public void itemStateChanged(final ItemEvent ie) {
		if(ie.getSource() == this.cbConfirmation) {
			this.appConf.setShowConfirmation(this.cbConfirmation.getSelectedObjects() != null ? Boolean.TRUE : Boolean.FALSE);
		} else if(ie.getSource() == this.cbPlugins) {
			this.appConf.setUsePlugins(this.cbPlugins.getSelectedObjects() != null ? Boolean.TRUE : Boolean.FALSE);
		}
	}

	/**
	 * @param struct
	 *            the struct to set
	 */
	public synchronized final void setStruct(final Struct struct) {
		this.struct = struct;
	}

}
