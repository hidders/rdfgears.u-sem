package nl.tudelft.rdfgears.engine.bindings;

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
