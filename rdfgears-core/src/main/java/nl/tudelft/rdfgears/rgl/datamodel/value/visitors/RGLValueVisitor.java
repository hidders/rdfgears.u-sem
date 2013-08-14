package nl.tudelft.rdfgears.rgl.datamodel.value.visitors;

import nl.tudelft.rdfgears.rgl.datamodel.value.BagValue;
import nl.tudelft.rdfgears.rgl.datamodel.value.BooleanValue;
import nl.tudelft.rdfgears.rgl.datamodel.value.GraphValue;
import nl.tudelft.rdfgears.rgl.datamodel.value.LiteralValue;
import nl.tudelft.rdfgears.rgl.datamodel.value.RGLNull;
import nl.tudelft.rdfgears.rgl.datamodel.value.RecordValue;
import nl.tudelft.rdfgears.rgl.datamodel.value.URIValue;
import nl.tudelft.rdfgears.rgl.workflow.LazyRGLValue;

public interface RGLValueVisitor {

	
    void visit(BagValue bag);
    void visit(GraphValue graph);
    void visit(BooleanValue bool);
    void visit(LiteralValue literal);
    void visit(RecordValue record);
    void visit(URIValue uri);
    void visit(LazyRGLValue lazyValue);
	void visit(RGLNull rglError);
    
}
