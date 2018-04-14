/*******************************************************************************
 * Copyright (C) Devamatre Inc. 2009-2018. All rights reserved.
 * 
 * This code is licensed to Devamatre under one or more contributor license
 * agreements. The reproduction, transmission or use of this code or the snippet
 * is not permitted without prior express written consent of Devamatre.
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the license is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied and the
 * offenders will be liable for any damages. All rights, including but not
 * limited to rights created by patent grant or registration of a utility model
 * or design, are reserved. Technical specifications and features are binding
 * only insofar as they are specifically and expressly agreed upon in a written
 * contract.
 * 
 * You may obtain a copy of the License for more details at:
 * http://www.devamatre.com/licenses/license.txt.
 * 
 * Devamatre reserves the right to modify the technical specifications and or
 * features without any prior notice.
 *******************************************************************************/
package jdepend.framework;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.security.KeyStore;
import java.util.Arrays;
import java.util.Date;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

/**
 * @author Rohtash Lakra (rohtash.lakra@devamatre.com)
 * @author Rohtash Singh Lakra (rohtash.singh@gmail.com)
 * @created 2018-04-13 01:46:02 PM
 * @version 1.0.0
 * @since 1.0.0
 */
public final class BeeHelper {
	
	/** UTF-8 */
	public static String UTF_8 = "UTF-8";
	/** ISO-8859-1 */
	public static String ISO_8859_1 = "ISO-8859-1";
	
	/** JVM-Type - JVM_ANDROID */
	public static String JVM_ANDROID = "Dalvik".intern();
	
	/** LINE_SEPARATOR */
	private static final String LINE_SEPARATOR = System.getProperty("line.separator").intern();
	
	/** USER_DIR */
	private static final String USER_DIR = System.getProperty("user.dir").intern();
	
	/** USER_HOME */
	private static final String USER_HOME = System.getProperty("user.home").intern();
	
	/** JAVA_HOME */
	private static final String JAVA_HOME = System.getProperty("java.home").intern();
	
	/** LOG_PATTERN */
	// private static final String LOG_PATTERN = "[%d{MM-dd-yyyy hh:mm:ss.S a}]
	// %5p [%t] [%c{1}(%L)] - %m%n";
	private static final String LOG_PATTERN = "[%1$tF %1$tT] [%2$-7s] - %3$s%n".intern();
	
	/** mRootLogger */
	private static Logger mRootLogger;
	
	/** singleton instance. */
	private BeeHelper() {
		throw new RuntimeException("Object creation is not allowed for this object!");
	}
	
	/**
	 * Returns the system line separator.
	 * 
	 * @return
	 */
	public static final String getLineSeparator() {
		return LINE_SEPARATOR;
	}
	
	/**
	 * Returns the user's directory.
	 * 
	 * @return
	 */
	public static final String getUserDir() {
		return USER_DIR;
	}
	
	/**
	 * Returns the user's home directory.
	 * 
	 * @return
	 */
	public static final String getUserHome() {
		return USER_HOME;
	}
	
	/**
	 * Returns the java home folder.
	 * 
	 * @return
	 */
	public static final String getJavaHome() {
		return JAVA_HOME;
	}
	
	/**
	 * Returns the logs directory under the project.
	 * 
	 * @return
	 */
	public static final String getLogsDir() {
		return getUserDir() + File.separator + "logs";
	}
	
	/**
	 * Returns the java's temp folder.
	 * 
	 * @return
	 */
	public static final String getTempDir() {
		return System.getProperty("java.io.tmpdir");
	}
	
	/**
	 * Returns true if the object is null otherwise false.
	 * 
	 * @param string
	 * @return
	 */
	public static final boolean isNull(Object object) {
		return (object == null);
	}
	
	/**
	 * Returns true if the object is not null otherwise false.
	 * 
	 * @param object
	 * @return
	 */
	public static final boolean isNotNull(Object object) {
		return (!isNull(object));
	}
	
	/**
	 * Returns true if either the string is null or length is 0(zero) otherwise
	 * false.
	 * 
	 * @param string
	 * @return
	 */
	public static final boolean isNullOrEmpty(CharSequence string) {
		return (isNull(string) || string.length() == 0);
	}
	
	/**
	 * Returns true if either the string is null or length is 0(zero) otherwise
	 * false.
	 * 
	 * @param string
	 * @return
	 */
	public static final boolean isNullOrEmpty(final byte[] dataBytes) {
		return (isNull(dataBytes) || dataBytes.length == 0);
	}
	
