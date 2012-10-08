package nl.tudelft.rdfgears.rgl.datamodel.value;

import nl.tudelft.rdfgears.rgl.datamodel.value.visitors.RGLValueVisitor;

import com.hp.hpl.jena.rdf.model.RDFNode;

/**
 * Abstract class, interface can be MemoryURIValue 
 * (although I don't think other implementations will be relevant)
 * 
 * @author Eric Feliksik
 *
 */
public abstract class URIValue extends RDFValue {
	
	protected RDFNode node;
	protected URIValue(RDFNode node) {
		this.node = node;
	}
	
	public String toString(){
		return "<"+ uriString() +">";
	}
	
	public String uriString(){
		return getRDFNode().toString();
	}
	public void accept(RGLValueVisitor visitor){
		visitor.visit(this);
	}
	
	@Override
	public URIValue asURI(){
		return this;
	}
	
	@Override
	public boolean isURI(){
		return true;
	}
}


