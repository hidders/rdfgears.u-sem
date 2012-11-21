package nl.tudelft.rdfgears.engine.valuefactory;
import java.util.List;

import nl.tudelft.rdfgears.rgl.datamodel.value.BagValue;
import nl.tudelft.rdfgears.rgl.datamodel.value.BooleanValue;
import nl.tudelft.rdfgears.rgl.datamodel.value.GraphValue;
import nl.tudelft.rdfgears.rgl.datamodel.value.LiteralValue;
import nl.tudelft.rdfgears.rgl.datamodel.value.RDFValue;
import nl.tudelft.rdfgears.rgl.datamodel.value.RGLValue;
import nl.tudelft.rdfgears.rgl.datamodel.value.RecordValue;
import nl.tudelft.rdfgears.rgl.datamodel.value.URIValue;
import nl.tudelft.rdfgears.rgl.datamodel.value.impl.BooleanValueImpl;
import nl.tudelft.rdfgears.rgl.datamodel.value.impl.ModifiableRecord;
import nl.tudelft.rdfgears.rgl.datamodel.value.impl.bags.EmptyBag;
import nl.tudelft.rdfgears.util.row.FieldIndexMap;
import nl.tudelft.rdfgears.util.row.ValueRow;

import com.hp.hpl.jena.datatypes.RDFDatatype;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.RDFNode;

public interface ValueFactoryIface {
	public static final BooleanValue trueValue = BooleanValueImpl.getTrueInstance();
	public static final BooleanValue falseValue = BooleanValueImpl.getFalseInstance();
	public static final BagValue emptyBag = new EmptyBag(); 
	
	public GraphValue createGraphValue(Model model);
	
	/**
	 * Create a List that can be used as backing structure for the createBagValue() call.
	 * @return
	 */
	public List<RGLValue> createBagBackingList();
	
	public RecordValue createRecordValue(ValueRow row);

	public ModifiableRecord createModifiableRecordValue(FieldIndexMap map);
	
	public RDFValue createRDFValue(RDFNode node);
	
	public LiteralValue createLiteralTyped(Object value, RDFDatatype dtype);
	
	public LiteralValue createLiteralDouble(double d);
	
	public URIValue createURI(String uri);

	public BooleanValue createFalse();
	
	public BooleanValue createTrue();
	
	/**
	 * Create a Jena model
	 */
	Model createModel();
	public LiteralValue createLiteralPlain(String str, String language);

	public BagValue createBagEmpty();

	public BagValue createBagSingleton(RGLValue value);

	public RGLValue createNull(String string);
	
}
