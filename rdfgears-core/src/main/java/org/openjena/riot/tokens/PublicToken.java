package org.openjena.riot.tokens;

/**
 * A class to provide public tokens; the construtors in Jena are private. 
 * 
 * @author Eric Feliksik
 *
 */
public class PublicToken {
	public static Token create(String s){
		return Token.create(s);
	}
}
