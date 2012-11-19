
package org.deri.pipes.ui;

import nl.tudelft.rdfgears.rgl.function.RGLFunction;
import nl.tudelft.rdfgears.rgl.function.core.RecordUnion;

import org.w3c.dom.Document;
import org.w3c.dom.Element;


/**
 * @author Eric Feliksik
 *
 */
public class GearsRecordUnionNode extends Gears_FixedInput_AbstractNNRCNode {
	
	public GearsRecordUnionNode(int x, int y){
		super(x,y);
	}
	
	/* (non-Javadoc)
	 * @see org.deri.pipes.ui.PipeNode#getTagName()
	 */
	@Override
	public String getTagName() {
		return "record-union";
	}
	
	
	public static GearsRecordUnionNode loadConfig(Element elm, PipeEditor wsp){
		GearsRecordUnionNode node = new GearsRecordUnionNode(
				Integer.parseInt(elm.getAttribute("x")), Integer.parseInt(elm.getAttribute("y")));
		
		wsp.addFigure(node); // calls node.initialize(). But I must do addFigure before I can do other stuff
		node.configureIterationMarkers(elm);
		return node;
	}

	/* (non-Javadoc)
	 * @see org.deri.pipes.ui.Gears_FixedInput_AbstractNNRCNode#getNRCFunctionInstance()
	 */
	@Override
	protected RGLFunction getNRCFunctionInstance() {
		return new RecordUnion();
	}

	/** 
	 * we cannot do this in the constructor because the workspace wasn't known yet 
	 */
	public void initialize(){
		super.initialize();
		setOperationName("Record Union");
	}
	

}
