package nl.tudelft.rdfgears.rgl.function.imreal;

/*
 * #%L
 * RDFGears
 * %%
 * Copyright (C) 2013 WIS group at the TU Delft (http://www.wis.ewi.tudelft.nl/)
 * %%
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 * #L%
 */


import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import nl.tudelft.rdfgears.engine.ValueFactory;
import nl.tudelft.rdfgears.rgl.datamodel.type.RDFType;
import nl.tudelft.rdfgears.rgl.datamodel.type.RGLType;
import nl.tudelft.rdfgears.rgl.datamodel.value.RGLValue;
import nl.tudelft.rdfgears.rgl.function.SimplyTypedRGLFunction;
import nl.tudelft.rdfgears.util.row.ValueRow;

/**
 * A function that takes as input an JavaScript expression and two parameters
 * that can be used in the expression. The function is responsible to evaluate
 * the expression and return the result;
 * 
 */
public class ExpressionEvaluation extends SimplyTypedRGLFunction {

	public static final String INPUT_A = "a";
	public static final String INPUT_B = "b";
	public static final String INPUT_EXPRESSION = "expression";

	public ExpressionEvaluation() {
		this.requireInputType(INPUT_A, RDFType.getInstance());
		this.requireInputType(INPUT_B, RDFType.getInstance());
		this.requireInputType(INPUT_EXPRESSION, RDFType.getInstance());
	}

	public RGLType getOutputType() {
		return RDFType.getInstance();
	}

	@Override
	public RGLValue simpleExecute(ValueRow inputRow) {
		// typechecking the input
		RGLValue rdfValue = inputRow.get(INPUT_A);
		if (!rdfValue.isLiteral())
			return ValueFactory.createNull("Cannot handle URI input in "
					+ getFullName());

		double a = 0;
		try {
			a = rdfValue.asLiteral().getValueDouble();
		} catch (NumberFormatException e) {
			e.printStackTrace();
			return ValueFactory.createNull("Cannot handle input. "
					+ getFullName());
		}

		// /////////////////////////////////////////////////////////////////

		// typechecking the input
		rdfValue = inputRow.get(INPUT_B);
		if (!rdfValue.isLiteral())
			return ValueFactory.createNull("Cannot handle URI input in "
					+ getFullName());

		double b = 0;
		try {
			b = rdfValue.asLiteral().getValueDouble();
		} catch (NumberFormatException e) {
			e.printStackTrace();
			return ValueFactory.createNull("Cannot handle input. "
					+ getFullName());
		}

		// ////////////////////////////////////////////////////////////////

		// typechecking the input
		rdfValue = inputRow.get(INPUT_EXPRESSION);
		if (!rdfValue.isLiteral())
			return ValueFactory.createNull("Cannot handle URI input in "
					+ getFullName());

		String expression = rdfValue.asLiteral().getValueString();

		// ////////////////////////////////////////////////////////////////

		Double result = null;
		try {
			result = evaluateExpression(expression, a, b);
		} catch (ScriptException e) {
			e.printStackTrace();
			return ValueFactory.createNull("Cannot evaluate expression: "
					+ e.getMessage());
		}

		if (result != null)
			return ValueFactory.createLiteralDouble(result);
		else
			return ValueFactory.createNull("The result value is null.");
	}

	/**
	 * Evaluates the JavaScript expression
	 */
	private Double evaluateExpression(String expression, double a, double b)
			throws ScriptException {
		// create a script engine manager
		ScriptEngineManager factory = new ScriptEngineManager();
		// create a JavaScript engine
		ScriptEngine engine = factory.getEngineByName("JavaScript");

		engine.put(INPUT_A, a);
		engine.put(INPUT_B, b);
		// evaluate JavaScript code from String
		Object result = engine.eval(expression);

		if (result instanceof Double) {
			return (Double) result;
		}
		return null;
	}
}
