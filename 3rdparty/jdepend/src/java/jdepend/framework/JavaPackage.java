package jdepend.framework;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

/**
 * The <code>JavaPackage</code> class represents a Java package.
 *
 * @author <b>Mike Clark</b> (mike@clarkware.com)
 * @author Clarkware Consulting, Inc.
 */
public class JavaPackage {
	
	private String name;
	private int volatility;
	
	/** classes */
	private HashSet<JavaClass> classes;
	
	// Packages that use this package.
	private List<JavaPackage> afferents;
	
	// Packages that this package uses.
	private List<JavaPackage> efferents;
	
	/**
	 * Constructs a <code>JavaPackage</code> instance with
	 * the specified package name.
	 *
	 * @param name
	 *            Package name.
	 */
	public JavaPackage(String name) {
		this(name, 1);
	}
	
	/**
	 * 
	 * @param name
	 * @param volatility
	 */
	public JavaPackage(String name, int volatility) {
		this.name = name;
		setVolatility(volatility);
		classes = new HashSet<JavaClass>();
		afferents = new ArrayList<JavaPackage>();
		efferents = new ArrayList<JavaPackage>();
	}
	
	/**
	 * Returns the package name.
	 *
	 * @return Name.
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * Returns the volatility of this package.
	 *
	 * @return Volatility (0-1).
	 */
	public int getVolatility() {
		return volatility;
	}
	
	/**
	 * Sets the volatility of this package.
	 *
	 * @param v
	 *            Volatility (0-1).
	 */
	public void setVolatility(int v) {
		volatility = v;
	}
	
	/**
	 * Indicates whether the package contains
	 * a package dependency cycle.
	 *
	 * @return <code>true</code> if a cycle exist;
	 *         <code>false</code> otherwise.
	 */
	public boolean containsCycle() {
		return collectCycle(new ArrayList<JavaPackage>());
	}
	
	/**
	 * Collects the packages participating in the
	 * first package dependency cycle detected which
	 * originates from this package.
	 *
	 * @param javaPackages
	 *            Collecting object to be populated with
	 *            the list of JavaPackage instances in a cycle.
	 * @return <code>true</code> if a cycle exist;
	 *         <code>false</code> otherwise.
	 */
	public boolean collectCycle(final List<JavaPackage> javaPackages) {
		if (javaPackages.contains(this)) {
			javaPackages.add(this);
			return true;
		}
		
		javaPackages.add(this);
		Iterator<JavaPackage> efferents = getEfferents().iterator();
		while (efferents.hasNext()) {
			JavaPackage efferent = efferents.next();
			if (efferent.collectCycle(javaPackages)) {
				return true;
			}
		}
		
		javaPackages.remove(this);
		return false;
	}
	
	/**
	 * Collects all the packages participating in
	 * a package dependency cycle which originates
	 * from this package.
	 * <p>
	 * This is a more exhaustive search than that
	 * employed by <code>collectCycle</code>.
	 *
	 * @param javaPackages
	 *            Collecting object to be populated with
	 *            the list of JavaPackage instances in a cycle.
	 * @return <code>true</code> if a cycle exist;
	 *         <code>false</code> otherwise.
	 */
	public boolean collectAllCycles(final List<JavaPackage> javaPackages) {
		if (javaPackages.contains(this)) {
			javaPackages.add(this);
			return true;
		}
		
		javaPackages.add(this);
		Iterator<JavaPackage> efferents = getEfferents().iterator();
		boolean containsCycle = false;
		while (efferents.hasNext()) {
			JavaPackage efferent = efferents.next();
			if (efferent.collectAllCycles(javaPackages)) {
				containsCycle = true;
			}
		}
		
		if (containsCycle) {
			return true;
		} else {
			javaPackages.remove(this);
			return false;
		}
	}
	
	/**
	 * Adds the specified Java class to the package.
	 *
	 * @param javaClass
	 *            Java class to add.
	 */
	public void addClass(JavaClass javaClass) {
		classes.add(javaClass);
	}
	
	/**
	 * Returns the collection of Java classes
	 * in this package.
	 *
	 * @return Collection of Java classes.
	 */
	public Collection<JavaClass> getClasses() {
		return classes;
	}
	
	/**
	 * Returns the total number of classes in
	 * this package.
	 *
	 * @return Number of classes.
	 */
	public int getClassCount() {
		return classes.size();
	}
	
