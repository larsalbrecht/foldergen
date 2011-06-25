/**
 * 
 */
package com.lars_albrecht.foldergen.gui;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;

import com.lars_albrecht.foldergen.FolderGen;
import com.lars_albrecht.foldergen.core.Generator;

/**
 * The view is a java JFrame to choose the file with a gui.
 * 
 * @author lalbrecht
 * @version 1.0.0.0
 */
@SuppressWarnings("serial")
public class View extends JFrame implements ActionListener {

	private JButton btnChooseFile = null;
	private JFileChooser fcChooser = null;

	private Boolean isDebug = false;

	/**
	 * 
	 * @param rootFile
	 * @param isDebug
	 */
	public View(final File rootFile, final Boolean isDebug) {
		super("FolderGen");
		this.isDebug = isDebug;
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setLayout(null);
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		this.setBounds((screenSize.width - 130) / 2, (screenSize.height - 75) / 2, 130, 75);

		this.btnChooseFile = new JButton("Choose File");
		this.btnChooseFile.setBounds(10, 10, 100, 25);
		this.btnChooseFile.addActionListener(this);

		this.add(this.btnChooseFile);

		this.setVisible(true);
	}

	/**
	 * @param e
	 *            ActionEvent
	 */
	@Override
	public void actionPerformed(final ActionEvent e) {
		if(e.getSource().equals(this.btnChooseFile)) {
			if(this.isDebug) {
				System.out.println("btnChooseFile clicked");
			}
			this.fcChooser = new JFileChooser();
			this.fcChooser.setFileFilter(new FolderGenFileFilter());
			this.fcChooser.setDialogTitle("Choose FolderGen config file...");
			Integer returnVal = this.fcChooser.showOpenDialog(this);
			if(returnVal == JFileChooser.APPROVE_OPTION) {
				File file = this.fcChooser.getSelectedFile();
				if(this.isDebug) {
					System.out.println("fcChooser approved. File " + file.getName() + " choosed");
				}
				if(file.isFile() && file.exists()) {
					new Generator(file, this.isDebug, FolderGen.IS_GUI);
				}
			} else {
				if(this.isDebug) {
					System.out.println("fcChooser canceled");
				}
			}
		}

	}

}
