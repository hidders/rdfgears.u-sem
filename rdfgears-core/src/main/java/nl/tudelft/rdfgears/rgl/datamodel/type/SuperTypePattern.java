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
 * A Type that can be any of the types specified at the constructor. 
 * 
 * 
 * @author Eric Feliksik
 *
 */
public class SuperTypePattern extends RGLType {
	
	RGLType[] allMySubTypes = null;
	
	/**
	 * Instantiate a universal supertype. Will accept any other type as its subtype. 
	 */
	public SuperTypePattern(){
		
	}
	
	/**
	 * Behave like the supertype of all types given in the subTypes arguments (variable number of arguments )
	 * @param subTypes
	 */
	public SuperTypePattern(RGLType... subTypes){
		for (int i=0; i<subTypes.length; i++){
			if (subTypes[i]==null){
				throw new IllegalArgumentException("cannot pass a subTypes[] argument with null values");
			}
		}
		this.allMySubTypes = subTypes;
	}
	
	
	public boolean isType(RGLType type){
		return this.equals(type);
	}
	
	/**
	 * The SuperType class is supertype of every other type that is not a SuperType, i.e. 
	 * of every concrete type
	 */
//	@Override
//	public boolean isSupertypeOf(RGLType otherType) {
//		if (otherType instanceof SuperType){
//			/* we cannot assume that thatType is a subtype of this type */
//			Engine.getLogger().warn("We do NOT assume that one SuperType is the supertype of the other. ");
//			return false;
//		}
//		
//		if (otherType==null)
//			return false;
//		
//		return true;
//	}
	

	@Override
	public boolean acceptsAsSubtype(RGLType otherType){
		if (allMySubTypes==null){
			return true; // any type is my subtype
		}
		
		if (otherType instanceof SuperTypePattern){
			return ((SuperTypePattern) otherType).isSubtypeOf(this);
		}
		
		for (int i=0; i<allMySubTypes.length; i++){
			RGLType mySubType = allMySubTypes[i];
			if (mySubType.acceptsAsSubtype(otherType))
				return true; // otherType is the subtype of one of the types that I have defined as my subtypes. 
		}
		
		return false;
		
	}
	
	public boolean isSubtypeOf(SuperTypePattern type){
		throw new RuntimeException("Not implemented. Must compare whether our subtype-sets are compatible");
	}
	
	
	public String toString(){
		// types are not SO deeply nested, so no need to write to pass a stringbuilder.
		if (allMySubTypes==null){
			return "ANY(*)";
		} else {
			String typestr = "ANY(";
			
			for (int i=0; i<allMySubTypes.length; i++){
				RGLType subtype = allMySubTypes[i];
				typestr += subtype.toString()+"|";
			}
			return typestr += ")";
		}
	}
}
