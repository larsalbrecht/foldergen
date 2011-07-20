/*
 * Copyright (c) 2011 Lars Chr. Albrecht
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 * 
 * Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 * 
 * Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 * 
 * Neither the name of the project's author nor the names of its
 * contributors may be used to endorse or promote products derived from
 * this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS
 * FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
 * HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED
 * TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
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

import com.lars_albrecht.foldergen.plugin.classes.CoreMarkerReplacer;
import com.lars_albrecht.foldergen.plugin.filter.JarFilter;

/**
 * Modified version of twit88. Search and loads the plugins.
 * 
 * @author lalbrecht
 * @see http://twit88.com/blog/2007/10/08/develop-a-java-plugin-framework-search-for-plugin-dynamically/
 * @version 1.0.0.0
 * 
 */
@SuppressWarnings("unchecked")
public class PluginFinder {
	// Parameters
	private static final Class<CoreMarkerReplacer>[] parameters = new Class[] { URL.class };

	private List<CoreMarkerReplacer> pluginCollection;

	public PluginFinder() {
		this.pluginCollection = new ArrayList<CoreMarkerReplacer>(5);
	}

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
				Class<CoreMarkerReplacer> clazz = this.getClass(f, name);
				Class<CoreMarkerReplacer> superClass = (Class<CoreMarkerReplacer>) clazz.getSuperclass();
				if(superClass.equals(CoreMarkerReplacer.class)) {
					this.pluginCollection.add(clazz.newInstance());
				}
			}
		}
	}

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

	public Class<CoreMarkerReplacer> getClass(final File file, final String name) throws Exception {
		this.addURL(file.toURI().toURL());

		URLClassLoader clazzLoader;
		Class<CoreMarkerReplacer> clazz;
		String filePath = file.getAbsolutePath();
		filePath = "jar:file://" + filePath + "!/";
		URL url = new File(filePath).toURI().toURL();
		clazzLoader = new URLClassLoader(new URL[] { url });
		clazz = (Class<CoreMarkerReplacer>) clazzLoader.loadClass(name);
		return clazz;

	}

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

	public List<CoreMarkerReplacer> getPluginCollection() {
		return this.pluginCollection;
	}

	public void setPluginCollection(final List<CoreMarkerReplacer> pluginCollection) {
		this.pluginCollection = pluginCollection;
	}
}