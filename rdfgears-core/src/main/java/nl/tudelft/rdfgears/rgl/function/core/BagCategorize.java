package nl.tudelft.rdfgears.rgl.function.core;

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

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import nl.tudelft.rdfgears.engine.ValueFactory;
import nl.tudelft.rdfgears.engine.WorkflowLoader;
import nl.tudelft.rdfgears.rgl.datamodel.type.BagType;
import nl.tudelft.rdfgears.rgl.datamodel.type.RDFType;
import nl.tudelft.rdfgears.rgl.datamodel.type.RGLType;
import nl.tudelft.rdfgears.rgl.datamodel.type.RecordType;
import nl.tudelft.rdfgears.rgl.datamodel.type.SuperTypePattern;
import nl.tudelft.rdfgears.rgl.datamodel.value.BagValue;
import nl.tudelft.rdfgears.rgl.datamodel.value.RGLValue;
import nl.tudelft.rdfgears.rgl.datamodel.value.StreamingBagValue;
import nl.tudelft.rdfgears.rgl.datamodel.value.impl.ModifiableRecord;
import nl.tudelft.rdfgears.rgl.exception.FunctionTypingException;
import nl.tudelft.rdfgears.rgl.exception.WorkflowCheckingException;
import nl.tudelft.rdfgears.rgl.exception.WorkflowLoadingException;
import nl.tudelft.rdfgears.rgl.function.AtomicRGLFunction;
import nl.tudelft.rdfgears.rgl.function.RGLFunction;
import nl.tudelft.rdfgears.util.row.FieldIndexMap;
import nl.tudelft.rdfgears.util.row.FieldIndexMapFactory;
import nl.tudelft.rdfgears.util.row.SingleElementValueRow;
import nl.tudelft.rdfgears.util.row.TypeRow;
import nl.tudelft.rdfgears.util.row.ValueRow;


/**
 * A function that classifies a bag by separating the values in a number of different bags. 
 * Each resulting bag in contained in the tuple result of this function.
 * 
 * The record-fieldnames containing these bags are determined by the classification function.
 * The internal classification function takes an element of type T and outputs a Literal string. If the 
 * resulting classification mechanism is annotated with a finite, non-empty subset of the classification
 * functions domain, e.g. {"field1", "field2"}. 
 * 
 * Then the overall classification is a mapping of type {{ T }} to a record < field1: {{ T }} , field2: {{ T }} >
 * 
 * The resulting Record contains CategoryBag elements. Those are implementations of BagValue that generate
 * their results on-demand. However, if elements are read from CategoryBag 'field1', many values may be categorized
 * for 'field2' in the the meantime. This means these values are pushed into the 'field2' bag. Thus this 'field2' bag may 
 * become very big.  
 * 
 * For this reason, the values are pushed on a Stack (remember that a Bag doesn't offer guarantees any order). This allows 
 * easy insertion and removal from the pushed values, and the Stack can (theoretically!) shrink in memory size when needed. 
 * If caching is really needed, this will be enabled by the auto-caching mechanism with a CachingBag, that absorbs the values 
 * once they are popped from Stack.
 * 
 * Note: 
 * 1) Unfortunately java.util.Stack doesn't do Shrinking. So currently we do not reclaim memory. 
 * 2) The current implementation of this bag can only be generated ONCE. That means that the default BagValue caching mechanism 
 * MUST be enabled if you do want to iterate multiple times. Internally the iterators over the CategoryBags collaboratively 
 * fill each others bags. Because the Stack also doesn't shrink, this costs twice the memory. 
 * 
 * Another approach would be to store values in a BagBackingList (from ValueFactories' createBagBackingList() method) so 
 * if this factory gives a List-implementation that does smart disk-swapping, you should be fine. If it does not, this may 
 * cost a lot of memory. But this approach would have the drawbacks of net being able to reclaim memory, and that different iterators
 * over the same and different categories should not interfere with the BagCache, if any.   
 * 
 * Yet another approach would require not using the default BagValue/CacheBag caching and to do it all in this class. 
 * It can then cache or not cache, depending on the requirements, and make sure that multiple iterators over various CategoryBags
 * are handled gracefully. But this is complex. 
 *  
 * @author Eric Feliksik
 *
 */
