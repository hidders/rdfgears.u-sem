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

import nl.tudelft.rdfgears.engine.bindings.BindingFactory;
import nl.tudelft.rdfgears.engine.bindings.ComplexBinding;
import nl.tudelft.rdfgears.engine.bindings.DiskRGLBinding;
import nl.tudelft.rdfgears.engine.diskvalues.DatabaseManager;
import nl.tudelft.rdfgears.rgl.datamodel.value.visitors.RGLValueVisitor;

import com.hp.hpl.jena.rdf.model.RDFNode;
import com.sleepycat.bind.tuple.TupleBinding;

public class DiskRGLValue implements RGLValue {

	private long id;
	private String className;
	private RGLValue fetchedResult = null;

	public DiskRGLValue(String className, long id) {
		this.id = id;
		this.className = className;
	}

	private RGLValue fetch() {
		if (fetchedResult == null) {
			ComplexBinding binding = (ComplexBinding) BindingFactory
					.createBinding(className);
			fetchedResult = binding.complexEntryToObject(DatabaseManager
					.getComplexEntry(id));
		}
		return fetchedResult;
	}

	@Override
	public void accept(RGLValueVisitor visitor) {
		fetch().accept(visitor);
	}

	@Override
	public BagValue asBag() {
		return fetch().asBag();
	}

	@Override
	public BooleanValue asBoolean() {
		return fetch().asBoolean();
	}

	@Override
	public GraphValue asGraph() {
		return fetch().asGraph();
	}

	@Override
	public LiteralValue asLiteral() {
		return fetch().asLiteral();
	}

	@Override
	public RDFValue asRDFValue() {
		return fetch().asRDFValue();
	}

	@Override
	public RecordValue asRecord() {
		return fetch().asRecord();
	}

	@Override
	public URIValue asURI() {
		return fetch().asURI();
	}

	@Override
	public int compareTo(RGLValue value) {
		return fetch().compareTo(value);
	}

	@Override
	public TupleBinding<RGLValue> getBinding() {
		return new DiskRGLBinding(className);
	}

	@Override
	public long getId() {
		return id;
	}

	@Override
	public RDFNode getRDFNode() {
		return fetch().getRDFNode();
	}

	@Override
	public boolean isBag() {
		return fetch().isBag();
	}

	@Override
	public boolean isBoolean() {
		return fetch().isBoolean();
	}

	@Override
	public boolean isGraph() {
		return fetch().isGraph();
	}

	@Override
	public boolean isLiteral() {
		return fetch().isLiteral();
	}

	@Override
	public boolean isNull() {
		return fetch().isNull();
	}

	@Override
	public boolean isRDFValue() {
		return fetch().isRDFValue();
	}

	@Override
	public boolean isRecord() {
		return fetch().isRecord();
	}

	@Override
	public boolean isSimple() {
		return true;
	}

	@Override
	public boolean isURI() {
		return fetch().isURI();
	}

	@Override
	public void prepareForMultipleReadings() {
		// TODO Auto-generated method stub

	}
	
	@Deprecated //only for testing purpose
	public String getClassName() {
		return className;
	}

}
