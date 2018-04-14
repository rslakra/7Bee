// $Id: write.java,v 1.9 2008/03/04 04:05:27 dmitriy Exp $
//Bee Copyright (c) 2004 Dmitriy Rogatkin
// Created on Mar 26, 2004
package org.bee.func;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import jdepend.framework.BeeHelper;

/**
 * @author <a href="Dmitriy@mochamail.com">Dmitriy Rogatkin</a>
 *
 *         Provide class description here
 */
public class write {
	
	/**
	 * 
	 */
	public write() {
		super();
	}
	
	/**
	 * 
	 * @param params
	 * @return
	 */
	public static boolean eval(Object... params) {
		return write(false, params);
	}
	
	/**
	 * 
	 * @param append
	 * @param params
	 * @return
	 */
	protected static boolean write(boolean append, Object... params) {
		if (params.length < 2) {
			return false;
		}
		
		FileWriter fileWriter = null;
		File file = new File(params[0].toString());
		if (file.getParentFile() != null && !file.getParentFile().exists()) {
			if (!file.getParentFile().mkdirs()) {
				BeeHelper.warn("Unable to create folder:" + file.getParentFile().getAbsolutePath());
				return false;
			}
		}
		
		try {
			fileWriter = new FileWriter(file, append);
			for (int i = 1; i < params.length; i++) {
				if (params[i] != null) {
					fileWriter.write(params[i].toString());
				}
			}
		} catch (IOException ex) {
			System.err.printf("bee:func:write an exception %s in writing of %s.\n", ex, params[0]);
			return false;
		} finally {
			BeeHelper.closeSilently(fileWriter);
		}
		
		return true;
	}
}
