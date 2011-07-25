package com.lars_albrecht.foldergen.plugin;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;

import com.lars_albrecht.foldergen.plugin.classes.FolderGenPlugin;
import com.lars_albrecht.foldergen.plugin.interfaces.IFolderGenPlugin;

/**
 * Generate a new file with the content "This is DEMO content from ${user.name}". "${user.name}" will be replaced with your local username.
 * 
 * @author lalbrecht
 * @version 1.0.0.0
 */
public class MyWorkerPluginDemo extends FolderGenPlugin {

	@Override
	public HashMap<String, Object> doWork(final HashMap<String, Object> workerMap) {
		@SuppressWarnings("unchecked")
		HashMap<String, String> tempAdditionalData = (HashMap<String, String>) workerMap.get("additionalData");
		File rootFolder = (File) workerMap.get("rootFolder");
		String name = (String) workerMap.get("name");
		this.workFile(rootFolder, tempAdditionalData, name);

		return null;
	}

	public MyWorkerPluginDemo() {
		this.infoMap.put(IFolderGenPlugin.INFO_TITLE, "MyWorkerPluginDemo");
		this.infoMap.put(IFolderGenPlugin.INFO_FILEMARKER, "#");
		this.infoMap.put(IFolderGenPlugin.INFO_INFOMARKER, "demo");
		this.infoMap.put(IFolderGenPlugin.INFO_CONTENTREPLACE, Boolean.TRUE);

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
			if(!f.exists() && new File(f.getParent()).isDirectory()) {
				f.createNewFile();
				if(tempAdditionalData.containsKey("content") && (tempAdditionalData.get("content") != null)
						&& !tempAdditionalData.get("content").equals("")) {
					FileOutputStream fos = new FileOutputStream(f);
					for(int j = 0; j < tempAdditionalData.get("content").length(); j++) {
						fos.write((byte) tempAdditionalData.get("content").charAt(j));
					}
					fos.close();
				}
			}
		} catch(IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public HashMap<String, String> getAdditionlInfo(final String[] basicInfo) {
		HashMap<String, String> tempMap = new HashMap<String, String>();
		tempMap.put("content", "This is DEMO content from ${user.name}");
		return tempMap;
	}

	@Override
	public String getItemTitle(final String[] basicInfo) {
		return basicInfo[1].trim();
	}

	@Override
	public Integer getPluginType() {
		return IFolderGenPlugin.PLUGINTYPE_CONFEXTENSION_CONTENT;
	}

	@Override
	public String replaceContent(final String content) {
		return null;
	}

}