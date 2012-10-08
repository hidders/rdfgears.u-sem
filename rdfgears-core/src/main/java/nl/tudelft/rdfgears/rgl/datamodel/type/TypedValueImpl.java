package nl.tudelft.rdfgears.rgl.datamodel.type;

import nl.tudelft.rdfgears.rgl.datamodel.value.RGLValue;

public class TypedValueImpl implements TypedValue {
	RGLType type;
	RGLValue value;
	public TypedValueImpl(RGLType type, RGLValue value){
		this.type = type;
		this.value = value; 
	}
	public RGLType getType(){
		return type;
	}
	public RGLValue getValue(){
		return value;
	}
}
