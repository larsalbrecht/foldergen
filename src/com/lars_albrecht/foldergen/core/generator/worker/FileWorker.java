/**
 * 
 */
package com.lars_albrecht.foldergen.core.generator.worker;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;

import com.lars_albrecht.foldergen.core.generator.worker.interfaces.IWorker;

/**
 * @author lalbrecht
 * 
 */
public class FileWorker implements IWorker {

	@Override
	public HashMap<String, Object> doWork(final HashMap<String, Object> workerMap) {
		@SuppressWarnings("unchecked")
		HashMap<String, String> tempAdditionalData = (HashMap<String, String>) workerMap.get("additionalData");
		File rootFolder = (File) workerMap.get("rootFolder");
		String name = (String) workerMap.get("name");
		workFile(rootFolder, tempAdditionalData, name);

		return null;
	}

	@Override
	public String getFileMarker() {
		return "-";
	}

	@Override
	public String getMarker() {
		return "file";
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
		return Boolean.FALSE;
	}

	/**
	 * If needed, creates a file and - if needed - insert content.
	 * 
	 * @param rootFolder
	 *            File
	 * @param tempAdditionalData
	 *            HashMap<String,String>
	 * @param name
	 *            String
	 */
	private void workFile(final File rootFolder, final HashMap<String, String> tempAdditionalData, final String name) {
		try {
			File f = new File(rootFolder.getAbsolutePath() + File.separator + name);
			// file not exists and format respectively filepath correct
			if (!f.exists() && new File(f.getParent()).isDirectory()) {
				f.createNewFile();
				if (tempAdditionalData.containsKey("content") && (tempAdditionalData.get("content") != null) && !tempAdditionalData.get("content").equals("")) {
					FileOutputStream fos = new FileOutputStream(f);
					for (int j = 0; j < tempAdditionalData.get("content").length(); j++) {
						fos.write((byte) tempAdditionalData.get("content").charAt(j));
					}
					fos.close();
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
