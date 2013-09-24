package nl.tudelft.rdfgears.engine.bindings;

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

import nl.tudelft.rdfgears.rgl.datamodel.value.RGLValue;
import nl.tudelft.rdfgears.rgl.datamodel.value.impl.MemoryLiteralValue;

import com.hp.hpl.jena.datatypes.xsd.impl.XSDDouble;
import com.sleepycat.bind.tuple.TupleBinding;
import com.sleepycat.bind.tuple.TupleInput;
import com.sleepycat.bind.tuple.TupleOutput;

public class MemoryLiteralBinding extends TupleBinding<RGLValue> {
	final int DOUBLE = 0;
	final int STRING = 1;

	@Override
	public RGLValue entryToObject(TupleInput in) {
		RGLValue ret = null;
		switch (in.readInt()) {
		case DOUBLE:
			ret = MemoryLiteralValue.createLiteralTyped(in.readDouble(),
					new XSDDouble("double"));
			break;
		case STRING:
			ret = MemoryLiteralValue.createPlainLiteral(in.readString(), null);
			break;
		}
		return ret;
	}

	@Override
	public void objectToEntry(RGLValue value, TupleOutput out) {
		// TODO Auto-generated method stub
		MemoryLiteralValue literal = (MemoryLiteralValue) value;
		if (literal.getRDFNode().getDatatypeURI() == null) {
			out.writeInt(STRING);
			out.writeString(literal.getValueString());
		} else if (literal.getRDFNode().getDatatypeURI().equals(
				"http://www.w3.org/2001/XMLSchema#double")) {
			out.writeInt(DOUBLE);
			out.writeDouble(literal.getValueDouble());
		} else {
			out.writeInt(STRING);
			out.writeString(literal.getValueString());
		}
	}

}