
package org.deri.pipes.ui;

import nl.tudelft.rdfgears.rgl.function.RGLFunction;
import nl.tudelft.rdfgears.rgl.function.core.RecordUnion;
import nl.tudelft.rdfgears.rgl.exception.WorkflowLoadingException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;


/**
 * @author Eric Feliksik
 *
 */
public abstract class Gears_FixedInput_AbstractNNRCNode extends NNRC_Node {
	
	public Gears_FixedInput_AbstractNNRCNode(int x, int y){
		super(x,y);
		
	}

	/**
	 * we cannot do this in the constructor because the workspace wasn't known
	 * yet
	 */
	public void initialize(){
		super.initialize();
		setOperationName("AbstractNRC");
		setRGLFunction(getNRCFunctionInstance());
		try{
			getRGLFunction().initialize(null);
		}catch(WorkflowLoadingException e){
			throw new RuntimeException("will not happen"); 		
		}
		addRequiredInputs();
	}

	protected abstract RGLFunction getNRCFunctionInstance();
	
	/* (non-Javadoc)
	 * @see org.deri.pipes.ui.AbstractGearsNode#getSrcCode(org.w3c.dom.Document, boolean)
	 */
	@Override
	public Element getSrcCode(Element workflowElement , boolean includePosition) {
		Document doc = workflowElement.getOwnerDocument();
			/* save the query as a function-config-parameter */
			Element functionElm = doc.createElement("function");
			functionElm.setAttribute("type", getTagName());
			return createProcessorElement(functionElm, includePosition);
	}

	/**
	 * get the NRC function type name, e.g. "nrc-record-union"
	 */
	@Override
	public abstract String getTagName();
	
	

}
