package nl.tudelft.rdfgears.rgl.datamodel.value.impl;

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

import nl.tudelft.rdfgears.engine.Engine;
import nl.tudelft.rdfgears.engine.bindings.MemoryURIBinding;
import nl.tudelft.rdfgears.rgl.datamodel.value.RGLValue;
import nl.tudelft.rdfgears.rgl.datamodel.value.URIValue;
import nl.tudelft.rdfgears.rgl.exception.ComparisonNotDefinedException;

import com.hp.hpl.jena.rdf.model.RDFNode;
import com.sleepycat.bind.tuple.TupleBinding;

public class MemoryURIValue extends URIValue {
	public MemoryURIValue(String uri) {
		super(Engine.getDefaultModel().createResource(uri));
	}

	public MemoryURIValue(RDFNode uriNode) {
		super(uriNode);

		if (!uriNode.isURIResource()) {
			throw new RuntimeException(
					"Need a Node_URI; cannot instantiate URIValue with Node of type "
							+ uriNode.getClass());
		}
	}

	/**
	 * Override getRDFNode, do not create some Jena-resource with our value-id,
	 * but instead just return our Jena equivalent URI
	 */
	@Override
	public RDFNode getRDFNode() {
		assert (node != null);
		return node;
	}

	public int compareTo(RGLValue v2) {
		if (v2.isURI()) { // most likely
			return uriString().compareTo(v2.asURI().uriString());
		} else if (v2.isNull()) {
			return 1; // larger than all nulls
		} else if (v2.isLiteral()) {
			return -1; // we are bigger than null values / booleans
		} else {
			throw new ComparisonNotDefinedException(this, v2);
		}
	}

	@Override
	public TupleBinding<RGLValue> getBinding() {
		return new MemoryURIBinding();
	}

}
