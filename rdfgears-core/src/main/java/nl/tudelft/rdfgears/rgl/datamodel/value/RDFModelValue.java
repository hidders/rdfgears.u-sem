package nl.tudelft.rdfgears.rgl.datamodel.value;

import java.io.StringWriter;

import nl.tudelft.rdfgears.rgl.datamodel.value.visitors.RGLValueVisitor;

import com.hp.hpl.jena.rdf.model.Model;

public class RDFModelValue extends RDFValue {

	private Model rdfModel;

	public RDFModelValue(Model rdfModel) {
		this.rdfModel = rdfModel;
	}

	public void accept(RGLValueVisitor visitor){
		visitor.visit(rdfModel);
	}

	@Override
	public int compareTo(RGLValue v2) {
		return 0;
	}

	@Override
	public String toString() {
		StringWriter sw = new StringWriter();
		rdfModel.write(sw);

		return sw.toString();
	}

}
