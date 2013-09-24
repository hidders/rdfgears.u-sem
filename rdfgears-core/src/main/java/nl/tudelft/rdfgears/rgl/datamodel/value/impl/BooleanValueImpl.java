package nl.tudelft.rdfgears.rgl.datamodel.value.impl;

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

import nl.tudelft.rdfgears.rgl.datamodel.value.BooleanValue;

/** 
 * In RGL, a 'False' Boolean is simulated by an empty set. a 'True' Boolean is simulated by a singleton set 
 * containing an empty record.  
 * This allows for some NRC optimalizations. But in our implementation we don't do this. It is just a boolean. 
 *
 * @author Eric Feliksik
 *
 */
public class BooleanValueImpl extends BooleanValue {
	/**
	 * Singleton true/false values
	 */
	private static BooleanValueImpl falseInstance = new BooleanValueImpl(false); 
	private static BooleanValueImpl trueInstance = new BooleanValueImpl(true);
	public static BooleanValueImpl getTrueInstance(){ return trueInstance; }
	public static BooleanValueImpl getFalseInstance(){ return falseInstance; }
	
	boolean isTrue;
	private BooleanValueImpl(boolean val){
		isTrue = val;
	}
	
	@Override
	public boolean isTrue() {
		return isTrue;
	}
//
//	@Override
//	public Iterator<Expression> iterator() {
//		return new Iterator<Expression>(){
//			private boolean hasNext = isTrue;
//			
//			@Override
//			public boolean hasNext() {
//				return hasNext;
//			}
//			@Override
//			public Expression next() {
//				if (! hasNext){
//					throw new RuntimeException("you must call hasNext() first, and a false-boolean has no bag-elements");
//				}
//				hasNext = false;
//				return EmptyRecordValue.getInstance();
//			}
//			@Override
//			public void remove() { /* not implemented */ }
//		};	
//	}
//	
//	@Override
//	public int size() {
//		return isTrue ? 1 : 0; 
//	}
	
	@Override
	public void prepareForMultipleReadings() {
		/* nothing to do */
	}


}
