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

import java.util.Iterator;
import java.util.Map;

import com.hp.hpl.jena.rdf.model.Model;

import nl.tudelft.rdfgears.engine.ValueFactory;
import nl.tudelft.rdfgears.rgl.datamodel.type.BagType;
import nl.tudelft.rdfgears.rgl.datamodel.type.GraphType;
import nl.tudelft.rdfgears.rgl.datamodel.type.RGLType;
import nl.tudelft.rdfgears.rgl.datamodel.type.SuperTypePattern;
import nl.tudelft.rdfgears.rgl.datamodel.value.BagValue;
import nl.tudelft.rdfgears.rgl.datamodel.value.GraphValue;
import nl.tudelft.rdfgears.rgl.datamodel.value.RGLValue;
import nl.tudelft.rdfgears.rgl.datamodel.value.StreamingBagValue;
import nl.tudelft.rdfgears.rgl.datamodel.value.serialization.rglxml.ValueXMLSerializer;
import nl.tudelft.rdfgears.rgl.exception.FunctionTypingException;
import nl.tudelft.rdfgears.rgl.function.NNRCFunction;
import nl.tudelft.rdfgears.util.row.TypeRow;
import nl.tudelft.rdfgears.util.row.ValueRow;

/**
 * NNRC Flatten operation for bags. 
 * 
 * Requires that a bag contains bags of the same type, and generates a single bag containing the union of all inner bags.
 * 
 * If an inner bag IS an NON-value, it cannot be iterated and that that NON-value occurs once in the result bag. 
 * 
 * If an inner bag CONTAINS a non-value, those are iterated as normally and included in the result bag. 
 * 
 * @author Eric Feliksik
 *
 */
public class BagFlatten extends NNRCFunction {
	public static final String bag = "bag";
	
	private boolean mergesGraphs = false; // if true, we merge graphs.
	private boolean typechecked = false;

	@Override
	public void initialize(Map<String, String> config) {
		this.requireInput(bag);
	}
	
	@Override
	public RGLValue execute(ValueRow inputRow) {
		RGLValue bagVal = inputRow.get(bag);
		if (!typechecked ){
			throw new RuntimeException("BagFlatten needs typechecking first, to configure its polymorphism of merging bags/graphs");
		}
		if (bagVal.isNull()){
			return bagVal;
		}
		
		BagValue outerBag = bagVal.asBag();
		if (! mergesGraphs){
			return new FlattenedBagValue(outerBag);
		} else {
			Model newModel = ValueFactory.createModel();
			/* we must merge the graphs */
			for (RGLValue val : outerBag){
				if (val.isNull()){
					/* return null value, completely failing the flattening.
					 * Otherwise the error will not be noted and RDF queries may return no results without any error,
					 * driving the user crazy
					 */
					return val;  
				} else {
					/* ok, data */
					newModel.add(val.asGraph().getModel());
				}
				
			}
			
			GraphValue resultGraph = ValueFactory.createGraphValue(newModel);
			
			System.err.print("###################################\n");
			(new ValueXMLSerializer(System.err)).serialize(resultGraph);
			
			return resultGraph;
		}
	}
	
	
	
	@Override
	public RGLType getOutputType(TypeRow inputTypes) throws FunctionTypingException {
		RGLType bagOfBags = BagType.getInstance(BagType.getInstance(new SuperTypePattern()));
		RGLType bagOfGraphs = BagType.getInstance(GraphType.getInstance());
		
		typechecked = true;
		
		RGLType type = inputTypes.get(bag);
		if (type.isSubtypeOf(bagOfBags)){
			mergesGraphs = false;
			return ((BagType)type).getElemType();
		} else if (type.isSubtypeOf(bagOfGraphs)){
			/* we can merge graphs, too! */
			mergesGraphs = true;
			return GraphType.getInstance();
		} else {
			throw new FunctionTypingException(bag, bagOfBags, inputTypes.get(bag));
			
		}
		
	}

}

/**
 * A FlattenedBagValue is defined by a Bag of Bags. When iterating, it iterates over all the internal bags. 
 * So it does not materialize anything. 
 * 
 * If it is iterated multiple times, it may benefit from caching. 
 * @author Eric Feliksik
 *
 */
class FlattenedBagValue extends StreamingBagValue {
	BagValue outerBag;
	
	public FlattenedBagValue(BagValue bagOfBags){
		outerBag = bagOfBags;
	}
	
	@Override
	public Iterator<RGLValue> getStreamingBagIterator() {
		return new FlattenedBagIterator();
	}
	
	/**
	 * Dont really flatten the bags, just calculate what the result would look like. 
	 */
	@Override
	public int size() {
		int totalSize = 0;
		for (RGLValue innerBag : outerBag){
			totalSize += innerBag.asBag().size();
		}
		return totalSize;
	}
	
	class FlattenedBagIterator implements Iterator<RGLValue> {
		private Iterator<RGLValue> outerBagIter = outerBag.iterator();
		private Iterator<RGLValue> innerBagIter; 
		private boolean haveNext = false;
		
		FlattenedBagIterator(){
			if (! outerBagIter.hasNext()){
				haveNext = false;
			} else {
				setInnerBagIter(outerBagIter.next());
				setHaveNext();
			}
		}
		
		@Override
		public boolean hasNext() {
			return haveNext;
		}

		private void setHaveNext() {
			while (true){
				if (innerBagIter.hasNext()){
					haveNext = true;
					break;
				} else {
					if (outerBagIter.hasNext()){
						setInnerBagIter(outerBagIter.next());
					} else {
						haveNext = false; 
						break;
					}
				}	
			}
		}

		@Override
		public RGLValue next() {
			if (!haveNext) 
				throw new java.util.NoSuchElementException();
			
			assert(innerBagIter.hasNext());
			RGLValue res = innerBagIter.next();
			setHaveNext();
			return res;
		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException();
		}
		

		/**
		 * Set the inner bag iterator based on the given innerBag. 
		 * applies a  small hack : put in a singleton iterator with the error, so it seems that we are  
		 * gracefully iterating over the inner bag. 
		 * @param innerBag
		 */
		private void setInnerBagIter(RGLValue bagOrError) {
			if (bagOrError.isNull()){
				/* quick way to create single-error-Iterator */
				innerBagIter = ValueFactory.createBagSingleton(bagOrError).iterator();
			} else {
				innerBagIter = bagOrError.asBag().iterator();	
			}
			
		}
		
	}
	
	
}
