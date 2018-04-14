// $Id: cropname.java,v 1.6 2008/04/21 04:32:00 dmitriy Exp $
//Bee Copyright (c) 2004 Dmitriy Rogatkin
// Created on Apr 1, 2004
package org.bee.func;

import static org.bee.util.Misc.splitBy;
import static org.bee.util.Misc.wildCardToRegExpr;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="Dmitriy@mochamail.com">Dmitriy Rogatkin</a>
 *
 *         Provide class description here
 */
public class cropname {
	
	/**
	 * 
	 */
	public cropname() {
		super();
	}
	
	/**
	 * 
	 * @param param1
	 * @param param2
	 * @return
	 */
	public static List<String> eval(String fileName, String regularExpression) {
		return eval(fileName, regularExpression, "");
	}
	
	/**
	 * 
	 * @param param1
	 * @param param2
	 * @param replaceTo
	 * @return
	 */
	public static List<String> eval(String fileName, String regularExpression, final String replaceTo) {
		return eval(fileName, regularExpression, replaceTo, null);
	}
	
	/**
	 * 
	 * @param param1
	 * @param regularExpression
	 * @param replaceTo
	 * @param all
	 * @return
	 */
	public static List<String> eval(String fileName, String regularExpression, final String replaceTo, final String all) {
		final List<String> result = new ArrayList<String>();
		final String mask = regularExpression.replaceAll("\\?", "[^/\\]").replaceAll("\\*", "[^/\\]*");
		if (fileName.indexOf('*') < 0 && fileName.indexOf('?') < 0 || fileName.indexOf('\n') >= 0) {
			if (all == null) {
				result.add(fileName.replaceFirst(mask, replaceTo));
			} else {
				result.add(fileName.replaceAll(mask, replaceTo));
			}
		} else {
			final String[] pe = splitBy(fileName);
			// TODO: pe.length <= 1
			if (pe.length > 1) {
				new File(pe[0]).listFiles(new FileFilter() {
					public boolean accept(File pathname) {
						String n = pathname.getName();
						if (n.matches(wildCardToRegExpr(pe[pe.length - 1]))) {
							if (all == null) {
								result.add((pe[0] + n).replaceFirst(mask, replaceTo));
							} else {
								result.add((pe[0] + n).replaceAll(mask, replaceTo));
							}
						}
						
						return false;
					}
				});
			}
		}
		
		return result;
	}
}
