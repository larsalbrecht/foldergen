/*
 * Copyright (c) 2011 Lars Chr. Albrecht
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:
 * Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
 * Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 */

/**
 * 
 */
package com.lars_albrecht.foldergen.plugin.finder;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

import com.lars_albrecht.foldergen.plugin.classes.FolderGenPlugin;
import com.lars_albrecht.foldergen.plugin.filter.JarFilter;

/**
 * Modified version of twit88. Search and loads the plugins.
 * 
 * @author lalbrecht
 * @see "http://twit88.com/blog/2007/10/08/develop-a-java-plugin-framework-search-for-plugin-dynamically/"
 * @version 1.0.0.0
 * 
 */
@SuppressWarnings("unchecked")
public class PluginFinder {
	// Parameters
	private static final Class<FolderGenPlugin>[] parameters = new Class[] { URL.class };

	private List<FolderGenPlugin> pluginCollection;

	/**
	 * Constructor.
	 */
	public PluginFinder() {
		this.pluginCollection = new ArrayList<FolderGenPlugin>(5);
	}

	/**
	 * Search function.
	 * 
	 * @param directory
	 *            String
	 * @throws Exception
	 */
	public void search(final String directory) throws Exception {
		File dir = new File(directory);
		if(dir.isFile()) {
			return;
		}
		File[] files = dir.listFiles(new JarFilter());
		for(File f : files) {
			List<String> classNames = this.getClassNames(f.getAbsolutePath());
			for(String className : classNames) {
				// Remove the ".class" at the back
				String name = className.substring(0, (className.length() - 6));
				Class<FolderGenPlugin> clazz = this.getClass(f, name);
				Class<FolderGenPlugin> superClass = (Class<FolderGenPlugin>) clazz.getSuperclass();
				if(superClass.equals(FolderGenPlugin.class)) {
					this.pluginCollection.add(clazz.newInstance());
				}
			}
		}
	}

	/**
	 * Returns classnames.
	 * 
	 * @param jarName
	 *            String
	 * @return List<String>
	 * @throws IOException
	 */
	protected List<String> getClassNames(final String jarName) throws IOException {
		ArrayList<String> classes = new ArrayList<String>(10);
		JarInputStream jarFile = new JarInputStream(new FileInputStream(jarName));
		JarEntry jarEntry;
		while(true) {
			jarEntry = jarFile.getNextJarEntry();
			if(jarEntry == null) {
				break;
			}
			if(jarEntry.getName().endsWith(".class")) {
				classes.add(jarEntry.getName().replaceAll("/", "\\."));
			}
		}

		return classes;
	}

	/**
	 * Returns the class.
	 * 
	 * @param file
	 *            File
	 * @param name
	 *            String
	 * @return Class<CoreMarkerReplacer>
	 * @throws Exception
	 */
	public Class<FolderGenPlugin> getClass(final File file, final String name) throws Exception {
		this.addURL(file.toURI().toURL());

		URLClassLoader clazzLoader;
		Class<FolderGenPlugin> clazz;
		String filePath = file.getAbsolutePath();
		filePath = "jar:file://" + filePath + "!/";
		URL url = new File(filePath).toURI().toURL();
		clazzLoader = new URLClassLoader(new URL[] { url });
		clazz = (Class<FolderGenPlugin>) clazzLoader.loadClass(name);
		return clazz;

	}

	/**
	 * Returns the url.
	 * 
	 * @param u
	 *            URL
	 * @throws IOException
	 */
	public void addURL(final URL u) throws IOException {
		URLClassLoader sysLoader = (URLClassLoader) ClassLoader.getSystemClassLoader();
		URL urls[] = sysLoader.getURLs();
		for(URL url : urls) {
			if(url.toString().equalsIgnoreCase(u.toString())) {
				return;
			}
		}
		Class<URLClassLoader> sysclass = URLClassLoader.class;
		try {
			Method method = sysclass.getDeclaredMethod("addURL", PluginFinder.parameters);
			method.setAccessible(true);
			method.invoke(sysLoader, new Object[] { u });
		} catch(Throwable t) {
			t.printStackTrace();
			throw new IOException("Error, could not add URL to system classloader");
		}
	}

	/**
	 * Returns a list of plugins.
	 * 
	 * @return List<CoreMarkerReplacer>
	 */
	public List<FolderGenPlugin> getPluginCollection() {
		return this.pluginCollection;
	}

	/**
	 * Sets the list of plugins.
	 * 
	 * @param pluginCollection
	 *            List<CoreMarkerReplacer>
	 */
	public void setPluginCollection(final List<FolderGenPlugin> pluginCollection) {
		this.pluginCollection = pluginCollection;
	}
}
