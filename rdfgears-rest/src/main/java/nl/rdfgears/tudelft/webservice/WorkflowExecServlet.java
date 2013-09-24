package nl.rdfgears.tudelft.webservice;

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
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


import nl.rdfgears.tudelft.webservice.requests.WorkflowExecutionRequest;
import nl.rdfgears.tudelft.webservice.requests.WorkflowExecutionRequestGET;
import nl.tudelft.rdfgears.rgl.exception.FunctionTypingException;
import nl.tudelft.rdfgears.rgl.exception.WorkflowCheckingException;
import nl.tudelft.rdfgears.rgl.exception.WorkflowLoadingException;

/**
 * Servlet implementation class WorkflowExecServlet
 */
public class WorkflowExecServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private ServletOutputStream output;
     
    /**
     * @see HttpServlet#HttpServlet()
     */
    public WorkflowExecServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		if (request.getAttribute("javax.servlet.forward.servlet_path")==null){
			throw new RuntimeException("Don't call this path directly; call the dispatcher. ");
		}

		output = response.getOutputStream();
		
//		String workflowId = WorkflowTools.getWorkflowId(request);
		
		WorkflowExecutionRequest execRequest ;
		try {
			execRequest = new WorkflowExecutionRequestGET(request, response);
			execRequest.configureWorkflowInputs();
			execRequest.typeCheck();
			execRequest.execute();
							
		} catch (WorkflowLoadingException e) {
			output.print("Error: ");
			output.print(e.getMessage());
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			output.print("Error: ");
			output.print(e.getMessage());
			e.printStackTrace();
		} catch (WorkflowCheckingException e) {
			output.print("Error: ");
			
			/** Code duplication from RDFGears.java command line tool */ 
			
			output.println("The workflow is not executable, as it did not pass the typechecking test: ");
		
			WorkflowCheckingException rootCause = e.getRootCause();
			if (rootCause instanceof FunctionTypingException) {
				FunctionTypingException fEx = (FunctionTypingException) rootCause;
				if (fEx.isIterationProblem())
					output.println("I think you forgot an iteration marker somewhere! ");
			}
			output.println(e.getProblemDescription());
			
		}
		
		
		
		
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}
	

}
