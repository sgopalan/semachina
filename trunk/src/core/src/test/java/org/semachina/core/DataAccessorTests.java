package org.semachina.core;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Date;

import org.junit.BeforeClass;
import org.junit.Test;
import org.semachina.jena.core.OWLFactory;
import org.semachina.jena.core.OWLModel;
import org.semachina.xml.types.Base64;
import org.semachina.xml.types.Day;

import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.vocabulary.XSD;


public class DataAccessorTests {

	private static OWLModel _owlModel;
	
	@BeforeClass
	public static void setup() {
		_owlModel = OWLFactory.createOWLModel();
	}

	@Test
	public void testBase64() throws Exception {
	
		byte[] bytes = getClass().toString().getBytes();
		
		Base64 value = new Base64( bytes );
		
		Literal literal = 
			_owlModel.createTypedLiteral(value, XSD.base64Binary.getURI() );
		
		assertNotNull( literal );
		
		assertTrue( literal.toString().endsWith( "^^http://www.w3.org/2001/XMLSchema#base64Binary" ) );
		
		assertEquals( literal.getValue(), value );
	}
	
	@Test
	public void testBooleanTrue() throws Exception {
	
		Boolean value = Boolean.TRUE;
		
		Literal literal = 
			_owlModel.createTypedLiteral(value, XSD.xboolean.getURI() );
		
		assertNotNull( literal );
		
		assertEquals( literal.toString(), "true^^http://www.w3.org/2001/XMLSchema#boolean" );
		
		assertEquals( literal.getValue(), Boolean.TRUE );
	}
	
	@Test
	public void testBooleanFalse() throws Exception {
		
		Boolean value = Boolean.FALSE;
		
		Literal literal = 
			_owlModel.createTypedLiteral(value, XSD.xboolean.getURI() );
		
		assertNotNull( literal );
		
		assertEquals( literal.toString(), "false^^http://www.w3.org/2001/XMLSchema#boolean" );
		
		assertEquals( literal.getValue(), Boolean.FALSE );
	}
	
	@Test
	public void testByte() throws Exception {
		
		Byte value = Byte.MAX_VALUE;
		
		Literal literal = 
			_owlModel.createTypedLiteral(value, XSD.xbyte.getURI() );
		
		assertNotNull( literal );
		
		assertTrue( literal.toString().endsWith( "^^http://www.w3.org/2001/XMLSchema#byte" ) );
		
		assertEquals( literal.getValue(), Byte.MAX_VALUE );
	}
	
	@Test
	public void testDate() throws Exception {
		
		Date value = new Date();
		
		Literal literal = 
			_owlModel.createTypedLiteral(value, XSD.date.getURI() );
		
		assertNotNull( literal );
		
		assertTrue( literal.toString().endsWith( "^^http://www.w3.org/2001/XMLSchema#date" ) );
		
		assertEquals( literal.getValue(), value );
	}
	
	@Test
	public void testDateTime() throws Exception {
		
		Date value = new Date();
		
		Literal literal = 
			_owlModel.createTypedLiteral(value, XSD.dateTime.getURI() );
		
		assertNotNull( literal );
		
		assertTrue( literal.toString().endsWith( "^^http://www.w3.org/2001/XMLSchema#dateTime" ) );
		
		assertEquals( literal.getValue(), value );
		
		Literal typed = _owlModel.parseLiteral( literal.toString() );
		
		assertEquals( value, typed.getValue());
	}
	
	@Test
	public void testDay() throws Exception {
		
		Day value = new Day( 1 );
		
		Literal literal = 
			_owlModel.createTypedLiteral(value, XSD.gDay.getURI() );
		
		assertNotNull( literal );
		
		assertTrue( literal.toString().endsWith( "^^http://www.w3.org/2001/XMLSchema#gDay" ) );
		
		assertEquals( literal.getValue(), value );
		
		Literal typed = _owlModel.parseLiteral( literal.toString() );
		
		assertEquals( value, typed.getValue());
	}
}
