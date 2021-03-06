/* bee - LoggerConfig.java
 * Copyright (C) 1999-2004 Dmitriy Rogatkin.  All rights reserved.
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 *  THIS SOFTWARE IS PROVIDED BY THE AUTHOR AND CONTRIBUTORS ``AS IS'' AND
 *  ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 *  IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 *  ARE DISCLAIMED. IN NO EVENT SHALL THE AUTHOR OR CONTRIBUTORS BE LIABLE FOR
 *  ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 *  (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 *  LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 *  ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 *  (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 *  SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 *  $Id: LoggerConfig.java,v 1.2 2005/06/15 08:02:23 rogatkin Exp $
 * Created on Jul 9, 2004
 */

package org.bee.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.logging.Level;

/**
 * @author dmitriy
 */
public class LoggerConfig extends Properties {
	
	/** serialVersionUID */
	private static final long serialVersionUID = 1L;
	
	public LoggerConfig() {
		setProperty("java.util.logging.FileHandler.pattern", "%h/java%u.log");
		setProperty("java.util.logging.FileHandler.limit", "50000");
		setProperty("java.util.logging.FileHandler.count", "1");
		setProperty("java.util.logging.FileHandler.formatter", "java.util.logging.XMLFormatter");
		setProperty("java.util.logging.ConsoleHandler.level", "SEVERE");
		setProperty("java.util.logging.ConsoleHandler.formatter", "java.util.logging.SimpleFormatter");
		setProperty("handlers", "java.util.logging.ConsoleHandler");
	}
	
	/**
	 * 
	 * @param logLevel
	 */
	public void setLogLevel(Level logLevel) {
		if (logLevel != null) {
			setProperty(".level", logLevel.toString());
			setProperty("java.util.logging.ConsoleHandler.level", logLevel.toString());
		} else {
			remove("java.util.logging.ConsoleHandler.formatter");
		}
	}
	
	/**
	 * @param logFileName
	 */
	public void setLogFile(String logFileName) {
		// check can write
		setProperty("java.util.logging.FileHandler.pattern", logFileName);
		setProperty("handlers", "java.util.logging.ConsoleHandler, java.util.logging.ConsoleHandler");
	}
	
	/**
	 * 
	 * @return
	 */
	public String create() {
		File logFile = null;
		try {
			logFile = File.createTempFile("bee", "logger-config.properties");
			logFile.deleteOnExit();
			FileOutputStream fos;
			store(fos = new FileOutputStream(logFile), "bee temporary logger config file");
			fos.close();
		} catch (IOException ex) {
			System.err.printf("LoggerConfig:error: Problem in creation logger config file %s\n", ex);
			return null;
		}
		
		return logFile.getAbsolutePath();
	}
}
