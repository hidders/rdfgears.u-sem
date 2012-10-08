package nl.rdfgears.tudelft.webservice.requests;

import java.io.IOException;
import java.io.OutputStream;
import java.text.ParseException;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import nl.tudelft.rdfgears.engine.Engine;
import nl.tudelft.rdfgears.engine.Optimizer;
import nl.tudelft.rdfgears.engine.WorkflowLoader;
import nl.tudelft.rdfgears.rgl.datamodel.type.RDFType;
import nl.tudelft.rdfgears.rgl.datamodel.type.RGLType;
import nl.tudelft.rdfgears.rgl.datamodel.value.RGLValue;
import nl.tudelft.rdfgears.rgl.datamodel.value.visitors.ImRealXMLSerializer;
import nl.tudelft.rdfgears.rgl.datamodel.value.visitors.ValueSerializer;
import nl.tudelft.rdfgears.rgl.exception.WorkflowCheckingException;
import nl.tudelft.rdfgears.rgl.exception.WorkflowLoadingException;
import nl.tudelft.rdfgears.rgl.workflow.Workflow;
import nl.tudelft.rdfgears.util.ValueParser;
import nl.tudelft.rdfgears.util.row.HashValueRow;
import nl.tudelft.rdfgears.util.row.TypeRow;

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
