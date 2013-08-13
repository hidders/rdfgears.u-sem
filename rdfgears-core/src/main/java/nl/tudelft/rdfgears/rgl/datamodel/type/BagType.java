package nl.tudelft.rdfgears.rgl.datamodel.type;



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
