package pers.aprakash.spanqit.rdf.adapter;

import org.openrdf.model.BNode;
import org.openrdf.model.Literal;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.impl.ValueFactoryImpl;

import com.anqit.spanqit.rdf.Iri;
import com.anqit.spanqit.rdf.RdfResource;
import com.anqit.spanqit.rdf.RdfValue;

/**
 * An crude example of an adapter class to create Spanqit RDF elements from 
 * OpenRdf ones.
 * <p>
 * 
 * @author Ankit
 *
 */
public class OpenRdfAdapter {
	private static ValueFactory vf =  new ValueFactoryImpl();
	
	public static Iri iri(String uri) {
		return OpenRdfAdapter.iri(vf.createURI(uri));
	}
	
	public static Iri iri(String ns, String localName) {
		return OpenRdfAdapter.iri(vf.createURI(ns, localName));
	}
	
	public static RdfValue blankNode() {
		return value(vf.createBNode());
	}
	
	public static RdfValue literal(int num) {
		return value(vf.createLiteral(num));
	}
	
	public static RdfValue literal(double num) {
		return value(vf.createLiteral(num));
	}
	
	public static RdfValue literal(String label) {
		return value(vf.createLiteral(label));
	}

	public static RdfValue literalWithLangTag(String label, String lang) {
		return value(vf.createLiteral(label, lang));
	}

	public static RdfValue literalWithDatatype(String label, String datatype) {
		return value(vf.createLiteral(label, vf.createURI(datatype)));
	}

	public static RdfValue literalWithDatatype(String label, String ns, String datatype) {
		return value(vf.createLiteral(label, vf.createURI(ns, datatype)));
	}
	
	public static RdfValue value(final org.openrdf.model.Value value) {
		return new RdfValue() {
			@Override
			public String getQueryString() {
				return queryString(value);
			}
		};
	}

	public static RdfResource resource(final org.openrdf.model.Resource resource) {
		return new RdfResource() {
			@Override
			public String getQueryString() {
				return queryString(resource);
			}
		};
	}

	public static Iri iri(final org.openrdf.model.URI uri) {
		return new Iri() {
			@Override
			public String getQueryString() {
				return queryString(uri);
			}
		};
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