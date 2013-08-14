package nl.tudelft.rdfgears.engine.valuefactory;
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
