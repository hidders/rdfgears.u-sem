package nl.tudelft.rdfgears.rgl.datamodel.type;


public class BooleanType extends RGLType {
	private static BooleanType instance = new BooleanType();
	private BooleanType(){
	}
	
	public static synchronized BooleanType getInstance(){
		return instance;
	}

	@Override
	public boolean isType(RGLType type) {
		return type.isBooleanType();
	}

//	@Override
//	public boolean isSupertypeOf(RGLType otherType) {
//		return isType(otherType);
//	}
	
	@Override
	public boolean acceptsAsSubtype(RGLType otherType){
		return otherType.isSubtypeOf(this);
	}
	
	public boolean isSubtypeOf(BooleanType type){
		return true;
	}
	
	
	public String toString(){
		return "Boolean";
	}
	
	@Override
	public boolean isBooleanType(){
		return true;
	}
	
}
