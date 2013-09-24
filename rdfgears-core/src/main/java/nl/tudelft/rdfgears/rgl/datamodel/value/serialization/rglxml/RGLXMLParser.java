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

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.xml.sax.SAXException;
import com.hp.hpl.jena.datatypes.RDFDatatype;
import com.hp.hpl.jena.datatypes.TypeMapper;
import com.hp.hpl.jena.rdf.arp.SAX2Model;
import com.hp.hpl.jena.rdf.model.Model;

import nl.tudelft.rdfgears.engine.Engine;
import nl.tudelft.rdfgears.engine.ValueFactory;
import nl.tudelft.rdfgears.rgl.datamodel.type.BagType;
import nl.tudelft.rdfgears.rgl.datamodel.type.GraphType;
import nl.tudelft.rdfgears.rgl.datamodel.type.RDFType;
import nl.tudelft.rdfgears.rgl.datamodel.type.RGLType;
import nl.tudelft.rdfgears.rgl.datamodel.type.RecordType;
import nl.tudelft.rdfgears.rgl.datamodel.type.SubType;
import nl.tudelft.rdfgears.rgl.datamodel.type.TypedValue;
import nl.tudelft.rdfgears.rgl.datamodel.value.GraphValue;
import nl.tudelft.rdfgears.rgl.datamodel.value.RGLValue;
import nl.tudelft.rdfgears.rgl.datamodel.value.RecordValue;
import nl.tudelft.rdfgears.rgl.datamodel.value.impl.bags.ListBackedBagValue;
import nl.tudelft.rdfgears.util.row.FieldIndexMap;
import nl.tudelft.rdfgears.util.row.FieldIndexMapFactory;
import nl.tudelft.rdfgears.util.row.TypeRow;
import nl.tudelft.rdfgears.util.row.ValueRowWithPut;

/**
 * I considered using XStream for this. But it seemed to make the simple problems simple, 
 * and the hard problems harder. More specifically:  
 * 
 * - we want to use record fieldIndexMaps as much as possible for records of the same type. 
 * - we want to be able to fetch an inferred type from the generated values 
 * - if NULL values are encountered, a generic SubType should be put in the type place -- unless
 *   a later value of the same type does have more specific value than NULL, then that type should
 *   be used. 
 * - we want to delegate parsing of RDF/XML to Jena. 
 *   
 * This implementation does those things. 
 * 
 * @author Eric Feliksik
 *
 */
public class RGLXMLParser extends AbstractRGLValueParser  {
	
	static XMLInputFactory factory = XMLInputFactory.newInstance();
	XMLStreamReader streamReader;

	private static GraphValueParser graphValueParserInstance;
	
	public RGLXMLParser(InputStream inputStream) throws XMLStreamException{
		this(factory.createXMLStreamReader(inputStream));
	}
	
	public RGLXMLParser(XMLStreamReader streamReader) throws XMLStreamException{
		this.streamReader = streamReader;
		graphValueParserInstance = new GraphValueParser();
		doParse();
	}
	
	private void doParse() throws XMLStreamException{
		if (! streamReader.isStartElement()){
			streamReader.nextTag(); // it is possible the before construction of this class instance, the tag was already peeked at.  
		} 
		
		String rootTag = streamReader.getLocalName();
		if (rootTag.equals(RGLXML.tagRoot)){
			streamReader.nextTag();
			
			if (streamReader.isEndElement())
				throw new XMLStreamException("<"+RGLXML.tagRoot+"> root element is empty");
			
			XMLValueParser valueParser = getValueParser(streamReader.getLocalName());
			
			setParsedValue(valueParser.parseValue(streamReader));
			setParsedType(valueParser.getParsedType());
			
		} else {
			
			/* create GraphParser here if we want to be able to parse RDF/XML directly */
			
			throw new XMLStreamException("Unexpected tag: '"+rootTag+"'");
		} 
	}
	
	