	/**
	 * Returns true if the JVM is android (dalvik) otherwise false.
	 * 
	 * @param
	 * @return
	 */
	public static final boolean isAndroid() {
		return (System.getProperty("java.vm.name").startsWith(JVM_ANDROID));
	}
	
	/**
	 * Returns the path string for the given class.
	 * 
	 * @param className
	 * @return
	 */
	public static final String pathString(Class<?> className, boolean pathOnly) {
		String urlString = null;
		URL url = className.getResource(className.getSimpleName() + ".class");
		if (url != null) {
			urlString = url.toExternalForm();
			/*
			 * The <code>urlString</code> most likely ends with a /, then the
			 * full
			 * class name with . replaced with /, and .class. Cut that part off
			 * if
			 * present. If not also check
			 * for backslashes instead. If that's also not present just return
			 * null
			 */
			if (pathOnly) {
				int fileIndex = urlString.indexOf(className.getSimpleName());
				urlString = urlString.substring(0, fileIndex);
			}
			
			/*
			 * <code>urlString</code> is now the URL of the location, but
			 * possibly
			 * with jar: in front and a trailing !
			 */
			if (urlString.startsWith("jar:") && urlString.endsWith("!")) {
				urlString = urlString.substring(4, urlString.length() - 1);
			}
			
			/*
			 * <code>urlString</code> is now the URL of the location, but
			 * possibly
			 * with file: in front.
			 */
			else if (urlString.startsWith("file:")) {
				urlString = urlString.substring("file:".length(), urlString.length());
			}
		}
		
		return urlString;
	}
	
	/**
	 * Returns the path string for the given class.
	 * 
	 * @param className
	 * @return
	 */
	public static final String pathString(Class<?> className) {
		return pathString(className, true);
	}
	
	/**
	 * 
	 * @param parentFolder
	 * @param fileName
	 * @return
	 */
	public static final String pathString(final String parentFolder, final String fileName) {
		if (isNullOrEmpty(parentFolder)) {
			return fileName;
		} else if (isNullOrEmpty(fileName)) {
			return parentFolder;
		} else if (parentFolder.endsWith(File.separator) || fileName.startsWith(File.separator)) {
			return parentFolder + fileName;
		} else {
			return parentFolder + File.separator + fileName;
		}
	}
	
	/**
	 * Closes the specified <code>mCloseables</code> objects.
	 *
	 * @param mCloseables
	 */
	public static final void closeSilently(Object... mCloseables) {
		if (isNotNull(mCloseables)) {
			for (Object mCloseable : mCloseables) {
				try {
					if (mCloseable instanceof Closeable) {
						((Closeable) mCloseable).close();
					} else if (mCloseable instanceof Socket) {
						((Socket) mCloseable).close();
					} else if (mCloseable instanceof ServerSocket) {
						((ServerSocket) mCloseable).close();
					}
				} catch (IOException ex) {
					error(ex);
				}
			}
		}
	}
	
	/**
	 * Returns the bytes of the specified input stream.
	 *
	 * @param inputStream
	 * @return
	 * @throws IOException
	 */
	public static final byte[] readBytes(final InputStream inputStream, final boolean closeStream) throws IOException {
		info("+readBytes(inputStream, " + closeStream + ")");
		byte[] resultBytes = null;
		if (inputStream != null) {
			ByteArrayOutputStream outputStream = null;
			BufferedInputStream bInputStream = new BufferedInputStream(inputStream);
			try {
				bInputStream = new BufferedInputStream(inputStream);
				outputStream = new ByteArrayOutputStream();
				byte[] buffer = new byte[bInputStream.available()];
				int length = 0;
				while ((length = inputStream.read(buffer)) != -1) {
					outputStream.write(buffer, 0, length);
				}
				
				outputStream.flush();
				resultBytes = outputStream.toByteArray();
			} catch (IOException ex) {
				error(ex);
				throw ex;
			} finally {
				/* close streams. */
				closeSilently(outputStream);
				if (closeStream) {
					closeSilently(bInputStream);
				}
			}
		}
		
		info("-readBytes(), resultBytes:" + resultBytes);
		return resultBytes;
	}
	
	/**
	 * Returns the bytes of the specified pathString.
	 * 
	 * @param pathString
	 * @param closeStream
	 * @return
	 * @throws IOException
	 */
	public static final byte[] readBytes(final String pathString, final boolean closeStream) throws IOException {
		return readBytes(new FileInputStream(pathString), closeStream);
	}
	
