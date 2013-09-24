package nl.tudelft.rdfgears.rgl.datamodel.value.impl.bags;

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

import java.util.Iterator;
import java.util.List;

import nl.tudelft.rdfgears.engine.ValueFactory;
import nl.tudelft.rdfgears.rgl.datamodel.value.BagValue;
import nl.tudelft.rdfgears.rgl.datamodel.value.RGLValue;

/**
 * A MemoryBag value supports adding elements, for during the construction. 
 * 
 * @author Eric Feliksik
 *
 */
public class ListBackedBagValue extends BagValue {
	protected List<RGLValue> backingList;
	
	public ListBackedBagValue(long id, List<RGLValue> list) {
		this(list);
		myId = id;
	}
	
	public ListBackedBagValue(List<RGLValue> list){
		backingList = list;
	}
	
	public ListBackedBagValue(){
		backingList = ValueFactory.createBagBackingList();
	}
	
	/**
	 * Return the List<RGLValue> Object that is backing this bag implementation. 
	 * Note that it must NOT be changed after an iterator() has been instantiated
	 * 
	 * Making private for now (not used) as it is not yet needed. 
	 * @return The backing list.
	 */
	private List<RGLValue> getBackingList(){
		return backingList;
	}
	
	/**
	 * Get an iterator over the bag; that is, an iterator over the backingList, as it is 
	 * assumed to be completely filled.  
	 */
	@Override
	public Iterator<RGLValue> iterator() {
		return backingList.iterator();
	}
	
	/**
	 * Get the size of this bag. That is, the size of the backingList, as it is 
	 * assumed to be completely filled.
	 */
	@Override
	public int size() {
		return backingList.size();
	}

	@Override
	public void prepareForMultipleReadings() {
		// nothing to do
	}
}
