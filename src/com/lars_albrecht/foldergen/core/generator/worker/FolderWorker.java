/**
 * 
 */
package com.lars_albrecht.foldergen.core.generator.worker;

import java.util.HashMap;

import com.lars_albrecht.foldergen.core.generator.worker.interfaces.IWorker;

/**
 * @author lalbrecht
 * 
 */
public class FolderWorker implements IWorker {

	@Override
	public HashMap<String, Object> doWork(final HashMap<String, Object> workerMap) {
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

}
