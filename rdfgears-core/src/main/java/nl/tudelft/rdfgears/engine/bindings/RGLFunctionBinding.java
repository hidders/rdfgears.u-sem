package nl.tudelft.rdfgears.engine.bindings;

import nl.tudelft.rdfgears.engine.diskvalues.ValueInflator;
import nl.tudelft.rdfgears.rgl.function.RGLFunction;

import com.sleepycat.bind.tuple.TupleBinding;
import com.sleepycat.bind.tuple.TupleInput;
import com.sleepycat.bind.tuple.TupleOutput;

public class RGLFunctionBinding extends TupleBinding<RGLFunction> {

	@Override
	public RGLFunction entryToObject(TupleInput in) {
		return ValueInflator.getFunction(in.readInt());
	}

	@Override
	public void objectToEntry(RGLFunction function, TupleOutput out) {
		int fId = ValueInflator.registerFunction(function);
		out.writeInt(fId);
	}

}
