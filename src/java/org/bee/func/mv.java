// $Id: mv.java,v 1.7 2006/06/23 06:00:12 rogatkin Exp $
//Bee Copyright (c) 2004 Dmitriy Rogatkin
// Created on Apr 20, 2004
package org.bee.func;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import jdepend.framework.BeeHelper;

/**
 * @author <a href="dmitriy@mochamail.com">Dmitriy Rogatkin</a>
 *
 *         Provide class description here
 */
public class mv extends cp {
	
	public static List<String> eval(String... copyPairs) {
		List<String> result = new ArrayList<String>();
		for (int i = 0; i < copyPairs.length - 1; i += 2) {
			new mv().copy(result, copyPairs[i], copyPairs[i + 1], false);
		}
		return result;
	}
	
	/**
	 * 
	 * @param srcFile
	 * @param destFile
	 * @param append
	 * @return
	 * @see org.bee.func.cp#action(java.io.File, java.io.File, boolean)
	 */
	@Override
	protected String action(File srcFile, File destFile, boolean append) {
		if (BeeHelper.isDebugEnabled()) {
			BeeHelper.debug("mv: plan to move %s %s\n", srcFile.toString(), destFile.toString());
		} else {
			if (destFile.isDirectory()) {
				destFile = new File(destFile, srcFile.getName());
			}
			if (srcFile.renameTo(destFile) == false) {
				return null;
			}
		}
		
		return srcFile.getName();
	}
}
