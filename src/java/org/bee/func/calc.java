// $Id: calc.java,v 1.6 2011/07/27 04:13:16 dmitriy Exp $
//Bee Copyright (c) 2011 Dmitriy Rogatkin
// Created on Jul 07, 2011

package org.bee.func;

import java.util.Collection;
import java.util.Stack;

import jdepend.framework.BeeHelper;

/**
 * this function is arithmetic calculator
 * 
 * @author Dmitriy Rogatkin
 */
public class calc {
	
	enum Operator {
		NONE,
		LEFT_PARENTHESIS,
		RIGHT_PARENTHESIS,
		PLUS,
		MINUS,
		MULTIPLY,
		DIVISION,
		POWER,
		SQR,
		SQRT,
		COS,
		SIN,
		TAN,
		ATAN,
		LOG10,
		LOG
	};
	
	/**
	 * processes operands as arithmetic operations and functions the following
	 * +,-,*,/,(,),power, sqrt, sqr, sin, cos, tan, atan, log, ln
	 * <p>
	 * Only scalar value are currently supported, all arguments will be
	 * converted in double
	 * <p>
	 * Result is double value as well
	 * 
	 * @param args
	 * @return
	 */
	public static Object eval(Object... args) {
		if (args[0] instanceof Collection) {
			args = ((Collection<?>) args[0]).toArray();
		}
		
		Stack<StateHolder> stateHolders = new Stack<StateHolder>();
		double result = 0d;
		double current_oprd;
		Operator currentOperator, nonCommitted = Operator.PLUS;
		Operator defferedOperator = Operator.NONE;
		double currentResult = result;
		currentOperator = Operator.PLUS;
		for (Object arg : args) {
			current_oprd = 0d;
			BeeHelper.debug("oper %s %f %s = %f  deferred %s/%s%n", currentOperator, currentResult, arg, result, nonCommitted, defferedOperator);
			if (arg != null) {
				if (arg instanceof Number) {
					current_oprd = ((Number) arg).doubleValue();
				} else {
					String normVal = arg.toString().trim().toLowerCase();
					if (normVal.length() > 0) {
						try {
							current_oprd = Double.parseDouble(normVal);
						} catch (NumberFormatException nfe) {
							Operator lastOperator = currentOperator;
							currentOperator = toOperator(normVal);
							switch (currentOperator) {
								case PLUS:
								case MINUS:
									switch (nonCommitted) {
										case PLUS:
											result += currentResult;
											break;
										case MINUS:
											result -= currentResult;
											break;
										default:
											break;
									}
									nonCommitted = currentOperator;
									defferedOperator = Operator.NONE;
									break;
								case LEFT_PARENTHESIS:
									// push current status in stack and reinit
									// as in
									// start
									stateHolders.push(new StateHolder(lastOperator, nonCommitted, defferedOperator, result, currentResult));
									currentResult = result = 0d;
									currentOperator = Operator.PLUS;
									nonCommitted = Operator.NONE;
									defferedOperator = Operator.NONE;
									break;
								case RIGHT_PARENTHESIS:
									if (stateHolders.isEmpty()) {
										throw new RuntimeException("No matching open ( for this )");
									}
									switch (nonCommitted) {
										case PLUS:
											result += currentResult;
											break;
										case MINUS:
											result -= currentResult;
											break;
										default:
											break;
									}
									
									StateHolder stateHolder = stateHolders.pop();
									current_oprd = result;
									lastOperator = currentOperator;
									currentOperator = stateHolder.oper;
									nonCommitted = stateHolder.non_comit_oper;
									currentResult = stateHolder.cur_res;
									result = stateHolder.res;
									defferedOperator = stateHolder.deffered;
									break; // to pass through
								case MULTIPLY:
								case DIVISION:
								case POWER:
									defferedOperator = currentOperator;
									break;
								default:
								
							}
							
							if (lastOperator != Operator.RIGHT_PARENTHESIS) {
								continue;
							}
						}
					}
				}
				// found numeric operand
				switch (currentOperator) {
					case SIN:
						current_oprd = Math.sin(current_oprd);
						break;
					case COS:
						current_oprd = Math.cos(current_oprd);
						break;
					case TAN:
						current_oprd = Math.tan(current_oprd);
						break;
					case ATAN:
						current_oprd = Math.atan(current_oprd);
						break;
					case LOG10:
						current_oprd = Math.log10(current_oprd);
						break;
					case LOG:
						current_oprd = Math.log(current_oprd);
						break;
					case SQRT:
						current_oprd = Math.sqrt(current_oprd);
						break;
					case SQR:
						current_oprd = current_oprd * current_oprd;
						break;
					default:
						break;
				}
				
				if (defferedOperator != Operator.NONE) {
					currentOperator = defferedOperator;
				}
				
				switch (currentOperator) {
					case MULTIPLY:
						currentResult *= current_oprd;
						break;
					case DIVISION:
						currentResult /= current_oprd;
						break;
					case POWER:
						currentResult = Math.pow(currentResult, current_oprd);
						break;
					case PLUS:
					case MINUS:
					case NONE:
						nonCommitted = currentOperator;
					default:
						currentResult = current_oprd;
				}
			}
		}
		
		switch (nonCommitted) {
			case PLUS:
				result += currentResult;
				break;
			case MINUS:
				result -= currentResult;
				break;
			default:
				break;
		}
		
		return result;
	}
	
	/**
	 * 
	 * @param string
	 * @return
	 */
	static private Operator toOperator(String string) {
		if ("+".equals(string)) {
			return Operator.PLUS;
		} else if ("-".equals(string)) {
			return Operator.MINUS;
		} else if ("*".equals(string)) {
			return Operator.MULTIPLY;
		} else if ("/".equals(string)) {
			return Operator.DIVISION;
		} else if ("(".equals(string)) {
			return Operator.LEFT_PARENTHESIS;
		} else if (")".equals(string)) {
			return Operator.RIGHT_PARENTHESIS;
		} else if ("power".equals(string)) {
			return Operator.POWER;
		} else if ("sqr".equals(string)) {
			return Operator.SQR;
		} else if ("sqrt".equals(string)) {
			return Operator.SQRT;
		} else if ("sin".equals(string)) {
			return Operator.SIN;
		} else if ("cos".equals(string)) {
			return Operator.COS;
		} else if ("tan".equals(string)) {
			return Operator.TAN;
		} else if ("atan".equals(string)) {
			return Operator.ATAN;
		} else if ("log".equals(string)) {
			return Operator.LOG10;
		} else if ("ln".equals(string)) {
			return Operator.LOG;
		} else {
			throw new RuntimeException("Unrecognized parameter:" + string);
		}
	}
	
	static class StateHolder {
		Operator oper, non_comit_oper, deffered;
		double res, cur_res;
		
		StateHolder(Operator o, Operator nco, Operator def, double r, double cr) {
			oper = o;
			non_comit_oper = nco;
			res = r;
			cur_res = cr;
			deffered = def;
		}
	}
}