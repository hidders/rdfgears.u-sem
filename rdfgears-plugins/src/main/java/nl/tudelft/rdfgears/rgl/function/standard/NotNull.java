package nl.tudelft.rdfgears.rgl.function.standard;

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
        
import java.util.Map;

import nl.tudelft.rdfgears.engine.ValueFactory;
import nl.tudelft.rdfgears.rgl.datamodel.type.BooleanType;
import nl.tudelft.rdfgears.rgl.datamodel.type.RGLType;
import nl.tudelft.rdfgears.rgl.datamodel.value.RGLValue;
import nl.tudelft.rdfgears.rgl.exception.WorkflowCheckingException;
import nl.tudelft.rdfgears.rgl.function.AtomicRGLFunction;
import nl.tudelft.rdfgears.util.row.TypeRow;
import nl.tudelft.rdfgears.util.row.ValueRow;

/**
 * A simple function to fetch Linked Data from the web.  
 * @author Eric Feliksik
 *
 */
public class NotNull extends AtomicRGLFunction  {
	public static String port = "value";
	
	public NotNull(){
		this.requireInput(port); 
	}

	public RGLType getOutputType() {
		return BooleanType.getInstance();
	}
	
	@Override
	public void initialize(Map<String, String> config) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public RGLValue execute(ValueRow inputRow) {
		if (inputRow.get(port).isNull())
			return ValueFactory.createFalse(); 
		else 
			return ValueFactory.createTrue(); 

	}

	@Override
	public RGLType getOutputType(TypeRow inputTypes) throws WorkflowCheckingException {
		// any type is ok. However, this class does not extend SimplyTypedRGLFunction because that would 
		// also mean that NULL value inputs would be automatically propagated, which is not what we want. 
		return BooleanType.getInstance(); 
	}

}
