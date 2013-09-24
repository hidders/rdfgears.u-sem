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
import nl.tudelft.rdfgears.rgl.function.RGLFunction;
import nl.tudelft.rdfgears.rgl.workflow.LazyRGLValue;
import nl.tudelft.rdfgears.util.row.ValueRow;

import com.sleepycat.bind.tuple.TupleBinding;
import com.sleepycat.bind.tuple.TupleInput;
import com.sleepycat.bind.tuple.TupleOutput;

public class LazyRGLBinding extends TupleBinding<RGLValue> {

	private RGLValue cachedResultValue;
	private RGLFunction function;
	private ValueRow inputRow;

	public LazyRGLBinding() {}

	public LazyRGLBinding(RGLValue cachedValue, RGLFunction function,
			ValueRow inputRow) {
		this.cachedResultValue = cachedValue;
		this.function = function;
		this.inputRow = inputRow;
	}

	@Override
	public RGLValue entryToObject(TupleInput in) {
		if (in.readBoolean()) { // there's cachedResultValue
			return BindingFactory.createBinding(in.readString()).entryToObject(
					in);
		} else { // the value is actually lazy
			return new LazyRGLValue(new RGLFunctionBinding().entryToObject(in),
					new ValueRowBinding().entryToObject(in));
		}
	}

	@Override
	public void objectToEntry(RGLValue v, TupleOutput out) {
		if (cachedResultValue != null) {
			out.writeBoolean(true);
			out.writeString(cachedResultValue.getClass().getName());
			cachedResultValue.getBinding()
					.objectToEntry(cachedResultValue, out);
		} else {
			out.writeBoolean(false);
			new RGLFunctionBinding().objectToEntry(function, out);
			new ValueRowBinding().objectToEntry(inputRow, out);
		}
	}

}
