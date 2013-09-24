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

import nl.tudelft.rdfgears.engine.diskvalues.DatabaseManager;
import nl.tudelft.rdfgears.engine.diskvalues.ValueInflator;
import nl.tudelft.rdfgears.rgl.datamodel.value.RGLValue;

import com.sleepycat.bind.tuple.TupleBinding;
import com.sleepycat.bind.tuple.TupleInput;
import com.sleepycat.bind.tuple.TupleOutput;
import com.sleepycat.je.Database;
import com.sleepycat.je.DatabaseEntry;

public abstract class ComplexBinding extends TupleBinding<RGLValue> {

	protected long id;
	protected TupleBinding<RGLValue> innerBinding;

	public ComplexBinding() {
		innerBinding = getInnerBinding();
	}

	protected abstract TupleBinding<RGLValue> getInnerBinding();

	public final RGLValue complexEntryToObject(DatabaseEntry entry) {
		return innerBinding.entryToObject(entry);
	}
	
	@Override
	public final RGLValue entryToObject(TupleInput in) {
		id = in.readLong();
		return innerBinding.entryToObject(DatabaseManager.getComplexEntry(id));
	}

	@Override
	public final void objectToEntry(RGLValue value, TupleOutput out) {
		if (ValueInflator.registerComplex(value.getId())) {
			Database complexStore = DatabaseManager.getComplexStore();
			DatabaseEntry valueEntry = new DatabaseEntry();
			innerBinding.objectToEntry(value, valueEntry);
			DatabaseEntry idEntry = DatabaseManager.long2entry(value.getId());
			complexStore.put(null, idEntry, valueEntry);
		}
		out.writeLong(value.getId());
	}
}