	/**
	 * Writes the <code>bytes</code> to <code>outputStream</code> and closes it.
	 *
	 * @param dataBytes
	 * @param outputStream
	 * @param closeStream
	 * @throws IOException
	 */
	public static final boolean writeBytes(byte[] dataBytes, OutputStream outputStream, boolean closeStream) throws IOException {
		boolean result = false;
		if (dataBytes != null && outputStream != null) {
			try {
				outputStream.write(dataBytes);
				/* flush output streams. */
				outputStream.flush();
				result = true;
			} catch (IOException ex) {
				error(ex);
				throw ex;
			} finally {
				/* close streams. */
				if (closeStream) {
					closeSilently(outputStream);
				}
			}
		}
		
		return result;
	}
	
	/**
	 * Copies the contents of an <code>sourceStream</code> into an
	 * <code>targetStream</code>.
	 *
	 * @param sourceStream
	 * @param targetStream
	 * @param closeStreams
	 * @return
	 * @throws IOException
	 */
	public static final int copyStream(final InputStream sourceStream, final OutputStream targetStream, boolean closeStreams) throws IOException {
		info("+copyStream(" + sourceStream + ", " + targetStream + ", " + closeStreams + ")");
		int fileSize = 0;
		if (sourceStream != null && targetStream != null) {
			try {
				// buffer
				byte[] buffer = new byte[sourceStream.available()];
				int byteCount = 0;
				while ((byteCount = sourceStream.read(buffer)) != -1) {
					targetStream.write(buffer, 0, byteCount);
					fileSize += byteCount;
				}
				
				/* flush output streams. */
				targetStream.flush();
			} catch (IOException ex) {
				error(ex);
				throw ex;
			} finally {
				/* close streams. */
				if (closeStreams) {
					closeSilently(sourceStream, targetStream);
				}
			}
		}
		
		info("-copyStream(), fileSize:" + fileSize);
		return fileSize;
	}
	
	/**
	 * Converts the specified <code>bytes</code> to the specified
	 * <code>charsetName</code> String.
	 * 
	 * @param bytes
	 * @param charsetName
	 * @return
	 */
	public static final String toString(byte[] bytes, String charsetName) {
		String bytesAsString = null;
		if (!isNull(bytes)) {
			try {
				if (isNullOrEmpty(charsetName)) {
					bytesAsString = new String(bytes);
				} else {
					bytesAsString = new String(bytes, charsetName);
				}
			} catch (Exception ex) {
				error(ex);
				bytesAsString = (isNull(bytes) ? null : bytes.toString());
			}
		}
		
		return bytesAsString;
	}
	
	/**
	 * Returns the string representation of the given bytes.
	 * 
	 * @param bytes
	 * @return
	 */
	public static final String toString(byte[] bytes) {
		return toString(bytes, null);
	}
	
	/**
	 * Returns the string representation of the specified <code>object</code>
	 * object, if it's not
	 * null otherwise empty string.
	 *
	 * @param object
	 * @return
	 */
	public static final String toString(final Object object) {
		return (object == null ? "".intern() : object.toString());
	}
	
	/**
	 * Returns the string representation of the <code>throwable</code> object.
	 * 
	 * @param throwable
	 * @return
	 */
	public static final String toString(final Throwable throwable) {
		if (throwable == null) {
			return "".intern();
		} else {
			final StringWriter stringWriter = new StringWriter();
			final PrintWriter printWriter = new PrintWriter(stringWriter);
			throwable.printStackTrace(printWriter);
			printWriter.close();
			return stringWriter.toString();
		}
	}
	
	/**
	 * Returns the string representation of the given objects.
	 * 
	 * @param objects
	 * @param addNewLine
	 * @return
	 */
	public static final String toString(final Object[] objects, final boolean addNewLine) {
		final StringBuffer strBuilder = new StringBuffer();
		if (isNotNull(objects)) {
			if (addNewLine) {
				strBuilder.append("\n");
			}
			
			for (int i = 0; i < objects.length; i++) {
				if (isNotNull(objects[i])) {
					if (objects[i] instanceof String) {
						strBuilder.append((String) objects[i]);
					} else {
						strBuilder.append(objects[i].toString());
					}
				}
				
				// append new line character.
				if (i < objects.length - 1) {
					if (addNewLine) {
						strBuilder.append("\n");
					} else {
						strBuilder.append(" ");
					}
				}
			}
		}
		
		return strBuilder.toString();
	}
	
	/**
	 * Returns the string representation of the given objects.
	 * 
	 * @param objects
	 * @return
	 */
	public static final String toString(final Object[] objects) {
		return toString(objects, false);
	}
	
	/**
	 * Converts the <code>objects</code> into <code>String[]</code>
	 * 
	 * @param objects
	 * @return
	 */
	public static final String[] toStringArray(final Object... objects) {
		// return Arrays.copyOf(objects, objects.length, String[].class);
		return Arrays.stream(objects).toArray(String[]::new);
	}
	