public class BagCategorize extends AtomicRGLFunction  {
	public static String inputName = "bag";
	private RGLFunction categorizer;
	public static String categorizerInputName; 
	
	/** just assume standard size for now. Better HashMap size estimation based on size of categoriesMap 
	 * is unlikely to be significant */
	Map<String, CategoryBag> categoryBagMap = new HashMap<String, CategoryBag>();
	FieldIndexMap categoriesMap;
	
	// iterator over the input bag. Input bag is iterated only once by this function 
	private Iterator<RGLValue> inputBagIter;  
	

	@Override
	public void initialize(Map<String, String> config) throws WorkflowLoadingException {
		requireInput(inputName);

		categorizer = WorkflowLoader.instantiateFunction(config.get("categorizeFunction"));
		
		/** find the input name of the function; function will be called with the bag elements as only (named) argument */
		List<String> requiredInputNames = categorizer.getRequiredInputNames();
		if (requiredInputNames.size()!=1){
			throw new IllegalArgumentException("The categorizeFunction must have exactly one inputname, not "+requiredInputNames.size());
		}
		
		categorizerInputName = requiredInputNames.iterator().next();

		/* configure the classification categories based on the ';' separated list of field names */
		String fieldsStr = config.get("categories");
		String[] fieldsArray = fieldsStr.split(";");
		for (int i=0; i<fieldsArray.length; i++){
			if (fieldsArray[i].length()==0)
				throw new RuntimeException("your categories configuration parameter contains ';;' - that is an empty field name, which we cannot handle.");
		}
		categoriesMap = FieldIndexMapFactory.create(fieldsArray);
		
	}
	
	public RGLFunction getCategorizerFunction(){
		return this.categorizer;
	}
	
	
	@Override
	public RGLValue execute(ValueRow inputRow) {
		RGLValue bag = inputRow.get(inputName);
		assert(bag!=null): "Something went wrong with typechecking";
		if (bag.isNull())
			return bag; // return the error
		
		ModifiableRecord rec = ValueFactory.createModifiableRecordValue(categoriesMap);
		
		/**
		 * create self-filling bags to put in this record. 
		 */
		for (String fieldName : categoriesMap.getFieldNameSet()){
			CategoryBag categoryBag = new CategoryBag(fieldName);
			categoryBagMap.put(fieldName, categoryBag);
			rec.put(fieldName, categoryBag);
		}
		
		inputBagIter = bag.asBag().iterator();

		return rec;
		
//		BagValue inputBag = bag.asBag();
//		return new BagFilteringBagValue(categorizer, inputBag);
	}

	@Override
	public RGLType getOutputType(TypeRow inputTypes) throws FunctionTypingException {
		RGLType elemType = inputTypes.get(inputName); 
		RGLType actualType = elemType;
		RGLType requiredType = BagType.getInstance(new SuperTypePattern());
		
		if (!(elemType instanceof BagType )){
			throw new FunctionTypingException(inputName,requiredType,actualType);
		}
		
		BagType bagType = (BagType) elemType;
		
		TypeRow inputTypeRow = new TypeRow();
		inputTypeRow.put(categorizerInputName, bagType.getElemType());
		
		RGLType testingOutputType;
		try {
			testingOutputType = categorizer.getOutputType(inputTypeRow);
		} catch (WorkflowCheckingException e) {
			/* the filter function is not well typed. We will throw an error with a trace to this filter function, which *includes* an error 
			 * with a trace to the problem IN the filter function. 
			 */
			e.setProcessorAndFunction(null, categorizer);
			String filterProblemMsg = e.getProblemDescription();
			throw new FunctionTypingException(filterProblemMsg+"\n\nWhich is used as a filter-function: ");
		} 
		
		if (!(testingOutputType instanceof RDFType)){
			throw new FunctionTypingException("The "+categorizer.getRole()+" '"+categorizer.getFullName()+"' is used as classifyFunction and must therefore return a Literal String.");
		}
		
		/** 
		 * ok so far, create a record with all the category-name-fields, all 
		 * containing bags of same type as the input Bag type
		 */
		TypeRow outputTypeRow = new TypeRow();
		for (String fieldName : categoriesMap.getFieldNameSet() ){
			outputTypeRow.put(fieldName, bagType);	
		}
		return RecordType.getInstance(outputTypeRow);
	}