	/**
	 * Get a value parser that is able to parse the next element. 
	 * It will learn what kind of values it parses (e.g. certain record types) and 
	 * can then optimize itself for that type (e.g. by creating records with Field Map Indexing
	 * 
	 * So a new instance must be created if the value parsed is of a possibly different type. 
	 * 
	 * @param tag
	 * @return
	 * @throws XMLStreamException 
	 */
	public static XMLValueParser getValueParser(String tag) throws XMLStreamException{
//		System.out.println("Getting value parser for tag "+tag);
		if (tag.equals(RGLXML.tagUri)){
			return RDFValueParser.getInstance();
		} else if (tag.equals(RGLXML.tagLiteral)){
			return RDFValueParser.getInstance();
		} else if (tag.equals(RGLXML.tagRecord)){
			return new RecordValueParser();
		} else if (tag.equals(RGLXML.tagBag)){
			return new BagParser();
		} else if (tag.equals(RGLXML.tagGraph)){
			return graphValueParserInstance;
		} else if (tag.equals(RGLXML.tagBoolean)){
			throw new RuntimeException("not implemented");
		} else if (tag.equals(RGLXML.tagNull)){
			return NullParser.getInstance();
		} else {
			throw new RuntimeException("Unknown tag '"+tag+"', some value-tag was expected. "); 
		}
	}
	


}



interface XMLValueParser {
	public RGLValue parseValue(XMLStreamReader streamReader) throws XMLStreamException;
	
	/**
	 * Returns the type of the value that was parsed. 
	 * 
	 * WARNING: Only works correctly if:
	 * - parsing didn't fail, AND
	 * - the value parser will never be used again. If it will be used again, it may be that the 
	 *    type defined here will still be refined, as the parser may have only encountered NULL values 
	 *    (for some nested level) so far.  
	 * @return
	 */
	public RGLType getParsedType();
}





class BagParser implements XMLValueParser{
	XMLValueParser subValueParser = null;
	  
	public RGLValue parseValue(XMLStreamReader streamReader) throws XMLStreamException{
		List<RGLValue> elementList = ValueFactory.createBagBackingList();
		
		if (! streamReader.getLocalName().equals(RGLXML.tagBag))
			throw new XMLStreamException("A tag with name "+RGLXML.tagBag+" was expected, but instead "+streamReader.getLocalName()+" was found. Is this a well-typed value? ");
		
		while (true){
			int event = streamReader.next();
			switch (event) {
				case XMLStreamConstants.START_ELEMENT:
					
					RGLValue parsedValue; 
					if (streamReader.getLocalName().equals(RGLXML.tagNull)){
						parsedValue = NullParser.getInstance().parseValue(streamReader);
					} else {
						if (subValueParser==null){ /* get a parser to parse this sub element */
							subValueParser = RGLXMLParser.getValueParser(streamReader.getLocalName());
						}
						parsedValue = subValueParser.parseValue(streamReader);
					}
					
					elementList.add(parsedValue);
					break;
				case XMLStreamConstants.END_ELEMENT:
					/* </bag>  */
					assert(streamReader.getLocalName().equals(RGLXML.tagBag));
					
					return new ListBackedBagValue(elementList);
			} 
			 
		}
		
	}

	@Override
	public RGLType getParsedType() {
		RGLType elemType; 
		if (subValueParser==null){
			/* no values, or only null values */ 
			elemType = SubType.getInstance();
		} else {
			elemType = subValueParser.getParsedType();
		}
		
		return BagType.getInstance(elemType);
	}
}


class RecordValueParser implements XMLValueParser {
	private Map<String, XMLValueParser> fieldParsers = new HashMap<String, XMLValueParser>();;
	private FieldIndexMap fiMap = null;
	
	class UnFieldMappedRecord extends RecordValue implements ValueRowWithPut {
		Map<String, RGLValue> map = new HashMap<String,RGLValue>();
		@Override
		public RGLValue get(String fieldName) {
			return map.get(fieldName);
		}

		@Override
		public Set<String> getRange() {
			return map.keySet();
		}

		@Override
		public void put(String fieldName, RGLValue value) {
			map.put(fieldName,  value);
		} 
	}
	
