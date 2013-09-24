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

import java.util.ArrayList;
import java.util.List;

import nl.tudelft.rdfgears.rgl.datamodel.value.DiskRGLValue;
import nl.tudelft.rdfgears.rgl.datamodel.value.RGLValue;

import com.sleepycat.bind.tuple.TupleBinding;
import com.sleepycat.bind.tuple.TupleInput;
import com.sleepycat.bind.tuple.TupleOutput;

public class RGLListBinding extends TupleBinding<List<RGLValue>> {

	@Override
	public List<RGLValue> entryToObject(TupleInput in) {
		ArrayList<RGLValue> result = new ArrayList<RGLValue>();
		int size = in.readInt();
		for (int i = 0; i < size; ++i) {
			if (in.readBoolean()) { //reading simple value
				result.add((RGLValue) BindingFactory.createBinding(
						in.readString()).entryToObject(in));
			} else { // creating DiskRGLValue representation of complex value
				result.add(new DiskRGLValue(in.readString(), in.readLong()));
			}
		}
		return result;
	}

	@Override
	public void objectToEntry(List<RGLValue> list, TupleOutput out) {
		out.writeInt(list.size());
		for (RGLValue element : list) {
			out.writeBoolean(element.isSimple());
			out.writeString(element.getClass().getName());

			TupleBinding<RGLValue> elementBinding = element.getBinding();
			elementBinding.objectToEntry(element, out);

		}

	}
}