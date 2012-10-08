package nl.tudelft.rdfgears.engine.bindings;

import nl.tudelft.rdfgears.rgl.datamodel.value.BagValue;
import nl.tudelft.rdfgears.rgl.datamodel.value.DiskRGLValue;
import nl.tudelft.rdfgears.rgl.datamodel.value.RGLValue;
import nl.tudelft.rdfgears.rgl.datamodel.value.RecordValue;
import nl.tudelft.rdfgears.rgl.datamodel.value.impl.MemoryLiteralValue;
import nl.tudelft.rdfgears.rgl.datamodel.value.impl.MemoryURIValue;
import nl.tudelft.rdfgears.rgl.datamodel.value.impl.bags.EmptyBag;
import nl.tudelft.rdfgears.rgl.datamodel.value.impl.bags.SingletonBag;
import nl.tudelft.rdfgears.rgl.workflow.LazyRGLValue;

import com.sleepycat.bind.tuple.TupleBinding;

public class BindingFactory {
	/**
	 * This method should only be used to get binding when deserializing objects
	 * from DB.
	 * 
	 * For the purpose of serialization always use RGLValue.getBinding().
	 * 
	 * @param className
	 *            Class of which object we would like to deserialize from DB
	 * @return Binding designed to read object.
	 * 
	 * 
	 */
	public static TupleBinding<RGLValue> createBinding(String className) {
		try {
			if (className.equals(MemoryLiteralValue.class.getName())) {
				return new MemoryLiteralBinding();
			} else if (className.equals(MemoryURIValue.class.getName())) {
				return new MemoryURIBinding();
			} else if (className.equals(LazyRGLValue.class.getName())) {
				return new LazyRGLBinding();
			} else if (isRecordClassName(className)) {
				return new RecordBinding();
			} else if (isBagClassName(className)) {
				if (className
						.equals("nl.tudelft.rdfgears.rgl.function.core.UnionBagValue")) {
					return new UnionBagBinding();
				} else if (className.equals(SingletonBag.class.getName())) {
					return new SingletonBagBinding();
				} else if (className.equals(EmptyBag.class.getName())) {
					return new EmptyBagBinding();
				} else {
					return new NaiveBagBinding();
				}
			} else if (className.equals(DiskRGLValue.class.getName())) {
				return new DiskRGLBinding();
			} else {
				return new PoorMansBinding();
			}
		} catch (ClassNotFoundException e) {
			throw new RuntimeException("Class " + className + " not found.");
		}
	}

	private static boolean isBagClassName(String className)
			throws ClassNotFoundException {
		return isAssignable(BagValue.class, className);
	}

	private static boolean isRecordClassName(String className)
			throws ClassNotFoundException {
		return isAssignable(RecordValue.class, className);
	}

	private static boolean isAssignable(Class<?> cls, String className)
			throws ClassNotFoundException {
		return cls.isAssignableFrom(Class.forName(className));
	}
}