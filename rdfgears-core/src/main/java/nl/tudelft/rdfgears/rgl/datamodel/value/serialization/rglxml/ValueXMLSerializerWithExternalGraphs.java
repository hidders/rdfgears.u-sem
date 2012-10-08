package nl.tudelft.rdfgears.rgl.datamodel.value.serialization.rglxml;

import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;
import java.util.Iterator;
import java.util.Set;
import java.util.UUID;

import javanet.staxutils.IndentingXMLStreamWriter;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;

import org.openjena.riot.system.JenaWriterBase;

import nl.tudelft.rdfgears.engine.Engine;
import nl.tudelft.rdfgears.rgl.datamodel.type.GraphType;
import nl.tudelft.rdfgears.rgl.datamodel.type.RGLType;
import nl.tudelft.rdfgears.rgl.datamodel.type.TypedValueImpl;
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
import nl.tudelft.rdfgears.util.BufferedIndentedWriter;

import com.hp.hpl.jena.graph.Graph;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.RDFWriter;

/**
 * A visitor that serializes an RGL Value, writing it to an Output Stream. 
 * 
 * @author Eric Feliksik
 *
 */
public class ValueXMLSerializerWithExternalGraphs extends ValueXMLSerializer {
	
	
	private String dirPath;

	public ValueXMLSerializerWithExternalGraphs(OutputStream out, String dirPath) {
		super(out);
		this.dirPath = dirPath;
	}
	
	@Override
	public void serialize(RGLValue value) {
		if (value.isGraph()){
			// skip RGL headers, serialize directly as RDF/XML to the bufferedStream
			RDFWriter rdfWriter = new com.hp.hpl.jena.xmloutput.impl.Basic();
			rdfWriter.write(value.asGraph().getModel(), bufferedStream, null);		
		} else {
			super.serialize(value);	
		}
		
	}
	
	@Override
	public void visit(GraphValue graph)  {
		try {
			xmlwriter.writeStartElement(RGLXML.tagGraph);
			
			String graphId;
			try {
				graphId = Engine.getSimpleValueStore().putValue(new TypedValueImpl(GraphType.getInstance(), graph), null);
			} catch (IOException e) {
				e.printStackTrace();
				throw new RuntimeException(e); // FIXME 
			}
			
			xmlwriter.writeAttribute("external", graphId);
			
//			xmlwriter.writeCharacters(""); // to write the ">"  part of the <graph> tag
			xmlwriter.flush(); // flush xmlwriter before writing RDF/XML directly to bufferedStream
			
//			RDFWriter rdfWriter = new com.hp.hpl.jena.xmloutput.impl.Basic();
//			rdfWriter.write(graph.getModel(), bufferedStream, null);
			
			xmlwriter.writeEndElement();
			
		} catch (XMLStreamException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	public String generateGraphId(){
		return UUID.randomUUID().toString();
	}
	

}
