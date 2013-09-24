package nl.tudelft.rdfgears.util;

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

import java.text.ParseException;

import org.openjena.riot.tokens.PublicToken;
import org.openjena.riot.tokens.Token;

import com.hp.hpl.jena.datatypes.RDFDatatype;
import com.hp.hpl.jena.datatypes.xsd.XSDDatatype;
import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.impl.LiteralLabel;

import nl.tudelft.rdfgears.engine.ValueFactory;
import nl.tudelft.rdfgears.rgl.datamodel.value.RGLValue;

public class ValueParser {
	

//	} else if (serializedRDFVal.toLowerCase().equals("true")){
//		return ValueFactory.createTrue();
//	} else if (serializedRDFVal.toLowerCase().equals("false")){
//		return ValueFactory.createFalse();
//	} else if (serializedRDFVal.toLowerCase().equals("null")){
//		return ValueFactory.createNull(null);
//	}
	
	

	/**
	 * Simple RGL values URI's/Literals in N-Triple encoding, or the strings
	 * "null"
	 * "true"
	 * "false"
	 * 
	 * @param serializedRDFVal
	 * @return
	 * @throws ParseException
	 */
	public static RGLValue parseSimpleRGLValue(String s) throws ParseException {
		s = s.trim();
		if(s.startsWith("<") || s.startsWith("\"")){
			return parseNTripleValue(s);
		} else {
			// not uri/literal
			s = s.toLowerCase();
			if (s.equals("true")){
				return ValueFactory.createTrue();
			} else if (s.equals("false")){
				return ValueFactory.createFalse();
			} else if (s.equals("null")){
				return ValueFactory.createNull(null);
			}
		}
		
		throw new ParseException("Value is not a correct URI/Literal/Boolean encoding", 0);
		
	}
	
	public static RGLValue parseNTripleValue(String serializedRDFVal) throws ParseException {
		Token token = PublicToken.create(serializedRDFVal);
		Node node = token.asNode();
		if (node==null){
			ParseException e = new ParseException("N-Triple encoded node should start with '<' (URI) or '\"' (literal)", 0);
			e.printStackTrace();
			throw(e);
		}
		
		if (node.isURI()){
			return ValueFactory.createURI(node.getURI());
		} else if (node.isLiteral()){
			LiteralLabel literal = node.getLiteral();
			RDFDatatype datatype = literal.getDatatype();
			if (datatype==null){
				// untyped (plain) literal
				String lang = literal.language();
				if ("".equals(lang)){
					lang = null;
				}
				return ValueFactory.createLiteralPlain(literal.getLexicalForm(), lang);
			} else {
				// typed literal 
				return ValueFactory.createLiteralTyped(literal.getValue(), datatype);
			}
		} else { 
			ParseException e = new ParseException("N-Triple encoded node should start with '<' (URI) or '\"' (literal)", 0);
			e.printStackTrace();
			throw(e);
		}
		
		
	}
//	
//	// OLD STUFF, THROW AWAY IF ABOVE REPLACEMENTS GIVES NO PROBLEMS
//	
//	/**
//	 * Create a typed literal from a literal serialized in N-Triples format (no namespace abbreviation allowed)
//	 * 
//	 * FIXME TODO : Very simple and inefficient parsing algorithm. Inefficient is no problem currently (only used for workflow parsing), 
//	 * but it would be nice to use a more solid one (e.g. from Jena)
//	 * 
//	 * @param serializedRDFVal
//	 * @return
//	 */
//	public static RGLValue parseNTripleValue_old(String serializedRDFVal) throws ParseException {
//		String str = serializedRDFVal.trim();
//		
//		try {
//			char cFirst = str.charAt(0);
//			char cLast = str.charAt(str.length()-1);
//			if(cFirst=='<'){
//				return ValueFactory.createURI( parseBracketedURI(str));
//			} else if (cFirst=='"'){
//				
//				// this is a Literal
//				
//				/** find the string part */
//				int lastQuote = str.lastIndexOf("\""); // last " character must be the end of string, as language tags and URI shouldn't contain these
//				if (lastQuote<1) // not found
//					throw new ParseException("Literal is not closed by \"", lastQuote);
//				
//				String stringPart = str.substring(1,lastQuote);
//				
//				if (str.length()-1 == lastQuote ){ // it is the last character, just a plain literal without language tag
//					
//					return ValueFactory.createLiteralPlain(stringPart, null);
//					
//				} else {
//					/* we must parse the type / language tag. There is a minimum of two more characters required for the type */
//					String typePart = str.substring(lastQuote+1);
//					if (typePart.length() < 3)
//						throw new ParseException("Cannot parse N-Triples value: '"+str+"', expect a type prefixed with two dakjes ('^^') or an @lang tag ", lastQuote);
//					
//					if (typePart.charAt(0)=='^'){ // typed literal
//						if (typePart.charAt(1)!='^'){
//							throw new ParseException("Cannot parse N-Triples value: '"+str+"', expect a type prefixed with two dakjes ('^^') :-) ", lastQuote);
//						}
//						String typeURI = typePart.substring(2, typePart.length());
//						
//						String type = parseBracketedURI(typeURI);
//						
//						/* 
//						 * FIXME TODO ugly, fix this. I don't know how to deal with all types! 
//						 * 
//						 */
//						XSDDatatype dtype = getDataTypeFromURI(type);
//						
//						return ValueFactory.createLiteralTyped(stringPart, dtype);
//						
//					} else {
//						// plain literal 
//						String langTag = null;
//						if (typePart.charAt(0)=='@'){ // with language tag
//							langTag = typePart.substring(1);
//						}
//						
//						return ValueFactory.createLiteralPlain(stringPart, langTag);
//					}
//				}
//				
//			} else if (serializedRDFVal.toLowerCase().equals("true")){
//				return ValueFactory.createTrue();
//			} else if (serializedRDFVal.toLowerCase().equals("false")){
//				return ValueFactory.createFalse();
//			} else if (serializedRDFVal.toLowerCase().equals("null")){
//				return ValueFactory.createNull(null);
//			}
//			else {
//				throw new ParseException("N-Triple encoded node should start with '<' (URI) or '\"' (literal)", 0);
//			}
//
//		}
//		catch (StringIndexOutOfBoundsException e){
//			throw new ParseException(e.getMessage(), 0);
//		}
//		
//	}
//
	public static XSDDatatype getDataTypeFromURI(String uri) throws ParseException {
		XSDDatatype dtype = null;
		if (uri.equals(XSDDatatype.XSDboolean.getURI())){
			dtype = XSDDatatype.XSDboolean;
		} else if (uri.equals(XSDDatatype.XSDdouble.getURI())){
			dtype = XSDDatatype.XSDdouble;
		} else if (uri.equals(XSDDatatype.XSDstring.getURI())){
			dtype = XSDDatatype.XSDstring;
		} else if (uri.equals(XSDDatatype.XSDint.getURI())){
			dtype = XSDDatatype.XSDint;
		} else if (uri.equals(XSDDatatype.XSDinteger.getURI())){
			dtype = XSDDatatype.XSDinteger;
		} else  {
			throw new ParseException("I don't know the datatype "+uri, 0);
		}
		return dtype;
	}
//
//	private static String parseBracketedURI(String str) throws ParseException {
//		char cFirst = str.charAt(0);
//		char cLast = str.charAt(str.length()-1);
//		if(cFirst!='<' || cLast!='>')
//			throw new ParseException("Cannot parse N-Triples value: '"+str+"', a URI should start with '<' and end with '>'. ", 0);
//		
//		return str.substring(1, str.length()-1);
//	}

}
