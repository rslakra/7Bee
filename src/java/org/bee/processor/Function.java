// $Id: Function.java,v 1.28 2008/03/11 03:46:53 dmitriy Exp $
//Bee Copyright (c) 2004 Dmitriy Rogatkin
// Created on Mar 11, 2004
package org.bee.processor;

import static java.util.logging.Level.SEVERE;
import static org.bee.processor.Configuration.FUNCTION_METHOD_NAME;
import static org.bee.processor.Configuration.PKG_FUNCTIONS;
import static org.bee.util.Logger.logger;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;

import org.bee.util.InfoHolder;

import jdepend.framework.BeeHelper;

/**
 * @author <a href="Dmitriy@mochamail.com">Dmitriy Rogatkin</a>
 * 
 *         Provide class description here
 */
public class Function extends AbstractBlock {
	protected List<Parameter> parameters;
	
	/**
	 * 
	 * @param xpath
	 */
	public Function(String xpath) {
		super(xpath);
		parameters = new ArrayList<Parameter>();
	}
	
	/**
	 * 
	 * @param child
	 * @see org.bee.processor.AbstractValue#childDone(org.bee.processor.Instruction)
	 */
	public void childDone(Instruction child) {
		if (child instanceof Parameter) {
			parameters.add((Parameter) child);
		} else {
			logger.severe("Function '" + name + "' can process only <Parameter> type arguments, however <" + child.getClass().getName() + "> was found.");
		}
	}
	
	/**
	 * 
	 * @return
	 * @see org.bee.processor.Instruction#eval()
	 */
	public InfoHolder eval() {
		logger.entering("func.eval", name);
		InfoHolder result = null;
		// TODO :do everything on generic level and use Objects everywhere
		Object[] callParameters = new Object[parameters.size()];
		int lastSameType = 0;
		for (int i = 0; i < callParameters.length; i++) {
			Parameter param = parameters.get(i);
			BeeHelper.debug("param:" + param);
			InfoHolder<String, String, Object> paramValue = param.eval();
			if (paramValue != null) {
				callParameters[i] = paramValue.getType();
				logger.finest("callParameters[" + i + "]=" + callParameters[i]);
				if (callParameters[i] == null) {
					callParameters[i] = paramValue.getValue();
				}
			} else {
				callParameters[i] = null;
			}
			
			if (i > 0 && callParameters[i] != null && callParameters[i - 1] != null && callParameters[i].getClass().equals(callParameters[i - 1].getClass()) == false) {
				lastSameType = i;
			}
		}
		
		if (logger.isLoggable(Level.FINEST)) {
			logger.finest("Call parameters of " + name + " are :" + Arrays.deepToString(callParameters) + "; last same: " + lastSameType);
		}
		
		Object object = null;
		Method method = getMethod(findFunctionClass(name), FUNCTION_METHOD_NAME, Object.class);
		try {
			if (method == null) {
				method = getMethod(findFunctionClass(name), FUNCTION_METHOD_NAME, Object[].class);
				if (method == null) {
					Object[] methodParameter = new Object[callParameters.length];
					for (int i = 0; i < callParameters.length; i++) {
						methodParameter[i] = callParameters[i] == null ? null : callParameters[i].toString();
					}
					
					method = getMethod(findFunctionClass(name), FUNCTION_METHOD_NAME, String.class);
					if (method == null) {
						method = getMethod(findFunctionClass(name), FUNCTION_METHOD_NAME, String[].class);
						if (method == null) {
							logger.log(SEVERE, "Can't find function method " + name + " matching (Object*)," + " ([Object), (String*), ([String), length:" + callParameters.length);
							// TODO check if getHelp is in function to print
							// usage
						} else {
							object = method.invoke(null, new Object[] { methodParameter });
						}
					} else {
						object = method.invoke(null, methodParameter);
					}
				} else {
					object = method.invoke(null, new Object[] { callParameters });
				}
			} else {
				object = method.invoke(null, callParameters);
			}
			
			if (object instanceof ProcessException) {
				throw (ProcessException) object;
			}
			
			logger.exiting("func.eval", object != null ? object.toString() : null);
			result = new InfoHolder<String, String, Object>(name, object == null ? null : (object instanceof Object[] ? Arrays./* deepT */toString((Object[]) object) : object.toString()), object);
		} catch (IllegalArgumentException e) {
			logger.severe("Illegal arguments at call of " + method + " with " + callParameters);
			logger.throwing(getName(), "eval", e);
		} catch (IllegalAccessException e) {
			logger.severe("An exception in calling function '" + getName() + "': " + e);
			logger.throwing(getName(), "eval", e);
		} catch (InvocationTargetException ex) {
			logger.severe("An exception in called function '" + getName() + "': " + ex.getCause());
			logger.throwing(getName(), "eval", ex.getCause());
		}
		
		return result;
	}
	
	/**
	 * 
	 * @param classType
	 *            a target class to find method
	 * @param name
	 *            of method
	 * @param paramPattern
	 *            represents a pattern of parameters, can be list or array of
	 *            the same type, if parameter is an array of some class then
	 *            method with one array of this type searched, if it's a single
	 *            class than method with list of parameters of this class
	 *            searched
	 * @return Method if found, and null if not.
	 */
	public Method getMethod(Class<?> classType, String name, Class<?> paramPattern) {
		if (classType == null) {
			return null;
		}
		
		Class<?>[] callParamClasses = null;
		if (paramPattern.isArray()) {
			callParamClasses = new Class[1];
			Arrays.fill(callParamClasses, paramPattern);
		} else {
			callParamClasses = new Class[parameters.size()];
			Arrays.fill(callParamClasses, (Class<?>) paramPattern);
		}
		try {
			return classType.getDeclaredMethod(name, callParamClasses);
		} catch (NoSuchMethodException ex) {
			if (logger.isLoggable(Level.FINEST)) {
				logger.finest("Method '" + name + "' with " + callParamClasses.length + ", or variable parameters not found, last exception: " + ex);
			}
		}
		
		return null;
	}
	
	/**
	 * 
	 * @param name
	 * @return
	 */
	public static Class<?> findFunctionClass(String name) {
		if (name == null) {
			return null;
		}
		
		if (name.indexOf('.') < 0) {
			// consider as full qualified name
			name = PKG_FUNCTIONS + name;
		}
		
		try {
			return Class.forName(name, true, Function.class.getClassLoader());
		} catch (Error ex) {
			// too bad
			logger.severe("Function class " + name + " " + ex);
		} catch (Exception ex) {
			logger.severe("Function class " + name + " not found or " + ex);
		}
		
		return null;
	}
}
