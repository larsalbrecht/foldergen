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

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JLabel;
import javax.swing.JPanel;

import com.lars_albrecht.foldergen.core.helper.properies.PropertiesReader;
import com.lars_albrecht.foldergen.gui.tree.FolderGenTreeController;

/**
 * Extend JPanel to set GridBagLayout by default.
 * 
 * @author lalbrecht
 * @version 1.0.0.0
 */
@SuppressWarnings("serial")
public class AdditionalInfoPanel extends JPanel {

	@SuppressWarnings("unused")
	private final FolderGenTreeController controller = null;
	private JPanel pInformation = null;

	public AdditionalInfoPanel(final FolderGenTreeController controller) {
		super(new GridBagLayout());
		this.createComponents();
	}

	/**
	 * Create components and add them to this panel.
	 */
	private void createComponents() {
		final GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridwidth = 1;
		gbc.insets = new Insets(10, 0, 0, 0);

		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.gridx = 1;
		gbc.gridy = 1;
		gbc.gridwidth = 2;
		this.add(new JLabel(PropertiesReader.getInstance().getProperties("application.gui.tree.form.additionaltitle")), gbc);

		gbc.gridx = 1;
		gbc.gridy = 2;
		gbc.gridwidth = 1;
		this.pInformation = new JPanel(new GridBagLayout());
		this.add(this.pInformation, gbc);
	}

	/**
	 * @return the pInformation
	 */
	public synchronized final JPanel getpInformation() {
		return this.pInformation;
	}

}
