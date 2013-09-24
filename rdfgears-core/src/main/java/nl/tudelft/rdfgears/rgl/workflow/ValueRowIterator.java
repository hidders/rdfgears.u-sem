package nl.tudelft.rdfgears.rgl.workflow;

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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import nl.tudelft.rdfgears.rgl.datamodel.value.RGLValue;
import nl.tudelft.rdfgears.util.row.FieldMappedValueRow;
import nl.tudelft.rdfgears.util.row.ValueRow;


/**
 * ValueRowIterator is an iterator over ValueRow objects. 
 * Construction takes an ValueRow and a processor that iterates. It then offers a way to iterate over
 * ValueRows that are constructed from the given ValueRow according to the RGL specification. 
 * That is, the types of the non-iterating ports are preserved and the values simply passed on; 
 * But for the iterating ports, the value in the returned ValueRow is some value taken from the bag 
 * in the original ValueRow.
 * 
 * @author Eric Feliksik
 * 
 */
public class ValueRowIterator implements Iterator<ValueRow> {

	/* a list of marked inputs, because we want to iterate in the same order every time */
	private List<String> markedInputList = new ArrayList<String>();
	
	/* the input-row, some of the elements over which we iterate */ 
	private ValueRow originalInputs;  
	
	/* the previously returned row. We can copy its contents when iterating, and replace one or more values to  
	 * make it unique. Its contents should not be modified, as a function will be using it as input! */
	private FieldMappedValueRow previousRow; 
	
	/* A map with iterators over the bags. Each iterator has a certain state. Altogether these 
	 * states determine how far the iteration is. */
	private HashMap<String, Iterator<RGLValue>> inputIterMap = new HashMap<String, Iterator<RGLValue>>(); 
	
	/* administration */
	boolean rowReadyToBeReturned; /* only true if we just initialized and next() was never called */
	private boolean doHaveNext = true; /* indicates whether there's a next value. */
	
	/* if true, we will not clone the returnRow each time, but instead return the same row over and over again. 
	 * This is possible if we are not used in a lazy context. */
	private boolean recycleReturnRow = false;
	
	
	/**
	 * Instantiate a ValueRowIterator; only tested with  recycleReturnRow==false, we may use it internally
	 * so it should not be modified. 
	 * 
	 * @param originalInputs
	 * @param processor
	 * @param recycleReturnRow Should be FALSE
	 */
	public ValueRowIterator(ValueRow originalInputs, FunctionProcessor processor, boolean recycleReturnRow ){
		this(originalInputs, processor);
		this.recycleReturnRow = recycleReturnRow;
	}
	
	/**
	 * Construct a ValueRowIterator.
	 * 
	 * @param inputRow An inputrow that contains bags over which we must iterate. Must provide a value for every name in requiredInputs.  
	 * @param requiredInputs A set of required input-names. 
	 * @param iteratingInputs A set of input-names over which to iterate. Subset of requiredInputs. For every name in this set, inputRow[name] must be a bag. 
	 * 
	 */
	private ValueRowIterator(ValueRow originalInputs, FunctionProcessor processor ){
		this.originalInputs = originalInputs;
		
		/**
		 *  Prepare the returnrow for the first next() call.   
		 */
		
		if (! processor.iterates()){
			/* There are not bags to iterate over, and the result is just the original valueRow - 
			 * which has been copied already. */
			doHaveNext = false;
		}
		else {
			/* initialize first row */
			previousRow = new FieldMappedValueRow(processor.getFunction().getFieldIndexMap());
			Iterator<InputPort> portIter = processor.getPortSet().iterator();
			
			/* We must initialize doHaveNext */
			
			while(portIter.hasNext()){
				InputPort port = portIter.next();
				String portName = port.getName();
				
				if (port.iterates()){
					/* allow guaranteed-order traversal of input names*/
					markedInputList.add(portName);
					
					/* the number of results will be the product of the size of all the bags */
					//this.expectedSize *= originalInputs.get(portName).asBag().size();
					
					/*
					 *  replace the iterable bags in returnRow with values of those bags 
					 */
					Iterator<RGLValue> bagIter = resetBagIterator(portName); /* initialize iterator for the first time */
					if (! bagIter.hasNext()){
						/* We found a marked port with empty bag, so the iterable set of returnRows is empty */
						doHaveNext = false;
						return;
					}
					/* init returnRow with the first value of the bag */
					previousRow.put(portName, bagIter.next());
					
				} else {
					/* get value from the generating producer */
					previousRow.put(portName, this.originalInputs.get(portName)); 
				}
				
			}
			doHaveNext = true;
		}
		rowReadyToBeReturned = true;
		
	}
	
