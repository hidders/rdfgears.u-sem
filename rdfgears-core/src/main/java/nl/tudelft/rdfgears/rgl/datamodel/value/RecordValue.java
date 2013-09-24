package nl.tudelft.rdfgears.rgl.datamodel.value;

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

import java.util.Set;

import nl.tudelft.rdfgears.engine.bindings.RecordBinding;
import nl.tudelft.rdfgears.rgl.datamodel.value.visitors.RGLValueVisitor;
import nl.tudelft.rdfgears.rgl.exception.ComparisonNotDefinedException;
import nl.tudelft.rdfgears.util.row.ValueRow;

import com.sleepycat.bind.tuple.TupleBinding;

/**
 * An abstract implementation of RecordValue. Implementing classes must behave like a ValueRow
 * @author Eric Feliksik
 *
 */
public abstract class RecordValue extends DeterminedRGLValue implements ValueRow {
	public abstract RGLValue get(String fieldName);

	//public abstract Iterator<String> fieldNames();
	public abstract Set<String> getRange();
	
	public void accept(RGLValueVisitor visitor){
		visitor.visit(this);
	}
	
	@Override
	public RecordValue asRecord(){
		return this;
	}
	
	@Override
	public boolean isRecord(){
		return true;
	}

	public int compareTo(RGLValue v2) {
		// but may be implemented by subclass. It must be determined what is comparable, i think it'd be elegant to make as much as possible comparable.
		throw new ComparisonNotDefinedException(this, v2);
	}
	

	@Override
	public void prepareForMultipleReadings() {
		/* nothing to do */
	}

	@Override
	public TupleBinding<RGLValue> getBinding() {
		return new RecordBinding();
	}
	
	

}
