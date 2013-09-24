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

import nl.tudelft.rdfgears.rgl.datamodel.type.RGLType;
import nl.tudelft.rdfgears.rgl.datamodel.type.RecordType;
import nl.tudelft.rdfgears.rgl.datamodel.type.SuperTypePattern;
import nl.tudelft.rdfgears.rgl.datamodel.value.RGLValue;
import nl.tudelft.rdfgears.rgl.datamodel.value.RecordValue;
import nl.tudelft.rdfgears.rgl.datamodel.value.impl.MemoryRecordValue;
import nl.tudelft.rdfgears.rgl.exception.FunctionTypingException;
import nl.tudelft.rdfgears.rgl.function.NNRCFunction;
import nl.tudelft.rdfgears.util.row.FieldIndexMap;
import nl.tudelft.rdfgears.util.row.FieldIndexMapFactory;
import nl.tudelft.rdfgears.util.row.FieldMappedValueRow;
import nl.tudelft.rdfgears.util.row.TypeRow;
import nl.tudelft.rdfgears.util.row.ValueRow;

/**
 * The NNRC project function project_A takes a Record r1 for which r1.A is defined, and returns a record
 * r2 that only has r2.A defined as r1.A==r2.A
 * 
 * @author Eric Feliksik
 *
 */
public class RecordReduce extends NNRCFunction {
	private String projectFieldName;
	public static final String record1key = "r1"; // a record
	
	FieldIndexMap fiMap;
		
	public RecordReduce(String fieldName){
		this.requireInput(record1key);
		this.projectFieldName = fieldName;
	}
	
	public RGLValue execute(ValueRow input) {
		RGLValue record1expr = input.get(record1key);
		RecordValue recordIn = record1expr.asRecord();
		
//		LazyValueRow rowOut = new LazyValueRow();
//		rowOut.setExpression(projectFieldName, recordIn.getEntry(projectFieldName));
//		return new MemoryRecordValue(rowOut);
		
		if (fiMap==null){
			fiMap = FieldIndexMapFactory.create(projectFieldName);
		}
		FieldMappedValueRow exprRow = new FieldMappedValueRow(fiMap);
		exprRow.put(projectFieldName, recordIn.get(projectFieldName));
		return new MemoryRecordValue(exprRow);
	}

	@Override
	public RGLType getOutputType(TypeRow inputTypeRow) throws FunctionTypingException {
		RGLType inputType = inputTypeRow.get(record1key);
		if (inputType instanceof RecordType){
			RGLType projectType = ((RecordType) inputType).getFieldType(projectFieldName);
			
			TypeRow row = new TypeRow();
			row.put(projectFieldName, projectType);
			if (projectType!=null){
				return RecordType.getInstance(row);
			}
		}
		
		/* we wanted a record with field projectFieldName of SOME type, but it wasn't there.
		 * Throw exception */
		TypeRow expectedTypeRow = new TypeRow();
		expectedTypeRow.put(projectFieldName, new SuperTypePattern()); 
		RecordType expectedType = RecordType.getInstance(expectedTypeRow);
		throw new FunctionTypingException("Need some record with a field "+projectFieldName+" in port '"+record1key+"'");
	}

	@Override
	public void initialize(Map<String, String> config) {
		// TODO Auto-generated method stub
		
	}


}
