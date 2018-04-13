// $Id: Expression.java,v 1.12 2005/06/15 08:02:22 rogatkin Exp $
// Bee Copyright (c) 2004 Dmitriy Rogatkin
package org.bee.processor;

import static org.bee.util.Logger.logger;

import java.util.ArrayList;
import java.util.List;

import org.bee.util.InfoHolder;

/**
 * @author <a href="mailto:dmitriy@mochamail.com">Dmitriy Rogatkin</a>
 */
public class Expression extends AbstractBlock {
	protected List<Instruction> terms;
	
	public Expression(String xpath) {
		super(xpath);
		terms = new ArrayList<Instruction>();
	}
	
	public void childDone(Instruction child) {
		terms.add(child);
	}
	
	public InfoHolder eval() {
		InfoHolder retValue = null;
		for (Instruction instruction : terms) {
			retValue = instruction.eval();
		}
		if (name != null) {
			InfoHolder<String, InfoHolder, Object> valHolder = new InfoHolder<String, InfoHolder, Object>(name, retValue);
			updateInNameSpace(name, valHolder);
		}
		logger.finer(":= " + retValue);
		return retValue;
	}
}