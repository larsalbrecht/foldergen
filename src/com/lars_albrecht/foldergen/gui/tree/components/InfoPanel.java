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
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import com.lars_albrecht.foldergen.core.helper.properies.PropertiesReader;
import com.lars_albrecht.foldergen.gui.tree.FolderGenTreeController;

/**
 * Extended JPanel that represents the panel on the right side in the view. In this panel you will find all settings defined to the selected item in tree.
 * 
 * @author lalbrecht
 * @version 1.0.0.0
 */
@SuppressWarnings("serial")
public class InfoPanel extends JPanel {

	private FolderGenTreeController controller = null;

	private JTextField tfTitleValue = null;
	private JComboBox cbTypeValue = null;
	private DefaultComboBoxModel cbTypeValueModel = null;
	private JTextArea taContent = null;
	private JButton bSave = null;

	/**
	 * Constructor of InfoPanel.
	 * 
	 * @param controller
	 *            FolderGenTreeController
	 */
	public InfoPanel(final FolderGenTreeController controller) {
		super(new GridBagLayout());
		this.controller = controller;

		this.createComponents();
	}

	/**
	 * Create components and add them to this panel.
	 */
	private void createComponents() {
		final GridBagConstraints gbc = new GridBagConstraints();

		this.cbTypeValueModel = new DefaultComboBoxModel();

		this.tfTitleValue = new JTextField("");
		this.cbTypeValue = new JComboBox(this.cbTypeValueModel);
		this.cbTypeValue.addItemListener(this.controller);
		this.taContent = new JTextArea();
		this.taContent.setTabSize(2);

		gbc.insets = new Insets(10, 0, 0, 0);

		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.gridx = 1;
		gbc.gridy = 1;
		gbc.gridwidth = 2;
		this.add(new JLabel(PropertiesReader.getInstance().getProperties("application.gui.tree.form.title")), gbc);

		gbc.gridwidth = 1;
		gbc.gridx = 2;
		gbc.gridy = 2;
		gbc.ipadx = 75;
		this.add(new JLabel(PropertiesReader.getInstance().getProperties("application.gui.tree.label.title")), gbc);

		gbc.gridx = 3;
		gbc.ipadx = 2;
		this.add(this.tfTitleValue, gbc);

		gbc.gridx = 2;
		gbc.gridy = 3;
		gbc.ipadx = 75;
		this.add(new JLabel(PropertiesReader.getInstance().getProperties("application.gui.tree.label.type")), gbc);

		gbc.gridx = 3;
		gbc.ipadx = 2;
		this.add(this.cbTypeValue, gbc);

		JScrollPane spTaContent = new JScrollPane(this.taContent);
		gbc.gridx = 2;
		gbc.gridy = 4;
		gbc.gridwidth = 2;
		gbc.weightx = 1.0;
		gbc.weighty = 1.0;
		gbc.fill = GridBagConstraints.BOTH;
		spTaContent.setPreferredSize(new Dimension(300, 150));
		this.add(spTaContent, gbc);

		gbc.weightx = 0.0;
		gbc.weighty = 0.0;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.gridx = 2;
		gbc.gridy = 5;
		gbc.gridheight = 1;
		gbc.gridwidth = 2;
		gbc.insets = new Insets(10, 0, 10, 0);
		this.bSave = new JButton(PropertiesReader.getInstance().getProperties("application.gui.tree.button.save"));
		this.bSave.addActionListener(this.controller);
		this.add(this.bSave, gbc);

	}

	/**
	 * @return the tfTitleValue
	 */
	public synchronized final JTextField getTfTitleValue() {
		return this.tfTitleValue;
	}

	/**
	 * @param tfTitleValue
	 *            the tfTitleValue to set
	 */
	public synchronized final void setTfTitleValue(final JTextField tfTitleValue) {
		this.tfTitleValue = tfTitleValue;
	}

	/**
	 * @return the cbTypeValue
	 */
	public synchronized final JComboBox getCbTypeValue() {
		return this.cbTypeValue;
	}

	/**
	 * @param cbTypeValue
	 *            the cbTypeValue to set
	 */
	public synchronized final void setCbTypeValue(final JComboBox cbTypeValue) {
		this.cbTypeValue = cbTypeValue;
	}

	/**
	 * @return the cbTypeValueModel
	 */
	public synchronized final DefaultComboBoxModel getCbTypeValueModel() {
		return this.cbTypeValueModel;
	}

	/**
	 * @param cbTypeValueModel
	 *            the cbTypeValueModel to set
	 */
	public synchronized final void setCbTypeValueModel(final DefaultComboBoxModel cbTypeValueModel) {
		this.cbTypeValueModel = cbTypeValueModel;
	}

	/**
	 * @return the taContent
	 */
	public synchronized final JTextArea getTaContent() {
		return this.taContent;
	}

	/**
	 * @param taContent
	 *            the taContent to set
	 */
	public synchronized final void setTaContent(final JTextArea taContent) {
		this.taContent = taContent;
	}

	/**
	 * @return the bSave
	 */
	public synchronized final JButton getbSave() {
		return this.bSave;
	}

	/**
	 * @param bSave
	 *            the bSave to set
	 */
	public synchronized final void setbSave(final JButton bSave) {
		this.bSave = bSave;
	}

}
