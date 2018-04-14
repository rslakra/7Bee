// $Id: Logger.java,v 1.2 2004/04/24 07:42:45 rogatkin Exp $
// Bee Copyright (c) 2004 Dmitriy Rogatkin
package org.bee.util;

/**
 * @author <a href="mailto:dmitriy@mochamail.com">Dmitriy Rogatkin</a>
 */
import java.util.logging.LogManager;

public final class Logger {
	
	/** logger */
	public static java.util.logging.Logger logger;
	
	static {
		logger = java.util.logging.Logger.getLogger("Bee");
		LogManager.getLogManager().addLogger(logger);
	}
	
	/** singleton instance. */
	private Logger() {
		throw new RuntimeException("Object creation is not allowed for this object!");
	}
	
	/**
	 * Adds the logger for the <code>logClassName</code>.
	 * 
	 * @param logClassName
	 */
	public static final void addLoggerClass(String logClassName) {
		try {
			logger = (java.util.logging.Logger) Class.forName(logClassName).newInstance();
			LogManager.getLogManager().addLogger(logger);
		} catch (Error ex) {
			logger.severe("An error happened at initiation or adding logger class:" + logClassName + " " + ex);
		} catch (Exception ex) {
			logger.severe("An exception happened at initiation or adding logger class:" + logClassName + " " + ex);
		}
	}
}