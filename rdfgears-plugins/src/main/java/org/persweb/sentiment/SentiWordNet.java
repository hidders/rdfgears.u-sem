package org.persweb.sentiment;

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
import java.io.File;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;

import nl.tudelft.rdfgears.engine.Config;

/**
 * A functionality that allows for processing SentinWordNet3, adapted from the
 * example given by <a href=""> http://sentiwordnet.isti.cnr.it/code/SWN3.java</a> 
 * 
 * This class doesn't deal with Part-of-Speech tagging.
 * 
 * @author Qi Gao <a href="mailto:q.gao@tudelft.nl">q.gao@tudelft.nl</a>
 * @version created on Oct 10, 2012 2:29:42 PM
 * 
 */
public class SentiWordNet {
	
	private String pathToSWN;
	private HashMap<String, Double> _dict;

	public SentiWordNet() {

		pathToSWN = Config.getLexiconPath();
		if(pathToSWN.endsWith(File.separator)==false) {
			pathToSWN += File.separator;
		}
		pathToSWN += "/SentiWordNet_3.0.0_20120510.txt";
		
		_dict = new HashMap<String, Double>();
		HashMap<String, Vector<Double>> _temp = new HashMap<String, Vector<Double>>();
		try {
			BufferedReader csv = new BufferedReader(new FileReader(pathToSWN));
			String line = "";
			while ((line = csv.readLine()) != null) {
				String[] data = line.split("\t");
				Double score = Double.parseDouble(data[2])
						- Double.parseDouble(data[3]);
				String[] words = data[4].split(" ");
				for (String w : words) {
					String[] w_n = w.split("#");
					//does not consider POS tagging
					// w_n[0] += "#" + data[0];
					int index = Integer.parseInt(w_n[1]) - 1;
					if (_temp.containsKey(w_n[0])) {
						Vector<Double> v = _temp.get(w_n[0]);
						if (index > v.size())
							for (int i = v.size(); i < index; i++)
								v.add(0.0);
						v.add(index, score);
						_temp.put(w_n[0], v);
					} else {
						Vector<Double> v = new Vector<Double>();
						for (int i = 0; i < index; i++)
							v.add(0.0);
						v.add(index, score);
						_temp.put(w_n[0], v);
					}
				}
			}
			Set<String> temp = _temp.keySet();
			for (Iterator<String> iterator = temp.iterator(); iterator
					.hasNext();) {
				String word = (String) iterator.next();
				Vector<Double> v = _temp.get(word);
				double score = 0.0;
				double sum = 0.0;
				for (int i = 0; i < v.size(); i++)
					score += ((double) 1 / (double) (i + 1)) * v.get(i);
				for (int i = 1; i <= v.size(); i++)
					sum += (double) 1 / (double) i;
				score /= sum;
//				String sent = "";
//				if (score >= 0.75)
//					sent = "strong_positive";
//				else if (score > 0.25 && score <= 0.5)
//					sent = "positive";
//				else if (score > 0 && score >= 0.25)
//					sent = "weak_positive";
//				else if (score < 0 && score >= -0.25)
//					sent = "weak_negative";
//				else if (score < -0.25 && score >= -0.5)
//					sent = "negative";
//				else if (score <= -0.75)
//					sent = "strong_negative";
				_dict.put(word, score);
			}
			csv.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public Double extract(String word) {
		return _dict.get(word);
	}
	
	public HashMap<String, Double> getSentiWords() {
		return _dict;
	}
	
	public boolean contain(String word) {
		if(_dict.containsKey(word)) {
			return true;
		}
		return false;
	}
	
	public void overview() {
		Iterator<String> it = _dict.keySet().iterator();
		int numPositive = 0;
		int numNegative = 0;
		double score;
		while(it.hasNext()) {
			score = _dict.get(it.next());
			if(score > 0) {
				numPositive++;
			} else if (score < 0) {
				numNegative++;
			}
		}
		System.out.println("number of positive words" + numPositive);
		System.out.println("number of negative words" + numNegative);
	}
}
