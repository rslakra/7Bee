// $Id: NameSpaceImpl.java,v 1.4 2004/03/26 05:56:02 rogatkin Exp $
//Bee Copyright (c) 2004 Dmitriy Rogatkin
// Created on Mar 16, 2004
package org.bee.util;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.bee.processor.Instruction;

/**
 * @author <a href="Dmitriy@mochamail.com">Dmitriy Rogatkin</a>
 *
 *         Provide class description here
 */
public final class NameSpaceImpl implements Instruction.NameSpace {
	Map<String, InfoHolder<String, InfoHolder, Object>> nameSpaces;
	
	public NameSpaceImpl() {
		nameSpaces = new HashMap<String, InfoHolder<String, InfoHolder, Object>>();
	}
	
	/**
	 * 
	 * @return
	 * @see org.bee.processor.Instruction.NameSpace#iterator()
	 */
	public Iterator<InfoHolder<String, InfoHolder, Object>> iterator() {
		return nameSpaces.values().iterator();
	}
	
	/**
	 * 
	 * @param var
	 * @see org.bee.processor.Instruction.NameSpace#inScope(org.bee.util.InfoHolder)
	 */
	public void inScope(InfoHolder<String, InfoHolder, Object> value) {
		InfoHolder<String, InfoHolder, Object> oldValue = nameSpaces.get(value.getKey());
		if (oldValue == null) {
			nameSpaces.put(value.getKey(), value);
		} else {
			nameSpaces.put(value.getKey(), value);
		}
	}
	
	/**
	 * 
	 * @param value
	 * @see org.bee.processor.Instruction.NameSpace#outScope(org.bee.util.InfoHolder)
	 */
	public void outScope(InfoHolder<String, InfoHolder, Object> value) {
		nameSpaces.remove(value.getKey());
	}
	
	/**
	 * 
	 * @param name
	 * @return
	 * @see org.bee.processor.Instruction.NameSpace#lookup(java.lang.String)
	 */
	public InfoHolder<String, InfoHolder, Object> lookup(String name) {
		return nameSpaces.get(name);
	}
}
