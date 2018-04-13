// $Id: InfoHolder.java,v 1.2 2004/03/23 09:47:04 rogatkin Exp $
//Bee Copyright (c) 2004 Dmitriy Rogatkin
// Created on Mar 10, 2004
package org.bee.util;

/**
 * @author <a href="Dmitriy@mochamail.com">Dmitriy Rogatkin</a>
 * @author <a href="rohtash.singh@gmail.com">Rohtash Singh Lakra</a>
 */
public class InfoHolder<K, V, T> {
	K key;
	V value;
	T type;
	// long id;
	
	public InfoHolder(K k, V v) {
		key = k;
		value = v;
	}
	
	public InfoHolder(K k, V v, T t) {
		this(k, v);
		type = t;
	}
	
	public K getKey() {
		return key;
	}
	
	public V getValue() {
		return value;
	}
	
	public T getType() {
		return type;
	}
	
	/**
	 * Returns the string represenation of this object.
	 * 
	 * @return
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return "InfoHolder<" + key + ", " + value + ", " + type + ">";
	}
	
}
