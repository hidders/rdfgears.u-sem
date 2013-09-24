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