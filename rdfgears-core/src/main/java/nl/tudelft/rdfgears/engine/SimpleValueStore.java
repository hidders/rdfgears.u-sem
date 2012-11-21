package nl.tudelft.rdfgears.engine;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.UUID;

import javax.xml.stream.XMLStreamException;

import nl.tudelft.rdfgears.rgl.datamodel.type.GraphType;
import nl.tudelft.rdfgears.rgl.datamodel.type.TypedValue;
import nl.tudelft.rdfgears.rgl.datamodel.type.TypedValueImpl;
import nl.tudelft.rdfgears.rgl.datamodel.value.GraphValue;
import nl.tudelft.rdfgears.rgl.datamodel.value.serialization.rglxml.RGLXMLParser;
import nl.tudelft.rdfgears.rgl.datamodel.value.serialization.rglxml.ValueXMLSerializer;

import com.hp.hpl.jena.rdf.arp.JenaReader;
import com.hp.hpl.jena.rdf.model.Model;


/**
 * A valuestore to which RGL values can be written and read from. 
 * 
 * 
 * @author Eric Feliksik
 *
 */
public class SimpleValueStore {
	
	private int valueCounter = 1;
	private final static String STORAGE_PATH = Config.getWritableDir()+"/valuestore/";
	
	public SimpleValueStore(){
	}

	/**
	 * 
	 * @param id
	 * @return
	 * @throws IOException 
	 */
	public TypedValue getValue(String id) throws IOException{
		
		String fileName = STORAGE_PATH+id;
		FileInputStream fin = new FileInputStream(fileName);
		
		try {
			RGLXMLParser parser = new RGLXMLParser(fin);
			return parser;
			
		} catch (XMLStreamException e) {
			/* maybe it was RDF/XML, not RGL/XML. Try a generic Jena reader */
			
			System.err.println("Could not read file "+fileName+" as RGL/XML. Trying as RDF/XML"); 
			e.printStackTrace();
			try {
			
				JenaReader rdfReader = new JenaReader();
				FileInputStream fin2 = new FileInputStream(STORAGE_PATH+id);
				Model model = ValueFactory.createModel();
				rdfReader.read(model, fin2, null);
				GraphValue graph = ValueFactory.createGraphValue(model);
				return new TypedValueImpl(GraphType.getInstance(), graph);
					
			} catch (Exception e2){
				throw new IOException("Could not read file "+fileName+" as RDF/XML: "+e.getMessage());	
			}
			
		} finally {
			fin.close();
		}
	}
	

	/**
	 * store a value
	 * @param value The value to store
	 * @param id the id to use. If null, a new id will be generated.  
	 * @return the id under which the value was stored
	 * @throws IOException 
	 */
	public String putValue(TypedValueImpl typedValue, String id) throws IOException {
		/* mkdir, if it does not yet exist */
		File file = new File(STORAGE_PATH);
		file.mkdirs();
		
		if (id==null){
			id = getNewId();
		}
		
		FileOutputStream fout;
		fout = new FileOutputStream(STORAGE_PATH+id);
//		(new ValueXMLSerializerWithExternalGraphs(fout, STORAGE_PATH)).serialize(typedValue.getValue());
		(new ValueXMLSerializer(fout)).serialize(typedValue.getValue());
		fout.close();
		return id; 
	}

	private String getNewId() {
		return UUID.randomUUID().toString();
	}
}
