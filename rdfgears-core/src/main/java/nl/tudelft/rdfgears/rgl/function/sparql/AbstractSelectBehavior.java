package nl.tudelft.rdfgears.rgl.function.sparql;

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

import nl.tudelft.rdfgears.rgl.datamodel.type.BagType;
import nl.tudelft.rdfgears.rgl.datamodel.type.RDFType;
import nl.tudelft.rdfgears.rgl.datamodel.type.RGLType;
import nl.tudelft.rdfgears.rgl.datamodel.type.RecordType;
import nl.tudelft.rdfgears.util.row.TypeRow;

import com.hp.hpl.jena.sparql.core.Var;

/**
 * A SPARQL SELECT function implementation for rdfgears. 
 * 
 * @author Eric Feliksik
 *
 */
public abstract class AbstractSelectBehavior implements SparqlBehavior {
	protected SPARQLFunction sparqlFunc;
	private RGLType correctReturnType;

	public AbstractSelectBehavior(SPARQLFunction func){
		sparqlFunc = func;
	}
	
	public RGLType getOutputType() {
		
		if (correctReturnType == null){
			TypeRow recordTypeRow = new TypeRow();
			/* the return type is a bag of records, and the Strings of the records are determined 
			 * by the SELECT clause of the query. 
			 * At least we know that each String in the record will contain something of type RDFValueType. 
			 * 
			 */

			RDFType rdfType = RDFType.getInstance();
			if(sparqlFunc.getQuery()==null) throw new RuntimeException("The query was not initialize()'d");
			for (Var var : sparqlFunc.getQuery().getProject().getVars()){
				/* create a String from the variable name */
				String fieldName = var.getVarName();
				recordTypeRow.put(fieldName, rdfType);
			}
			
			correctReturnType = BagType.getInstance(RecordType.getInstance(recordTypeRow));	
		}
		return correctReturnType;
		
	}
}

