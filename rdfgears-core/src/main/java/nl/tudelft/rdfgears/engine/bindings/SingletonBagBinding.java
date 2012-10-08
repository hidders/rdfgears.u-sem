package nl.tudelft.rdfgears.engine.bindings;

import nl.tudelft.rdfgears.rgl.datamodel.value.RGLValue;
import nl.tudelft.rdfgears.rgl.datamodel.value.impl.bags.SingletonBag;

import com.sleepycat.bind.tuple.TupleBinding;
import com.sleepycat.bind.tuple.TupleInput;
import com.sleepycat.bind.tuple.TupleOutput;

public class SingletonBagBinding extends ComplexBinding {
	private class InnerBinding extends TupleBinding<RGLValue> {

		@Override
		public RGLValue entryToObject(TupleInput in) {
			return new SingletonBag(BindingFactory.createBinding(
					in.readString()).entryToObject(in));
		}

		@Override
		public void objectToEntry(RGLValue value, TupleOutput out) {
			RGLValue element = value.asBag().iterator().next();
			out.writeString(element.getClass().getName());
			element.getBinding().objectToEntry(element, out);
		}

	}

	@Override
	protected TupleBinding<RGLValue> getInnerBinding() {
		return new InnerBinding();
	}
}
