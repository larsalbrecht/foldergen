/**
 * 
 */
package com.lars_albrecht.foldergen.gui;

import java.io.File;

import javax.swing.filechooser.FileFilter;

/**
 * @author lalbrecht
 * @version 1.0.0.0
 */
public class FolderGenFileFilter extends FileFilter {

	@Override
	public boolean accept(final File f) {
		return f.getName().toLowerCase().endsWith(".foldergenconf") || f.isDirectory();
	}

	@Override
	public String getDescription() {
		return "FolderGen configfile(*.foldergenconf)";
	}

}
