package nl.tudelft.rdfgears.rgl.datamodel.value.visitors;

import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Iterator;

import nl.tudelft.rdfgears.rgl.datamodel.value.BagValue;
import nl.tudelft.rdfgears.rgl.datamodel.value.BooleanValue;
import nl.tudelft.rdfgears.rgl.datamodel.value.GraphValue;
import nl.tudelft.rdfgears.rgl.datamodel.value.LiteralValue;
import nl.tudelft.rdfgears.rgl.datamodel.value.RGLNull;
import nl.tudelft.rdfgears.rgl.datamodel.value.RGLValue;
import nl.tudelft.rdfgears.rgl.datamodel.value.RecordValue;
import nl.tudelft.rdfgears.rgl.datamodel.value.URIValue;
import nl.tudelft.rdfgears.rgl.workflow.LazyRGLValue;
import nl.tudelft.rdfgears.util.BufferedIndentedWriter;

import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.RDFWriter;

/**
 * A visitor that serializes an RGL Value, writing it to an Output Stream. 
 * 
 * @author Eric Feliksik
 *
 */
public class CopyOfValueXMLSerializer extends ValueSerializer {
	BufferedIndentedWriter writer;
	
	public CopyOfValueXMLSerializer(){
		this(System.out);
	}
	
	public CopyOfValueXMLSerializer(OutputStream out){
		this.writer = new BufferedIndentedWriter(out);
	}
	
	public void serialize(RGLValue value){
		
		try {
			/* print xml header */
			//writer.print("<?xml version=\"1.0\" encoding=\"utf-8\" ?>");
			//writer.newline();
			writer.print("<rgl:xml\n");
			
			/* print namespaces */
			writer.print(" xmlns:rgl=\"http://wis.ewi.tudelft.nl/rgl/datamodel#\"");
			writer.newline();
			writer.print(" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\" ");
			writer.newline();
			writer.print(" xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\"");
			writer.print(">");
			
			
			writer.newline();
			value.accept(this);
			
			writer.newline();
			writer.print("</rgl:xml>");
			writer.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Override
	public void visit(BagValue bag) {
		try {   
			int elemCounter = 0;
			Iterator<RGLValue> iter = bag.iterator();
			writer.print("<rgl:bag>");
			writer.incIndent();
			writer.newline();
			
			
			boolean hasValues = iter.hasNext();
			while(iter.hasNext()){
				
				elemCounter++;
				RGLValue val = iter.next();
				val.accept(this);
			}
			if (hasValues)
				writer.newline();
			
			
			writer.outdent();
			writer.print("</rgl:bag>");
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		
	}
	
	@Override
	public void visit(GraphValue graph) {
		try {
			writer.newline();
			writer.print("<rgl:graph>");
			RDFWriter rdfWriter = new com.hp.hpl.jena.xmloutput.impl.Basic();
			rdfWriter.write(graph.getModel(), writer, null);

			writer.print("</rgl:graph>");
			writer.newline();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Override
	public void visit(BooleanValue bool) {
		try {
			String s = bool.isTrue() ? "True" : "False";
			writer.print(s);
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		
	}
	
	@Override
	public void visit(LiteralValue literal) {
		try {
			Literal jenaLiteral = literal.getRDFNode().asLiteral();
			writer.print("<rgl:literal");
			
			String datatypeURI = jenaLiteral.getDatatypeURI();
			if (datatypeURI!=null){
				writer.print(" rdf:datatype=\"");
				writer.print(datatypeURI);
				writer.print("\"");
			} else {
				String lang = jenaLiteral.getLanguage();
				if (lang.length()>0){
					writer.print(" xml:lang=\"");
					writer.print(lang);
					writer.print("\"");
				} 
			}
			writer.print(">");
			writer.print(literal.getValueString());
			writer.print("</rgl:literal>");
			
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	@Override
	public void visit(RecordValue record) {
		
		try {  

			writer.newline();
			writer.print("<rgl:record>");
			writer.incIndent();
			
			for (String fieldName : record.getRange()){
				writer.newline();
				writer.resetNewlineFlag(); // we will make sure it is flagged at the end. 
				writer.print("<rgl:field name=\"");
				writer.print(fieldName);
				writer.print("\">");
				writer.incIndent();
				
				RGLValue rglValue = record.get(fieldName);
				rglValue.accept(this);
				
				writer.outdent();
				
				if (writer.getNewlineFlag())
					writer.newline(); // extra newline, because our field didn't fit on one line
				
				writer.print("</rgl:field>");
				
			}
			writer.outdent();
			writer.newline();
			writer.print("</rgl:record>");
			
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	@Override
	public void visit(URIValue uri) {
		try {
			writer.print("<rgl:uri resource=\"");
			writer.print(uri.uriString());
			writer.print("\"/>");
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}


	@Override
	public void visit(RGLNull rglNull) {
		try {
			writer.print("<rgl:null ");
			String msg = rglNull.getErrorMessage();
			if (msg != null){
				writer.print("message=\"");
				writer.print(msg);
				writer.print("\"");
			}
			writer.print("/>");
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}
	
	@Override
	public void visit(LazyRGLValue lazyValue) {
		// we cannot deal with this value, let the value evaluate itself and call this visitor 
		// again with right method signature for OO-dispatching
		lazyValue.accept(this);
	}



}
