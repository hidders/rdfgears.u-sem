package nl.tudelft.rdfgears.engine.bindings;

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

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import nl.tudelft.rdfgears.engine.ValueFactory;
import nl.tudelft.rdfgears.engine.diskvalues.DatabaseManager;
import nl.tudelft.rdfgears.engine.diskvalues.DiskList;
import nl.tudelft.rdfgears.rgl.datamodel.value.BagValue;
import nl.tudelft.rdfgears.rgl.datamodel.value.DiskRGLValue;
import nl.tudelft.rdfgears.rgl.datamodel.value.LiteralValue;
import nl.tudelft.rdfgears.rgl.datamodel.value.RGLValue;
import nl.tudelft.rdfgears.rgl.datamodel.value.RecordValue;
import nl.tudelft.rdfgears.rgl.datamodel.value.impl.MemoryLiteralValue;
import nl.tudelft.rdfgears.rgl.datamodel.value.impl.MemoryRecordValue;
import nl.tudelft.rdfgears.rgl.datamodel.value.impl.MemoryURIValue;
import nl.tudelft.rdfgears.rgl.datamodel.value.impl.bags.ListBackedBagValue;
import nl.tudelft.rdfgears.rgl.function.core.BagUnion;
import nl.tudelft.rdfgears.util.row.FieldIndexHashMap;
import nl.tudelft.rdfgears.util.row.FieldMappedValueRow;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.hp.hpl.jena.datatypes.xsd.impl.XSDDouble;
import com.sleepycat.bind.tuple.TupleBinding;
import com.sleepycat.je.DatabaseEntry;

public class BindingsTest {

	@Before
	public void init() {
		DatabaseManager.initialize();
	}

	@After
	public void clean() {
		DatabaseManager.cleanUp();
	}

	private BagValue getBagOfDouble() {
		List<RGLValue> list = new DiskList();
		for (double i = 0.0; i < 100.0; ++i) {
			list.add(MemoryLiteralValue.createLiteralTyped(i, new XSDDouble(
					"double")));
		}
		return new ListBackedBagValue(list);
	}

	private RecordValue getRecord() {
		FieldIndexHashMap fiMap = new FieldIndexHashMap();

		for (char a = 'a'; a < 'z'; ++a) {
			fiMap.addFieldName(String.valueOf(a));
		}

		FieldMappedValueRow row = new FieldMappedValueRow(fiMap);

		for (char a = 'a'; a < 'z'; ++a) {
			row.put(String.valueOf(a), getBagOfDouble());
		}

		return new MemoryRecordValue(row);
	}

	private LiteralValue getMemoryLiteral(double d) {
		return MemoryLiteralValue
				.createLiteralTyped(d, new XSDDouble("double"));
	}

	private LiteralValue getMemoryLiteral(String s) {
		return MemoryLiteralValue.createPlainLiteral(s, "en");
	}

	private RGLValue thereAndBack(RGLValue val) {
		DatabaseEntry dbe = new DatabaseEntry();
		val.getBinding().objectToEntry(val, dbe);
		return val.getBinding().entryToObject(dbe);
	}

	private Boolean sameDoubleBags(BagValue bag1, BagValue bag2) {
		Iterator<RGLValue> it1 = bag1.iterator();
		Iterator<RGLValue> it2 = bag2.iterator();

		for (int i = 0; i < 100; ++i) {
			if (it1.next().asLiteral().getValueDouble() != it2.next()
					.asLiteral().getValueDouble())
				return false;
		}

		return true;
	}

	@SuppressWarnings("deprecation")
	@Test
	public void testSimpleBinidngs() {
		LiteralValue l = getMemoryLiteral(1.0);
		assertTrue(thereAndBack(l).asLiteral().getValueDouble() == l
				.asLiteral().getValueDouble());

		l = getMemoryLiteral("foo");
		assertTrue(l.asLiteral().getValueString() + " != "
				+ thereAndBack(l).asLiteral().getValueString(), thereAndBack(l)
				.asLiteral().getValueString().equals(
						l.asLiteral().getValueString()));

		MemoryURIValue uri = new MemoryURIValue("dbpedia.org");
		assertTrue(uri.compareTo(thereAndBack(uri)) == 0);
		
		DiskRGLValue dv1 = new DiskRGLValue("foo.class", 1152);
		DiskRGLValue dv2 = (DiskRGLValue) thereAndBack(dv1);
		
		assertTrue(dv1.getId() == dv2.getId());
		assertTrue(dv2.getClassName().equals(dv1.getClassName()));

	}

	@Test
	public void testRGLListSimple() {
		List<RGLValue> list1 = new ArrayList<RGLValue>();
		TupleBinding<List<RGLValue>> binding = new RGLListBinding();
		DatabaseEntry entry = new DatabaseEntry();

		for (double d = 0.0; d < 100.0; ++d) {
			list1.add(getMemoryLiteral(d));
		}

		binding.objectToEntry(list1, entry);
		List<RGLValue> list2 = binding.entryToObject(entry);

		for (int i = 0; i < list1.size(); ++i) {
			assertTrue(list1.get(i).asLiteral().compareTo(
					list2.get(i).asLiteral()) == 0);
		}

	}

	@Test
	public void testRGLListComplex() {
		List<RGLValue> list1 = new ArrayList<RGLValue>();
		TupleBinding<List<RGLValue>> binding = new RGLListBinding();
		DatabaseEntry entry = new DatabaseEntry();

		for (double d = 0.0; d < 100.0; ++d) {
			list1.add(getBagOfDouble());
		}

		binding.objectToEntry(list1, entry);
		List<RGLValue> list2 = binding.entryToObject(entry);

		for (int i = 0; i < list1.size(); ++i) {
			assertTrue(sameDoubleBags(list1.get(i).asBag(), list2.get(i)
					.asBag()));
		}

	}

	@Test
	public void testEmptyBag() {
		BagValue emptyBag = ValueFactory.createBagEmpty();
		List<RGLValue> backingList = new DiskList();

		for (int i = 0; i < 100; ++i) {
			backingList.add(emptyBag);
		}

		BagValue outer = new ListBackedBagValue(backingList);

		assertTrue(outer.size() == 100);
		assertTrue(outer.asBag().iterator().next().isBag());
		assertTrue(outer.asBag().iterator().next().asBag().size() == 0);
	}

	@Test
	public void testNaiveBag() {
		BagValue bag1 = getBagOfDouble();

		ComplexBinding naive = new NaiveBagBinding();
		DatabaseEntry entry = new DatabaseEntry();
		naive.objectToEntry(bag1, entry);

		BagValue bag2 = naive.entryToObject(entry).asBag();

		assertTrue(sameDoubleBags(bag1, bag2));

	}

	@Test
	public void testRecord() {
		RecordValue record1 = getRecord();
		RecordValue record2 = thereAndBack(record1).asRecord();

		assertTrue(sameDoubleBags(record2.get("x").asBag(), getBagOfDouble()));
	}

	@Test
	public void testUnionBag() {
		BagValue bag1 = getBagOfDouble();
		BagValue bag2 = getBagOfDouble();

		BagValue union1 = BagUnion.createUnionBag(0, bag1, bag2);
		BagValue union2 = thereAndBack(union1).asBag();

		assertTrue(sameDoubleBags(union1, union2));
	}

}
