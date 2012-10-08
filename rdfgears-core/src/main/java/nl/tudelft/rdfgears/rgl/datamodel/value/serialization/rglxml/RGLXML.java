package nl.tudelft.rdfgears.rgl.datamodel.value.serialization.rglxml;

public class RGLXML {
	public static final String tagRoot       = "rgl";
	public static final String tagUri        = "uri";
	public static final String tagBag        = "bag";
	public static final String tagRecord     = "record";
	public static final String tagRecordField     = "field";
	public static final String attrRecordFieldName     = "name";
	
	public static final String tagBoolean    = "boolean";
	public static final String attrBooleanValue    = "value";
	
	public static final String tagNull       = "null";
	public static final String attrNullMessage = "message";
	
	
	public static final String tagGraph          = "graph";
	public static final String attrGraphExternal = "external";
	
	
	public static final String tagLiteral    = "literal";
	public static final String attrLiteralLang      = "lang";
	public static final String attrLiteralDatatype  = "datatype";
	
	public static final String nameSpacePrefix = "rgl";
	public static final String nameSpaceFull = "http://wis.ewi.tudelft.nl/rgl/datamodel#";
	public static final String literalTrueValue = "True";
	public static final String literalFalseValue = "False";
	
	
}
