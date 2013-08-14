package nl.tudelft.rdfgears.rgl.datamodel.value.serialization.rglxml;

import nl.tudelft.rdfgears.rgl.datamodel.type.RGLType;
import nl.tudelft.rdfgears.rgl.datamodel.type.TypedValue;
import nl.tudelft.rdfgears.rgl.datamodel.value.RGLValue;

/**
 * This class is an implementation of the TypedValue. It is assumed that any constructor for a 
 * subclass is built as follows: 
 * - constructor accepts an InputStream (or some other object that provides input, such as an 
 * 		XMLStreamReader)
 * - constructor (calls some functions that) parses the input and internally sets the parsedValue 
 * 		and parsedType fields using the setParsedValue() and setParsedType() fields.   
 * - constructor should throw an exception if parsing fails. 
 * 
 * The value and type can be fetched after constructing the instance, using the functions defined 
 * by the TypedValue interface. 
 * 
 * @author Eric Feliksik
 *
 */
public class AbstractRGLValueParser implements TypedValue {
	private RGLValue parsedValue;
	private RGLType parsedType;
	

	/**
	 * Get the value that was parsed. 
	 */
	@Override
	public RGLValue getValue() {
		return parsedValue;
	}
	/**
	 * Get the type of the value that was parsed. 
	 */
	@Override
	public RGLType getType() {
		return parsedType;
	}
	
	
	protected void setParsedValue(RGLValue value) {
		this.parsedValue = value;
	}
	
	protected void setParsedType(RGLType type) {
		this.parsedType = type;
	}


}
