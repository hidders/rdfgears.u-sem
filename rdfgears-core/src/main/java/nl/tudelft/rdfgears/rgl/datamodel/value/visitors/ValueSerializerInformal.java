package nl.tudelft.rdfgears.rgl.datamodel.value.visitors;

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
import java.io.OutputStream;
import java.io.Writer;
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

import com.hp.hpl.jena.rdf.model.RDFWriter;


/**
 * A visitor that serializes an RGL Value, writing it to an Output Stream. 
 * 
 * @author Eric Feliksik
 *
 */
public class ValueSerializerInformal extends ValueSerializer {
	
	BufferedIndentedWriter writer; 
	
	public ValueSerializerInformal(){
		this(System.out);
	}
	
	public ValueSerializerInformal(OutputStream out){
		this.writer = new BufferedIndentedWriter(out);
	}

	public ValueSerializerInformal(Writer writer){
		this.writer = new BufferedIndentedWriter(writer);
	}
	
	
	
	public void serialize(RGLValue value){
		
		value.accept(this);
		try {
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
			writer.print("{{");
			writer.incIndent();
			
			boolean hasValues = iter.hasNext();
			while(iter.hasNext()){
				writer.newline();
				elemCounter++;
				writer.print("(elem:"+elemCounter+") = ");
				RGLValue val = iter.next();
				val.accept(this);
			}
			writer.outdent();
			if (hasValues)
				writer.newline();
			writer.print("}}");
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		
	}
	
	@Override
	public void visit(GraphValue graph) {
		RDFWriter rdfWriter = graph.getModel().getWriter("RDF/XML-ABBREV");
		rdfWriter.write(graph.getModel(), writer, null);
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
			writer.print(literal.toString());
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	@Override
	public void visit(RecordValue record) {
		
		try {  
			writer.print("[ ");
			writer.incIndent();
			
			for (String fieldName : record.getRange()){
				writer.newline();
				writer.print(fieldName);
				writer.print(":");
				RGLValue rglValue = record.get(fieldName);
				rglValue.accept(this);
				
				writer.print(", ");
			}
			writer.outdent();
			writer.newline();
			writer.print("]");
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	@Override
	public void visit(URIValue uri) {
		try {
			writer.print("<");
			writer.print(uri.uriString());
			writer.print(">");
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}


	@Override
	public void visit(RGLNull rglNull) {
		try {
			writer.print("<null (");
			writer.print(rglNull.getErrorMessage());
			writer.print(")>");
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