	/**
	 * Get the next ValueRow of this iterator. Only works if hasNext() returned true. 
	 */
	@Override
	public ValueRow next() {
		if (!this.doHaveNext)
			throw new java.util.NoSuchElementException("You should call hasNext() first."); 
		
		Iterator<String> bagNameIter = markedInputList.iterator();
		boolean foundNewCombination = false; /* whether we found a value in an iterator without resetting that iterator. */
		boolean evenMore = false; /* whether we have somewhere seen that an iterator has more values, except for the consumed one */
		
		FieldMappedValueRow returnRow = previousRow;
		if (this.rowReadyToBeReturned){ 
			/** only the first time, the row was already prepared by taking the first value of every 
			 * iterator; so we don't need to modify returnRow */
			this.rowReadyToBeReturned = false;
		}
		else {
			/**
			 * We need a new returnRow value. 
			 * For every Bag, see if the associated valueIterator has more values. 
			 * As soon as we find a next value, we are done. 
			 * If none of the iterators has a next value, something went wrong, because we should have
			 * detected that in advance. If we have selected a new, unique value, THEN we decide to set 
			 * doHaveNext to true iff there is some BagValueIterator that has more values.  
			 */
			
//			if (! previousRow.isRecyclable()) // it is not proven yet that recycling actually solves a garbage problem. 
				returnRow = previousRow.clone(); 
			
			
			while (bagNameIter.hasNext()){
				String bagName = bagNameIter.next();
				Iterator<RGLValue> valueIter = getBagIterator(bagName);
				
				if (valueIter.hasNext()){
					foundNewCombination = true; /* We will set it in returnRow and we're done */
				}
				else {
					/* we will reset the iterator and take a new value for returnRow, but the combination is not unique yet. */
					valueIter = resetBagIterator(bagName);
					assert(valueIter.hasNext()) : "There must be a value in the iterator, as we checked for empty bags on instantiation.";
				}
				RGLValue val = null;
				val = valueIter.next();
				
				
				/* modify returnRow */
				returnRow.put(bagName, val);
				
				/* if we somewhere encounter an iterator with more values, the next call to hasNext() should be true */
				if (!evenMore)
					evenMore = valueIter.hasNext(); 

				if (foundNewCombination){
					break;
				}
					
			}
		}
		
		/**
		 * returnRow is now prepared. Prepare this.doHaveNext 
		 */
		
		/* If all the bagIterators we have visited so far are exhausted (i.e. haveNext()==false), then evenMore==false; 
		 * But there may be a not-yet-visited bagIterator that has more values. 
		 */
		if (! evenMore){
			while (bagNameIter.hasNext()){
				if (getBagIterator(bagNameIter.next()).hasNext()){ /* this iterator has more values, for the following next() call */
					evenMore = true;
					break;
				}
			}	
		}
		
		this.doHaveNext = evenMore;
		
		previousRow = returnRow;
		
		return returnRow;
	}

	/**
	 * Create a new iterator for the bag with given name, set it in the inputIterMap, 
	 * and return it.    
	 * @param name The name of the bag. 
	 */
	private Iterator<RGLValue> resetBagIterator(String name){
		Iterator<RGLValue> bagIter = originalInputs.get(name).asBag().iterator();
		inputIterMap.put(name, bagIter);
		return bagIter;
	}
	
	/**
	 * Get the bag iterator for the given bag inputName.  
	 * It is not a fresh iterator (unless it was just reset); it is in the state in which we left it.  
	 * @param name
	 * @return
	 */
	private Iterator<RGLValue> getBagIterator(String name){
		return inputIterMap.get(name);
	}
	
	@Override
	public boolean hasNext() {
		return doHaveNext;
	}
	
	@Override
	public void remove() {
		assert(false): "not implemented";
	}


//	public int size() {
//		// calculate by multiplying the sizes of all bags. But bag size retrieval may be expensive, when
//		// the size is not known before iterating it completely
//		return expectedSize ;
//	}
//	


}