	/**
	 * Returns the number of abstract classes
	 * (and interfaces) in this package.
	 *
	 * @return Number of abstract classes.
	 */
	public int getAbstractClassCount() {
		int count = 0;
		Iterator<JavaClass> itrClass = classes.iterator();
		while (itrClass.hasNext()) {
			JavaClass javaClass = itrClass.next();
			if (javaClass.isAbstract()) {
				count++;
			}
		}
		
		return count;
	}
	
	/**
	 * Returns the number of concrete classes in
	 * this package.
	 *
	 * @return Number of concrete classes.
	 */
	public int getConcreteClassCount() {
		int count = 0;
		Iterator<JavaClass> itrClass = classes.iterator();
		while (itrClass.hasNext()) {
			JavaClass javaClass = itrClass.next();
			if (!javaClass.isAbstract()) {
				count++;
			}
		}
		
		return count;
	}
	
	/**
	 * Adds the specified Java package as an efferent
	 * of this package and adds this package as an
	 * afferent of it.
	 *
	 * @param imported
	 *            Java package.
	 */
	public void dependsUpon(JavaPackage imported) {
		addEfferent(imported);
		imported.addAfferent(this);
	}
	
	/**
	 * Adds the specified Java package as an afferent
	 * of this package.
	 *
	 * @param jPackage
	 *            Java package.
	 */
	public void addAfferent(JavaPackage javaPackage) {
		if (!javaPackage.getName().equals(getName())) {
			if (!afferents.contains(javaPackage)) {
				afferents.add(javaPackage);
			}
		}
	}
	
	/**
	 * Returns the collection of afferent packages.
	 *
	 * @return Collection of afferent packages.
	 */
	public Collection<JavaPackage> getAfferents() {
		return afferents;
	}
	
	/**
	 * Sets the collection of afferent packages.
	 *
	 * @param afferents
	 *            Collection of afferent packages.
	 */
	public void setAfferents(Collection<JavaPackage> afferents) {
		this.afferents = new ArrayList<JavaPackage>(afferents);
	}
	
	/**
	 * Adds the specified Java package as an efferent
	 * of this package.
	 *
	 * @param jPackage
	 *            Java package.
	 */
	public void addEfferent(JavaPackage jPackage) {
		if (!jPackage.getName().equals(getName())) {
			if (!efferents.contains(jPackage)) {
				efferents.add(jPackage);
			}
		}
	}
	
	/**
	 * Returns the collection of efferent packages.
	 *
	 * @return Collection of efferent packages.
	 */
	public Collection<JavaPackage> getEfferents() {
		return efferents;
	}
	
	/**
	 * Sets the collection of efferent packages.
	 *
	 * @param efferents
	 *            Collection of efferent packages.
	 */
	public void setEfferents(Collection<JavaPackage> efferents) {
		this.efferents = new ArrayList<JavaPackage>(efferents);
	}
	
	/**
	 * Returns the afferent coupling (Ca) of this package.
	 *
	 * @return Ca
	 */
	public int afferentCoupling() {
		return afferents.size();
	}
	
	/**
	 * Returns the efferent coupling (Ce) of this package.
	 *
	 * @return Ce
	 */
	public int efferentCoupling() {
		return efferents.size();
	}
	
	/**
	 * Returns the instability (I) of this package.
	 *
	 * @return Instability (0-1).
	 */
	public float instability() {
		float totalCoupling = (float) efferentCoupling() + (float) afferentCoupling();
		if (totalCoupling > 0) {
			return ((float) efferentCoupling()) / totalCoupling;
		}
		
		return 0;
	}
	
	/**
	 * Returns the abstractness (A) of this package.
	 *
	 * @return Abstractness (0-1).
	 */
	public float abstractness() {
		if (getClassCount() > 0) {
			return (float) getAbstractClassCount() / (float) getClassCount();
		}
		
		return 0;
	}
	
	/**
	 * Returns this package's distance from the main sequence (D).
	 *
	 * @return Distance.
	 */
	public float distance() {
		return (Math.abs(abstractness() + instability() - 1) * volatility);
	}
	
	/**
	 * Indicates whether the specified package is equal
	 * to this package.
	 *
	 * @param other
	 *            Other package.
	 * @return <code>true</code> if the packages are equal;
	 *         <code>false</code> otherwise.
	 */
	public boolean equals(Object other) {
		return ((other instanceof JavaPackage) && ((JavaPackage) other).getName().equals(getName()));
	}
}