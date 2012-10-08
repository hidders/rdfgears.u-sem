
package org.deri.pipes.ui;

import nl.tudelft.rdfgears.rgl.function.RGLFunction;
import nl.tudelft.rdfgears.rgl.function.core.BagSingleton;
import nl.tudelft.rdfgears.rgl.function.core.RecordUnion;

import org.w3c.dom.Document;
import org.w3c.dom.Element;


/**
 * @author Eric Feliksik
 *
 */
public class GearsBagSingletonNode extends Gears_FixedInput_AbstractNNRCNode {
	
	public GearsBagSingletonNode(int x, int y){
		super(x,y);
	}
	
	/* (non-Javadoc)
	 * @see org.deri.pipes.ui.PipeNode#getTagName()
	 */
	@Override
	public String getTagName() {
		return "bag-singleton";
	}
	
	
	public static GearsBagSingletonNode loadConfig(Element elm, PipeEditor wsp){
		GearsBagSingletonNode node = new GearsBagSingletonNode(
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
		return new BagSingleton();
	}

	/** 
	 * we cannot do this in the constructor because the workspace wasn't known yet 
	 */
	public void initialize(){
		super.initialize();
		setOperationName("Bag Singleton");
	}
	

}
