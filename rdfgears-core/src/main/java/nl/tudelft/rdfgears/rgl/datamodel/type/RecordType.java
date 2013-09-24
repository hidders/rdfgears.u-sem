package nl.tudelft.rdfgears.rgl.datamodel.type;

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

import java.util.Set;

import nl.tudelft.rdfgears.util.row.TypeRow;


public class RecordType extends RGLType {
	private TypeRow row = null;
	private RecordType(TypeRow row){
		this.row = row;
	}
	/* FIXME
	 * Singleton to allow returning the same RecordType for the same row type, if we may want this later
	 * Now it's not an effective singleton... 
	 */
	public static synchronized RecordType getInstance(TypeRow row){
		return new RecordType(row);
	}
	
	/**
	 * fixme: maybe not return the row, but give accessor methods for the row?
	 * @return
	 */
	private TypeRow getTypeRow(){
		return row;
	}
	
	public Set<String> getRange(){
		return row.getRange();
	}
	
	public RGLType getFieldType(String String){
		return  getTypeRow().get(String);
	}
	
	public boolean equals(Object that){
		if (that instanceof RecordType){
			return this.getTypeRow().equals(((RecordType)that).getTypeRow());
		}
		return false;
	}
	
	@Override
	public boolean isType(RGLType type) {
		// TODO Auto-generated method stub
		return this.equals(type);
	}
	

	/**
	 * A Record is a supertype of an otherType if otherType is a RecordType, and 
	 * the TypeRow of this Record is a superType of the other records TypeRow. 
	 *   
	 */
//	@Override
//	public boolean isSupertypeOf(RGLType otherType) {
//		if(otherType instanceof RecordType){
//			return this.getTypeRow().isSupertypeOf(((RecordType) otherType).getTypeRow());
//		}
//		return false;
//	}
	@Override
	public boolean acceptsAsSubtype(RGLType otherType){
		return otherType.isSubtypeOf(this);
	}
	
	public boolean isSubtypeOf(RecordType type){
		return getTypeRow().isSubtypeOf(type.getTypeRow());
	}
	
	
	


	@Override
	public boolean isRecordType(){
		return true;
	}
	

	@Override
	public RecordType asRecordType(){
		return this;
	}
	

	
	public String toString(){
		// types are not SO deeply nested, so no need to recursively use the StringBuilder
		StringBuilder builder = new StringBuilder();
		builder.append("Record(< ");
		
		for (String field: getRange()){
			builder.append(field);
			builder.append(":");
			builder.append(row.get(field));
			builder.append(", ");
		}
		
		builder.append(" >)");
		return builder.toString();
	}
	
}
