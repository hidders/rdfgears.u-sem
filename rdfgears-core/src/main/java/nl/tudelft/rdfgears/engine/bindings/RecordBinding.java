package nl.tudelft.rdfgears.engine.bindings;

import nl.tudelft.rdfgears.rgl.datamodel.value.RGLValue;
import nl.tudelft.rdfgears.rgl.datamodel.value.impl.MemoryRecordValue;

import com.sleepycat.bind.tuple.TupleBinding;
import com.sleepycat.bind.tuple.TupleInput;
import com.sleepycat.bind.tuple.TupleOutput;

/**
 * @author Tomasz Traczyk
 * 
 *         This class relys only on fulfilling by Record implementation of the
 *         ValueRow interface and thus will work (not necessarily optimal) for
 *         any further implementations of Record that would fulfill the
 *         mentioned intefrace.
 */
public class RecordBinding extends ComplexBinding {

	private class InnerBinding extends TupleBinding<RGLValue> {

		@Override
		public RGLValue entryToObject(TupleInput in) {
			return new MemoryRecordValue(id, new ValueRowBinding()
					.entryToObject(in));
		}

		@Override
		public void objectToEntry(RGLValue record, TupleOutput out) {
			new ValueRowBinding().objectToEntry(record.asRecord(), out);
		}

	}

	@Override
	protected TupleBinding<RGLValue> getInnerBinding() {
		return new InnerBinding();
	}
}