	public RecordValue parseValue(XMLStreamReader streamReader) throws XMLStreamException{
		
		if (! streamReader.getLocalName().equals(RGLXML.tagRecord))
			throw new XMLStreamException("A tag with name "+RGLXML.tagRecord+" was expected, but instead "+streamReader.getLocalName()+" was found. Is this a well-typed value? ");
		
		RecordValue myRecord;
		if (fiMap==null){
			/* first time, use  a HashValueRow because we don't know what fields we will encounter. 
			 * The second time we can use the generated fieldIndexMap. */
			myRecord = new UnFieldMappedRecord(); 
		} else {
			
			myRecord = ValueFactory.createModifiableRecordValue(fiMap);
			myRecord = new UnFieldMappedRecord(); // DELETE THIS LINE 
		}
		
		while (true){
			int event = streamReader.next();
			switch (event) {
				case XMLStreamConstants.START_ELEMENT:
					String foundTag = streamReader.getLocalName();
					if(! foundTag.equals(RGLXML.tagRecordField)){
						throw new XMLStreamException("<"+RGLXML.tagRecord+"> element has a sub-element ('"+foundTag+"') but this is illegal");
					}
					String fieldName = null;
					for (int i=0; i<streamReader.getAttributeCount(); i++){
						String attrName = streamReader.getAttributeLocalName(i);
						if (! attrName.equals(RGLXML.attrRecordFieldName)){
							throw new XMLStreamException("A tag <"+RGLXML.tagRecordField+"> with attribute  '"+attrName+"' was found, but this attribute is illegal ");
						}	
						fieldName = streamReader.getAttributeValue(i);
					}
					if (fieldName==null){
						throw new XMLStreamException("A tag <"+RGLXML.tagRecordField+"> was found without attribute  '"+RGLXML.attrRecordFieldName+"' . This is illegal ");
					}
					
					streamReader.nextTag(); 
					if (streamReader.isEndElement()){
						throw new XMLStreamException("A tag <"+RGLXML.tagRecordField+"> was found without contents. This is illegal ");
					}
					
					assert(streamReader.isStartElement());
					
					String valueTag = streamReader.getLocalName(); 
					
					/* use a NULL parser if we find NULL, or an existing field parser if a non-null field is encountered */ 
					RGLValue parsedValue;
					
					if (valueTag.equals(RGLXML.tagNull)){
						parsedValue = NullParser.getInstance().parseValue(streamReader);
						if (! fieldParsers.containsKey(fieldName)){
							fieldParsers.put(fieldName, null); // make sure that the field is registered (but do not set a parser yet) */ 
						}
						
					} else {
						/* use an existing ValueParser, if we already have one for this field name */ 
						XMLValueParser thisFieldParser = fieldParsers.get(fieldName);
						if (thisFieldParser==null){
							thisFieldParser = RGLXMLParser.getValueParser(valueTag);
							fieldParsers.put(fieldName, thisFieldParser);
						}	
						parsedValue = thisFieldParser.parseValue(streamReader);
					}
					/* store the value in the record */ 
					
					/* this is a small hack. We are treating the record as ValueRowWithPut now, but there 
					 * would be more consistent solutions. Maybe fix value hierarchy after merging with Tomek  */
					((ValueRowWithPut)myRecord).put(fieldName, parsedValue); 
					
					streamReader.nextTag(); // read </field> tag 
					assert(streamReader.getLocalName().equals(RGLXML.tagRecordField));
					
					if (! streamReader.isEndElement()){
						throw new XMLStreamException("A tag <"+RGLXML.tagRecordField+"> can only have one sub element. ");
					}
					
					break;
				case XMLStreamConstants.END_ELEMENT:
					/* </record> */
					
					if (fiMap==null){ /* first time: create the fieldMap using the fields we encountered */
						fiMap = FieldIndexMapFactory.create(myRecord.getRange());
					}
					assert(streamReader.getLocalName().equals(RGLXML.tagRecord));
					
					return myRecord;
				default: 
					/* ignore */ 
			}
		}
	}

	@Override
	public RGLType getParsedType() {
		TypeRow row = new TypeRow();
		for (String fieldName : fieldParsers.keySet()){
			XMLValueParser fieldParser = fieldParsers.get(fieldName);
			RGLType fieldType; 
			if (fieldParser==null){
				fieldType = SubType.getInstance();
			} else {
				fieldType = fieldParser.getParsedType();
			}
			row.put(fieldName, fieldType);
		}
		return RecordType.getInstance(row); 
	}
}




