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

/**
 * The universal subtype. Used as a placeholder type when the actual type is not known, 
 * but should never yield typing problems -- that is, when the specified value is null.
 * 
 * The task of this type is to claim that is the subtype of any other type. 
 * 
 * @author Eric Feliksik
 *
 */
public class SubType extends RGLType {
	
	private static final SubType singleton = new SubType();
	
	public boolean isType(RGLType type){
		return this.equals(type);
	}
	
//	/**
//	 * The SuperType class is supertype of every other type that is not a SuperType, i.e. 
//	 * of every concrete type
//	 */
//	@Override
//	public boolean isSupertypeOf(RGLType otherType) {
//		return false;
//	}
	


	@Override
	public boolean acceptsAsSubtype(RGLType otherType){
		return otherType.isSubtypeOf(this);
	}
	
	public boolean isSubtypeOf(BagType type){
		return true;
	}
	
	public boolean isSubtypeOf(RecordType type){
		return true;
	}
	
	public boolean isSubtypeOf(GraphType type){
		return true;
	}
	
	public boolean isSubtypeOf(RDFType type){
		return true;
	}
	
	public boolean isSubtypeOf(BooleanType type){
		return true;
	}

	public boolean isSubtypeOf(SubType type){
		return true;
	}
	
	public boolean isSubtypeOf(RGLType type){
		/* we don't know how to handle this, let it be dispatched */
		return type.acceptsAsSubtype(this);
	}
	
	
	public String toString(){ // types are not SO deeply nested, so no need to write to pass a stringbuilder. 
		return "SubType";
	}
	
	public static SubType getInstance(){
		return singleton; 
	}
}
