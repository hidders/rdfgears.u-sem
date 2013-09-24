package nl.tudelft.rdfgears.rgl.function.imreal;

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

import java.io.BufferedReader;
import nl.tudelft.rdfgears.engine.Config;
import java.io.FileReader;
import java.util.HashMap;

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

	private static final String HOFSTEDE_FILE = Config.getHofstedePath() +"/hofstede_index";
	
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

				String tokens[] = line.split("\\s+");
				
				// format: Country PDI IDV MAS UAI LTO
				int ltoVal = -1;//some countries don't have a value for LTO
				int uaiVal = 0;
				int masVal = 0;
				int idvVal = 0;
				int pdiVal = 0;
				
				//are we dealing with 4 or 5 numbers?
				int num=0;
				for(int i=tokens.length-1; i>=0; i--)
				{
					try
					{
						Integer.parseInt(tokens[i]);
						num++;
					}
					catch(NumberFormatException nfe){break;}
				}

				int indexCounter=tokens.length-1;
				if(num==5)
					ltoVal = Integer.parseInt(tokens[indexCounter--].trim());
				
				uaiVal = Integer.parseInt(tokens[indexCounter--].trim());
				masVal = Integer.parseInt(tokens[indexCounter--].trim());
				idvVal = Integer.parseInt(tokens[indexCounter--].trim());
				pdiVal = Integer.parseInt(tokens[indexCounter--].trim());

				StringBuilder sb = new StringBuilder();
				for(int i=0; i<=indexCounter; i++)
				{
					if(i>0)
						sb.append(" ");
					sb.append(tokens[i]);
				}
				String country = sb.toString().trim();

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

		String dimension = rdfValueDimension.asLiteral().getValueString().toLowerCase();

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