class RDFValueParser implements XMLValueParser{
	private static final RDFValueParser instance = new RDFValueParser(); // parser always receives same subtype, so can use singleton
	
	public RGLValue parseValue(XMLStreamReader streamReader) throws XMLStreamException{
		
		
		String foundTag = streamReader.getLocalName();
		
		if (foundTag.equals(RGLXML.tagLiteral)){
			return parseLiteralValue(streamReader);
		} else if (foundTag.equals(RGLXML.tagUri)){
			return parseUriValue(streamReader);

		} else {
			throw new XMLStreamException("RDFValueParser cannot parse  <"+foundTag+">");
		}
		
	}

	private RGLValue parseUriValue(XMLStreamReader streamReader) throws XMLStreamException {

		String uriString = null;

		while (true){
			int event = streamReader.next();
			switch (event) {
				case XMLStreamConstants.START_ELEMENT:
					throw new XMLStreamException("<"+RGLXML.tagUri+"> element has a sub-element ('"+streamReader.getLocalName()+"') but this is illegal");
				case XMLStreamConstants.CDATA:
				case XMLStreamConstants.CHARACTERS:
					uriString = streamReader.getText();			
					break;
//				case XMLStreamConstants.ATTRIBUTE: // no, should be fetchable in beginning already
				case XMLStreamConstants.END_ELEMENT:
					/* </uri> */
					assert(streamReader.getLocalName().equals(RGLXML.tagUri));
					if (uriString==null)
						throw new XMLStreamException("Empty <"+RGLXML.tagUri+"> tag was found");
					
					return ValueFactory.createURI(uriString);
			}
		}
	}

	private RGLValue parseLiteralValue(XMLStreamReader streamReader) throws XMLStreamException {
		String stringPart=null;
		String lang=null;
		String datatype=null;
		
		assert(streamReader.isStartElement());
		assert(streamReader.getLocalName().equals(RGLXML.tagLiteral));
		
		/* get lang or datatype */
		for (int i=0; i<streamReader.getAttributeCount(); i++){
			String attrName = streamReader.getAttributeLocalName(i);
			if (attrName.equals(RGLXML.attrLiteralDatatype) ){
				datatype = streamReader.getAttributeValue(i);
				break;
			} else if (attrName.equals(RGLXML.attrLiteralLang)){
				lang = streamReader.getAttributeValue(i);
				break;
			}
			/* no need to be so strict, but let's be conservative for now by warning the user */
			throw new XMLStreamException("Sorry I'm so strict, but I didn't expect an attribute '"+attrName+"' for a <"+RGLXML.tagLiteral+"> tag"); 
		}
		
		while (true){
			int event = streamReader.next();
			switch (event) {
				case XMLStreamConstants.START_ELEMENT:
					throw new XMLStreamException("<Literal> element has a sub-element ('"+streamReader.getLocalName()+"') but this is illegal");
				case XMLStreamConstants.CDATA:
				case XMLStreamConstants.CHARACTERS:
					stringPart = streamReader.getText();
					break;
//				case XMLStreamConstants.ATTRIBUTE: // no, should be fetchable in beginning already
				case XMLStreamConstants.END_ELEMENT:
					/* </literal> */
					assert(streamReader.getLocalName().equals(RGLXML.tagLiteral));
					if (stringPart==null)
						throw new XMLStreamException("Empty <"+RGLXML.tagLiteral+"> tag was found");
					
					if (datatype==null){
						return ValueFactory.createLiteralPlain(stringPart, lang);
					} else {
						RDFDatatype xsdType = TypeMapper.getInstance().getSafeTypeByName(datatype) ;
						return ValueFactory.createLiteralTyped(stringPart, xsdType);
					}
			} 
		}
	}

	public static XMLValueParser getInstance() {
		return instance;
	}

	@Override
	public RGLType getParsedType() {
		return RDFType.getInstance();
	}
}



class GraphValueParser implements XMLValueParser {
	
