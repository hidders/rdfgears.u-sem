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

import com.sleepycat.bind.tuple.TupleBinding;

import nl.tudelft.rdfgears.rgl.datamodel.value.RGLValue;
import nl.tudelft.rdfgears.rgl.workflow.FunctionProcessor;
import nl.tudelft.rdfgears.rgl.workflow.InputPort;
import nl.tudelft.rdfgears.rgl.workflow.LazyRGLValue;
import nl.tudelft.rdfgears.util.row.ValueRow;


/**
 * 
 * If a value in the InputRow is null and the associated port of the processor is marked for iteration,
 * iteration is impossible. Then the result of mapping (iteration) is null. 
 * 
 * However, determining the null-ness of the inputs requires evaluating them. This class postpones this 
 * evaluation until the result of the iterated mapping itself is evaluated. 
 *     
 *  
 * @author Eric Feliksik
 *
 */
public class LazyRGLValueMapping extends LazyRGLValue {
	FunctionProcessor proc; 
	public LazyRGLValueMapping(FunctionProcessor proc, ValueRow inputRow) {
		super(proc.getFunction(), inputRow);
		this.proc = proc;
	}

	
	/** 
	 * return actual MappingBagValue iff the iterating ports are non-null;
	 */
	protected RGLValue evaluate() {
		if (cachedResultValue==null){
			assert(inputRow!=null);
			
			RGLValue nullInput = getNullInput();
			if (nullInput!=null){
				/* there is a null input, so we cannot iterate */
				return nullInput; 
			}
			
			cachedResultValue  = new MappingBagValue(proc, inputRow);
			this.inputRow = null; /* allow garbage collection */
			
			if(valueIsReadMultipleTimes) // beware of any evaluating log messages above, which may result in setting this flag too late
				cachedResultValue.prepareForMultipleReadings();	
		}
		
		assert(cachedResultValue!=null);
		return cachedResultValue;
	}
	
	/**
	 * Return the first found RGL-NULL input, if it exists. If there is such a value, 
	 * the mapping result is NULL because we cannot iterate. 
	 * @return
	 */
	private RGLValue getNullInput(){
		for (InputPort port : proc.getPortSet()){
			if (port.iterates()){
				RGLValue inputVal = inputRow.get(port.getName());
				if (inputVal.isNull())
					return inputVal;
			}
		}
		return null;
	}
	
	

	@Override
	public TupleBinding<RGLValue> getBinding() {
		// TODO FIXME 
		assert(false) : "not implemented!";
		throw new RuntimeException("getBinding() not implemented!");
	}

	
	

}


