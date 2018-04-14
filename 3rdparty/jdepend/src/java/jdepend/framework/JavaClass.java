package jdepend.framework;

import java.util.Collection;
import java.util.HashMap;

/**
 * The <code>JavaClass</code> class represents a
 * Java class or interface.
 *
 * @author <b>Mike Clark</b> (mike@clarkware.com)
 * @author Clarkware Consulting, Inc.
 */

public class JavaClass {
	
	private String className;
	private String packageName;
	private boolean isAbstract;
	private HashMap<String, JavaPackage> imports;
	private String sourceFile;
	
	/**
	 * Constructs a <code>JavaClass</code> instance.
	 */
	public JavaClass(String name) {
		className = name;
		packageName = "default";
		isAbstract = false;
		imports = new HashMap<String, JavaPackage>();
		sourceFile = "Unknown";
	}
	
	/**
	 * Sets the class name.
	 *
	 * @param name
	 *            Class name.
	 */
	public void setName(String name) {
		className = name;
	}
	
	/**
	 * Returns the class name.
	 *
	 * @return Class name.
	 */
	public String getName() {
		return className;
	}
	
	/**
	 * Sets the package name.
	 *
	 * @param name
	 *            Package name.
	 */
	public void setPackageName(String name) {
		packageName = name;
	}
	
	/**
	 * Returns the package name.
	 *
	 * @return Package name.
	 */
	public String getPackageName() {
		return packageName;
	}
	
	/**
	 * Sets the source file name.
	 *
	 * @param name
	 *            Source file name.
	 */
	public void setSourceFile(String name) {
		sourceFile = name;
	}
	
	/**
	 * Returns the source file name.
	 *
	 * @return Source file name.
	 */
	public String getSourceFile() {
		return sourceFile;
	}
	
	/**
	 * Returns a collection of imported
	 * package names.
	 *
	 * @return Imported package names.
	 */
	public Collection<JavaPackage> getImportedPackages() {
		return imports.values();
	}
	
	/**
	 * Adds the specified package to the collection
	 * of imported packages.
	 *
	 * @param jPackage
	 *            Package to add.
	 */
	public void addImportedPackage(JavaPackage javaPackage) {
		if (!javaPackage.getName().equals(getPackageName())) {
			imports.put(javaPackage.getName(), javaPackage);
		}
	}
	
	/**
	 * Indicates whether this class is abstract.
	 *
	 * @return <code>true</code> if this class is abstract;
	 *         <code>false</code> otherwise.
	 */
	public boolean isAbstract() {
		return isAbstract;
	}
	
	/**
	 * Determines whether this class is abstract.
	 *
	 * @param isAbstract
	 *            <code>true</code> if this class
	 *            is abstract; <code>false</code> otherwise.
	 */
	public void isAbstract(boolean isAbstract) {
		this.isAbstract = isAbstract;
	}
	
	/**
	 * Indicates whether the specified class is equal
	 * to this class.
	 *
	 * @param other
	 *            Other class.
	 * @return <code>true</code> if the classes are equal;
	 *         <code>false</code> otherwise.
	 */
	public boolean equals(Object other) {
		return ((other instanceof JavaClass) && ((JavaClass) other).getName().equals(getName()));
	}
	
	// /**
	// * The <code>ClassComparator</code> class is
	// * a <code>Comparator</code> used to compare
	// * two <code>JavaClass</code> instances for order.
	// */
	// public static class ClassComparator implements Comparator<JavaClass> {
	//
	// /**
	// * Compares the specified objects for order.
	// *
	// * @param p1
	// * First class.
	// * @param p2
	// * Second class.
	// * @return A negative integer, zero, or a positive integer
	// * as the first object is less than, equal to, or
	// * greater than the second object.
	// */
	// public int compare(Object javaClass, Object another) {
	// return javaClass.getName().compareTo(c2.getName());
	// }
	// }
}
