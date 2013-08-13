
package org.deri.pipes.ui;

import org.deri.pipes.utils.XMLUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * @author Eric Feliksik
 *
 */
public class GearsRecordCreateNode extends NNRC_Node {
	private static final long serialVersionUID = 1L;
	private static final String DEFAULT_FIELD_NAME = "field_name";
	public GearsRecordCreateNode(int x, int y){
		super(x,y);
		setOperationName("Record create");
		getInputListVbox().setVariableInputs(true);
		getInputListVbox().setEditableInputs(true);
	}
	
	/** 
	 * we cannot do this in the constructor because the workspace wasn't known yet 
	 */
	public void initialize(){
		super.initialize();
		addInputName(DEFAULT_FIELD_NAME);
	}


	/**
	 * get standard height of this window
	 */
	protected int getBaseHeight() {
		return BASE_HEIGHT + 15; // higher because of add-button
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
		
		String bindVarStr = "";
		for (NamedInputIface namedInput : getInputListVbox().getNamedInputList()){
			String inputName = namedInput.getInputName();
			if (inputName.equals("") || inputName.contains(";"))
				throw new RuntimeException("A record fieldname must not be the empty string and must not contain the ';' character. ");
			bindVarStr += inputName + ";";
		}
		
		Element bindVarParam = doc.createElement("config");
		bindVarParam.setAttribute("param", "fields");
		bindVarParam.appendChild(doc.createTextNode(bindVarStr));
		functionElm.appendChild(bindVarParam);
		
		return createProcessorElement(functionElm, includePosition);
	}
	
	public static GearsRecordCreateNode loadConfig(Element elm, PipeEditor wsp){
		GearsRecordCreateNode node = new GearsRecordCreateNode(
				Integer.parseInt(elm.getAttribute("x")), Integer.parseInt(elm.getAttribute("y")));
		
		wsp.addFigure(node); // calls node.initialize(). But I must do addFigure before I can do other stuff
		
		node.getInputListVbox().removeAllInputs();
		for (Element inputPortElem : XMLUtil.getSubElementByName(elm, "inputPort")){
			String portName = inputPortElem.getAttribute("name");
			node.getInputListVbox().addInputName(portName);
		}
		
		node.configureIterationMarkers(elm);
		return node;
	}
	
	@Override
	public String getTagName() {
		return "record-create";
	}

}
