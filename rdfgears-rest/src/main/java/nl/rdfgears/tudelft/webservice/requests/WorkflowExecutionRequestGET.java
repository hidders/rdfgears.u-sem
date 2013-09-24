package nl.rdfgears.tudelft.webservice.requests;

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

import java.io.IOException;
import java.io.PrintStream;
import java.text.ParseException;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import nl.tudelft.rdfgears.engine.Engine;
import nl.tudelft.rdfgears.rgl.datamodel.type.RDFType;
import nl.tudelft.rdfgears.rgl.datamodel.type.RGLType;
import nl.tudelft.rdfgears.rgl.datamodel.type.TypedValue;
import nl.tudelft.rdfgears.rgl.datamodel.type.TypedValueImpl;
import nl.tudelft.rdfgears.rgl.datamodel.value.RGLValue;
import nl.tudelft.rdfgears.rgl.datamodel.value.visitors.ValueSerializer;
import nl.tudelft.rdfgears.rgl.exception.WorkflowLoadingException;
import nl.tudelft.rdfgears.util.ValueParser;
import nl.tudelft.rdfgears.util.row.HashValueRow;
import nl.tudelft.rdfgears.util.row.TypeRow;

public class WorkflowExecutionRequestGET extends WorkflowExecutionRequest {

	public WorkflowExecutionRequestGET(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException,	WorkflowLoadingException {
		super(request, response);
		
	}

	public void configureWorkflowInputs(){
		/* check whether all inputs are provided */
		valueRow = new HashValueRow();
		typeRow = new TypeRow();
		for (String inputName : getWorkflow().getRequiredInputNames()){
			String strVal = request.getParameter(inputName);
			
			if (strVal==null){
				throw new IllegalArgumentException("The GET parameter '"+inputName+"' needs a value");
			}
    		try {
    			TypedValue typedValue = getValue(strVal);
    			valueRow.put(inputName, typedValue.getValue());
    			typeRow.put(inputName, typedValue.getType()); 
			} catch (Exception e){
				e.printStackTrace();
    			throw new IllegalArgumentException("Cannot use input value '"+strVal+"' (given as for input '"+inputName+"'): "+e.getMessage());
    		}
		}
	}
	
	/** get a value and its type from an encoding. Encoding can be direct NTriple-like encoding of 
	 * an RDFValue, but it can also refer to a complex type that is stored somehow.
	 * 
	 * @throws ParseException 
	 * @throws IOException 
	 */
	private TypedValue getValue(String valueEncoding) throws ParseException, IOException{
		if (valueEncoding.startsWith("_")){
			// is reference to stored value
			String valueId = valueEncoding.substring(1);
			
			TypedValue tv = Engine.getSimpleValueStore().getValue(valueId);
			
			if (tv==null)
				throw new RuntimeException("The value '"+valueId+"' is not stored");
			
			return tv; 
			
		} else {
			// is encoded directly
			RGLValue val = ValueParser.parseNTripleValue(valueEncoding);
			// all directly encoded values are RDF values and typechecking doesn't distinguish Literal/URI
			RGLType type = RDFType.getInstance();  
			return new TypedValueImpl(type, val);	
		}
		
	}
	
	public void execute()  {
		response.setContentType("application/xml;charset=utf-8");
		response.setStatus(HttpServletResponse.SC_OK);
		try {
			response.flushBuffer();
		} catch (IOException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		try {
			ServletOutputStream out = response.getOutputStream();
			try {
				TypedValueImpl typedValue = new TypedValueImpl(getResultType(), getResult());
				if (request.getParameter("-store")!=null){
					
					String newId;
					
					newId = Engine.getSimpleValueStore().putValue(typedValue, null);
					response.setContentType("application/xml;charset=utf-8");
					response.setStatus(HttpServletResponse.SC_OK);
					response.getOutputStream().print("<msg>Your result was stored under name "+newId+". </msg>");
					return;
				} else {
					ValueSerializer serializer = getSerializer(returnType, response.getOutputStream());
					serializer.serialize(typedValue.getValue());	
				}
			} catch (IOException e) {
				out.print(e.getMessage());
				e.printStackTrace(new PrintStream(out));
			}
			
			
		} catch (IOException e1) {
			// Cannot get output stream, so we cannot even inform the user.
			response.addHeader("Status", "cannot write to response output stream"); 
			e1.printStackTrace();
		}
		
	}
	
}
