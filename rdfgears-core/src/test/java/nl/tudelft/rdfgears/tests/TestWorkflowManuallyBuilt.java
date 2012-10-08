package nl.tudelft.rdfgears.tests;


import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;

import nl.tudelft.rdfgears.engine.Engine;
import nl.tudelft.rdfgears.rgl.datamodel.type.BagType;
import nl.tudelft.rdfgears.rgl.datamodel.type.GraphType;
import nl.tudelft.rdfgears.rgl.datamodel.type.RDFType;
import nl.tudelft.rdfgears.rgl.datamodel.value.BagValue;
import nl.tudelft.rdfgears.rgl.datamodel.value.GraphValue;
import nl.tudelft.rdfgears.rgl.datamodel.value.RGLValue;
import nl.tudelft.rdfgears.rgl.datamodel.value.RecordValue;
import nl.tudelft.rdfgears.rgl.function.core.RecordCreate;
import nl.tudelft.rdfgears.rgl.function.core.RecordProject;
import nl.tudelft.rdfgears.rgl.function.obsolete.JaroSimilarityFunction;
import nl.tudelft.rdfgears.rgl.function.sparql.SPARQLFunction;
import nl.tudelft.rdfgears.rgl.function.obsolete.MaxVal2;
import nl.tudelft.rdfgears.rgl.workflow.ConstantProcessor;
import nl.tudelft.rdfgears.rgl.workflow.FunctionProcessor;
import nl.tudelft.rdfgears.rgl.workflow.Workflow;
import nl.tudelft.rdfgears.util.row.FieldIndexMapFactory;
import nl.tudelft.rdfgears.util.row.FieldMappedValueRow;
import nl.tudelft.rdfgears.util.row.ValueRow;

import org.junit.Before;
import org.junit.Test;

import tools.Util;

import com.hp.hpl.jena.datatypes.xsd.XSDDatatype;
import static org.junit.Assert.*;

public class TestWorkflowManuallyBuilt {
	private static ConstantProcessor lit1generator = new ConstantProcessor (Engine.getValueFactory().createLiteralTyped("1.0", XSDDatatype.XSDdouble));
	private static ConstantProcessor lit2generator = new ConstantProcessor (Engine.getValueFactory().createLiteralTyped("2.0", XSDDatatype.XSDdouble));
	
	private static ValueRow emptyValueGeneratorRow = new FieldMappedValueRow(FieldIndexMapFactory.create()); // empty! 
	
	@Before public void init() {
		
	}
	
	@Test 
    public void testSimple() {
    	/**
    	 * Now create a workflow with no input ports 
    	 */
    	
    	Workflow wflow = new Workflow();
    	wflow.setOutputProcessor(TestProcessorNetwork.getTestedNetwork());
    	GraphValue graph = wflow.execute(emptyValueGeneratorRow).asGraph();
    	assertTrue("Must have 3 entries, but have "+graph.getModel().size(), graph.getModel().size()==3);
    	
    } 

	/** 
	 * create a selector processor that selects 'selectField' from a record.
	 * @param projectField
	 * @return
	 */
	private FunctionProcessor createProjector(String projectField){
		RecordProject rp = new RecordProject();
    	rp.initialize(Collections.singletonMap(RecordProject.CONFIGKEY_PROJECTFIELD, projectField));
    	FunctionProcessor projector = new FunctionProcessor(rp);
    	
    	
		return projector;
	}
		
