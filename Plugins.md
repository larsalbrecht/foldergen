


# Plugins (Version 1.4.9.0 | Pluginframework Version 1.0.0.0) #
_Be carefully with this. This is **NOT** final and the code to produce a plugin can be changed in near future_

## How to use ##
You need only to put the jar of the plugin to the folder where FolderGen is.

## How to code ##
It is really simple to create a plugin in this version. You need to add "foldergenpluginframeworkjar-< version >.jar" to you buildpath. Then you can create a class that extends "CoreMarkerReplacer" (com.lars\_albrecht.foldergen.plugin.classes.CoreMarkerReplacer).

Now, you need only to overwrite two methods:
`replaceContent()`
`getName()`

In "getName" you return the name of the plugin, e.g.: MyDemoPlugin

In "replaceContent" you have the var "content" in "this.getContent()". In here you will have the content from FolderGen. Here you can do something like:
```
return this.getContent().replaceAll("(\\$\\{plugin.demo\\})", this.getName() + " DEMO");
```

### Example Demo Class ###
```
/**
 * 
 */
package com.lars_albrecht.foldergen.plugin;

import com.lars_albrecht.foldergen.plugin.classes.CoreMarkerReplacer;

/**
 * Replaces ${plugin.demo} with "MyPluginDemo DEMO".
 * 
 * @author lalbrecht
 * @version 1.0.0.0
 */
public class MyPluginDemo extends CoreMarkerReplacer {

	@Override
	public String replaceContent() {
		return this.getContent().replaceAll("(\\$\\{plugin.demo\\})", this.getName() + " DEMO");
	}

	@Override
	public String getName() {
		return "MyPluginDemo";
	}

}

```


# Plugins (Version 1.5.0.0 | Pluginframework Version 1.5.0.0) #
In version 1.5.0.0 there is a new pluginframework to create plugins for FolderGen. You can now not only add a marker replacer, the plugin framework in version 1.5.0.0 can do more.

## Plugintypes ##

A Plugin to ...
  * ... replace own markers in content.
  * ... create special types of files.
  * ... create special types of folders.
  * ... create special types of files with content.


## How to use ##
You need only to put the jar of the plugin to the folder where FolderGen is.

## How to code ##
It is really simple to create a plugin in this version. You need to add "foldergenpluginframeworkjar-< version >.jar" to you buildpath.
Now you can start to create your own plugin.

You need to extend your class from PluginClass "FolderGenPlugin".

### Build a marker replacer plugin ###
Have a look at the download area, there is the plugin I describe here.

```
/**
 * Replaces ${plugin.demo} with "MyPluginDemo DEMO".
 * 
 * @author lalbrecht
 * @version 1.0.0.0
 */
public class MyReplacerDemo extends FolderGenPlugin {

	@Override
	public String replaceContent(final String content) {
		return content.replaceAll("(\\$\\{plugin.demo\\})", this.infoMap.get(IFolderGenPlugin.INFO_TITLE) + " DEMO");
	}

	public MyReplacerDemo() {
		this.infoMap.put(IFolderGenPlugin.INFO_TITLE, "MyReplacerDemo");
	}

	@Override
	public HashMap<String, Object> doWork(final HashMap<String, Object> workerMap) {
		return null;
	}

	@Override
	public HashMap<String, String> getAdditionlInfo(final String[] basicInfo) {
		return null;
	}

	@Override
	public Integer getPluginType() {
		return IFolderGenPlugin.PLUGINTYPE_CONTENTEXTENSION_REPLACER;
	}

	@Override
	public String getItemTitle(final String[] basicInfo) {
		return null;
	}

}

```

#### replaceContent ####
This function replaces the content. You became a String as parameter and send a String back in FolderGen with return.

In this example, the plugin returns the pluginname + " DEMO". So it returns "MyReplacerDemo DEMO".

#### MyReplacerDemo ####
This is the constructor. Here you fill the infoMap HashMap with the pluginname:
`this.infoMap.put(IFolderGenPlugin.INFO_TITLE, "MyReplacerDemo");`

#### doWork ####
return null in this kind of plugin.

#### getAdditionlInfo ####
return null in this kind of plugin.

