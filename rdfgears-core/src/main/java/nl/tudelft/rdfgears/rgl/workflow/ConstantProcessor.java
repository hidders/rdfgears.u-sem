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

import nl.tudelft.rdfgears.rgl.datamodel.type.BooleanType;
import nl.tudelft.rdfgears.rgl.datamodel.type.GraphType;
import nl.tudelft.rdfgears.rgl.datamodel.type.RDFType;
import nl.tudelft.rdfgears.rgl.datamodel.type.RGLType;
import nl.tudelft.rdfgears.rgl.datamodel.type.SubType;
import nl.tudelft.rdfgears.rgl.datamodel.value.RGLValue;

/**
 * ConstantProcessor, a Workflow node producing constants.
 * 
 * Can't we dump this class in favor a generic processor that uses a function without 
 * inputs, which is initialized with the value string??? I think so...
 *  
 * No iteration and other things, as it has no inputs. 
 * 
 * It is supposed to only contain RDFValues, Graphs, Booleans or Null.
 *  
 * Not bags/records. 
 *  
 * @author Eric Feliksik
 *
 */
public class ConstantProcessor extends ProcessorNode {
	
	RGLValue value;

	/**
	 * Constructor. A ConstantProcessor takes some Constant value on instantiation. 
	 * @param function
	 */
	public ConstantProcessor(RGLValue value, String id){
		super(id);
		this.value = value;
	}
	
	public ConstantProcessor(RGLValue value){
		this(value, null);
	}
	
	
	/*****************************************************************************
	 * 
	 * AbstractProcessor implementation
	 * 
	 *****************************************************************************/
	
	/** 
	 * Execute the processor's function on the given input row over values, 
	 * and cache the result.   
	 * @param inputs
	 * @return the result value of the execution.
	 */
	@Override
	public RGLValue getResultValue(){
		return value;
	}
	@Override
	public RGLType getOutputType() {
		assert(value!=null);
		if (value.isRDFValue())
			return RDFType.getInstance();
		else if (value.isGraph())
			return GraphType.getInstance();
		else if (value.isBoolean())
			return BooleanType.getInstance();
		else if (value.isNull())
			return new SubType();
		
		assert(false) : "shouldn't have this value in this class "+value.getClass().getCanonicalName();
		return null;
	}

	@Override
	public void resetProcessorCache() {
		/* nothing to do, we are not caching as we're constant. And we have no inputs to reset. */
	}


	

}
