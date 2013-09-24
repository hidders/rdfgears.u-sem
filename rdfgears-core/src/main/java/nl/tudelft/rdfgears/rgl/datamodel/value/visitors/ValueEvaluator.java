package nl.tudelft.rdfgears.rgl.datamodel.value.visitors;

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

import nl.tudelft.rdfgears.rgl.datamodel.value.BagValue;
import nl.tudelft.rdfgears.rgl.datamodel.value.BooleanValue;
import nl.tudelft.rdfgears.rgl.datamodel.value.GraphValue;
import nl.tudelft.rdfgears.rgl.datamodel.value.LiteralValue;
import nl.tudelft.rdfgears.rgl.datamodel.value.RGLNull;
import nl.tudelft.rdfgears.rgl.datamodel.value.RGLValue;
import nl.tudelft.rdfgears.rgl.datamodel.value.RecordValue;
import nl.tudelft.rdfgears.rgl.datamodel.value.URIValue;
import nl.tudelft.rdfgears.rgl.workflow.LazyRGLValue;

/**
 * A visitor that just visites all values, recursively, thus evaluating them.
 * 
 * @author Eric Feliksik
 * 
 */
public class ValueEvaluator implements RGLValueVisitor {

	public ValueEvaluator() {
	}

	@Override
	public void visit(BagValue bag) {
		for (RGLValue val : bag) {
			val.accept(this);
		}
	}

	@Override
	public void visit(GraphValue graph) {
		// nothing to evaluate
	}

	@Override
	public void visit(BooleanValue bool) {
		// nothing to evaluate
	}

	@Override
	public void visit(LiteralValue literal) {
		// nothing to evaluate

	}

	@Override
	public void visit(RecordValue record) {

		for (String fieldName : record.getRange()) {
			record.get(fieldName).accept(this);
		}
	}

	@Override
	public void visit(URIValue uri) {
		// nothing to evaluate
	}

	@Override
	public void visit(RGLNull rglError) {
		// nothing to evaluate
	}

	@Override
	public void visit(LazyRGLValue lazyValue) {
		// we cannot deal with this value, let the value evaluate itself and
		// call this visitor
		// again with right method signature for OO-dispatching
		lazyValue.accept(this);
	}

}
