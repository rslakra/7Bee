package jdepend.framework;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Properties;
import java.util.StringTokenizer;

/**
 * The <code>PropertyConfigurator</code> class contains
 * configuration information contained in the
 * <code>jdepend.properties</code> file, if such a
 * file exists either in the user's home directory or
 * somewhere in the classpath.
 *
 * @author <b>Mike Clark</b> (mike@clarkware.com)
 * @author Clarkware Consulting, Inc.
 */

public class PropertyConfigurator {
	
	private Properties properties;
	
	public static final String DEFAULT_PROPERTY_FILE = "jdepend.properties";
	
	/**
	 * Constructs a <code>PropertyConfigurator</code> instance
	 * containing the properties specified in the
	 * <code>jdepend.properties</code>file, if it exists.
	 */
	public PropertyConfigurator() {
		this(getDefaultPropertyFile());
	}
	
	/**
	 * Constructs a <code>PropertyConfigurator</code> instance
	 * with the specified property set.
	 *
	 * @param p
	 *            Property set.
	 */
	public PropertyConfigurator(Properties p) {
		this.properties = p;
	}
	
	/**
	 * Constructs a <code>PropertyConfigurator</code> instance
	 * with the specified property file.
	 *
	 * @param f
	 *            Property file.
	 */
	public PropertyConfigurator(File file) {
		this(loadProperties(file));
	}
	
	/**
	 * Returns the collection of filtered package names.
	 * 
	 * @return Filtered package names.
	 */
	public Collection<String> getFilteredPackages() {
		Collection<String> packages = new ArrayList<String>();
		Enumeration<?> enumPropNames = properties.propertyNames();
		while (enumPropNames.hasMoreElements()) {
			String key = (String) enumPropNames.nextElement();
			if (key.startsWith("ignore")) {
				String path = properties.getProperty(key);
				System.out.println(this.getClass().toString() + ": read " + key + "=" + path);
				StringTokenizer stringTokenizer = new StringTokenizer(path, ",");
				while (stringTokenizer.hasMoreTokens()) {
					String name = (String) stringTokenizer.nextToken();
					name = name.trim();
					packages.add(name);
				}
			}
		}
		
		return packages;
	}
	
	/**
	 * Returns the <code>Collection<JavaPackage></code>.
	 * 
	 * @return
	 */
	public Collection<JavaPackage> getConfiguredPackages() {
		Collection<JavaPackage> packages = new ArrayList<JavaPackage>();
		Enumeration<?> enumPropNames = properties.propertyNames();
		while (enumPropNames.hasMoreElements()) {
			String key = (String) enumPropNames.nextElement();
			if (!key.startsWith("ignore") && (!key.equals("analyzeInnerClasses"))) {
				String value = properties.getProperty(key);
				packages.add(new JavaPackage(key, new Integer(value).intValue()));
			}
		}
		
		return packages;
	}
	
	public boolean getAnalyzeInnerClasses() {
		String key = "analyzeInnerClasses";
		if (properties.containsKey(key)) {
			String value = properties.getProperty(key);
			return new Boolean(value).booleanValue();
		}
		
		return true;
	}
	
	public static File getDefaultPropertyFile() {
		String home = System.getProperty("user.home");
		String configuration = System.getProperty("jdepend.configuration");
		if (null == configuration) {
			return new File(home, DEFAULT_PROPERTY_FILE);
		} else {
			return new File(configuration);
		}
	}
	
	/**
	 * Loads the properties file.
	 * 
	 * @param file
	 * @return
	 */
	public static Properties loadProperties(final File file) {
		Properties jDependProperties = new Properties();
		InputStream inputStream = null;
		try {
			try {
				inputStream = new FileInputStream(file);
			} catch (Exception e) {
				inputStream = PropertyConfigurator.class.getResourceAsStream("/" + DEFAULT_PROPERTY_FILE);
			}
			
			if (inputStream != null) {
				jDependProperties.load(inputStream);
			}
		} catch (IOException ex) {
			// ignore me!
		} finally {
			BeeHelper.closeSilently(inputStream);
		}
		
		return jDependProperties;
	}
}