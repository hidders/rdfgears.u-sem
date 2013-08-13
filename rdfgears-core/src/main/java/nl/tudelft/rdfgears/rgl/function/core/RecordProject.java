package nl.tudelft.rdfgears.rgl.function.core;

import java.util.Map;

import nl.tudelft.rdfgears.rgl.datamodel.type.RGLType;
import nl.tudelft.rdfgears.rgl.datamodel.type.RecordType;
import nl.tudelft.rdfgears.rgl.datamodel.type.SuperTypePattern;
import nl.tudelft.rdfgears.rgl.datamodel.value.RGLValue;
import nl.tudelft.rdfgears.rgl.exception.FunctionTypingException;
import nl.tudelft.rdfgears.rgl.function.NNRCFunction;
import nl.tudelft.rdfgears.util.row.TypeRow;
import nl.tudelft.rdfgears.util.row.ValueRow;

public class RecordProject extends NNRCFunction {
	public static final String CONFIGKEY_PROJECTFIELD = "projectField";
	public static final String INPUT_NAME  = "record";  
	
	private String fieldName; // the field we project
	
	@Override
	public void initialize(Map<String, String> config) {
		this.requireInput(INPUT_NAME);
		this.fieldName = config.get(CONFIGKEY_PROJECTFIELD);
	}
	
	public RGLValue execute(ValueRow input) {
		RGLValue r = input.get(INPUT_NAME);
		
		if (r.isNull())
			return r; // propagate the error
		
		return r.asRecord().get(fieldName);
	}
	
	@Override
	public RGLType getOutputType(TypeRow inputTypeRow) throws FunctionTypingException {
		RGLType actualInputType = inputTypeRow.get(INPUT_NAME);
		
		TypeRow typeRow = new TypeRow();
		typeRow.put(fieldName, new SuperTypePattern()); /* any type will do */ 
		RGLType requiredInputType = RecordType.getInstance(typeRow);
		
		if (actualInputType.isSubtypeOf(requiredInputType)){
			RGLType fieldType = ((RecordType)actualInputType).getFieldType(fieldName);
			assert(fieldType!=null);
			return fieldType;
		} else {
			throw new FunctionTypingException(INPUT_NAME, requiredInputType, actualInputType);
		}
	}
}
