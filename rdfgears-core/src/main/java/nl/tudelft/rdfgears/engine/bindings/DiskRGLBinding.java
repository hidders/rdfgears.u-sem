package nl.tudelft.rdfgears.engine.bindings;

import nl.tudelft.rdfgears.rgl.datamodel.value.DiskRGLValue;
import nl.tudelft.rdfgears.rgl.datamodel.value.RGLValue;

import com.sleepycat.bind.tuple.TupleBinding;
import com.sleepycat.bind.tuple.TupleInput;
import com.sleepycat.bind.tuple.TupleOutput;

public class DiskRGLBinding extends TupleBinding<RGLValue> {

	private String className;

	public DiskRGLBinding(String className) {
		this.className = className;
	}

	public DiskRGLBinding() {} //nothing to do here

	@Override
	public RGLValue entryToObject(TupleInput in) {
		return new DiskRGLValue(in.readString(), in.readLong());
	}

	@Override
	public void objectToEntry(RGLValue value, TupleOutput out) {
		out.writeString(className);
		out.writeLong(value.getId());
	}

}
