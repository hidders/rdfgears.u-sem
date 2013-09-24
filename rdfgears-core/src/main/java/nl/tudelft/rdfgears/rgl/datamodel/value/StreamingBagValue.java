package nl.tudelft.rdfgears.rgl.datamodel.value;

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

public abstract class StreamingBagValue extends BagValue {
	
	/* Internal MaterializingBag to remember generated results. If enabled, 
	 * this implementation will create iterators over a MaterializingBag. */
	private MaterializingBag materializingBag = null;  
	
	/* rememberResults: use materializingBag or not. Must be false by default, because the interface
	 * does not allow it to be set to false. 
	 * The optimizer signals prepareForMultipleReadings() to processors, which signal it to the bags */
	private boolean rememberResults = false;
	
	public final Iterator<RGLValue> iterator(){  
		if (rememberResults){ 
			if (materializingBag == null) {
				materializingBag = new MaterializingBag(getStreamingBagIterator());
			}
//			Engine.getLogger().debug("Created materializing bag over bag "+(this.getClass().getName()));
			return materializingBag.iterator();
		}
		else {
			assert(materializingBag == null);
//			Engine.getLogger().debug("Creating steaming iterating over non-materialized  bag "+this.getClass().getName());
			
			// the internal bag may or may not do caching, but it probably won't unless it is a MaterializingBag
			return getStreamingBagIterator(); 
		}
	}
	
	public abstract Iterator<RGLValue> getStreamingBagIterator();
	
	
	/**
	 * A bag may decide itself how it likes to perform multiple iterations. 
	 * The default behaviour is to materialize the results.  
	 * However, a UNION bag may implement this by adding a materializing wrapper to it's two input bags, as combining at 
	 * iteration time is very cheap. 
	 * 
	 */
	@Override
	public void prepareForMultipleReadings() {
		rememberResults = true; // materialize results 
	}
	
	
}




/**
 * 
 * A MaterializingBag stores the results given by generatingBag, so that the generatingBag needs 
 * to iterate (and thus calculates it values) only once. 
 * 
 * Does not affect the pipelining nature of the bag. Can generate many Iterator instances with the iterator() method, 
 * and takes care that these iterators don't interfere with each other. 
 *  
 * 
 * @author Eric Feliksik
 *
 */
class MaterializingBag extends BagValue {
	protected List<RGLValue> storedList;
	// shared by all iterators over this bagValue
	private boolean storedListComplete = false; // boolean whether the backingList is completely filled 
	private Iterator<RGLValue> generatingBagIter;
	
	/**
	 * create a CachingBag using the provided BagBackingList;
	 * @param contents
	 */
	public MaterializingBag(List<RGLValue> contents, Iterator<RGLValue> generatingBagIter){
		this.storedList = contents;
		this.generatingBagIter = generatingBagIter;
	}
	

	public MaterializingBag(Iterator<RGLValue> generatingBagIter){
		this(ValueFactory.createBagBackingList(), generatingBagIter);
	}
	
	/**
	 * If the bag with RGLValue's is available, we can just return an iterator over the bag. 
	 * But if it is not available, we must give an iterator that will create one RGLValue at a time, 
	 * when next() is called on this iterator. Therefore we must create special Iterator instance. 
	 * If multiple of these iterator() instances are created, they will not interfere.  
	 */
	@Override
	public Iterator<RGLValue> iterator() {
		if (contentListComplete() ){
			return storedList.iterator(); 
		}
		else {
			/* bag is not full yet, we must give an iterator that fills the bag with RGLValues, while returning them */
			return new CachingBagIter();
		}
	}
	
	@Override
	public void prepareForMultipleReadings() {
		/* nothing to do; this store all our results, so we are always prepared */
	}
	
	
	
	
	

	/**
	 * Return the List<RGLValue> that is the storage. 
	 * 
	 * @return The backing list.
	 */
	private List<RGLValue> getStoredList(){
		return storedList;
	}
	/**
	 * Tell whether the getOutputBag() result is complete 
	 */
	public boolean contentListComplete() {
		return storedListComplete;
	}
	
	/** 
	 * Mark the stored list as complete 
	 */
	protected void markContentListComplete(){
		storedListComplete = true;
	}
	
	/**
	 * FIXME: method doesn't consult the generatingBag, although it may know its size. 
	 * We shoulnd't ask .size() because it may have an iterating implementation and then we don't store its elements. 
	 * So we need a knowsSize() method; if true, get size(); if not, iterate it with StoredBag and save the 
	 * results in storedList. 
	 */
	@Override
	public int size() {
		if (! contentListComplete()){
			BagValue.getNaiveSize(this);
		}
		
		assert(contentListComplete());
		return storedList.size();
	}
	
	
	
	/**
	 * An Iterator that can traverse the elements of a CachingBag, using/filling its storedList where possible. 
	 * 
	 * If the value is not yet contained, it fetches a value from the inputRowIterator, performs
	 * the processors RGLFunction on it (without iteration), stores the result in the bag, and returns it. 
	 * 
	 * @author Eric Feliksik
	 */
	public class CachingBagIter implements Iterator<RGLValue>{
		private int bagIndexPointer = 0; /* point to the index of the next element we have to return */
		private boolean haveNext;
		
		/**
		 * Create a LazyBagIterator. 
		 * @param resultList
		 * @param processor Only needed for its function implementation 
		 * @param inputRowIter
		 */
		public CachingBagIter(){
			setHaveNext();
		}
		
		private void setHaveNext() {
			haveNext = resultInStore() || generatingBagIter.hasNext();
			if (!haveNext){
				markContentListComplete();
			}
		}
		
		/**
		 * return true iff the next value (pointed by bagIndexPointer) is materialized in the result list 
		 * @return
		 */
		private boolean resultInStore(){
			return bagIndexPointer < getStoredList().size();
		}
		
		/**
		 * return true iff this iterator has a next element. 
		 */
		@Override
		public boolean hasNext() {
			return haveNext;
		}
		
		@Override
		public synchronized RGLValue next() {
			if (! hasNext()){
				throw new RuntimeException("there is no next() value, you should first call hasNext() on iterator");
			}
			
			RGLValue retVal; 
			if (resultInStore()){
				retVal = getStoredList().get(bagIndexPointer);
			} else {
				assert(!contentListComplete());
				assert(generatingBagIter.hasNext());
				retVal = generatingBagIter.next();
				getStoredList().add(retVal); // it is new
			}
			
			bagIndexPointer++;
			setHaveNext();
			return retVal;
		}	
		
		@Override
		public void remove() {
			assert(false): "Not implemented";
		}
	}

	
}



