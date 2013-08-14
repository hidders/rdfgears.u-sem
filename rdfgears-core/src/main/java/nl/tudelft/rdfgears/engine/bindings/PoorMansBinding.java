package nl.tudelft.rdfgears.engine.bindings;

import nl.tudelft.rdfgears.engine.diskvalues.ValueInflator;
import nl.tudelft.rdfgears.rgl.datamodel.value.RGLValue;

import com.sleepycat.bind.tuple.TupleBinding;
import com.sleepycat.bind.tuple.TupleInput;
import com.sleepycat.bind.tuple.TupleOutput;

public class PoorMansBinding extends TupleBinding<RGLValue> {

	@Override
	public RGLValue entryToObject(TupleInput in) {
		return ValueInflator.getValue(in.readLong());
	}

	@Override
	public void objectToEntry(RGLValue v, TupleOutput out) {
		ValueInflator.registerValue(v);
		out.writeLong(v.getId());
	}

}
