package nl.tudelft.rdfgears.rgl.function.core;

/*
 * #%L
 * RDFGears
 * %%
 * Copyright (C) 2013 WIS group at the TU Delft (http://www.wis.ewi.tudelft.nl/)
 * %%
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 * #L%
 */

import java.util.Map;

import nl.tudelft.rdfgears.engine.ValueFactory;
import nl.tudelft.rdfgears.rgl.datamodel.type.RGLType;
import nl.tudelft.rdfgears.rgl.datamodel.type.RecordType;
import nl.tudelft.rdfgears.rgl.datamodel.value.RGLValue;
import nl.tudelft.rdfgears.rgl.datamodel.value.impl.ModifiableRecord;
import nl.tudelft.rdfgears.rgl.exception.FunctionTypingException;
import nl.tudelft.rdfgears.rgl.function.NNRCFunction;
import nl.tudelft.rdfgears.util.row.TypeRow;
import nl.tudelft.rdfgears.util.row.ValueRow;

/** 
 * Create a Record that contains a number of fields. The fields are defined in the initialize() function.
 *   
 * @param fieldNames the names of the ports (and record fields).
 */
public class RecordCreate extends NNRCFunction {
	
	/**
	 * The config map should contain the key "fields", and value should be a ';'-separated list of fieldnames.
	 * Example: 
	 * config.get("fields") => "field1;field2;anotherField" 
	 */
	@Override
	public void initialize(Map<String, String> config) {
		
		/* configure the required inputs based on the ';' separated list of field names */
		String fieldsStr = config.get("fields");
		String[] split = fieldsStr.split(";");
		for (int i=0; i<split.length; i++){
			if (split[i].length()>0)
				requireInput(split[i]);
		}
	}
	

	public RGLValue execute(ValueRow input) {
		/* the ValueRow we want to create a record from, happens to have the same 
		 * set of keys as our input. So we can use our own FieldIndexMap
		 */
		ModifiableRecord rec = ValueFactory.createModifiableRecordValue(this.getFieldIndexMap());
		for (String fieldName : getRequiredInputNames()){
			rec.put(fieldName, input.get(fieldName));
		}
		
		return rec;
	}
	
	@Override
	public RGLType getOutputType(TypeRow inputTypeRow) throws FunctionTypingException {
		TypeRow typerow = new TypeRow();
		
		for (String fieldName : getRequiredInputNames()){
			RGLType type = inputTypeRow.get(fieldName); 
			if (type==null){
				throw new FunctionTypingException("Expected some input on port "+fieldName+". ");
			}
			typerow.put(fieldName, type);
		}
		return RecordType.getInstance(typerow);
	}
}
