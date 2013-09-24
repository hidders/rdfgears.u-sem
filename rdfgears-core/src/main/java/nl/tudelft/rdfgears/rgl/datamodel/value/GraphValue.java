package nl.tudelft.rdfgears.rgl.datamodel.value;

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

import nl.tudelft.rdfgears.engine.Engine;
import nl.tudelft.rdfgears.rgl.datamodel.type.GraphType;
import nl.tudelft.rdfgears.rgl.datamodel.value.visitors.RGLValueVisitor;
import nl.tudelft.rdfgears.rgl.exception.ComparisonNotDefinedException;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;

public abstract class GraphValue extends DeterminedRGLValue {
	static String uriBase = "http://wis.ewi.tudelft.nl/RdfGears/graphURI/";
	static int uriCounter = 0;
	
	private Resource uriNode; // a random URI to determine this graph
	private Model model;
	
	public Resource getURI(){
		return uriNode;
	}
	
	/** 
	 * Get the ARQ Model for this RGL Graph.
	 * Note that 
	 * Note that this may be a heavy call if the RGL Graph is a virtual remote graph, as the entire
	 * graph then has to be downloaded. 
	 * 
	 * It is better to call getModel(Query query), and let the implementation determine whether it can/should 
	 * optimize the model to return. 
	 * @return
	 */
	public Model getModel(){
		return model;
	}
	
	public GraphType getType() {
		return GraphType.getInstance();
	}

	
	public synchronized RDFNode getRDFNode() {
		if (uriNode==null){
			/* make a random identifier */
			uriNode = Engine.getDefaultModel().createResource(uriBase + uriCounter);
			uriCounter++;	
		}
		return uriNode;
	}

	@Override
	public GraphValue asGraph(){
		return this;
	}
	
	/**
	 * Convert this graph value to an iterable bag of records
	 */
	@Override
	public BagValue asBag(){
		assert(false) : "not implemented yet, but it's easy to do ";
		return null;
	}
	
	@Override
	public boolean isBag(){
		return false; // may become true
	}
	
	@Override
	public boolean isGraph(){
		return true; // may become true
	}
	
	

	/** 
	 * load the data in the model to contain the patterns described in query under the GRAPH { } clause identified
	 * with varname graphClauseVarName. 
	 * 
	 * This method does nothing, unless it is overridden by a subclass. 
	 * @param query
	 * @param graphClauseVarName
	 */
	public void loadDataForQuery(Query query, String graphClauseVarName){
		/* nothing to do by default */
		
		// FIXME: REMOVE if unused
	}
	
	public void accept(RGLValueVisitor visitor){
		visitor.visit(this);
	}
	

	public int compareTo(RGLValue v2) {
		// but may be implemented by subclass. It must be determined what is comparable, i think it'd be elegant to make as much as possible comparable.
		throw new ComparisonNotDefinedException(this, v2);
	}
	

	@Override
	public void prepareForMultipleReadings() {
		/* nothing to do */
	}

}