	private static GraphValueParser instance = new GraphValueParser(); // parser always receives same subtype, so can use singleton
	
	public RGLValue parseValue(XMLStreamReader streamReader) throws XMLStreamException {
		
		/**
		 * The streamReader may have buffered data, so we have no clue how far the original inputStream has
		 * been read. Jena doesn't accept a stax XMLStreamReader, but requires a SAXparser or an InputStream. 
		 * So we have to use the StAX2SAX bridge to hand a SAX parser to Jena, based on the existing StAX XMLStreamReader
		 *  
		 */
		if (!streamReader.getLocalName().equals(RGLXML.tagGraph)){
			throw new XMLStreamException("Encountered tag "+streamReader.getLocalName()+" but expected "+RGLXML.tagGraph+". "); 
		}
		
		GraphValue parsedGraph; 
		
		if (streamReader.getAttributeCount()==0){ /* RDF/XML is embedded */
			streamReader.nextTag();
			if (! streamReader.getLocalName().toLowerCase().equals("rdf")){
				throw new XMLStreamException("Embedded RDF/XML expected. ", streamReader.getLocation()); 
			}
			
			
			Model newModel = ValueFactory.createModel();
			SAX2Model s2m;
			try {
				s2m = SAX2Model.create(null, newModel);  // We are not using a baseURI, but maybe we should. 
				StAX2SAX converter = new StAX2SAX(s2m);
				converter.parse(streamReader);
				
				
				parsedGraph = ValueFactory.createGraphValue(newModel);
			} catch (SAXException e) {
				throw new XMLStreamException("SAXException was: "+e.getMessage());
			}
		} else { /* RDF/XML is referenced externally by some graph identifier */
			String attr = streamReader.getAttributeLocalName(0);
			if (! RGLXML.attrGraphExternal.equals(attr)){
				throw new XMLStreamException("Encountered tag "+attr+" but I require an attribute "+RGLXML.attrGraphExternal+" (and no others) ");
			}
			
			String graphId = streamReader.getAttributeValue(0);
			TypedValue value;
			try {
				value = Engine.getSimpleValueStore().getValue(graphId);
				if (! value.getType().isGraphType()){
					throw new XMLStreamException("The value "+graphId+" is referenced as a graph, but it is of type "+value.getType());
				}
				parsedGraph = value.getValue().asGraph();
			} catch (IOException e) {
				throw new XMLStreamException(e.getMessage());
			}
		}
		

		streamReader.nextTag();
		assert(streamReader.getLocalName().equals(RGLXML.tagGraph));
		assert(streamReader.isEndElement());
		
		return parsedGraph;
		
		
//		com.hp.hpl.jena.rdf.arp.JenaReader.read(Model m, InputStream in, ...)
		
	}

	@Override
	public RGLType getParsedType() {
		return GraphType.getInstance();
	}
}


class NullParser implements XMLValueParser {
	private final static NullParser instance = new NullParser();  // parser always receives same subtype, so can use singleton
	
	private NullParser(){ /* singleton */ }
	
	public RGLValue parseValue(XMLStreamReader streamReader) throws XMLStreamException{
		String message = null;
		if (!streamReader.getLocalName().equals(RGLXML.tagNull)){
			throw new XMLStreamException("NullParser can only parse "+RGLXML.tagNull+" values, but found "+streamReader.getLocalName());
		}
		if (streamReader.getAttributeCount()==1){
			String name = streamReader.getAttributeLocalName(0);
			if (name.equals(RGLXML.attrNullMessage)){
				message = streamReader.getAttributeValue(0);
			} else { 
				throw new XMLStreamException("attribute '"+name+"' not allowed for "+RGLXML.tagNull+" elements");
			}
		}
		
		streamReader.nextTag();
		if (! streamReader.isEndElement()){
			throw new XMLStreamException(RGLXML.tagNull+" should be closed immediately, no subelements allowed but found "+streamReader.getLocalName());
		}
		return ValueFactory.createNull(message); 
	}

	@Override
	public RGLType getParsedType() {
		return SubType.getInstance();
	}
	
	public static NullParser getInstance(){
		return instance;
	}
}


