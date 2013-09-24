package nl.tudelft.rdfgears.engine.valuefactory;

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
import java.util.List;

import nl.tudelft.rdfgears.engine.diskvalues.DiskList;
import nl.tudelft.rdfgears.rgl.datamodel.value.GraphValue;
import nl.tudelft.rdfgears.rgl.datamodel.value.RGLValue;
import nl.tudelft.rdfgears.rgl.datamodel.value.impl.MemoryGraphValue;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.tdb.TDBFactory;

/**
 * Overrides some memory valuefactory functions to create disk based values. 
 * 
 * 
 * @author Eric Feliksik
 *
 */
public class DiskValueFactory extends MemoryValueFactory {
	
	/**
	 * Make disk based model
	 */
	@Override
	public Model createModel(){
		return TDBFactory.createNamedModel("someName", "/tmp/tdbModels/");
	}
	
	/**
	 * Hmmm we actually make a memorygraphvalue, it depends on the model whether it's disk based or not... 
	 */
	@Override
	public GraphValue createGraphValue(Model model){
		return new MemoryGraphValue(model);
	}
	
	
	@Override
	public List<RGLValue> createBagBackingList() {
		return new DiskList();
	}
	

}