	/**
	 * /**
	 * Returns the UTF-8 String representation of the given <code>bytes</code>.
	 * 
	 * @param bytes
	 * @param replaceNonDigitCharacters
	 * @return
	 */
	public static final String toUTF8String(byte[] bytes, boolean replaceNonDigitCharacters) {
		String utf8String = toString(bytes, UTF_8);
		if (replaceNonDigitCharacters && isNullOrEmpty(utf8String)) {
			utf8String = utf8String.replaceAll("\\D+", "");
		}
		
		return utf8String;
	}
	
	/**
	 * Returns the UTF-8 String representation of the given <code>bytes</code>.
	 * 
	 * @param bytes
	 * @return
	 */
	public static final String toUTF8String(byte[] bytes) {
		return toUTF8String(bytes, false);
	}
	
	/**
	 * Returns the UTF-8 String representation of the given <code>string</code>.
	 * 
	 * @param string
	 * @return
	 */
	public static final String toUTF8String(String string) {
		return toUTF8String(string.getBytes());
	}
	
	/**
	 * Returns the ISO-8859-1 String representation of the given
	 * <code>bytes</code>.
	 * 
	 * @param bytes
	 * @return
	 */
	public static final String toISOString(byte[] bytes) {
		return toString(bytes, ISO_8859_1);
	}
	
	/**
	 * Converts the specified <code>string</code> into bytes using the specified
	 * <code>charsetName</code>.
	 * 
	 * @param string
	 * @param charsetName
	 * @return
	 */
	public static final byte[] toBytes(String string, String charsetName) {
		byte[] stringAsBytes = null;
		if (!isNullOrEmpty(string)) {
			try {
				stringAsBytes = isNullOrEmpty(charsetName) ? string.getBytes() : string.getBytes(charsetName);
			} catch (Exception ex) {
				error(ex);
			}
		}
		
		return stringAsBytes;
	}
	
	/**
	 * Converts the specified <code>string</code> into bytes.
	 * 
	 * @param string
	 * @return
	 */
	public static final byte[] toBytes(String string) {
		return toBytes(string, null);
	}
	
	/**
	 * Converts the specified <code>string</code> into UTF-8 bytes.
	 * 
	 * @param string
	 * @return
	 */
	public static final byte[] toUTF8Bytes(String string) {
		return toBytes(string, UTF_8);
	}
	
	/**
	 * Returns the bytes of the specified input stream.
	 *
	 * @param inputStream
	 * @return
	 * @throws IOException
	 */
	public static final InputStream toInputStream(final byte[] dataBytes) {
		return new ByteArrayInputStream(dataBytes);
	}
	
	/**
	 * Returns the boolean value of the given object.
	 * 
	 * @param object
	 * @return
	 */
	public static final boolean parseBoolean(final Object object) {
		return (isNull(object) ? false : Boolean.valueOf(object.toString()));
	}
	
	/**
	 * Returns true if the key store is supported otherwise false.
	 *
	 * @param keyStoreStream
	 * @param keyStorePass
	 * @return
	 */
	public static final KeyStore initKeyStore(final InputStream keyStoreStream, final char[] keyStorePass) {
		KeyStore keyStore = null;
		try {
			keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
			info("keyStoreType:" + keyStore.getType());
			keyStore.load(keyStoreStream, keyStorePass);
		} catch (Exception ex) {
			error(ex);
		}
		
		return keyStore;
	}
	
	/**
	 * Returns true if the key store is supported otherwise false.
	 *
	 * @param keyStoreStream
	 * @param keyStorePassword
	 * @return
	 */
	public static final boolean isSupportedKeyStore(final InputStream keyStoreStream, final String keyStorePassword) {
		return (initKeyStore(keyStoreStream, keyStorePassword.toCharArray()) != null);
	}
	
	/**
	 * Returns the new IOException.
	 * 
	 * @param throwable
	 * @return
	 */
	public static final IOException newIOException(final Throwable throwable) {
		return (isNull(throwable) ? new IOException() : new IOException(throwable.toString(), throwable));
	}
	
