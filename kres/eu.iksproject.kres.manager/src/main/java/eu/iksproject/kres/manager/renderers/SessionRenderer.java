package eu.iksproject.kres.manager.renderers;

import java.util.LinkedList;
import java.util.List;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.io.RDFXMLOntologyFormat;
import org.semanticweb.owlapi.io.StringDocumentTarget;
import org.semanticweb.owlapi.model.AddAxiom;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLDatatype;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyChange;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;
import org.semanticweb.owlapi.model.OWLTypedLiteral;
import org.slf4j.LoggerFactory;

import eu.iksproject.kres.api.manager.session.KReSSession;

public class SessionRenderer {

	private static OWLDataFactory __factory = OWLManager.getOWLDataFactory();

	private static final IRI _sessionIri = IRI
			.create("http://kres.iks-project.eu/ontology/onm/meta.owl#Session");

	private static final IRI _hasIdIri = IRI
			.create("http://kres.iks-project.eu/ontology/onm/meta.owl#hasID");

	private static OWLClass cSession = __factory.getOWLClass(_sessionIri);

	private static OWLDataProperty hasId = __factory
			.getOWLDataProperty(_hasIdIri);

	static {

	}

	public static String getSessionMetadataRDF(KReSSession session) {
		OWLOntologyManager mgr = OWLManager.createOWLOntologyManager();
		OWLOntology ont = null;
		try {
			ont = mgr.createOntology(IRI.create(session.getID() + "/meta.owl"));
		} catch (OWLOntologyCreationException e) {
			LoggerFactory
					.getLogger(ScopeSetRenderer.class)
					.error(
							"KReS :: could not create empty ontology for rendering sesion metadata.",
							e);
			return null;
		}

		List<OWLOntologyChange> additions = new LinkedList<OWLOntologyChange>();

		OWLNamedIndividual iSes = __factory.getOWLNamedIndividual(session
				.getID());
		additions.add(new AddAxiom(ont, __factory.getOWLClassAssertionAxiom(
				cSession, iSes)));
		OWLDatatype anyURI = __factory.getOWLDatatype(IRI
				.create("http://www.w3.org/2001/XMLSchema#anyURI"));
		OWLTypedLiteral hasIdValue = __factory.getOWLTypedLiteral(session
				.getID().toString(), anyURI);
		additions.add(new AddAxiom(ont, __factory
				.getOWLDataPropertyAssertionAxiom(hasId, iSes, hasIdValue)));
		mgr.applyChanges(additions);

		StringDocumentTarget tgt = new StringDocumentTarget();
		try {
			mgr.saveOntology(ont, new RDFXMLOntologyFormat(), tgt);
		} catch (OWLOntologyStorageException e) {
			LoggerFactory.getLogger(ScopeSetRenderer.class).error(
					"KReS :: could not save session metadata ontology.", e);
		}
		return tgt.toString();

	}

}