	/**
	 * A categoryBag lazily fills the categories. 
	 * When iterated, it fetches elements from the inputBag of the categorizer. 
	 * It inserts the elements in the appropriate categorybag, until it finds an 
	 * element of its own category. If it finds such an element it returns it. 
	 * 
	 * It does *NOT* do permanent caching for itself. So throws away elements after 
	 * they are requested. For this reason, it needs to use a wrapping CachingBag if 
	 * iterated more than once.  
	 *  
	 * 		
	 * @author Eric Feliksik
	 *
	 */
	public class CategoryBag extends StreamingBagValue {
		
		
		private String category; /* the category for which I am a bag. */ 
		
		/** this implementation can only have ONE iterator */  
		private CategoryBagIterator instantiatedIterator = new CategoryBagIterator(); 
		
		private boolean iteratorHasBeenRequested = false;
		
		public CategoryBag(String category){
			this.category = category;
		}
		
		public void push(RGLValue val){
			instantiatedIterator.push(val);
		}
		
		@Override
		public Iterator<RGLValue> getStreamingBagIterator() {
			/**
			 * if caching is disabled, we cannot create a new iterator as the different iterators of the different
			 * categories all interact... So prevent this as it would lead to obscure failures or incorrect results. 
			 */
			if (iteratorHasBeenRequested)
				throw new RuntimeException("You are instantiating an iterator over CategoryBag for the 2nd time. This is not possible and will break the Bag. Make sure you enable materialization for this bag (enable the optimizer).  ");
			
			iteratorHasBeenRequested = true;
			return instantiatedIterator;
		}

		@Override
		public int size() {
			return BagValue.getNaiveSize(this);
		}
		
		/**
		 * generate values, filling the appropriate CategoryBag until we find a value for our category.  
		 * @author Eric Feliksik
		 *
		 */
		class CategoryBagIterator implements Iterator<RGLValue>{

			/* A stack as a buffer for the pushed values in this category. 
			 * 
			 * note that the java.util.Stack implementation doesn't reclaim memory when items are pop'ed!! 
			 * This is a shame. We would rather have such an implementation that does, as we are using memory twice    
			 * (in the stack, which doesn't shrink, and possibly in the cache). 
			 * 
			 * Also, not that we maintain this Stack in the iterator, not in the bag. The current implementation 
			 * doesn't support iterating multiple times, so the caching is not only nice for performance, 
			 * but it is actually necessary. 
			 */
			private Stack<RGLValue> stack = new Stack<RGLValue>();
			public void push(RGLValue val){
				stack.push(val);
			}
			
			boolean haveNext = false; 
			
			public CategoryBagIterator(){
				prepareForMultipleReadings(); // enable materialization, as we can only generate an iterator ONCE! 
				// (not this doesn't respect our optimizer, but it prevents bugs in the setup where we do stream everything, 
				// but do not cache anything 
			}
			
			@Override
			public RGLValue next() {
				if (! haveNext)
					throw new RuntimeException("there is no next() value, you should first call hasNext() on iterator");
				
				assert(haveNext && stack.size()>0);
				
				return stack.pop();
			}

			@Override
			public void remove() {
				assert(false);			
			}
			
			@Override
			public boolean hasNext(){
				setHaveNext();
				return haveNext;
			}
			
			/**
			 * Find the next value, configuring 'haveNext'
			 */
			private void setHaveNext(){
				if (stack.size()>0){
					haveNext = true;
					return;
				}
					
				while (inputBagIter.hasNext() ){
					RGLValue val = inputBagIter.next();
					String valCategory = categorizeValue(val);
					
					CategoryBag categoryBag = categoryBagMap.get(valCategory);
					if (categoryBag!=null){
						categoryBag.push(val);
						
						if (category.equals(valCategory)){
							/* ah, it was my OWN category! great, then I need not look any further */
							haveNext = true;
							return;
						}
					}
				}
				
				haveNext = false;
			}
			
			/* return the category string for the given value */
			private String categorizeValue(RGLValue value) {
				RGLValue category = categorizer.execute(new SingleElementValueRow(categorizerInputName, value));
				if (category.isNull())
					return null; // no category
				else {
					// TODO: research whether this should and/or does return the complete lexicalform, or only the string part
					return category.asLiteral().getValueString();
				}
			}
		}

	}
	
}