    /**
     * A workflow inspired by the Silk2 linkedmdb_directors.xml example (see Silk distribution). 
     */
    @Test 
    public void testSimple2(){
    	String dbpediaQuery = 
			"PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n" +
			"PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n" +
			"PREFIX dbpedia: <http://dbpedia.org/ontology/> \n" +
			"SELECT DISTINCT ?label ?mov WHERE { \n"+
			"  graph $g { \n" +
			"    ?mov rdf:type dbpedia:Film. \n" +
			"    ?mov dbpedia:director/rdfs:label ?label_lang. \n" +
			"    BIND(str(?label_lang) AS ?label)\n" +
			"  } \n"+
			"} LIMIT 200";
    	
    	String linkedMdbQuery = 
			"PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n" +
			"PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n" +
			"PREFIX movie: <http://data.linkedmdb.org/resource/movie/> \n" +
			"SELECT DISTINCT ?dir ?label ?dir_name WHERE { \n"+
			"  graph $g { \n" +
			"    ?dir rdf:type movie:director. \n" +
			"    ?dir movie:director_name ?dir_name. \n" +
			"    ?dir rdfs:label ?label. \n" +
			"  } \n" +
			"} LIMIT 200";
    	
    	/**
    	 * Make 2 graph processors
    	 */
    	/* make dbpedia SPARQL processor */
    	SPARQLFunction dbpediaSelect = new SPARQLFunction();
    	dbpediaSelect.initialize(Collections.singletonMap("query", dbpediaQuery));
    	
    	dbpediaSelect.requireInputType("g", GraphType.getInstance());
    	FunctionProcessor dbpediaProc = new FunctionProcessor(dbpediaSelect);
    	dbpediaProc.getPort("g").setInputProcessor(new ConstantProcessor(Data.getGraphFromFile("./data/dbpedia.xml")));
    	assertTrue("Should be bag type, but is "+Util.getOutputType(dbpediaProc), Util.getOutputType(dbpediaProc) instanceof BagType);
    	
    	/* make linkedmdb SPARQL processor */
    	SPARQLFunction linkedMdbSelect = new SPARQLFunction();
    	linkedMdbSelect.initialize(Collections.singletonMap("query", linkedMdbQuery));
    	
    	linkedMdbSelect.requireInputType("g", GraphType.getInstance());
    	FunctionProcessor lmdbProc = new FunctionProcessor(linkedMdbSelect);
    	lmdbProc.getPort("g").setInputProcessor(new ConstantProcessor(Data.getGraphFromFile("./data/linkedmdb.xml")));
    	assertTrue("Should be bag type, but is "+Util.getOutputType(lmdbProc), Util.getOutputType(lmdbProc) instanceof BagType);
    	
    	Iterator<RGLValue> lmdbIter = lmdbProc.getResultValue().asBag().iterator();
    	int lmdbCounter = 0;
    	while (lmdbIter.hasNext()){
    		lmdbIter.next();
    		lmdbCounter++;
    	}
    	Iterator<RGLValue> dbpediaIter = dbpediaProc.getResultValue().asBag().iterator();
    	int dbpediaCounter = 0;
    	while (dbpediaIter.hasNext()){
    		RecordValue rec = dbpediaIter.next().asRecord();
    		for (String field : rec.getRange()){
    			assertTrue("The field '"+field+"' is null. ", rec.get(field)!=null);
    		}
    		dbpediaCounter++;
    	}
    	System.out.println("lmdb elements: "+lmdbCounter+", dbpedia elements: "+dbpediaCounter+", product: "+lmdbCounter*dbpediaCounter);
    	
    	
    	//System.out.println(linkedMdbProc.getValueGenerator().getValue());
    	//System.out.println(dbpediaProc.getValueGenerator().getValue());
    	
    	/*************************************************************
    	 * Make a workflow calculating two JaroSim's 
    	 */
    	Workflow silkFlow = new Workflow();
    	String dbpediaPortName = "dbpediaRec";
    	String lmdbPortName = "lmdbRec";
    	
    	
    	/**
    	 * jaro1proc: compare jaro ( linkedMdbRec[dir_name] , dbpediaRec[label] )  
    	 */
    	FunctionProcessor lmdbSelect_dir_name = createProjector("dir_name");
    	silkFlow.addInputReader(lmdbPortName, lmdbSelect_dir_name.getPort(RecordProject.INPUT_NAME));
    	FunctionProcessor dbpediaSelect_label = createProjector("label");
    	silkFlow.addInputReader(dbpediaPortName, dbpediaSelect_label.getPort(RecordProject.INPUT_NAME));
    	
    	FunctionProcessor jaro1proc = new FunctionProcessor(new JaroSimilarityFunction());
    	jaro1proc.getPort(JaroSimilarityFunction.s1).setInputProcessor(lmdbSelect_dir_name);
    	jaro1proc.getPort(JaroSimilarityFunction.s2).setInputProcessor(dbpediaSelect_label);
    	
    	/**
    	 * jaro2proc: compare jaro ( linkedMdbRec[label] , dbpediaRec[label] )  
    	 */
    	FunctionProcessor lmdbSelect_label = createProjector("label");
    	silkFlow.addInputReader(lmdbPortName, lmdbSelect_label.getPort(RecordProject.INPUT_NAME));
    	
    	FunctionProcessor jaro2proc = new FunctionProcessor(new JaroSimilarityFunction());
    	jaro2proc.getPort(JaroSimilarityFunction.s1).setInputProcessor(lmdbSelect_label);
    	jaro2proc.getPort(JaroSimilarityFunction.s2).setInputProcessor(dbpediaSelect_label);
    	
    	/**
    	 * take max value of jaro1proc and jaro2proc
    	 */
    	FunctionProcessor maxProc = new FunctionProcessor(new MaxVal2());
    	maxProc.getPort(MaxVal2.value1).setInputProcessor(jaro1proc);
    	maxProc.getPort(MaxVal2.value2).setInputProcessor(jaro2proc);
    	
    	/**
    	 * Create a tuple <s:mov, p:dbpedia_director_prop, o:dir, probability:max > 
    	 */
    	
    	FunctionProcessor dbpediaSelect_mov = createProjector("mov"); 
    	silkFlow.addInputReader(dbpediaPortName, dbpediaSelect_mov.getPort(RecordProject.INPUT_NAME));
    	FunctionProcessor lmdbSelect_dir = createProjector("dir");
    	silkFlow.addInputReader(lmdbPortName, lmdbSelect_dir.getPort(RecordProject.INPUT_NAME));
    	ConstantProcessor predicateProc = new ConstantProcessor(Engine.getValueFactory().createURI("http://dbpedia.org/ontology/director"));
    	
    	RecordCreate recordCreateFunc = new RecordCreate();
    	recordCreateFunc.requireInput("s");
    	recordCreateFunc.requireInput("p");
    	recordCreateFunc.requireInput("o");
    	recordCreateFunc.requireInput("probability");
    	recordCreateFunc.requireInput("lmdb_dir_name");
    	recordCreateFunc.requireInput("lmdb_label");
    	recordCreateFunc.requireInput("dbpedia_label");
    	
    	FunctionProcessor tupleCreator = new FunctionProcessor(recordCreateFunc);
    	tupleCreator.getPort("s").setInputProcessor(dbpediaSelect_mov); 
    	tupleCreator.getPort("p").setInputProcessor(predicateProc); 
    	tupleCreator.getPort("o").setInputProcessor(lmdbSelect_dir); 
    	tupleCreator.getPort("probability").setInputProcessor(maxProc); 
    	tupleCreator.getPort("lmdb_dir_name").setInputProcessor(lmdbSelect_dir_name); 
    	tupleCreator.getPort("lmdb_label").setInputProcessor(lmdbSelect_label); 
    	tupleCreator.getPort("dbpedia_label").setInputProcessor(dbpediaSelect_label); 
    	
    	silkFlow.setOutputProcessor(tupleCreator);
    	/*************************************************************
    	 * Finished silkFlow, integrate it in bigger network 
    	 */

    	FunctionProcessor silkProc = new FunctionProcessor(silkFlow);
    	silkProc.getPort(dbpediaPortName).setInputProcessor(dbpediaProc);
    	silkProc.getPort(dbpediaPortName).markIteration();
    	silkProc.getPort(lmdbPortName).setInputProcessor(lmdbProc);
    	silkProc.getPort(lmdbPortName).markIteration();
    	
    	
    	Util.getOutputType(silkProc);
    	assertTrue(Util.getOutputType(jaro1proc) instanceof RDFType );
    	assertTrue(Util.getOutputType(jaro2proc) instanceof RDFType );
    	assertTrue(Util.getOutputType(silkProc) instanceof BagType);
    	BagValue bagOfRecords = silkProc.getResultValue().asBag();
    	
    	Iterator<RGLValue> bagIter;
    	
    	int counter=0;
    	bagIter = bagOfRecords.iterator();
    	
    	HashSet<String> actualSet = new HashSet<String>();
    	while (bagIter.hasNext()){
    		RGLValue valGen = bagIter.next();
    		RecordValue rec = valGen.asRecord();
    		
    		/* get name and add it to bag */
    		String name = rec.get("lmdb_dir_name").asLiteral().getRDFNode().asLiteral().getString();
    		actualSet.add(name);
    		
    		if (counter%100000==0){
    			System.out.println(counter);
    		}
    		counter++;
    	}
    	
    	HashSet<String> expectedSet = new HashSet<String>();
    	/* we don't respect the cardinality of the results here, but that'll be checked somewhere else */
    	Iterator<RGLValue> expectedIter = lmdbProc.getResultValue().asBag().iterator();
    	while (expectedIter.hasNext()){
    		RecordValue rec = expectedIter.next().asRecord();
    		String name = rec.get("dir_name").asLiteral().getValueString() ;
    		expectedSet.add(name);
    	}
    	
    	assert(expectedSet.equals(actualSet)): "Something went with the workflow iteration. Is it properly iterating over all the values? ";
    	
       	
    }
    
}
