// $Id: newerthan.java,v 1.17 2005/07/27 18:40:05 rogatkin Exp $
//Bee Copyright (c) 2004 Dmitriy Rogatkin
// Created on Mar 24, 2004
package org.bee.func;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.bee.util.FolderFilter;

import jdepend.framework.BeeHelper;

/**
 * @author <a href="dmitriy@mochamail.com">Dmitriy Rogatkin </a>
 * 
 *         Provide class description here
 */
public class newerthan {
	
	/**
	 * find all files names which are newer than corresponding files in another
	 * tree
	 * 
	 */
	public newerthan() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	/**
	 * 
	 * @param srcPath
	 * @param dstPath
	 * @return
	 */
	public static List<String> eval(String srcPath, String dstPath) {
		// TODO: add 3rd parameter for possible default result if null list
		// happened
		// BeeHelper.debug("Parameters %s -> %s\n", srcPath, dstPath);
		ArrayList<String> result = new ArrayList<String>();
		srcPath = normalize(srcPath);
		dstPath = normalize(dstPath);
		String srcMask = extractFile(srcPath);
		String destMask = extractFile(dstPath);
		srcPath = extractPath(srcPath);
		dstPath = extractPath(dstPath);
		BeeHelper.debug("newerthan:debug: sp %s sm %s dp %s dm %s in %s\n", new File(srcPath).getAbsolutePath(), srcMask, dstPath, destMask, new File("./").getAbsolutePath());
		processDirectory(result, new File(srcPath)/* .getAbsoluteFile() */, srcPath, srcMask, dstPath, destMask);
		BeeHelper.debug("newerthan:debug: result: %s\n", result);
		return result;
	}
	
	/**
	 * 
	 * @param string
	 * @return
	 */
	protected static String normalize(String string) {
		assert File.separatorChar == '/' || File.separatorChar == '\\';
		char fileSeparator = '\\';
		if (File.separatorChar == fileSeparator) {
			fileSeparator = '/';
		}
		return string.replace(fileSeparator, File.separatorChar);
	}
	
	/**
	 * 
	 * @param string
	 * @return
	 */
	protected static String extractPath(String string) {
		int lastIndex = string.lastIndexOf(File.separatorChar);
		if (lastIndex < 0) {
			return ("." + File.separatorChar);
		} else {
			return string.substring(0, lastIndex);
		}
	}
	
	/**
	 * 
	 * @param string
	 * @return
	 */
	protected static String extractFile(String string) {
		int lastIndex = string.lastIndexOf(File.separatorChar);
		if (lastIndex < 0) {
			return string;
		} else {
			return string.substring(lastIndex + 1);
		}
	}
	
	/**
	 * 
	 * @param result
	 * @param path
	 * @param srcPath
	 * @param srcMask
	 * @param destPath
	 * @param destMask
	 */
	protected static void processDirectory(List<String> result, File path, String srcPath, String srcMask, String destPath, String destMask) {
		if (path.exists() == false || path.isDirectory() == false) {
			return;
		}
		
		BeeHelper.debug("newerthan:debug: process %s\n", path);
		File[] listFiles = path.listFiles(new SmartFileFilter(srcMask, srcPath, destPath, destMask));
		for (File file : listFiles) {
			result.add(file.getAbsolutePath());
		}
		listFiles = path.listFiles(new FolderFilter());
		for (File file : listFiles) {
			processDirectory(result, file, srcPath, srcMask, destPath, destMask);
		}
	}
	
	protected static class SmartFileFilter implements FileFilter {
		Pattern p;
		int ml, sl;
		
		String srcPath, destPath, destMask;
		String srcMask;
		List<String> result;
		
		SmartFileFilter(/* List <String> result, */String mask, String srcPath, String destPath, String destMask) {
			p = Pattern.compile("[^/\\\\?:\\*]*" + mask);
			ml = mask.length();
			this.srcMask = mask;
			this.destPath = destPath;
			this.srcPath = srcPath;
			this.destMask = destMask;
			
			sl = srcPath.length();
			if (sl > 0 && (srcPath.charAt(sl - 1) == '\\' || srcPath.charAt(sl - 1) == '/')) {
				sl--; // for sep
			}
		}
		
		/**
		 * 
		 * @param pathname
		 * @return
		 * @see java.io.FileFilter#accept(java.io.File)
		 */
		public boolean accept(File pathname) {
			if (pathname.isDirectory()) {
				return false;
				// processDirectory(result, pathname, srcPath,
			}
			
			// srcMask, destPath, destMask);
			// TODO: path name can be one .java file which generates multiple
			// .class
			// files, so every of them should count (consider only top level,
			// not nested classes
			String n = pathname.getName();
			BeeHelper.debug("newerthan:Path to accept %s, current parent len %d, mask len %d\n", n, sl, ml);
			if (p.matcher(n).matches()) {
				n = n.substring(0, n.length() - ml);
				String parent = pathname.getParent();
				BeeHelper.debug("newerthan:Parent %s, src %s\n", parent, srcPath);
				assert parent.indexOf(srcPath) == 0;
				parent = parent.substring(sl);
				File destFile = new File(destPath + parent, n + destMask);
				// System.out.println("DF"+destFile);
				if (destFile.exists() == false) {
					return true;
				}
				return pathname.lastModified() > destFile.lastModified();
			}
			return false;
		}
	}
	
}
