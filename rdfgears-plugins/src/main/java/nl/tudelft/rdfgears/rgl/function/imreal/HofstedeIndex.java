package nl.tudelft.rdfgears.rgl.function.imreal;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.HashMap;

import nl.tudelft.rdfgears.engine.Config;
import nl.tudelft.rdfgears.engine.ValueFactory;
import nl.tudelft.rdfgears.rgl.datamodel.type.RDFType;
import nl.tudelft.rdfgears.rgl.datamodel.type.RGLType;
import nl.tudelft.rdfgears.rgl.datamodel.value.RGLValue;
import nl.tudelft.rdfgears.rgl.function.SimplyTypedRGLFunction;
import nl.tudelft.rdfgears.util.row.ValueRow;

/**
 * A function that takes as input a country and a dimension (one of pdi, idv,
 * mas, uai, lto) and returns an integer (the value of the dimension)
 * 
 */
public class HofstedeIndex extends SimplyTypedRGLFunction {

	public static final String INPUT_COUNTRY = "country";
	public static final String INPUT_DIMENSION = "dimension";

	private static final String HOFSTEDE_FILE = Config.getWritableDir() + "/hofstede_index";// in
																					// case
																					// the
	// file is
	// moved
	// elsewhere,
	// update
	// the
	// location
	private static final String PDI = "pdi";
	private static final String IDV = "idv";
	private static final String MAS = "mas";
	private static final String UAI = "uai";
	private static final String LTO = "lto";

	/*
	 * the maps contain the dimension values of the Hofstede index
	 */
	public static boolean indexLoaded = false;
	private static HashMap<String, Integer> pdi = new HashMap<String, Integer>();
	private static HashMap<String, Integer> idv = new HashMap<String, Integer>();
	private static HashMap<String, Integer> mas = new HashMap<String, Integer>();
	private static HashMap<String, Integer> uai = new HashMap<String, Integer>();
	private static HashMap<String, Integer> lto = new HashMap<String, Integer>();

	public HofstedeIndex() {
		this.requireInputType(INPUT_COUNTRY, RDFType.getInstance());
		this.requireInputType(INPUT_DIMENSION, RDFType.getInstance());
	}

	public RGLType getOutputType() {
		return RDFType.getInstance();
	}

	private static void readHofstedeFile() {
		if (indexLoaded == true)
			return;

		try {
			BufferedReader br = new BufferedReader(
					new FileReader(HOFSTEDE_FILE));
			String line = null;
			while ((line = br.readLine()) != null) {
				// ignore comments
				if (line.startsWith("#"))
					continue;

				String tokens[] = line.split(",");
				// format: Country PDI IDV MAS UAI LTO
				int ltoVal = 0;
				int uaiVal = 0;
				int masVal = 0;
				int idvVal = 0;
				int pdiVal = 0;

				if (tokens.length > 1)
					ltoVal = Integer.parseInt(tokens[1].trim());
				if (tokens.length > 2)
					uaiVal = Integer.parseInt(tokens[2].trim());
				if (tokens.length > 3)
					masVal = Integer.parseInt(tokens[3].trim());
				if (tokens.length > 4)
					idvVal = Integer.parseInt(tokens[4].trim());
				if (tokens.length > 5)
					pdiVal = Integer.parseInt(tokens[5].trim());

				String country = tokens[0].trim();

				pdi.put(country, pdiVal);
				idv.put(country, idvVal);
				mas.put(country, masVal);
				uai.put(country, uaiVal);
				lto.put(country, ltoVal);
			}
			br.close();
			indexLoaded = true;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public RGLValue simpleExecute(ValueRow inputRow) {
		/*
		 * - typechecking guarantees it is an RDFType - simpleExecute guarantees
		 * it is non-null SanityCheck: we must still check whether it is URI or
		 * String, because typechecking doesn't distinguish this!
		 */
		RGLValue rdfValueCountry = inputRow.get(INPUT_COUNTRY);
		if (!rdfValueCountry.isLiteral())
			return ValueFactory.createNull("Cannot handle URI input in "
					+ getFullName());

		// we are happy, value can be safely cast with .asLiteral().
		String country = rdfValueCountry.asLiteral().getValueString();

		RGLValue rdfValueDimension = inputRow.get(INPUT_DIMENSION);
		if (!rdfValueDimension.isLiteral())
			return ValueFactory.createNull("Cannot handle URI input in "
					+ getFullName());

		String dimension = rdfValueDimension.asLiteral().getValueString();

		if (indexLoaded == false)
			readHofstedeFile();

		HashMap<String, Integer> map = null;
		if (dimension.equals(PDI))
			map = pdi;
		else if (dimension.equals(IDV))
			map = idv;
		else if (dimension.equals(MAS))
			map = mas;
		else if (dimension.equals(UAI))
			map = uai;
		else if (dimension.equals(LTO))
			map = lto;

		if (map != null && map.containsKey(country)) {
			int d = map.get(country);
			return ValueFactory.createLiteralDouble(d);
		}

		return ValueFactory.createLiteralDouble(-1.0);
	}
}
