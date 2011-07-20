/**
 * 
 */
package com.lars_albrecht.foldergen.core.generator.worker.interfaces;

import java.util.HashMap;

/**
 * @author lalbrecht
 * 
 */
public interface IWorker {

	HashMap<String, Object> doWork(final HashMap<String, Object> workerMap);

	String getFileMarker();

	String getMarker();

	String getContentStartMarker();

	String getContentEndMarker();

	String getItemTitle(final String[] basicInfo);

	Boolean isContent();

	HashMap<String, String> getAdditionlInfo(final String[] basicInfo);

	Boolean replaceMarker();

	Boolean isFolder();

}
