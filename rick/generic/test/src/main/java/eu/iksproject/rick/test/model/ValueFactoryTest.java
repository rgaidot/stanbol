package eu.iksproject.rick.test.model;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNotSame;
import static junit.framework.Assert.assertNull;
import static junit.framework.Assert.assertTrue;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import org.junit.Test;

import eu.iksproject.rick.servicesapi.model.Reference;
import eu.iksproject.rick.servicesapi.model.Representation;
import eu.iksproject.rick.servicesapi.model.Text;
import eu.iksproject.rick.servicesapi.model.UnsupportedTypeException;
import eu.iksproject.rick.servicesapi.model.ValueFactory;
import eu.iksproject.rick.servicesapi.yard.Yard;

/**
 * General tests for instantiation of model instances by using the different
 * value factory methods.
 * This also tests the construction of implementation of {@link Reference},
 * {@link Text} and {@link Representation}.
 * For the immutable {@link Text} and {@link Reference} this tests are sufficient.
 * For Representations there is an own Test class
 * @author Rupert Westenthaler
 *
 */
public abstract class ValueFactoryTest {
    /**
     * Subclasses must implement this method and provide a {@link ValueFactory}
     * instance that is used to test the actual model implementation
     * @return the {@link ValueFactory} for the RICK model implementation to be
     * tested
     */
    protected abstract ValueFactory getValueFactory();
    /**
     * Returns an instance of a unsupported Type to be parsed to 
     * {@link ValueFactory#createReference(Object)}. Used to check if this
     * Method correctly throws an {@link UnsupportedTypeException}
     * @return an instance of an unsupported type or <code>null</code> if all
     * types are supported
     */
    protected abstract Object getUnsupportedReferenceType();
    /**
     * Returns an instance of a unsupported Type to be parsed to
     * {@link ValueFactory#createText(Object)}. Used to check if this Method
     * correctly throws an {@link UnsupportedTypeException}
     * @return an instance of an unsupported type or <code>null</code> if all
     * types are supported
     */
    protected abstract Object getUnsupportedTextType();
    
    @Test(expected=NullPointerException.class)
    public void testNullReference(){
        testRef(null);
    }
    @Test(expected=UnsupportedTypeException.class)
    public void testUnsupportedReferenceType(){
        Object unsupported = getUnsupportedReferenceType();
        if(unsupported != null){
            testRef(unsupported);
        } else {
            // no unsupported types ... this test is not necessary 
            // -> create a dummy exception
            //TODO: is there a way to deactivate a test if not valid
            throw new UnsupportedTypeException(Object.class, 
                "dummy exception to successfully complete this unnecessary test");
        }
    }
    @Test(expected=IllegalArgumentException.class)
    public void testEmptyStringReference(){
        testRef("");
    }
    @Test
    public void testStringReference(){
        Object refObject = "urn:test.1";
        Reference ref = testRef(refObject);
        assertEquals(ref.getReference(), refObject);
    }
    @Test
    public void testUriReference() throws URISyntaxException {
        URI refObject = new URI("http://www.test.org/uriTest");
        Reference ref = testRef(refObject);
        assertEquals(ref.getReference(), refObject.toString());
    }
    @Test
    public void testURLReference() throws MalformedURLException {
        URL refObject = new URL("http://www.test.org/urlTest");
        Reference ref = testRef(refObject);
        assertEquals(ref.getReference(), refObject.toString());
    }
    @Test(expected=NullPointerException.class)
    public void testNullText(){
        testText(null, "en");
    }
    @Test
    public void testNullLanguage(){
        testText("test", null);
    }
    @Test(expected=UnsupportedTypeException.class)
    public void testUnsupportedTextType(){
        Object unsupported  =getUnsupportedTextType();
        if(unsupported != null){
            getValueFactory().createText(unsupported);
        } else {
            // no unsupported types ... this test is not necessary 
            // -> create a dummy exception
            //TODO: is there a way to deactivate a test if not valid
            throw new UnsupportedTypeException(Object.class, 
                "dummy exception to successfully complete this unnecessary test");
        }
    }
    @Test
    public void testNormalText(){
        testText("test","en");
    }
    /**
     * Some Systems use an empty string for the default language,
     * other use <code>null</code>. Text does currently not define that
     * <code>null</code> need to be used as default language. However it does
     * define that <code>null</code> is a valid value for the language!<p>
     * Based on that RICK allows implementations to convert an empty language to
     * <code>null</code> but does NOT allow to to convert <code>null</code> to
     * an empty string.<p>
     * This test currently assures, that parsing an empty string as language
     * results in an empty string OR <code>null</code>. It also tests that
     * parsing an empty string as language does not result in an Exception.
     */
    @Test
    public void testEmptyLanguageText(){
        testText("test","");
    }
    /**
     * One can not create a Representation with <code>null</code> as ID.
     * NOTE: automatic generation of IDs is supported by the {@link Yard#create()}
     * but not by the {@link Representation} itself.
     */
    @Test(expected=NullPointerException.class)
    public void testNullIdRepresentation(){
        testRepresentation(null);
    }
    /**
     * One can not create a Representation with an emtpy ID
     */
    @Test(expected=IllegalArgumentException.class)
    public void testEmptyIdRepresentation(){
        testRepresentation("");
    }
    @Test
    public void testMultipleInstanceForSameID(){
        Representation rep = testRepresentation("urn:testSameId");
        Representation rep1 = testRepresentation("urn:testSameId");
        //check that multiple calls with the same ID create different instances
        // -> this is very important to allow mapping of Representations (e.g.
        //    when they are stored within a cache
        assertNotSame(rep, rep1);
        //if an ID is parsed, than the two instance should be equal
        assertTrue(rep.equals(rep1));
        assertTrue(rep.hashCode() == rep1.hashCode()); //check the hash code
    }
    
    private Representation testRepresentation(String id){
        ValueFactory vf = getValueFactory();
        Representation rep = vf.createRepresentation(id);
        assertNotNull(rep);
        assertNotNull(rep.getId());
        if(id != null){
            assertEquals(rep.getId(), id);
        }
        return rep;
    }
    /**
     * Internally used to create and text {@link Text}s for the different tests
     * @param textString the natural language text as string
     * @param language the language
     * @return the created {@link Text} instance that can be used to perform
     * further tests.
     */
    private Text testText(String textString,String language){
        ValueFactory vf = getValueFactory();
        Text text = vf.createText(textString, language);
        assertNotNull(text.getText());
        assertNotNull(text.getText());
        assertEquals(text.getText(), textString);
        if(language == null){
            assertTrue(text.getLanguage() == null);
        } else if(language.isEmpty()){
            //implementations are free to change an empty language string to null
            //NOTE that it is not allowed to change NULL to an empty String!
            assertTrue(text.getLanguage() == null || text.getLanguage().isEmpty());
        } else {
            assertNotNull(text.getLanguage());
            assertEquals(text.getLanguage(), language);
        }
        return text;
    }
    /**
     * Internally used to create and test {@link Reference}s for the different tests
     * @param refObject the object representing the reference
     * @return the created {@link Reference} that can be used to perform further
     * tests.
     */
    private Reference testRef(Object refObject){
        ValueFactory vf = getValueFactory();
        Reference ref = vf.createReference(refObject);
        //check not null
        assertNotNull(ref);
        //check reference is not null
        assertNotNull(ref.getReference());
        return ref;
    }

}
