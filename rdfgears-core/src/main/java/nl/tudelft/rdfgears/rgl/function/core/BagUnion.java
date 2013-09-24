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
import java.util.NoSuchElementException;

import com.hp.hpl.jena.rdf.model.Model;
import com.sleepycat.bind.tuple.TupleBinding;

import nl.tudelft.rdfgears.engine.ValueFactory;
import nl.tudelft.rdfgears.engine.bindings.UnionBagBinding;
import nl.tudelft.rdfgears.rgl.datamodel.type.BagType;
import nl.tudelft.rdfgears.rgl.datamodel.type.RGLType;
import nl.tudelft.rdfgears.rgl.datamodel.type.SuperTypePattern;
import nl.tudelft.rdfgears.rgl.datamodel.value.BagValue;
import nl.tudelft.rdfgears.rgl.datamodel.value.RGLValue;
import nl.tudelft.rdfgears.rgl.datamodel.value.StreamingBagValue;
import nl.tudelft.rdfgears.rgl.exception.FunctionTypingException;
import nl.tudelft.rdfgears.rgl.function.NNRCFunction;
import nl.tudelft.rdfgears.util.row.TypeRow;
import nl.tudelft.rdfgears.util.row.ValueRow;

/**
 * NNRC Union operation for bags
 * 
 * Creates the union of two input bags 'bag1' and 'bag2'
 * 
 * @author Eric Feliksik
 * 
 */
public class BagUnion extends NNRCFunction {
	public static final String bag1 = "bag1";
	public static final String bag2 = "bag2";

	private boolean mergesGraphs = false; // if true, we merge graphs.

	@Override
	public void initialize(Map<String, String> config) {
		this.requireInput(bag1);
		this.requireInput(bag2);
	}

	@Override
	public RGLValue execute(ValueRow inputRow) {
		RGLValue bag1val = inputRow.get(bag1);
		RGLValue bag2val = inputRow.get(bag2);
		
		if (bag1val.isNull()){
			return bag1val; // return the absent value
		}
		if (bag2val.isNull()){
			return bag2val; // return the absent value
		}

		if (mergesGraphs) {
			Model m = ValueFactory.createModel();
			m.add(bag1val.asGraph().getModel());
			m.add(bag2val.asGraph().getModel());
			
			return ValueFactory.createGraphValue(m);
		} else {
			assert(bag1val.isBag()) : "typechecking problem! I was promised to receive bags only";
			assert(bag2val.isBag()) : "typechecking problem! I was promised to receive bags only";
			return new UnionBagValue(bag1val.asBag(), bag2val.asBag());

		}

	}

	@Override
	public RGLType getOutputType(TypeRow inputTypes)
			throws FunctionTypingException {
		RGLType type1 = inputTypes.get(bag1);
		RGLType type2 = inputTypes.get(bag2);

		boolean bothBags = type1.isBagType() && type2.isBagType();
		boolean bothGraphs = type1.isGraphType() && type2.isGraphType();

		if (bothBags) {
			if (type1.isSubtypeOf(type2))
				return type2; // use the supertype
			
			if (type2.isSubtypeOf(type1))
				return type1; // use the supertype
			
			// they are not equal, and no subtype. Thus they are incompatible. 
			
			throw new FunctionTypingException("bag2", type1, type2);
			
//			RGLType elem1 = ((BagType) type1).getElemType();
//			RGLType elem2 = ((BagType) type2).getElemType();
//
//			if (!elem1.equals(elem2)) {
//				throw new FunctionTypingException("bag2", type1, type2);
//			}
//			return type1; /* inputs ok, thus type1==type2==outputtype */
		} else if (bothGraphs) {
			mergesGraphs = true;
			return type1;
		} else {
			if (!(type1.isBagType())) {
				throw new FunctionTypingException(bag1, BagType
						.getInstance(new SuperTypePattern()), type1);
			}
			throw new FunctionTypingException(bag2, BagType
					.getInstance(new SuperTypePattern()), type1);

		}

	}

	public static UnionBagValue createUnionBag(long id, BagValue bag1, BagValue bag2) {
		return new UnionBagValue(id, bag1, bag2);
	}
}

/**
 * A FlattenedBagValue is defined by a Bag of Bags. When iterating, it iterates
 * over all the internal bags. So it does not materialize anything, and iterates
 * the united bags every time this bag is iterated.
 * 
 * @author Eric Feliksik
 * 
 */
class UnionBagValue extends StreamingBagValue {
	BagValue bag1, bag2;

	public UnionBagValue(BagValue bag1, BagValue bag2) {
		this.bag1 = bag1;
		this.bag2 = bag2;
	}

	public UnionBagValue(long id, BagValue bag1, BagValue bag2) {
		this(bag1, bag2);
		this.myId = id;
	}

	@Override
	public Iterator<RGLValue> getStreamingBagIterator() {
		return new UnionBagIterator();
	}

	/**
	 * Dont really flatten the bags, just calculate what the result would look
	 * like.
	 */
	@Override
	public int size() {
		return bag1.size() + bag2.size(); // may be costly evaluation, do we
											// want to cache this?
	}

	class UnionBagIterator implements Iterator<RGLValue> {
		Iterator<RGLValue> currentIter = bag1.iterator();
		boolean iteratingBag2 = false; // if true, currentIter is iterator over
										// bag2
		boolean haveNext = true;

		public UnionBagIterator() {
			setHaveNext();
		}

		@Override
		public RGLValue next() {
			if (!haveNext)
				throw new NoSuchElementException();
			RGLValue res = currentIter.next();
			setHaveNext();
			return res;
		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean hasNext() {
			return haveNext;
		}

		private void setHaveNext() {
			haveNext = currentIter.hasNext();
			if (!haveNext && !iteratingBag2) {
				iteratingBag2 = true;
				currentIter = bag2.iterator();
				haveNext = currentIter.hasNext();
			}
		}
	}

	@Override
	public TupleBinding<RGLValue> getBinding() {
		return new UnionBagBinding(bag1, bag2);
	}
}
