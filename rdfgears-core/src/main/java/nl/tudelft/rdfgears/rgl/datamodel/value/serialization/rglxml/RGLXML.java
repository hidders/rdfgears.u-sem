package nl.tudelft.rdfgears.rgl.datamodel.value.serialization.rglxml;

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
