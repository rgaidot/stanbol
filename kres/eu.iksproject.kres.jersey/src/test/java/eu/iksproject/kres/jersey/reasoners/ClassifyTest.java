/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package eu.iksproject.kres.jersey.reasoners;

import java.util.Iterator;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.apibinding.OWLManager;
import com.sun.jersey.api.client.ClientResponse;
import javax.ws.rs.core.MediaType;
import com.sun.jersey.api.representation.Form;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.Client;
import eu.iksproject.kres.jersey.JettyServer;
import java.io.File;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;

/**
 *
 * @author elvio
 */
public class ClassifyTest {

    public ClassifyTest() {
    }

    public static final int __PORT = 9999;
    public static final String __TEST_URI = "http://localhost:" + __PORT + "/";
    public static final String __ROOT_URI = __TEST_URI + "classify";
    private static JettyServer server;
    private Client client;
    private WebResource webres;

    @BeforeClass
    public static void setUpClass() throws Exception {
        server = new JettyServer();
        server.start(__TEST_URI);
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
        server.stop();
    }

    @Before
    public void setUp() {
        //RuleStore store = new KReSRuleStore();
        //server.setAttribute("kresRuleStore", store);

        client = Client.create();
	webres = client.resource(__ROOT_URI);

    }

    @After
    public void tearDown() {
    }

    /**
     * Test of getClassify method, of class Classify.
     */
    @Test
    public void testOntologyClassify() throws OWLOntologyCreationException {
        Form form = new Form();
        File inputfile = new File("./src/main/resources/TestFile/ProvaParent.owl");
        String scopeiri = "http://150.146.88.63:9090/kres/ontology/User";
        String recipeiri ="http://localhost:9999/recipe/http://kres.iks-project.eu/ontology/meta/rmi_config.owl%23ProvaParentRecipe";

        //form.add("scope", scopeiri);
        form.add("recipe", recipeiri);
        form.add("file", inputfile);

        ClientResponse response = webres.type(MediaType.MULTIPART_FORM_DATA).post(ClientResponse.class,form);

        System.out.println(response);
        if(response.getStatus()==200){
        OWLOntology model = OWLManager.createOWLOntologyManager().loadOntologyFromOntologyDocument(response.getEntityInputStream());
        Iterator<OWLAxiom> ax = model.getAxioms().iterator();
        System.out.println("AXIOM COUNT:"+model.getAxiomCount());
        while(ax.hasNext())
            System.out.println(ax.next());

            assertEquals(200,response.getStatus());
        }else
            fail("Some errors occurred");
    }

    /**
     * Test of getConsistencyCheckViaURL method, of class Classify.
     */
    @Test
    public void testOntologyClassifyViaURL() throws OWLOntologyCreationException {
        Form form = new Form();
        String inputiri = "http://www.ontologydesignpatterns.org/cp/owl/agentrole.owl";
        String scopeiri = "http://150.146.88.63:9090/kres/ontology/User";
        String recipeiri ="http://localhost:9999/recipe/http://kres.iks-project.eu/ontology/meta/rmi_config.owl%23ProvaParentRecipe";

        //form.add("scope", scopeiri);
        form.add("recipe",recipeiri);
        form.add("input-graph", inputiri);

        ClientResponse response = webres.type(MediaType.MULTIPART_FORM_DATA).post(ClientResponse.class,form);
        
        System.out.println(response);
        if(response.getStatus()==200){
        OWLOntology model = OWLManager.createOWLOntologyManager().loadOntologyFromOntologyDocument(response.getEntityInputStream());
        Iterator<OWLAxiom> ax = model.getAxioms().iterator();
        System.out.println("AXIOM COUNT:"+model.getAxiomCount());
        while(ax.hasNext())
            System.out.println(ax.next());

            assertEquals(200,response.getStatus());
        }else
            fail("Some errors occurred");
    }

}