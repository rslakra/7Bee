// $Id: timestamp.java,v 1.1 2004/03/19 06:43:45 rogatkin Exp $
//Bee Copyright (c) 2004 Dmitriy Rogatkin
// Created on Mar 18, 2004
package org.bee.func;

import java.io.File;
import java.util.Date;

/**
 * @author <a href="Dmitriy@mochamail.com">Dmitriy Rogatkin</a>
 *
 *         Returns last time file modification, or null if non exists
 */
public final class timestamp {
	
	/**
	 * 
	 */
	public timestamp() {
		super();
	}
	
	public static Date eval(String fileName) {
		File file = new File(fileName);
		if (file.exists())
			return new Date(file.lastModified());
		return null;
	}
	
}
