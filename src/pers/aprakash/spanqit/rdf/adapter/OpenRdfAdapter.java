package pers.aprakash.spanqit.rdf.adapter;

import org.openrdf.model.BNode;
import org.openrdf.model.Literal;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.impl.ValueFactoryImpl;

import com.anqit.spanqit.rdf.Iri;
import com.anqit.spanqit.rdf.RdfLiteral;
import com.anqit.spanqit.rdf.RdfLiteral.StringLiteral;
import com.anqit.spanqit.rdf.RdfResource;
import com.anqit.spanqit.rdf.RdfValue;

/**
 * An crude example of an adapter class to create Spanqit RDF elements from 
 * OpenRdf ones.
 */
public class OpenRdfAdapter {
	private static ValueFactory vf =  new ValueFactoryImpl();
	
	/**
	 * create a Spanqit iri from a URI
	 * 
	 * @param uri 
	 * @return a Spanqit Iri instance
	 */
	public static Iri iri(String uri) {
		return iri(vf.createURI(uri));
	}
	
	/**
	 * create a Spanqit iri from a namespace and local name
	 * 
	 * @param ns the namespace
	 * @param localName
	 * 
	 * @return  a Spanqit Iri instance
	 */
	public static Iri iri(String ns, String localName) {
		return iri(vf.createURI(ns, localName));
	}

	/**
	 * create a literal with a language tag
	 * 
	 * @param label the literal string
	 * @param lang the language tag
	 * 
	 * @return a Spanqit StringLiteral
	 */
	public static StringLiteral literalWithLangTag(String label, String lang) {
		return RdfLiteral.of(queryString(vf.createLiteral(label, lang)));
	}

	/**
	 * create a literal with a datatype
	 * 
	 * @param label the literal string
	 * @param datatype the datatype tag
	 * 
	 * @return a Spanqit StringLiteral
	 */
	public static StringLiteral literalWithDatatype(String label, String datatype) {
		return RdfLiteral.of(queryString(vf.createLiteral(label, vf.createURI(datatype))));
	}

	/**
	 * create a literal with a datatype
	 * 
	 * @param label the literal string
	 * @param ns the namespace of the datatype iri
	 * @param datatype the local name of the datatype tag
	 * 
	 * @return a Spanqit StringLiteral
	 */
	public static StringLiteral literalWithDatatype(String label, String ns, String datatype) {
		return RdfLiteral.of(queryString(vf.createLiteral(label, vf.createURI(ns, datatype))));
	}
	
	/**
	 * convert an OpenRdf Value into a Spanqit RdfValue
	 * 
	 * @param value the OpenRdf Value instance to convert
	 * 
	 * @return a Spanqit RdfValue instance
	 */
	public static RdfValue value(final org.openrdf.model.Value value) {
		return () -> queryString(value);
	}

	/**
	 * convert an OpenRdf Resource into a Spanqit RdfResource
	 * 
	 * @param resource the OpenRdf Resource instance to convert
	 * 
	 * @return a Spanqit RdfResource instance
	 */
	public static RdfResource resource(final org.openrdf.model.Resource resource) {
		return () -> queryString(resource);
	}

	/**
	 * convert an OpenRdf URI into a Spanqit Iri
	 * 
	 * @param uri the OpenRdf URI instance to convert
	 * 
	 * @return a Spanqit Iri instance
	 */
	public static Iri iri(final org.openrdf.model.URI uri) {
		return () -> queryString(uri);
	}

	private static String queryString(org.openrdf.model.Value value) {
		if (value instanceof Literal) {
			Literal literal = (Literal) value;

			StringBuilder queryString = new StringBuilder();

			queryString.append('"').append(literal.getLabel()).append("\"");
			if (literal.getDatatype() != null) {
				// probably should add a check for built-in datatypes, and
				// ignore adding tags for those
				queryString.append("^^").append(
						queryString(literal.getDatatype()));
			}
			if (literal.getLanguage() != null) {
				queryString.append("@").append(literal.getLanguage());
			}

			return queryString.toString();
		}
		if (value instanceof org.openrdf.model.Resource) {
			if (value instanceof BNode) {
				return "_:" + ((BNode) value).getID();
			} else {
				// TODO: handle prefixed URI's
				return "<" + ((org.openrdf.model.Resource) value).stringValue()
						+ ">";
			}
		}

		return null;
	}
}