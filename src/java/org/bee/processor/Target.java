// $Id: Target.java,v 1.13 2006/07/07 05:48:40 rogatkin Exp $
// Bee Copyright (c) 2004 Dmitriy Rogatkin
package org.bee.processor;

import static org.bee.util.Logger.logger;

import java.util.ArrayList;
import java.util.List;

import org.bee.util.InfoHolder;
import org.bee.util.Misc;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

/**
 * @author <a href="mailto:dmitriy@mochamail.com">Dmitriy Rogatkin</a>
 */

public class Target extends Expression {
	protected List<Dependency> dependencies;
	protected String comment;
	
	public Target(String xpath) {
		super(xpath);
		dependencies = new ArrayList<Dependency>();
	}
	
	public void childDone(Instruction child) {
		if (child instanceof Dependency)
			dependencies.add((Dependency) child);
		else
			super.childDone(child);
	}
	
	public InfoHolder eval() {
		logger.entering("Target:", "eval:" + name);
		boolean needToEval = dependencies.size() == 0;
		if (dir != null) {
			InfoHolder<String, InfoHolder, Object> targetDir = lookupInChain(dir);
			if (targetDir != null)
				getNameSpace().inScope(new InfoHolder<String, InfoHolder, Object>(RESERVE_NAME_DIR, targetDir.getValue()));
		}
		for (Dependency dependency : dependencies) {
			InfoHolder dependencyValue = dependency.eval();
			if (dependencyValue != null && Boolean.TRUE.toString().equals(dependencyValue.getValue())) {
				needToEval = true;
				// break;
			}
		}
		
		if (needToEval) {
			/* return */
			super.eval();
		}
		logger.exiting("Target", "eval:" + name, (Boolean) needToEval);
		return new InfoHolder<String, String, Boolean>(name, ((Boolean) needToEval).toString(), needToEval);
	}
	
	// TODO: add a base method something like fillAttributes
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
		logger.finest("Target: startElement(" + uri + ", " + localName + ", " + qName + ", " + attributes + ")");
		super.startElement(uri, localName, qName, attributes);
		dir = attributes.getValue("", ATTR_DIR);
		comment = attributes.getValue("", ATTR_COMMENT);
	}
	
	public String[] getAllowedAttributeNames() {
		return Misc.merge(new String[] { ATTR_DIR, ATTR_COMMENT }, super.getAllowedAttributeNames());
	}
	
	String getComment() {
		return comment;
	}
}