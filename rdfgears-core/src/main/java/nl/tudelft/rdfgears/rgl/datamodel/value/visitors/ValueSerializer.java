package nl.tudelft.rdfgears.rgl.datamodel.value.visitors;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;

import nl.tudelft.rdfgears.rgl.datamodel.value.RGLValue;
import nl.tudelft.rdfgears.util.BufferedIndentedWriter;


/**
 * A visitor that serializes an RGL Value, writing it to an Output Stream. 
 * 
 * @author Eric Feliksik
 *
 */
public abstract class ValueSerializer implements RGLValueVisitor {
	
	public abstract void serialize(RGLValue value);
	


}
