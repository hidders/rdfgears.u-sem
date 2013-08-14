package nl.tudelft.imreal;

import nl.tudelft.rdfgears.engine.ValueFactory;
import nl.tudelft.rdfgears.rgl.datamodel.type.BagType;
import nl.tudelft.rdfgears.rgl.datamodel.type.RDFType;
import nl.tudelft.rdfgears.rgl.datamodel.type.RGLType;
import nl.tudelft.rdfgears.rgl.datamodel.value.RGLValue;
import nl.tudelft.rdfgears.rgl.function.SimplyTypedRGLFunction;
import nl.tudelft.rdfgears.util.row.ValueRow;

public class DoSomethingFunky extends SimplyTypedRGLFunction  {
	private final static String INPUT1 = "input1";
	public DoSomethingFunky(){
		requireInputType(INPUT1, BagType.getInstance(RDFType.getInstance()) );
	}
	
	@Override
	public RGLType getOutputType() {
		return RDFType.getInstance();
	}

	@Override
	public RGLValue simpleExecute(ValueRow inputRow) {
		RGLValue i1 = inputRow.get(INPUT1);
		if (! i1.isLiteral()){
			return ValueFactory.createNull(null);
		}
		double d = i1.asLiteral().getValueDouble();
		return ValueFactory.createLiteralDouble(d*d);
	}

}
