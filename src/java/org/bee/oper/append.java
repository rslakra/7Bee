// $Id: append.java,v 1.8 2005/12/20 08:50:06 rogatkin Exp $
//Bee Copyright (c) 2004 Dmitriy Rogatkin
// Created on Mar 16, 2004
package org.bee.oper;

import java.util.List;

import org.bee.util.InfoHolder;

import jdepend.framework.BeeHelper;

/**
 * @author <a href="Dmitriy@mochamail.com">Dmitriy Rogatkin</a>
 *
 *         Provide class description here
 */
public class append {
	
	/**
	 * provides appending string values
	 * 
	 * @param op1
	 *            first operand = result
	 * @param op2
	 *            second operand
	 * 
	 * @return concatenation first to second operand
	 * 
	 *         TODO currently if operand is array and have size/length==1 then
	 *         1st
	 *         element is used, otherwise string value of it. A good solution
	 *         can
	 *         be concatenation elements of array, but what should be a
	 *         separator?
	 *         Maybe next argument?
	 */
	public static InfoHolder<String, String, Object> doOperator(InfoHolder<String, String, Object> operation, InfoHolder<String, String, Object> another) {
		BeeHelper.debug("append: doOperator(%s, %s)", operation, another);
		if (operation == null || operation.getValue() == null || operation.getValue().length() == 0) {
			return another;
		}
		
		Object type = operation.getType();
		Object valOp1 = null;
		if (type instanceof Object[]) {
			valOp1 = ((Object[]) type).length == 1 ? ((Object[]) type)[0] : operation.getValue();
		} else if (type instanceof List) {
			valOp1 = ((List<?>) type).size() == 1 ? ((List<?>) type).get(0) : operation.getValue();
		} else {
			valOp1 = operation.getValue();
		}
		
		if (valOp1 == null) {
			valOp1 = "";
		}
		
		if (another == null || another.getValue() == null) {
			return new InfoHolder<String, String, Object>(operation.getKey(), valOp1.toString(), valOp1);
		}
		
		Object valOp2 = null;
		Object op2Type = another.getType();
		if (op2Type instanceof Object[]) {
			valOp2 = ((Object[]) op2Type).length == 1 ? ((Object[]) op2Type)[0] : another.getValue();
		} else if (op2Type instanceof List) {
			valOp2 = ((List<?>) op2Type).size() == 1 ? ((List<?>) op2Type).get(0) : another.getValue();
		} else {
			valOp2 = another.getValue();
		}
		
		if (valOp2 == null) {
			valOp2 = "";
		}
		
		return new InfoHolder<String, String, Object>(operation.getKey(), valOp1.toString() + valOp2.toString());
	}
	
	/**
	 * 
	 * @param op1
	 * @param op2
	 * @return
	 */
	public static InfoHolder<String, String, Object> proceed(InfoHolder<String, String, Object> operation, InfoHolder<String, String, Object> another) {
		return doOperator(operation, another);
	}
}
