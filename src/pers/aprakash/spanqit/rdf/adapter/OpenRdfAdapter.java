package pers.aprakash.spanqit.rdf.adapter;

import org.openrdf.model.BNode;
import org.openrdf.model.Literal;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.impl.ValueFactoryImpl;

import com.anqit.spanqit.rdf.IRI;
import com.anqit.spanqit.rdf.Resource;
import com.anqit.spanqit.rdf.Value;

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
	
	public static IRI iri(String uri) {
		return OpenRdfAdapter.iri(vf.createURI(uri));
	}
	
	public static IRI iri(String ns, String localName) {
		return OpenRdfAdapter.iri(vf.createURI(ns, localName));
	}
	
	public static Value blankNode() {
		return value(vf.createBNode());
	}
	
	public static Value literal(int num) {
		return value(vf.createLiteral(num));
	}
	
	public static Value literal(double num) {
		return value(vf.createLiteral(num));
	}
	
	public static Value literal(String label) {
		return value(vf.createLiteral(label));
	}

	public static Value literalWithLangTag(String label, String lang) {
		return value(vf.createLiteral(label, lang));
	}

	public static Value literalWithDatatype(String label, String datatype) {
		return value(vf.createLiteral(label, vf.createURI(datatype)));
	}

	public static Value literalWithDatatype(String label, String ns, String datatype) {
		return value(vf.createLiteral(label, vf.createURI(ns, datatype)));
	}
	
	public static Value value(final org.openrdf.model.Value value) {
		return new Value() {
			@Override
			public String getQueryString() {
				return queryString(value);
			}
		};
	}

	public static Resource resource(final org.openrdf.model.Resource resource) {
		return new Resource() {
			@Override
			public String getQueryString() {
				return queryString(resource);
			}
		};
	}

	public static IRI iri(final org.openrdf.model.URI uri) {
		return new IRI() {
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