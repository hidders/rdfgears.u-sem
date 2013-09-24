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

import nl.tudelft.rdfgears.rgl.datamodel.value.BagValue;
import nl.tudelft.rdfgears.rgl.datamodel.value.RGLValue;
import nl.tudelft.rdfgears.rgl.function.core.BagUnion;

import com.sleepycat.bind.tuple.TupleBinding;
import com.sleepycat.bind.tuple.TupleInput;
import com.sleepycat.bind.tuple.TupleOutput;

public class UnionBagBinding extends ComplexBinding {
	private class InnerBinding extends TupleBinding<RGLValue> {			
		
		@Override
		public RGLValue entryToObject(TupleInput in) {
			bag1 = BindingFactory.createBinding(in.readString()).entryToObject(
					in).asBag();
			bag2 = BindingFactory.createBinding(in.readString()).entryToObject(
					in).asBag();
			return BagUnion.createUnionBag(id, bag1, bag2);
		}

		@Override
		public void objectToEntry(RGLValue bag, TupleOutput out) {
			out.writeString(bag1.getClass().getName());
			bag1.getBinding().objectToEntry(bag1, out);
			out.writeString(bag2.getClass().getName());
			bag2.getBinding().objectToEntry(bag2, out);

		}

	}

	private BagValue bag1;
	private BagValue bag2;
	private long id;

	public UnionBagBinding(BagValue bag1, BagValue bag2) {
		this.bag1 = bag1;
		this.bag2 = bag2;
	}

	public UnionBagBinding() {
		super();
	}

	@Override
	protected TupleBinding<RGLValue> getInnerBinding() {
		return new InnerBinding();
	}



}
