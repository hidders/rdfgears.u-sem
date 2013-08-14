package nl.tudelft.rdfgears.rgl.datamodel.value.impl.bags;

import java.util.Iterator;
import java.util.NoSuchElementException;

import nl.tudelft.rdfgears.engine.bindings.SingletonBagBinding;
import nl.tudelft.rdfgears.rgl.datamodel.value.BagValue;
import nl.tudelft.rdfgears.rgl.datamodel.value.RGLValue;

import com.sleepycat.bind.tuple.TupleBinding;


public class SingletonBag extends BagValue {
	final RGLValue value;
	public SingletonBag(RGLValue val){
		value = val;
	}
	
	@Override
	public Iterator<RGLValue> iterator() {
		return new Iterator<RGLValue>(){
			private boolean done = false;
			
			@Override
			public boolean hasNext() {
				return !done;
			}

			@Override
			public RGLValue next() {
				if(done) 
					throw new NoSuchElementException();
				done = true;
				return value;
			}

			@Override
			public void remove() {	throw new UnsupportedOperationException();	}
			
		};
	}

	@Override
	public int size() {
		return 1;
	}

	@Override
	public void prepareForMultipleReadings() {
		// nothing to do 
	}

	@Override
	public TupleBinding<RGLValue> getBinding() {
		return new SingletonBagBinding();
	}
	
	
	
}
