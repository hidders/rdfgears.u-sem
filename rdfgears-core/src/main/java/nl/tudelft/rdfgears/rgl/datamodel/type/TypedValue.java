package nl.tudelft.rdfgears.rgl.datamodel.type;

import nl.tudelft.rdfgears.rgl.datamodel.value.RGLValue;

public interface TypedValue {

	public RGLType getType();
	public RGLValue getValue();
}
