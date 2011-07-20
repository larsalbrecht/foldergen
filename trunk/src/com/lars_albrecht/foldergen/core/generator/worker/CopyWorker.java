/**
 * 
 */
package com.lars_albrecht.foldergen.core.generator.worker;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.ConnectException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;

import com.lars_albrecht.foldergen.core.generator.worker.interfaces.IWorker;
import com.lars_albrecht.foldergen.helper.Utilities;

/**
 * @author lalbrecht
 * 
 */
public class CopyWorker implements IWorker {

	@Override
	public HashMap<String, Object> doWork(final HashMap<String, Object> workerMap) {
		@SuppressWarnings("unchecked")
		HashMap<String, String> tempAdditionalData = (HashMap<String, String>) workerMap.get("additionalData");
		File rootFolder = (File) workerMap.get("rootFolder");
		String name = (String) workerMap.get("name");
		workGetCopy(rootFolder, tempAdditionalData, name);

		return null;
	}

	/**
	 * If needed, creates a copy of a file or a file from a HTTP-url.
	 * 
	 * @param rootFolder
	 *            File
	 * @param tempAdditionalData
	 *            HashMap<String,String>
	 * @param name
	 *            String
	 */
	private void workGetCopy(final File rootFolder, final HashMap<String, String> tempAdditionalData, final String name) {
		if (tempAdditionalData.get("src") != null) {
			if (tempAdditionalData.get("src").startsWith("http://") || tempAdditionalData.get("src").startsWith("https://")) {
				try {
					File f = new File(rootFolder.getAbsolutePath() + File.separator + name);
					if (!f.exists()) {
						URL source = new URL(tempAdditionalData.get("src"));

						String content = Utilities.getFileContentFromWeb(source);
						f.createNewFile();
						FileOutputStream fos = new FileOutputStream(f);
						for (int j = 0; j < content.length(); j++) {
							fos.write((byte) content.charAt(j));
						}
						fos.close();
					}
				} catch (MalformedURLException e) {
				} catch (ConnectException e) {
				} catch (IOException e) {
				}
			} else {
				try {
					// file
					File source = new File(tempAdditionalData.get("src"));
					if (source.exists() && source.isFile()) {
						Utilities.copyFile(source, new File(rootFolder.getAbsolutePath() + File.separator + name));
					} else if (source.exists() && source.isDirectory()) {
						Utilities.copyDir(source, new File(rootFolder.getAbsolutePath() + File.separator + name));
					}
				} catch (IOException e) {
				}
			}
		}

	}

	@Override
	public String getFileMarker() {
		return "~";
	}

	@Override
	public String getMarker() {
		return "copy";
	}

	@Override
	public String getItemTitle(final String[] basicInfo) {
		return basicInfo[1].trim().substring(0, basicInfo[1].trim().indexOf("->")).trim();
	}

	@Override
	public HashMap<String, String> getAdditionlInfo(final String[] basicInfo) {
		HashMap<String, String> tempMap = new HashMap<String, String>();
		tempMap.put("src", basicInfo[1].trim().substring(basicInfo[1].trim().indexOf("->") + 2).trim());
		return tempMap;
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
}
