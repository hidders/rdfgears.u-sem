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

import nl.tudelft.rdfgears.rgl.datamodel.type.RGLType;
import nl.tudelft.rdfgears.rgl.datamodel.value.RGLValue;
import nl.tudelft.rdfgears.util.row.ValueRow;

/**
 * A WorkflowInput port offers values to the consuming processors in a workflow. 
 *  
 * @author Eric Feliksik
 *
 */
public class WorkflowInputPort extends WorkflowNode {
	
	private Workflow workflow;
	private String portname;

	/** 
	 * Create a workflow input port. The nodeId is used for diagnostic purposes and may be null
	 * @param workflow
	 * @param portname
	 * @param nodeId
	 */
	protected WorkflowInputPort(Workflow workflow, String portname, String nodeId){
		super(null);
		this.portname = portname;
		this.workflow = workflow;
	}

	protected WorkflowInputPort(Workflow workflow, String portname){
		this(workflow, portname, null);
	}
	
	@Override
	public RGLValue getResultValue() {
		ValueRow inputRow = workflow.getCurrentInputRow();
		assert(inputRow!=null) : "Workflow input row is not set. This is a bug. ";
		return inputRow.get(portname);
	}
	
	@Override
	public RGLType getOutputType() {
		assert(workflow.getInputTypeRow()!=null) : "Cannot getValueType() for workflowInputPort '"+portname+"'. It looks like it is not (yet) connected";
		RGLType type = workflow.getInputTypeRow().get(portname);
		
		if (type==null){ // FIXME ? 
			throw new RuntimeException("Typing Error: Workflow input port '"+portname+"' of workflow "+workflow+"is not configured");
		}
		return type;
	}

	@Override
	public void resetProcessorCache() {
		/* nothing to be done, the workflow has to reset it's own currentInputRow. */
	}

	
}
