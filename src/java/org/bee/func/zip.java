// $Id: zip.java,v 1.5 2012/03/02 04:07:28 dmitriy Exp $
//Bee Copyright (c) 2011 Dmitriy Rogatkin
// Created on Jun 26, 2011
package org.bee.func;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.AbstractCollection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.bee.util.Misc;

/** provides similar functionality to zip
 *  
 * @author Dmitriy
 *
 */
public class zip {

	/** main method of zipping
	 * 
	 * @param delPats 1 - zip archive name 2.. files to be zipped
	 * @return true if no error happened otherwise false
	 */
	public static boolean eval(Object... args) {
		if (args.length < 2)
			return false;
		try {
			File zipFile = new File(args[0].toString());
			//System.out.printf("Zipping to %s args : %d%n", zipFile, args.length);
			//if (zipFile.createNewFile() == false) {
				//throw new IOException("Can't create zip file "+zipFile);
			//}
			ZipOutputStream zs = new ZipOutputStream(new FileOutputStream(zipFile));
			zs.setLevel(ZipOutputStream.DEFLATED);
			zs.setComment("Generated by 7Bee, Copyright 2004 - 2012 by Dmitriy Rogatkin");
			ArrayList<String> parameters = new ArrayList<String>(args.length);
			for (int i = 1; i < args.length; i++) {
				if (args[i] instanceof Collection) {
					parameters.addAll((AbstractCollection<? extends String>) args[i]);
				} else if (args[i] instanceof Object[])
					for (Object o : (Object[]) args[i])
						parameters.add(o.toString());
				else
					parameters.add(args[i].toString());
			}
			
			zipFiles(null, parameters.toArray(new String[parameters.size()]), "", zs);
			zs.close();
			return true;
		} catch (IOException e) {
			System.err.printf("zip:error: %s%n", e);
			e.printStackTrace();
		}
		return false;
	}

	private static void zipFiles(File folder, String[] selection, String current, ZipOutputStream zs)
			throws IOException {
		//System.out.printf("Processing: %s, %s / %s%n", folder, Arrays.toString(selection), current);
		for (String s : selection) {
			File f = folder==null?new File(s): new File(folder, s);
			if (f.isFile() && f.canRead()) {
				ZipEntry e = new ZipEntry(current + f.getName());
				e.setTime(f.lastModified());
				zs.putNextEntry(e);
				FileInputStream is;
				Misc.copyStream(is = new FileInputStream(f), zs, -1);
				zs.closeEntry();
				is.close();
			} else if (f.isDirectory()) {
				zipFiles(f, f.list(), current + f.getName() + '/', zs);
			}
		}
	}
}