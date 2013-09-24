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

import java.io.BufferedOutputStream;
import java.io.OutputStream;

import javanet.staxutils.IndentingXMLStreamWriter;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;

import nl.tudelft.rdfgears.rgl.datamodel.value.BagValue;
import nl.tudelft.rdfgears.rgl.datamodel.value.BooleanValue;
import nl.tudelft.rdfgears.rgl.datamodel.value.GraphValue;
import nl.tudelft.rdfgears.rgl.datamodel.value.LiteralValue;
import nl.tudelft.rdfgears.rgl.datamodel.value.RGLNull;
import nl.tudelft.rdfgears.rgl.datamodel.value.RGLValue;
import nl.tudelft.rdfgears.rgl.datamodel.value.RecordValue;
import nl.tudelft.rdfgears.rgl.datamodel.value.URIValue;
import nl.tudelft.rdfgears.rgl.datamodel.value.visitors.ValueSerializer;
import nl.tudelft.rdfgears.rgl.workflow.LazyRGLValue;

import com.hp.hpl.jena.rdf.model.RDFWriter;

/**
 * A visitor that serializes an RGL Value as RGL/XML, writing it to an Output
 * Stream. Included RDF graph values are embedded as RDF/XML within
 * &lt;graph&gt; tags.
 * 
 * 
 * @author Eric Feliksik
 * 
 */
public class ValueXMLSerializer extends ValueSerializer {
	private static XMLOutputFactory xmlOutputFactory = XMLOutputFactory
			.newInstance();
	IndentingXMLStreamWriter xmlwriter;
	BufferedOutputStream bufferedStream;

	public ValueXMLSerializer(OutputStream out) {
		this.bufferedStream = new BufferedOutputStream(out);
	}

	private void initWriter() throws XMLStreamException {
		if (xmlwriter == null) {
			xmlwriter = new IndentingXMLStreamWriter(
					xmlOutputFactory.createXMLStreamWriter(bufferedStream));
		}
	}

	public void serialize(RGLValue value) {

		try {
			initWriter();

			/* print xml header */
			xmlwriter.writeStartDocument("utf-8", "1.0");
//			xmlwriter.writeStartElement(RGLXML.tagRoot);
//			xmlwriter.writeNamespace(RGLXML.nameSpacePrefix,
//					RGLXML.nameSpaceFull);
//			xmlwriter.writeNamespace("rdfs",
//					"http://www.w3.org/2000/01/rdf-schema#");
//			xmlwriter.writeNamespace("rdf",
//					"http://www.w3.org/1999/02/22-rdf-syntax-ns#");

			value.accept(this);

//			xmlwriter.writeEndElement();
			xmlwriter.writeEndDocument();

			xmlwriter.flush();
			xmlwriter.close();
		} catch (XMLStreamException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void visit(BagValue bag) {
		try {
			xmlwriter.writeStartElement(RGLXML.tagBag);

			for (RGLValue elem : bag) {
				elem.accept(this);
			}

			xmlwriter.writeEndElement(); // bag

		} catch (XMLStreamException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@Override
	public void visit(GraphValue graph) {
		try {
			xmlwriter.writeStartElement(RGLXML.tagGraph);
			xmlwriter.writeCharacters(""); // to write the ">" part of the
											// <graph> tag
			xmlwriter.flush(); // flush xmlwriter before writing RDF/XML
								// directly to bufferedStream

			RDFWriter rdfWriter = graph.getModel().getWriter("RDF/XML-ABBREV");
			rdfWriter.write(graph.getModel(), bufferedStream, null);

			xmlwriter.writeEndElement();

		} catch (XMLStreamException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void visit(BooleanValue bool) {
		try {
			xmlwriter.writeStartElement(RGLXML.tagBoolean);
			xmlwriter.writeAttribute(RGLXML.attrBooleanValue,
					bool.isTrue() ? RGLXML.literalTrueValue
							: RGLXML.literalFalseValue);
		} catch (XMLStreamException ex) {
			ex.printStackTrace();
		}

	}

	@Override
	public void visit(LiteralValue literal) {
		try {
			assert (xmlwriter != null);
			xmlwriter.writeStartElement(RGLXML.tagLiteral);
			if (literal.getLiteralType() != null) {
				xmlwriter.writeAttribute(RGLXML.attrLiteralDatatype, literal
						.getLiteralType().getURI());
			} else if (literal.getLanguageTag() != null) {
				xmlwriter.writeAttribute(RGLXML.attrLiteralLang,
						literal.getLanguageTag());
			}

			xmlwriter.writeCharacters(literal.getValueString());
			xmlwriter.writeEndElement(); // </literal>
		} catch (XMLStreamException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	//
	//
	// @Override
	// public void visit(LiteralValue literal) {
	// try {
	// Literal jenaLiteral = literal.getRDFNode().asLiteral();
	// writer.print("<rgl:literal");
	//
	// String datatypeURI = jenaLiteral.getDatatypeURI();
	// if (datatypeURI!=null){
	// writer.print(" rdf:datatype=\"");
	// writer.print(datatypeURI);
	// writer.print("\"");
	// } else {
	// String lang = jenaLiteral.getLanguage();
	// if (lang.length()>0){
	// writer.print(" xml:lang=\"");
	// writer.print(lang);
	// writer.print("\"");
	// }
	// }
	// writer.print(">");
	// writer.print(literal.getValueString());
	// writer.print("</rgl:literal>");
	//
	// } catch (IOException ex) {
	// ex.printStackTrace();
	// }
	// }

	@Override
	public void visit(RecordValue record) {
		try {
			xmlwriter.writeStartElement(RGLXML.tagRecord);

			for (String fieldName : record.getRange()) {
				RGLValue bindingVal = record.get(fieldName);

				/* print <binding name="fieldName"> */
				xmlwriter.writeStartElement(RGLXML.tagRecordField);
				xmlwriter.writeAttribute(RGLXML.attrRecordFieldName, fieldName);

				/* print binding value */
				bindingVal.accept(this);

				xmlwriter.writeEndElement(); //
			}

			xmlwriter.writeEndElement(); // </record>

		} catch (XMLStreamException ex) {
			ex.printStackTrace();
		}
	}

	@Override
	public void visit(URIValue uri) {
		try {
			xmlwriter.writeStartElement(RGLXML.tagUri);
			xmlwriter.writeCharacters(uri.uriString());
			xmlwriter.writeEndElement(); // </uri>
		} catch (XMLStreamException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void visit(RGLNull rglNull) {
		try {
			xmlwriter.writeEmptyElement(RGLXML.tagNull);
			if (rglNull.getErrorMessage() != null)
				xmlwriter.writeAttribute(RGLXML.attrNullMessage,
						rglNull.getErrorMessage());

		} catch (XMLStreamException ex) {
			ex.printStackTrace();
		}
	}

	@Override
	public void visit(LazyRGLValue lazyValue) {
		// we cannot deal with this value, let the value evaluate itself and
		// call this visitor
		// again with right method signature for OO-dispatching
		lazyValue.accept(this);
	}

}
