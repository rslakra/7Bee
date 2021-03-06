// $Id: cp.java,v 1.13 2006/06/23 06:00:12 rogatkin Exp $
//Bee Copyright (c) 2004 Dmitriy Rogatkin
// Created on Apr 20, 2004
package org.bee.func;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.bee.util.Misc;

import jdepend.framework.BeeHelper;

/**
 * @author <a href="dmitriy@mochamail.com">Dmitriy Rogatkin</a>
 * 
 *         A function for copying files, sources can be URL
 */
public class cp {
	protected boolean append;
	
	/**
	 * 
	 * @param copyPairs
	 * @return
	 */
	public static List<String> eval(Object... copyPairs) {
		List<String> result = new ArrayList<String>();
		for (int i = 0; i < copyPairs.length - 1; i += 2) {
			new cp().copy(result, copyPairs[i].toString(), copyPairs[i + 1].toString(), false);
		}
		return result;
	}
	
	/**
	 * 
	 * @param copied
	 * @param srcMask
	 * @param dstMask
	 * @param append
	 */
	protected void copy(List<String> copied, String srcMask, String dstMask, boolean append) {
		BeeHelper.debug("cp: %s %s (append %b)\n", srcMask, dstMask, append);
		this.append = append;
		String[] srcParts = Misc.splitBy(srcMask);
		String[] dstParts = Misc.splitBy(dstMask);
		if (srcParts.length == 0 || dstParts.length == 0) {
			return;
		}
		File srcFile = new File(srcParts[0]);
		if (srcParts.length == 1) {
			File dstFile = new File(dstParts[0]);
			if (dstParts.length == 1) {
				if (dstFile.getParentFile() != null && dstFile.getParentFile().exists() == false) {
					dstFile.getParentFile().mkdirs();
				}
				action(srcFile, dstFile, append);
			} else {
				if (dstFile.exists() == false) {
					dstFile.mkdirs();
				}
				action(srcFile, new File(dstParts[0], srcFile.getName()), append);
			}
		} else {
			copy(copied, srcFile, srcParts, 1, dstParts[0], dstParts.length > 1 ? dstParts[dstParts.length - 1] : "", append);
		}
	}
	
	/**
	 * 
	 * @param copied
	 * @param srcPath
	 * @param parts
	 * @param pos
	 * @param destPath
	 * @param renMask
	 * @param append
	 */
	protected void copy(List<String> copied, File srcPath, String[] parts, int pos, String destPath, String renMask, boolean append) {
		srcPath.listFiles(new FileCopier(copied, parts, pos, destPath, renMask, append));
	}
	
	/**
	 * 
	 * @author Rohtash Lakra (rohtash.lakra@devamatre.com)
	 * @author Rohtash Singh Lakra (rohtash.singh@gmail.com)
	 * @created 2018-04-13 07:27:07 PM
	 * @version 1.0.0
	 * @since 1.0.0
	 */
	protected class FileCopier implements FileFilter {
		protected String[] parts;
		
		protected int pos;
		protected String pattern;
		protected String srcMask, renMask;
		protected String destPath;
		protected int srcConstLen;
		protected boolean append;
		protected List<String> copied;
		
		FileCopier(List<String> copied, String[] parts, int index, String destPath, String renMask, boolean append) {
			this.copied = copied;
			this.parts = parts;
			this.append = append;
			pos = index;
			pattern = Misc.wildCardToRegExpr(this.parts[pos]);
			this.destPath = destPath;
			srcMask = this.parts[parts.length - 1];
			int vp = srcMask.lastIndexOf('?');
			int vp2 = srcMask.lastIndexOf('*');
			if (vp >= 0 && vp > vp2) {
				srcConstLen = srcMask.length() - vp - 1;
			} else if (vp2 >= 0) {
				srcConstLen = srcMask.length() - vp2 - 1;
			}
			
			if (pos != (this.parts.length - 1)) {
				// TODO: it can be calculated one and then spreaded, however it
				// isn't big deal
				srcMask = Misc.wildCardToRegExpr(this.parts[parts.length - 1]);
			} else {
				srcMask = pattern;
			}
			vp = renMask.lastIndexOf('?');
			vp2 = renMask.lastIndexOf('*');
			if (vp >= 0 && vp > vp2) {
				renMask = renMask.substring(vp + 1);
			} else if (vp2 >= 0) {
				renMask = renMask.substring(vp2 + 1);
			}
			if (renMask.length() == 0) {
				srcConstLen = 0;
			}
			this.renMask = renMask;
		}
		
		/**
		 * 
		 * @param pathname
		 * @return
		 * @see java.io.FileFilter#accept(java.io.File)
		 */
		public boolean accept(File pathname) {
			String name = pathname.getName();
			if (name.matches(pattern)) {
				if (pathname.isFile()) {
					BeeHelper.debug("cp: %s(%s) \n", pathname, srcMask);
					if (name.matches(srcMask)) {
						String srcPath = pathname.toString(); // getParent();
						File destFile = new File(destPath, srcPath.substring(parts[0].length(), srcPath.length() - srcConstLen) + renMask);
						// System.out.printf("cp: dest %s \n", destFile);
						if (destFile.getParentFile().exists() == false) {
							destFile.getParentFile().mkdirs();
						}
						return action(pathname, destFile, append) != null;
					}
				} else if (pathname.isDirectory() && pos < parts.length - 1) {
					copy(copied, pathname, parts, pos + 1, destPath, renMask, append);
				}
			}
			
			return false;
		}
	}
	
	/**
	 * 
	 * @param srcFile
	 * @param destFile
	 * @param append
	 * @return
	 */
	protected String action(File srcFile, File destFile, boolean append) {
		if (BeeHelper.isDebugEnabled()) {
			BeeHelper.debug("cp: plan to copy %s %s (append %b)\n", srcFile.toString(), destFile.toString(), append);
		} else {
			FileInputStream fis = null;
			FileOutputStream fos = null;
			try {
				if (destFile.isDirectory()) {
					destFile = new File(destFile, srcFile.getName());
				}
				Misc.copyStream(fis = new FileInputStream(srcFile), fos = new FileOutputStream(destFile, append), 0);
			} catch (IOException ioe) {
				System.err.printf("bee:func:cp:error Can't copy %s to %s due exception %s\n", srcFile, destFile, ioe);
				return null;
			} finally {
				BeeHelper.closeSilently(fis, fos);
			}
		}
		
		return srcFile.getName();
	}
}