#### getPluginType ####
`return IFolderGenPlugin.PLUGINTYPE_CONTENTEXTENSION_REPLACER` in this kind of plugin.

#### getItemTitle ####
return null in this kind of plugin.


### Build a foldergenconf plugin ###
With this kind of plugins, you can create a special marker in the foldergenconf. Have a look at the download area, there is the plugin I describe here.

```
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
```

#### replaceContent ####
In this kind of plugin you can/must return null.

#### MyWorkerPluginDemo ####
This is the constructor. Here you fill the infoMap HashMap with the pluginname:
{{{		this.infoMap.put(IFolderGenPlugin.INFO\_TITLE, "MyWorkerPluginDemo");
> this.infoMap.put(IFolderGenPlugin.INFO\_FILEMARKER, "#");
> this.infoMap.put(IFolderGenPlugin.INFO\_INFOMARKER, "demo");
> this.infoMap.put(IFolderGenPlugin.INFO\_CONTENTREPLACE, Boolean.TRUE);}}}

  * INFO\_TITLE
    * This is the Pluginname
  * INFO\_FILEMARKER
    * This is the marker in the foldergenconf.
  * INFO\_INFOMARKER
    * This is a extended markername.
  * INFO\_CONTENTREPLACE
    * If set to "TRUE", the default replacements run over the content

#### workFile ####
This is a internal function. It creats the file and write it down to filesystem. If "content" is set, it writes the content in the file.

#### doWork ####
Here you do the "job". You will get a workerMap (`HashMap<String, Object>`). You can find some information here you can use.
  * additionalData
    * This is a `HashMap<String, String>`
      * type (see INFO\_INFOMARKER)
      * content (the content that can be written)
  * rootFolder
    * This is the top of the file. It is type File.
  * name
    * This is the name of the item.

#### getAdditionlInfo ####
You became a basicInfo String Array. Here you find some basic informations (see example above).
If you set "content" in result, this will be written later.

#### getPluginType ####
return the kind of the plugin (see `return IFolderGenPlugin.PLUGINTYPE_CONTENTEXTENSION[...]`).

#### getItemTitle ####
Returns the title for the file. Normally you use `basicInfo[1].trim()`.


# Plugins (Version 1.6.0.0 | Pluginframework Version 1.5.1.0) #
In version 1.5.1.0 of the pluginframework, you have to add something to your plugin code.

IFolderGenPlugin has a new INFO_variable: INFO\_ADDITIONALKEYS
This is a variable to add a list (seperate with semicolons) of additional keys you need for you plugin._

In example FolderGen Worker "zip":
Here we add a variable called "src" to specify the source of the zip file. Now we have to add this "src" to the list:

ZipWorker constructor:
```
	public ZipWorker() {
		this.infoMap.put(IFolderGenPlugin.INFO_TITLE, "ZipWorker");
		this.infoMap.put(IFolderGenPlugin.INFO_FILEMARKER, ">");
		this.infoMap.put(IFolderGenPlugin.INFO_INFOMARKER, "zip");
		this.infoMap.put(IFolderGenPlugin.INFO_ADDITIONALKEYS, "src");
	}
```

This is for the new treeview in version 1.6.0.0 of FolderGen.

In version 1.6.0.0 you can use chainworking. This means that you can do more jobs than one for you Plugin. In Example:
The default worker "CopyWorker" can download and copy files. In this version the worker is doing twice:
```
~ -> /my/zip/file.zip
```
This copies file.zip from /my/zip/ to root directory. CopyWorker looks at the filename (between ~ and ->) and "see" that there is nothing. This means that CopyWorker copy (or download) the file to the destination and unzip the file using the "ZipWorker". After that, the copied (downloaed) file will be deleted using the "DeleteWorker".
```
ArrayList<String> chain = (ArrayList<String>) workerMap.get("chain")
chain.add(new ZipWorker());
chain.add(new DeleteWorker());
```

Be carefully: If you add the same worker to chain it will ends in an endless loop:
```
ArrayList<String> chain = (ArrayList<String>) workerMap.get("chain")
chain.add(new CopyWorker());
```
You can prevent this by modify the "workerMap" in "doWork". All changes will be send to FolderGen.