package nl.tudelft.rdfgears.rgl.datamodel.value.impl;

import java.util.Collection;
import java.util.Iterator;

import com.hp.hpl.jena.rdf.model.RDFNode;
import com.sleepycat.bind.tuple.TupleBinding;

import nl.tudelft.rdfgears.engine.bindings.LazyRGLBinding;
import nl.tudelft.rdfgears.rgl.datamodel.value.BagValue;
import nl.tudelft.rdfgears.rgl.datamodel.value.BooleanValue;
import nl.tudelft.rdfgears.rgl.datamodel.value.GraphValue;
import nl.tudelft.rdfgears.rgl.datamodel.value.LiteralValue;
import nl.tudelft.rdfgears.rgl.datamodel.value.RDFValue;
import nl.tudelft.rdfgears.rgl.datamodel.value.RGLValue;
import nl.tudelft.rdfgears.rgl.datamodel.value.RecordValue;
import nl.tudelft.rdfgears.rgl.datamodel.value.StreamingBagValue;
import nl.tudelft.rdfgears.rgl.datamodel.value.URIValue;
import nl.tudelft.rdfgears.rgl.datamodel.value.visitors.RGLValueVisitor;
import nl.tudelft.rdfgears.rgl.function.RGLFunction;
import nl.tudelft.rdfgears.rgl.workflow.FunctionProcessor;
import nl.tudelft.rdfgears.rgl.workflow.InputPort;
import nl.tudelft.rdfgears.rgl.workflow.LazyRGLValue;
import nl.tudelft.rdfgears.rgl.workflow.ValueRowIterator;
import nl.tudelft.rdfgears.util.row.ValueRow;


/**
 * 
 * If a value in the InputRow is null and the associated port of the processor is marked for iteration,
 * iteration is impossible. Then the result of mapping (iteration) is null. 
 * 
 * However, determining the null-ness of the inputs requires evaluating them. This class postpones this 
 * evaluation until the result of the iterated mapping itself is evaluated. 
 *     
 *  
 * @author Eric Feliksik
 *
 */
public class LazyRGLValueMapping extends LazyRGLValue {
	FunctionProcessor proc; 
	public LazyRGLValueMapping(FunctionProcessor proc, ValueRow inputRow) {
		super(proc.getFunction(), inputRow);
		this.proc = proc;
	}

	
	/** 
	 * return actual MappingBagValue iff the iterating ports are non-null;
	 */
	protected RGLValue evaluate() {
		if (cachedResultValue==null){
			assert(inputRow!=null);
			
			RGLValue nullInput = getNullInput();
			if (nullInput!=null){
				/* there is a null input, so we cannot iterate */
				return nullInput; 
			}
			
			cachedResultValue  = new MappingBagValue(proc, inputRow);
			this.inputRow = null; /* allow garbage collection */
			
			if(valueIsReadMultipleTimes) // beware of any evaluating log messages above, which may result in setting this flag too late
				cachedResultValue.prepareForMultipleReadings();	
		}
		
		assert(cachedResultValue!=null);
		return cachedResultValue;
	}
	
	/**
	 * Return the first found RGL-NULL input, if it exists. If there is such a value, 
	 * the mapping result is NULL because we cannot iterate. 
	 * @return
	 */
	private RGLValue getNullInput(){
		for (InputPort port : proc.getPortSet()){
			if (port.iterates()){
				RGLValue inputVal = inputRow.get(port.getName());
				if (inputVal.isNull())
					return inputVal;
			}
		}
		return null;
	}
	
	

	@Override
	public TupleBinding<RGLValue> getBinding() {
		// TODO FIXME 
		assert(false) : "not implemented!";
		throw new RuntimeException("getBinding() not implemented!");
	}

	
	

}


