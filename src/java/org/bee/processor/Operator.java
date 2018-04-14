// $Id: Operator.java,v 1.11 2005/06/15 08:02:23 rogatkin Exp $
//Bee Copyright (c) 2004 Dmitriy Rogatkin
// Created on Mar 11, 2004
package org.bee.processor;

import static java.util.logging.Level.FINEST;
import static org.bee.processor.Configuration.OPERATOR_METHOD_NAME;
import static org.bee.processor.Configuration.PKG_OPERATORS;
import static org.bee.util.Logger.logger;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.bee.util.InfoHolder;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

/**
 * @author <a href="Dmitriy@mochamail.com">Dmitriy Rogatkin</a>
 *
 *         Provide class description here
 */
public class Operator extends AbstractBlock {
	// protected String type;
	protected Method method;
	protected List<AbstractValue> operands;
	
	public Operator(String xpath) {
		super(xpath);
		operands = new ArrayList<AbstractValue>();
	}
	
	public InfoHolder eval() {
		InfoHolder result = null;
		int nop = operands.size();
		if (nop > 0) {
			result = operands.get(0).eval();
			if (nop < 2) {
				result = doOperator(result, null);
			} else {
				for (int i = 1; i < nop; i++) {
					result = doOperator(result, operands.get(i).eval());
				}
			}
		} else {
			result = doOperator(result, null);
		}
		
		if (variable != null) {
			// getNameSpace().inScope(new InfoHolder < String, InfoHolder,
			// Object > (variable, result));
			if (parent.getNameSpace() != null) {
				parent.getNameSpace().inScope(new InfoHolder<String, InfoHolder, Object>(variable, result));
			}
		}
		
		logger.finest("'" + name + "'=" + result);
		return result;
	}
	
	/**
	 * 
	 * @param child
	 * @see org.bee.processor.AbstractValue#childDone(org.bee.processor.Instruction)
	 */
	public void childDone(Instruction child) {
		operands.add((AbstractValue) child);
	}
	
	/**
	 * 
	 * @param result
	 * @param operand
	 * @return
	 */
	protected InfoHolder doOperator(InfoHolder result, InfoHolder operand) {
		logger.finest("Operator: '" + name + "' doOperator(" + result + ", " + operand + ')');
		if (method != null) {
			try {
				return (InfoHolder) method.invoke(null, result, operand);
			} catch (InvocationTargetException ite) {
				if (logger.isLoggable(FINEST)) {
					logger.log(FINEST, "InvocationTargetException:", ite.getCause());
				} else {
					logger.warning("InvocationTargetException:" + ite.getCause());
				}
			} catch (Exception ex) {
				if (logger.isLoggable(FINEST)) {
					logger.log(FINEST, "", ex);
				} else {
					logger.warning("" + ex);
				}
			}
		}
		
		return null;
	}
	
	/**
	 * 
	 * @param name
	 * @return
	 */
	public static Class<?> findOperatorClass(String name) {
		if (name == null) {
			return null;
		}
		
		if (name.indexOf('.') < 0) { // consider as full qualified name
			name = PKG_OPERATORS + name;
		}
		
		try {
			return Class.forName(name);
		} catch (Error ex) {
			// too bad
			logger.severe("Class " + name + " " + ex);
		} catch (Exception ex) {
			logger.severe("Class " + name + " not found or " + ex);
		}
		
		return null;
	}
	
	/**
	 * 
	 * @param classType
	 * @param name
	 * @return
	 */
	public static Method getMethod(Class<?> classType, String name) {
		if (classType == null) {
			return null;
		}
		
		try {
			return classType.getMethod(name, InfoHolder.class, InfoHolder.class);
		} catch (NoSuchMethodException ex) {
			logger.severe("Method " + name + " not found or " + ex);
		}
		
		return null;
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
		logger.finest("Operator: startElement(" + uri + ", " + localName + ", " + qName + ", " + attributes + ")");
		super.startElement(uri, localName, qName, attributes);
		method = getMethod(findOperatorClass(name), OPERATOR_METHOD_NAME);
	}
}
