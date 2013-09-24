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

import java.util.Iterator;

import nl.tudelft.rdfgears.engine.bindings.NaiveBagBinding;
import nl.tudelft.rdfgears.rgl.datamodel.value.visitors.RGLValueVisitor;
import nl.tudelft.rdfgears.rgl.exception.ComparisonNotDefinedException;

import com.sleepycat.bind.tuple.TupleBinding;

public abstract class BagValue extends DeterminedRGLValue implements Iterable<RGLValue> {
	
	public abstract Iterator<RGLValue> iterator();
	
	/**
	 * Get the size of this bag. Note that this MAY (re)iterate the bag, which MAY be expensive if it is not Materialized.  
	 * @return
	 */
	public abstract int size();
	
	@Override
	public BagValue asBag(){
		return this;
	}
	
	@Override
	public boolean isBag(){
		return true;
	}
	
	@Override
	public boolean isGraph(){
		return false; // may become true iff bag contains (s,p,o) records - correction: now, we need explicit casting for now
	}

	public void accept(RGLValueVisitor visitor){
		visitor.visit(this);
	}
	
	/**
	 * A bag may decide itself how it likes to perform multiple iterations.
	 * A MaterializingBag may not need to do anything, a streaming bag may decide to apply a MaterializingBag to itself
	 * 
	 */
	@Override
	public abstract void prepareForMultipleReadings(); 
	
	/**
	 * A very naive function that gets bagsize by iterating over all elements. 
	 * Bag implementations that are smarter should override this
	 * 
	 * @param bag
	 * @return
	 */
	public static int getNaiveSize(BagValue bag){
		int size = 0;
		for (@SuppressWarnings("unused") RGLValue val : bag){
			size++;
		}
		return size;
	}
	

	public int compareTo(RGLValue v2) {
		// but may be implemented by subclass. It must be determined what is comparable, i think it'd be elegant to make as much as possible comparable.
		throw new ComparisonNotDefinedException(this, v2);
	}

	@Override
	public TupleBinding<RGLValue> getBinding() {
		return new NaiveBagBinding();
	}
	
	
	
}

