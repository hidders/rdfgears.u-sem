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
import nl.tudelft.rdfgears.util.row.FieldIndexHashMap;
import nl.tudelft.rdfgears.util.row.FieldMappedValueRow;
import nl.tudelft.rdfgears.util.row.ValueRow;

import com.sleepycat.bind.tuple.TupleBinding;
import com.sleepycat.bind.tuple.TupleInput;
import com.sleepycat.bind.tuple.TupleOutput;

public class ValueRowBinding extends TupleBinding<ValueRow> {

	@Override
	public ValueRow entryToObject(TupleInput in) {
		int size = in.readInt();
		FieldIndexHashMap fiMap = new FieldIndexHashMap();
		List<String> fieldNames = new ArrayList<String>();

		for (int i = 0; i < size; ++i) {
			String fieldName = in.readString();
			fieldNames.add(fieldName);
			fiMap.addFieldName(fieldName);
		}

		FieldMappedValueRow row = new FieldMappedValueRow(fiMap);

		for (String fieldName : fieldNames) {
			if (in.readBoolean()) { // reading simple value
				String className = in.readString();
				row.put(fieldName, BindingFactory.createBinding(className)
						.entryToObject(in));
			} else { // creating DiskRGLValue representation of complex value
				row.put(fieldName, new DiskRGLValue(in.readString(), in
						.readLong()));
			}
		}

		return row;
	}

	@Override
	public void objectToEntry(ValueRow inputRow, TupleOutput out) {
		out.writeInt(inputRow.getRange().size());
		for (String fieldName : inputRow.getRange()) {
			out.writeString(fieldName);
		}
		for (String fieldName : inputRow.getRange()) {
			RGLValue fieldValue = inputRow.get(fieldName);
			out.writeBoolean(fieldValue.isSimple());
			out.writeString(fieldValue.getClass().getName());
			fieldValue.getBinding().objectToEntry(fieldValue, out);
		}
	}
}
