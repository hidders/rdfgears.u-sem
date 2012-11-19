
package org.deri.pipes.ui;

import nl.tudelft.rdfgears.rgl.function.RGLFunction;
import nl.tudelft.rdfgears.rgl.function.core.BagGroup;
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
public class GearsGroupByNode extends NNRC_Node {
	private static final long serialVersionUID = 1L;
	private static final String INPUT_NAME = BagGroup.INPUT_PORT_NAME; // same as the input name configured in function implementation
	private Textbox groupFieldTextBox = new Textbox();
	
	public GearsGroupByNode(int x, int y){
		super(x,y);
		setOperationName("Group");
		getInputListVbox().setEditableInputs(false);
		
		Hbox projectHbox = new Hbox();
		this.vbox.appendChild(projectHbox);
		projectHbox.appendChild(new Space());
		projectHbox.appendChild(new Label("group by: "));
		projectHbox.appendChild(new Space());
		projectHbox.appendChild(groupFieldTextBox);
		groupFieldTextBox.setWidth("55px");
	}
	

	/**
	 * get standard height of this window
	 */
	@Override
	protected int getBaseHeight() {
		return super.getBaseHeight()+20; // extra space for config-field
	}

	/**
	 * get standard height of this window
	 */
	@Override
	protected int getBaseWidth() {
		return super.getBaseWidth()+30; // config field+label is a bit wide
	}
	
	/** 
	 * we cannot do this in the constructor because the workspace wasn't known yet 
	 */
	public void initialize(){
		super.initialize();
		addInputName(INPUT_NAME);
	}

	@Override
	public RGLFunction getRGLFunction(){
		return new BagGroup();
	}

	public String getProjectFieldName(){
		return groupFieldTextBox.getText();
	}
	public void setProjectFieldName(String fieldName){
		groupFieldTextBox.setText(fieldName);
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
			configElem.setAttribute("param", BagGroup.CONFIG_GROUP_BY_FIELD);
			configElem.setTextContent(getProjectFieldName());
			functionElm.appendChild(configElem);
			
			return createProcessorElement(functionElm, includePosition);
	}
	

	public static GearsGroupByNode loadConfig(Element elm, PipeEditor wsp){
		GearsGroupByNode node = new GearsGroupByNode(
				Integer.parseInt(elm.getAttribute("x")), Integer.parseInt(elm.getAttribute("y")));
		
		wsp.addFigure(node); // calls node.initialize(). But I must do addFigure before I can do other stuff
		
		node.setProjectFieldName(getXMLFunctionParameter(elm, BagGroup.CONFIG_GROUP_BY_FIELD));		
		node.configureIterationMarkers(elm);
		return node;
	}
	
	@Override
	public String getTagName() {
		return "bag-groupby";
	}
	
}
