package nl.tudelft.rdfgears.rgl.function.standard;

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

import java.util.Map;

import nl.tudelft.rdfgears.engine.Engine;
import nl.tudelft.rdfgears.engine.ValueFactory;
import nl.tudelft.rdfgears.rgl.datamodel.type.BagType;
import nl.tudelft.rdfgears.rgl.datamodel.type.GraphType;
import nl.tudelft.rdfgears.rgl.datamodel.type.RDFType;
import nl.tudelft.rdfgears.rgl.datamodel.type.RGLType;
import nl.tudelft.rdfgears.rgl.datamodel.type.RecordType;
import nl.tudelft.rdfgears.rgl.datamodel.value.BagValue;
import nl.tudelft.rdfgears.rgl.datamodel.value.GraphValue;
import nl.tudelft.rdfgears.rgl.datamodel.value.RGLValue;
import nl.tudelft.rdfgears.rgl.datamodel.value.RecordValue;
import nl.tudelft.rdfgears.rgl.function.SimplyTypedRGLFunction;
import nl.tudelft.rdfgears.util.row.TypeRow;
import nl.tudelft.rdfgears.util.row.ValueRow;

import com.hp.hpl.jena.rdf.model.Model;


/**
 * Converts a bag of record to a graph. The records should contain the 's', 'p' and 'o' names, with: 
 * 's' contains a URIValue.
 * 'p' contains a URIValue. 
 * 'o' contains a URIValue or LiteralValue.
 * 
 * Note these URI constraints are not guaranteed by typechecking, as we typecheck only for the RDFValue supertype.
 * 
 * Function returns RGL-NULL if the bag or some record thereof is NULL. 
 * 
 * NULL elements in the (s,p,o) fields of the record gracefully discard the triple from the result graph. 
 * 
 * Ideally, the typing system would be modified so that this function could be largely implemented in the asGraph()
 * function of a bag. But that would require we change the typechecking theory (graph is a theoretical subtype of bag)
 * and the implementatation (create BagType() funtions instead of comparisons with 'instanceof', among other things).  
 */
public class BagToGraph extends SimplyTypedRGLFunction  {
	public static String bag = "bag";
	
	public BagToGraph(){
		TypeRow row = new TypeRow();
		row.put("s", RDFType.getInstance());
		row.put("p", RDFType.getInstance());
		row.put("o", RDFType.getInstance());
		requireInputType(bag, BagType.getInstance(RecordType.getInstance(row))); 
	}
	
	@Override
	public RGLValue simpleExecute(ValueRow inputRow) {
		RGLValue bagValue = inputRow.get(bag);
		if (bagValue.isNull())
			return bagValue; // return null
		
		BagBackedGraphValue graph = new BagBackedGraphValue(bagValue.asBag());
		
		if (graph.getModel()==null){
			return ValueFactory.createNull(null);
		}
		
		return graph;
	}
	
	@Override
	public void initialize(Map<String, String> config) {
		// nothing to do 
	}

	@Override
	public RGLType getOutputType() {
		return GraphType.getInstance();
	}

}



class BagBackedGraphValue extends GraphValue {
	private Model model; // the model, if generated already.
	
	// if null after initialization, we are actually a NULL value.
	// this overloading is not very useful right now, but it illustrates the concept that we could
	// even postpone model creation upon instantiation, and only have it called when isNull() of isGraph() is 
	// called. 
	private BagValue bag;    
	
	public BagBackedGraphValue(BagValue bag){
		this.bag = bag;
		
		try {
			Model tentativeModel = ValueFactory.createModel();
			
			for (RGLValue elem : bag ){
				if (! elem.isNull()){
					RecordValue rec = elem.asRecord();
					
					RGLValue subj = rec.get("s");
					RGLValue pred = rec.get("p");
					RGLValue obj = rec.get("o");
					
					if (! (subj.isNull() || pred.isNull() || obj.isNull())){
						tentativeModel.add(
								subj.asRDFValue().getRDFNode().asResource(), 
								tentativeModel.createProperty(pred.asRDFValue().getRDFNode().asResource().getURI()), 
								obj.asRDFValue().getRDFNode()
							);	
					} else {
						// ignore this triple, as one of values is NULL so we cannot create it 
					}
				} else {
					Engine.getLogger().warn("Warning, could not convert bag to graph because the bag contains a NULL record");
					return;
				} 
			}	
			
			model = tentativeModel; // success
		} catch (Exception e){
			Engine.getLogger().error("Could not create a graph-model from the bag!");
			Engine.getLogger().error(e);
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see rdfgears.rgl.datamodel.value.GraphValue#getModel()
	 */
	@Override
	public Model getModel(){
		return model;
	}
	
	/**
	 * Return the bag that defined us.
	 * 
	 * Note that currently, isBag() returns false.  
	 */
	public BagValue asBag(){
		return bag; 
	}

}
