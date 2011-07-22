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
package com.lars_albrecht.foldergen.helper;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * Some helper functions.
 * 
 * @author lalbrecht
 * @version 1.0.0.0
 */
public class Utilities {

	/**
	 * Returns the number of appearces of "character" in "string".
	 * 
	 * @param string
	 *            String
	 * @param findStr
	 *            String
	 * 
	 * @return Integer
	 */
	public static Integer countChars(final String string, final String findStr) {
		String strCopy = new String(string);
		return strCopy.replaceAll("[" + findStr + "]", "").length();
	}

	/**
	 * Returns true is the given String is a boolean.
	 * 
	 * @param strBool
	 * @return Boolean
	 */
	public static Boolean isBoolean(final String strBool) {
		return strBool.equalsIgnoreCase("false") || strBool.equalsIgnoreCase("true");
	}

	/**
	 * Returns the content of a file as string.
	 * 
	 * @param srcFile
	 *            File
	 * @return String
	 * @throws IOException
	 */
	public static String getFileContent(final File srcFile) throws IOException {
		if((srcFile != null) && srcFile.exists() && srcFile.isFile()) {
			BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(srcFile)));
			StringBuffer contentOfFile = new StringBuffer();
			String line;
			while((line = br.readLine()) != null) {
				contentOfFile.append(line);
			}
			return contentOfFile.toString();
		}
		return null;
	}

	/**
	 * 
	 * @param srcUrl
	 *            URL
	 * @return String
	 * @throws IOException
	 */
	public static String getFileContentFromWeb(final URL srcUrl) throws IOException {
		String contentOfFile = "";
		if((srcUrl != null)) {
			BufferedReader br = new BufferedReader(new InputStreamReader(srcUrl.openStream()));
			String line;
			while((line = br.readLine()) != null) {
				contentOfFile += line;
			}
			return contentOfFile.toString();
		}
		return null;
	}

	/**
	 * Copy a file from source to target.
	 * 
	 * @param source
	 *            File
	 * @param target
	 *            File
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public static void copyFile(final File source, final File target) throws FileNotFoundException, IOException {
		FileChannel in = new FileInputStream(source).getChannel(), out = new FileOutputStream(target).getChannel();
		ByteBuffer buffer = ByteBuffer.allocate(1024);
		while(in.read(buffer) != -1) {
			buffer.flip(); // Prepare for writing
			out.write(buffer);
			buffer.clear(); // Prepare for reading
		}
		out.close();
		in.close();
	}

	/**
	 * Copy a directory from source to target.
	 * 
	 * @param source
	 *            File
	 * @param target
	 *            File
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public static void copyDir(final File source, final File target) throws FileNotFoundException, IOException {
		File[] files = source.listFiles();
		target.mkdirs();
		for(File file : files) {
			if(file.isDirectory()) {
				Utilities.copyDir(file,
						new File(target.getAbsolutePath() + System.getProperty("file.separator") + file.getName()));
			} else {
				Utilities.copyFile(file, new File(target.getAbsolutePath() + System.getProperty("file.separator")
						+ file.getName()));
			}
		}
	}
}