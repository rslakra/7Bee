// $Id: Block.java,v 1.10 2007/11/08 04:44:35 rogatkin Exp $
//Bee Copyright (c) 2004 Dmitriy Rogatkin
// Created on Mar 18, 2004
package org.bee.processor;

import static org.bee.util.Logger.logger;

import java.util.ArrayList;
import java.util.List;

import org.bee.util.InfoHolder;
import org.bee.util.Misc;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

/**
 * @author <a href="Dmitriy@mochamail.com">Dmitriy Rogatkin</a>
 *
 *         Provide class description here
 */
public class Block extends AbstractBlock {
	public enum BlockType {
		t_then, t_else, t_case, t_default, t_none
	};
	
	BlockType blockType;
	
	protected List<Instruction> instructions;
	
	/**
	 * @param xpath
	 */
	public Block(String xpath) {
		super(xpath);
		instructions = new ArrayList<Instruction>();
	}
	
	public void childDone(Instruction child) {
		instructions.add(child);
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.bee.processor.Instruction#eval()
	 */
	public InfoHolder eval() {
		InfoHolder result = null;
		try {
			for (Instruction instruction : instructions)
				result = instruction.eval();
		} catch (ProcessException pe) {
			// interrupt processing
			if (pe.getCause() != null || pe.getMessage() == null || pe.getMessage().equals(name) == false)
				throw pe;
			logger.finest("Block '" + name + "' interrupted");
		}
		return result;
	}
	
	/**
	 * 
	 * @param uri
	 * @param localName
	 * @param qName
	 * @param attributes
	 * @throws SAXException
	 * @see org.bee.processor.AbstractBlock#startElement(java.lang.String,
	 *      java.lang.String, java.lang.String, org.xml.sax.Attributes)
	 */
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
		logger.finest("Block: startElement(" + uri + ", " + localName + ", " + qName + ", " + attributes + ")");
		String s = attributes.getValue("", ATTR_TYPE);
		if ("then".equals(s)) {
			blockType = BlockType.t_then;
		} else if ("else".equals(s)) {
			blockType = BlockType.t_else;
		} else if ("case".equals(s)) {
			blockType = BlockType.t_case;
		} else if ("default".equals(s)) {
			blockType = BlockType.t_default;
		} else {
			blockType = BlockType.t_none;
		}
		type = Type.block;
		super.startElement(uri, localName, qName, attributes);
	}
	
	public String[] getAllowedAttributeNames() {
		return Misc.merge(new String[] { ATTR_DIR }, super.getAllowedAttributeNames());
	}
}
