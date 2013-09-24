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
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import nl.tudelft.rdfgears.engine.WorkflowLoader;
import nl.tudelft.rdfgears.rgl.exception.WorkflowLoadingException;
import nl.tudelft.rdfgears.rgl.workflow.Workflow;

public class WorkflowRequest {
	public enum OutputFormat {
	    RGL_XML, RDF_XML, SPARQL_RESULT 
	}

	
	protected HttpServletRequest request;
	protected HttpServletResponse response;
	protected Workflow workflow;
	private String workflowId; 
	
	public WorkflowRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException, WorkflowLoadingException {
		this.request = request;
		this.response = response;
		if (request==null || response==null){
			throw new IllegalArgumentException("Request and response must be non-null");
		}
		loadWorkflowFromRequest();
	}
	
	/**
	 * Load workflow from request. Either by the already generated Workflow attribute, or from the 
	 * requestURI or postData. 
	 * 
	 * @throws WorkflowLoadingException
	 */
	private void loadWorkflowFromRequest() throws WorkflowLoadingException {
		if (workflow==null){
			/* not available, fallback */
			String workflowId = (String) request.getAttribute("rdfgears.workflowId");
			if (workflowId==null){
				throw new RuntimeException("workflowId not available as attribute"); 	
			}
			
			workflow = WorkflowLoader.loadWorkflow(workflowId);
		}
	}
	
	
	public Workflow getWorkflow(){
		return workflow;
	}
	
}
