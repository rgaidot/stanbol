package eu.iksproject.kres.rules.atoms;

import java.net.URI;


import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLTypedLiteral;
import org.semanticweb.owlapi.model.SWRLArgument;
import org.semanticweb.owlapi.model.SWRLAtom;
import org.semanticweb.owlapi.model.SWRLDArgument;
import org.semanticweb.owlapi.model.SWRLIArgument;
import org.semanticweb.owlapi.model.SWRLLiteralArgument;
import org.semanticweb.owlapi.model.SWRLVariable;


import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.SWRLAtom;


import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Resource;

import eu.iksproject.kres.api.rules.KReSRuleAtom;
import eu.iksproject.kres.api.rules.URIResource;
import eu.iksproject.kres.ontologies.SWRL;

public class DatavaluedPropertyAtom implements KReSRuleAtom {

	private URIResource datatypeProperty;
	private URIResource argument1;
	private Object argument2;
	
	
	public DatavaluedPropertyAtom(URIResource datatypeProperty, URIResource argument1, Object argument2) {
		this.datatypeProperty = datatypeProperty;
		this.argument1 = argument1;
		this.argument2 = argument2;
	}
	
	@Override
	public String toSPARQL() {
		String arg1 = argument1.toString();
		String arg2 = argument2.toString();
		String dtP = datatypeProperty.toString();
		
		if(arg1.startsWith("http://kres.iks-project.eu/ontology/meta/variables#")){
			arg1 = "?"+arg1.replace("http://kres.iks-project.eu/ontology/meta/variables#", ""); 
		}
		else{
			arg1 = "<"+arg1+">";
		}
		
		if(dtP.startsWith("http://kres.iks-project.eu/ontology/meta/variables#")){
			dtP = "?"+dtP.replace("http://kres.iks-project.eu/ontology/meta/variables#", ""); 
		}
		else{
			dtP = "<"+dtP+">";
		}
		
		if(arg2.startsWith("http://kres.iks-project.eu/ontology/meta/variables#")){
			arg2 = "?"+arg2.replace("http://kres.iks-project.eu/ontology/meta/variables#", "");
			
			return arg1+" "+dtP+" "+arg2;
		}
		else{
			OWLLiteral literal = getOWLTypedLiteral(argument2);
			
			return arg1+" "+dtP+" "+literal.getLiteral();
		}
		
		
	}

	@Override
	public Resource toSWRL(Model model) {
		Resource datavaluedPropertyAtom = model.createResource(SWRL.DatavaluedPropertyAtom);
		
		Resource datatypePropertyResource = datatypeProperty.createJenaResource(model);
		Resource argument1Resource = argument1.createJenaResource(model);
		Literal argument2Literal = model.createTypedLiteral(argument2);
		
		datavaluedPropertyAtom.addProperty(SWRL.propertyPredicate, datatypePropertyResource);
		datavaluedPropertyAtom.addProperty(SWRL.argument1, argument1Resource);
		datavaluedPropertyAtom.addLiteral(SWRL.argument2, argument2Literal);
		
		return datavaluedPropertyAtom;
	}
	
	@Override
	public SWRLAtom toSWRL(OWLDataFactory factory) {
		OWLDataProperty owlDataProperty = factory.getOWLDataProperty(IRI.create(datatypeProperty.getURI().toString()));
		
		SWRLIArgument swrliArgument1;
		SWRLDArgument swrliArgument2;
		
		OWLIndividual owlIndividual = factory.getOWLNamedIndividual(IRI.create(argument1.getURI().toString()));
		swrliArgument1 = factory.getSWRLIndividualArgument(owlIndividual);
		
		
		swrliArgument2 =  getSWRLTypedLiteral(factory, argument2);
		
		
		
		return factory.getSWRLDataPropertyAtom(owlDataProperty, swrliArgument1, swrliArgument2);
	}
	
	public URIResource getDatatypeProperty() {
		return datatypeProperty;
	}
	
	public URIResource getArgument1() {
		return argument1;
	} 
	
	public Object getArgument2() {
		return argument2;
	}

	
	@Override
	public String toString() {
		return "Individual "+argument1.toString() + " has datatype property "+argument1.toString()+" with value "+argument2.toString();
	}
	
	private SWRLLiteralArgument getSWRLTypedLiteral(OWLDataFactory factory, Object argument){
		
		OWLLiteral owlLiteral;
		if(argument instanceof String){
			owlLiteral = factory.getOWLTypedLiteral((String) argument); 
		}
		else if(argument instanceof Integer){
			owlLiteral = factory.getOWLTypedLiteral(((Integer) argument).intValue());
		}
		else if(argument instanceof Double){
			owlLiteral = factory.getOWLTypedLiteral(((Double) argument).doubleValue());
		}
		else if(argument instanceof Float){
			owlLiteral = factory.getOWLTypedLiteral(((Float) argument).floatValue());
		}
		else if(argument instanceof Boolean){
			owlLiteral = factory.getOWLTypedLiteral(((Boolean) argument).booleanValue());
		}
		else{
			owlLiteral = factory.getOWLStringLiteral(argument.toString());
		}
		
		return factory.getSWRLLiteralArgument(owlLiteral); 
	}
	
	private OWLLiteral getOWLTypedLiteral(Object argument){
		
		OWLDataFactory factory = OWLManager.createOWLOntologyManager().getOWLDataFactory();
		
		OWLLiteral owlLiteral;
		if(argument instanceof String){
			owlLiteral = factory.getOWLTypedLiteral((String) argument); 
		}
		else if(argument instanceof Integer){
			owlLiteral = factory.getOWLTypedLiteral(((Integer) argument).intValue());
		}
		else if(argument instanceof Double){
			owlLiteral = factory.getOWLTypedLiteral(((Double) argument).doubleValue());
		}
		else if(argument instanceof Float){
			owlLiteral = factory.getOWLTypedLiteral(((Float) argument).floatValue());
		}
		else if(argument instanceof Boolean){
			owlLiteral = factory.getOWLTypedLiteral(((Boolean) argument).booleanValue());
		}
		else{
			owlLiteral = factory.getOWLStringLiteral(argument.toString());
		}
		
		
		
		return owlLiteral; 
	}


	@Override
	public String toKReSSyntax(){
		String arg1 = null;
		String arg2 = null;
		String arg3 = null;
		
		if(argument1.toString().startsWith("http://kres.iks-project.eu/ontology/meta/variables#")){
			arg1 = "?"+argument1.toString().replace("http://kres.iks-project.eu/ontology/meta/variables#", "");
		}
		else{
			arg1 = "<"+argument1.toString()+">";
		}
		
		
		if(datatypeProperty.toString().startsWith("http://kres.iks-project.eu/ontology/meta/variables#")){
			arg3 = "?"+datatypeProperty.toString().replace("http://kres.iks-project.eu/ontology/meta/variables#", "");
		}
		else{
			arg3 = "<"+datatypeProperty.toString()+">";
		}
		
		if(argument2.toString().startsWith("http://kres.iks-project.eu/ontology/meta/variables#")){
			arg2 = "?"+argument2.toString().replace("http://kres.iks-project.eu/ontology/meta/variables#", "");
			return "values(" + arg3 + ", " + arg1 + ", " + arg2 +")";
		}
		else{
			OWLLiteral literal = getOWLTypedLiteral(argument2);
			
			return "values(" + arg3 + ", " + arg1 + ", " + literal.getLiteral() +")";
		}
		
		
		
		
	}

}
