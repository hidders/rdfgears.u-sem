/*********************************************************
 *  Copyright (c) 2011 by Web Information Systems (WIS) Group, 
 *  Delft University of Technology.
 *  Qi Gao, http://wis.ewi.tudelft.nl/index.php/home-qi-gao
 *  
 *  Some rights reserved.
 *
 *  Contact: q.gao@tudelft.nl
 *
 **********************************************************/
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

/**
 * @author Qi Gao <a href="mailto:q.gao@tudelft.nl">q.gao@tudelft.nl</a>
 * @version created on May 10, 2012 3:02:07 PM
 */
public class SentiWord {

	private int id = 0;
	private String term = null;
	private double positiveScore = 0.0;
	private double negativeScore = 0.0;
	private String posTag = null;
	
	
	/**
	 * @param term
	 * @param positiveScore
	 * @param negativeScore
	 */
	public SentiWord(String term, double positiveScore, double negativeScore) {
		super();
		this.term = term;
		this.positiveScore = positiveScore;
		this.negativeScore = negativeScore;
	}
	/**
	 * @param id
	 * @param term
	 * @param positiveScore
	 * @param negativeScore
	 * @param posTag
	 */
	public SentiWord(int id, String term, double positiveScore,
			double negativeScore, String posTag) {
		super();
		this.id = id;
		this.term = term;
		this.positiveScore = positiveScore;
		this.negativeScore = negativeScore;
		this.posTag = posTag;
	}
	/**
	 * @return the id
	 */
	public int getId() {
		return id;
	}
	/**
	 * @param id the id to set
	 */
	public void setId(int id) {
		this.id = id;
	}
	/**
	 * @return the term
	 */
	public String getTerm() {
		return term;
	}
	/**
	 * @param term the term to set
	 */
	public void setTerm(String term) {
		this.term = term;
	}
	/**
	 * @return the positiveScore
	 */
	public double getPositiveScore() {
		return positiveScore;
	}
	/**
	 * @param positiveScore the positiveScore to set
	 */
	public void setPositiveScore(double positiveScore) {
		this.positiveScore = positiveScore;
	}
	/**
	 * @return the negativeScore
	 */
	public double getNegativeScore() {
		return negativeScore;
	}
	/**
	 * @param negativeScore the negativeScore to set
	 */
	public void setNegativeScore(double negativeScore) {
		this.negativeScore = negativeScore;
	}
	/**
	 * @return the posTag
	 */
	public String getPosTag() {
		return posTag;
	}
	/**
	 * @param posTag the posTag to set
	 */
	public void setPosTag(String posTag) {
		this.posTag = posTag;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "SentiWord [term=" + term + ", positiveScore=" + positiveScore
				+ ", negativeScore=" + negativeScore + "]";
	}
	
	
}
