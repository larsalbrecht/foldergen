/**
 * 
 */
package com.lars_albrecht.foldergen.core.generator.worker;

import java.io.File;
import java.util.HashMap;

import com.lars_albrecht.foldergen.core.generator.worker.interfaces.IWorker;

/**
 * @author lalbrecht
 * 
 */
public class FolderWorker implements IWorker {

	@Override
	public HashMap<String, Object> doWork(final HashMap<String, Object> workerMap) {
		File rootFolder = (File) workerMap.get("rootFolder");
		String name = (String) workerMap.get("name");
		workFolder(rootFolder, name);
		return null;
	}

	@Override
	public String getFileMarker() {
		return "+";
	}

	@Override
	public String getMarker() {
		return "folder";
	}

	@Override
	public String getItemTitle(final String[] basicInfo) {
		return basicInfo[1].trim();
	}

	@Override
	public HashMap<String, String> getAdditionlInfo(final String[] basicInfo) {
		return null;
	}

	@Override
	public Boolean isContent() {
		return Boolean.FALSE;
	}

	@Override
	public String getContentEndMarker() {
		return null;
	}

	@Override
	public String getContentStartMarker() {
		return null;
	}

	@Override
	public Boolean replaceMarker() {
		return Boolean.FALSE;
	}

	@Override
	public Boolean isFolder() {
		return Boolean.TRUE;
	}

	/**
	 * If needed, creates a folder.
	 * 
	 * @param rootFolder
	 *            File
	 * @param name
	 *            String
	 */
	private void workFolder(final File rootFolder, final String name) {
		File f = new File(rootFolder.getAbsolutePath() + File.separator + name);
		// folder not exists and format respectively filepath correct
		if (!f.exists() && new File(f.getParent()).isDirectory()) {
			f.mkdir();
		}
	}

}