	/**
	 * Returns the singleton instance of the root logger.
	 *
	 * @return
	 */
	private static final Logger getRootLogger() {
		if (mRootLogger == null) {
			synchronized (LogManager.class) {
				if (mRootLogger == null) {
					mRootLogger = LogManager.getLogManager().getLogger(Logger.GLOBAL_LOGGER_NAME);
					mRootLogger.setUseParentHandlers(false);
					/* DEFAULT LOG LEVEL. */
					mRootLogger.setLevel(Level.INFO);
					// configure the console appender
					final ConsoleHandler consoleHandler = new ConsoleHandler();
					consoleHandler.setFormatter(new SimpleFormatter() {
						@Override
						public synchronized String format(LogRecord logRecord) {
							return String.format(LOG_PATTERN, new Date(logRecord.getMillis()), logRecord.getLevel().getLocalizedName(), logRecord.getMessage());
						}
					});
					mRootLogger.addHandler(consoleHandler);
				}
			}
		}
		
		return mRootLogger;
	}
	
	/**
	 * Return the log level of the root logger
	 *
	 * @return Log level of the root logger
	 */
	public static final Level getLogLevel() {
		return getRootLogger().getLevel();
	}
	
	/**
	 * Sets log level for the root logger
	 *
	 * @param logLevel
	 *            Log level for the root logger
	 */
	public static final void setLogLevel(final Level logLevel) {
		getRootLogger().setLevel(logLevel);
	}
	
	/**
	 * Returns true if the current logLevel is >= the given logLevel otherwise
	 * false.
	 *
	 * @param logLevel
	 * @return
	 */
	public static final boolean isLogEnabledFor(final Level logLevel) {
		return (logLevel != null && logLevel.intValue() >= getLogLevel().intValue());
	}
	
	/**
	 * Returns the <code>Logger</code> object for the specified
	 * <code>logClassName</code> class name.
	 * <p>
	 *
	 * @param logClassName
	 * @return
	 */
	public static Logger getLogger(final String logClassName) {
		if (logClassName == null || logClassName.trim().length() == 0) {
			throw new IllegalArgumentException("logClass is NULL! it must provide!");
		}
		
		return Logger.getLogger(logClassName);
	}
	
	/**
	 * Returns true if debug is enabled otherwise false.
	 * 
	 * @return
	 */
	public static final boolean isDebugEnabled() {
		return false;
	}
	
	/**
	 * Returns the value of the <code>sDebugEnabled</code> property.
	 * 
	 * @return
	 */
	public static final boolean isInfoEnabled() {
		return isLogEnabledFor(Level.INFO);
	}
	
	/**
	 * 
	 * @param object
	 * @return
	 */
	public static final void error(final Object object) {
		getRootLogger().log(Level.SEVERE, ((object instanceof Throwable) ? toString((Throwable) object) : toString(object)));
	}
	
	/**
	 * 
	 * @param object
	 * @param throwable
	 */
	public static final void error(final Throwable throwable) {
		getRootLogger().log(Level.SEVERE, toString(throwable));
	}
	
	/**
	 * 
	 * @param object
	 * @param throwable
	 */
	public static final void error(final Object object, final Throwable throwable) {
		getRootLogger().log(Level.SEVERE, toString(object), throwable);
	}
	
	/**
	 * 
	 * @param object
	 */
	public static final void warn(final Object object) {
		getRootLogger().log(Level.WARNING, ((object instanceof Throwable) ? toString((Throwable) object) : toString(object)));
	}
	
	/**
	 * 
	 * @param object
	 * @param throwable
	 */
	public static final void warn(final Object object, final Throwable throwable) {
		getRootLogger().log(Level.WARNING, toString(object), throwable);
	}
	
	/**
	 * 
	 * @param object
	 */
	public static final void info(final Object object) {
		getRootLogger().log(Level.INFO, ((object instanceof Throwable) ? toString((Throwable) object) : toString(object)));
	}
	
	/**
	 * 
	 * @param object
	 * @param throwable
	 */
	public static final void info(final Object object, final Throwable throwable) {
		getRootLogger().log(Level.INFO, toString(object), throwable);
	}
	
	/**
	 * 
	 * @param format
	 * @param args
	 */
	public static final void debug(String format, Object... args) {
		if (isDebugEnabled()) {
			System.out.printf(format, args);
			System.out.println();
		}
	}
	
	/**
	 * 
	 * @param object
	 */
	public static final void debug(final Object object) {
		if (isDebugEnabled()) {
			System.out.println(((object instanceof Throwable) ? toString((Throwable) object) : toString(object)));
		}
	}
	
	/**
	 * 
	 * @param object
	 * @param throwable
	 */
	public static final void debug(final Object object, final Throwable throwable) {
		debug(object);
		debug(throwable);
	}
	
	/**
	 * Starting Point
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		System.out.println("-- main method starts --");
		BeeHelper.setLogLevel(Level.ALL);
		BeeHelper.info("This is info level log.");
		BeeHelper.warn("This is warn level log.");
		BeeHelper.error(new IOException("Testing IO Exception."));
	}
	
}
