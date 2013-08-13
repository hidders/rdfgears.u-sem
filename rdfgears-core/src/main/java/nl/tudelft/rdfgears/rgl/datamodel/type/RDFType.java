package nl.tudelft.rdfgears.rgl.datamodel.type;
public class RDFType extends RGLType {
	private static RDFType instance = new RDFType();
	private RDFType() {}

	/**
	 * singleton constructor
	 * @return
	 */
	public static synchronized RDFType getInstance(){
		if (instance == null){
			instance = new RDFType();
			assert(instance!=null);
		}

		assert(instance!=null);
		return instance;
	}

	@Override
	public boolean isType(RGLType type) {
		return this.equals(type);
	}

//	@Override
//	public boolean isSupertypeOf(RGLType otherType) {
//		return (otherType instanceof RDFType);
//	}
	

	@Override
	public boolean acceptsAsSubtype(RGLType otherType){
		return otherType.isSubtypeOf(this);
	}
	
	public boolean isSubtypeOf(RDFType type){
		return true;
	}
	
	@Override
	public boolean isRDFValueType(){
		return true;
	}

	@Override
	public RDFType asRDFType(){
		return this;
	}
	
	public String toString(){
		return "RDFValue";
	}

}
