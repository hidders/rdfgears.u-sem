package nl.tudelft.rdfgears.rgl.datamodel.type;

import nl.tudelft.rdfgears.engine.Engine;

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
