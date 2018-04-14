// $Id: AbstractBlock.java,v 1.6 2005/04/04 22:30:20 rogatkin Exp $
//Bee Copyright (c) 2004 Dmitriy Rogatkin
// Created on Mar 18, 2004
package org.bee.processor;

import java.util.Iterator;

import org.bee.util.InfoHolder;
import org.bee.util.Misc;
import org.bee.util.NameSpaceImpl;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

/**
 * @author <a href="Dmitriy@mochamail.com">Dmitriy Rogatkin</a>
 *
 *         Provide class description here
 */
public abstract class AbstractBlock extends AbstractValue {
	protected Instruction.NameSpace nameSpace;
	protected String dir;
	
	/**
	 * 
	 * @param xpath
	 */
	protected AbstractBlock(String xpath) {
		super(xpath);
		nameSpace = new NameSpaceImpl();
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.bee.processor.Instruction#getName()
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * 
	 * @return
	 * @see org.bee.processor.AbstractValue#getNameSpace()
	 */
	public NameSpace getNameSpace() {
		return nameSpace;
	}
	
	protected void clearNameSpace() {
		// TODO: possible clean name space???
		// for(InfoHolder < String, InfoHolder, Object >
		// lv:getNameSpace().iterator())
		// getNameSpace().outScope(lv);
		Iterator<InfoHolder<String, InfoHolder, Object>> it = getNameSpace().iterator();
		while (it.hasNext()) {
			it.next();
			it.remove();
			// getNameSpace().outScope(it.next());
		}
	}
	
	/**
	 * 
	 * @param uri
	 * @param localName
	 * @param qName
	 * @param attributes
	 * @throws SAXException
	 * @see org.bee.processor.AbstractValue#startElement(java.lang.String,
	 *      java.lang.String, java.lang.String, org.xml.sax.Attributes)
	 */
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
		super.startElement(uri, localName, qName, attributes);
		if (name == null) {
			name = attributes.getValue("", ATTR_VARIABLE);
		}
		dir = attributes.getValue("", ATTR_DIR);
		if (dir != null) {
			getNameSpace().inScope(new InfoHolder<String, InfoHolder, Object>(RESERVE_NAME_DIR, new InfoHolder<String, String, Object>(RESERVE_NAME_DIR, lookupStringValue(dir))));
		}
	}
	
	/**
	 * 
	 * @return
	 * @see org.bee.processor.AbstractValue#getAllowedAttributeNames()
	 */
	public String[] getAllowedAttributeNames() {
		return Misc.merge(new String[] { ATTR_DIR }, super.getAllowedAttributeNames());
	}
}
