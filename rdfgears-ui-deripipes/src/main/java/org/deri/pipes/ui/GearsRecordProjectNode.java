
package org.deri.pipes.ui;

import java.util.Collections;

import nl.tudelft.rdfgears.rgl.function.RGLFunction;
import nl.tudelft.rdfgears.rgl.function.core.RecordProject;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.zkoss.zul.Hbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Space;
import org.zkoss.zul.Textbox;


/**
 * @author Eric Feliksik
 *
 */
public class GearsRecordProjectNode extends NNRC_Node {
	private static final long serialVersionUID = 1L;
	private static final String DEFAULT_FIELD_NAME = "record"; // same as the input name configured in function implementation
	private Textbox projectFieldTextBox = new Textbox();
	
	public GearsRecordProjectNode(int x, int y){
		super(x,y);
		setOperationName("Record project");
		getInputListVbox().setEditableInputs(false);
		
		Hbox projectHbox = new Hbox();
		this.vbox.appendChild(projectHbox);
		projectHbox.appendChild(new Space());
		projectHbox.appendChild(new Label("field: "));
		projectHbox.appendChild(new Space());
		projectHbox.appendChild(projectFieldTextBox);
		projectFieldTextBox.setWidth("55px");
	}
	

	/**
	 * get standard height of this window
	 */
	protected int getBaseHeight() {
		return super.getBaseHeight()+20; // extra space for record project fieldname config
	}
	
	/** 
	 * we cannot do this in the constructor because the workspace wasn't known yet 
	 */
	public void initialize(){
		super.initialize();
		addInputName(DEFAULT_FIELD_NAME);
	}

	@Override
	public RGLFunction getRGLFunction(){
		RecordProject rp = new RecordProject();
		rp.initialize(Collections.singletonMap(RecordProject.CONFIGKEY_PROJECTFIELD, getProjectFieldName()));
		
		return rp;
	}

	public String getProjectFieldName(){
		return projectFieldTextBox.getText();
	}
	public void setProjectFieldName(String fieldName){
		projectFieldTextBox.setText(fieldName);
	}
	
	
	
	/* (non-Javadoc)
	 * @see org.deri.pipes.ui.AbstractGearsNode#getSrcCode(org.w3c.dom.Document, boolean)
	 */
	@Override
	public Element getSrcCode(Element workflowElement , boolean includePosition) {
		Document doc = workflowElement.getOwnerDocument();
			/* save the query as a function-config-parameter */
			Element functionElm = doc.createElement("function");
			functionElm.setAttribute("type", getTagName());
			
			Element configElem = doc.createElement("config");
			configElem.setAttribute("param", "projectField");
			configElem.setTextContent(getProjectFieldName());
			functionElm.appendChild(configElem);
			
			return createProcessorElement(functionElm, includePosition);
	}
	

	public static GearsRecordProjectNode loadConfig(Element elm, PipeEditor wsp){
		GearsRecordProjectNode node = new GearsRecordProjectNode(
				Integer.parseInt(elm.getAttribute("x")), Integer.parseInt(elm.getAttribute("y")));
		
		wsp.addFigure(node); // calls node.initialize(). But I must do addFigure before I can do other stuff
		
		node.setProjectFieldName(getXMLFunctionParameter(elm, "projectField"));
		
		node.configureIterationMarkers(elm);
		return node;
	}
	
	@Override
	public String getTagName() {
		return "record-project";
	}
	
}
