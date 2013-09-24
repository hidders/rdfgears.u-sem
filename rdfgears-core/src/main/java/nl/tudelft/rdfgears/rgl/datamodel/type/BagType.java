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



public class BagType extends RGLType {

	private RGLType elemType = null;
	private BagType(RGLType elemType){
		assert(elemType!=null);
		this.elemType = elemType;
	}
	/**
	 * 
	 * Singleton to allow returning the same RecordType for the same row type, if we may want this later
	 * Now it's not an effective singleton... 
	 */
	public static synchronized BagType getInstance(RGLType elemType){
		return new BagType(elemType);
	}
	
	public RGLType getElemType(){
		return this.elemType;
	}

	public boolean equals(Object that){
		if (! (that instanceof RGLType))
			return false;
		RGLType thatType = (RGLType) that;
		
		if (thatType.isBagType()){
			return this.getElemType().equals(thatType.asBagType().getElemType());
		}
		return false;
	}
	
	@Override
	public BagType asBagType(){
		return this;
	}
	
	@Override
	public boolean isType(RGLType otherType) {
		if (otherType.isBagType()){
			
			return this.getElemType().isType(otherType.asBagType().getElemType());
		}
		return false;
	}
	
//	
//	@Override
//	public boolean isSupertypeOf(RGLType otherType) {
//			if (otherType.isBagType()){
//				return this.getElemType().isSupertypeOf(otherType.asBagType().getElemType());
//			}
//			return false;
//	}
	
	@Override
	public boolean acceptsAsSubtype(RGLType otherType){
		return otherType.isSubtypeOf(this);
	}
	
	public boolean isSubtypeOf(BagType type){
		RGLType myType = getElemType();
		RGLType hisType = type.getElemType();
		return hisType.acceptsAsSubtype(myType);
	}
	
	
	
	@Override
	public boolean isBagType(){
		return true;
	}
	

	public String toString(){
		// types are not SO deeply nested, so no need to write to pass a strinbuilder. 
		return "Bag( "+elemType + " )";
	}
}
