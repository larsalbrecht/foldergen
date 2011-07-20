package com.lars_albrecht.foldergen.core.generator.worker;

import java.util.HashMap;

import com.lars_albrecht.foldergen.core.generator.worker.interfaces.IWorker;

public class DefaultContentWorker implements IWorker {

	@Override
	public HashMap<String, Object> doWork(final HashMap<String, Object> workerMap) {
		HashMap<String, Object> tempWorkerMap = new HashMap<String, Object>();
		tempWorkerMap.put("content", workerMap.get("content"));
		return tempWorkerMap;
	}

	@Override
	public HashMap<String, String> getAdditionlInfo(final String[] basicInfo) {
		return null;
	}

	@Override
	public String getContentEndMarker() {
		return ")))";
	}

	@Override
	public String getContentStartMarker() {
		return "(((";
	}

	@Override
	public String getFileMarker() {
		return null;
	}

	@Override
	public String getItemTitle(final String[] basicInfo) {
		return null;
	}

	@Override
	public String getMarker() {
		return null;
	}

	@Override
	public Boolean isContent() {
		return Boolean.TRUE;
	}

	@Override
	public Boolean replaceMarker() {
		return Boolean.TRUE;
	}

}
