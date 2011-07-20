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
public class CopyWorker implements IWorker {

	@Override
	public HashMap<String, Object> doWork(final HashMap<String, Object> workerMap) {
		return null;
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
		tempMap.put("src", basicInfo[1].trim().substring(basicInfo[1].trim().indexOf("->") + 2));
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
}
