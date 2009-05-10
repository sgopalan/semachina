package org.semachina.core;

import java.util.HashMap;
import java.util.Map;

import org.mindswap.pellet.jena.PelletReasonerFactory;
import org.semachina.data.accessor.Base64BinaryAccessor;
import org.semachina.data.accessor.BooleanAccessor;
import org.semachina.data.accessor.ByteAccessor;
import org.semachina.data.accessor.DateAccessor;
import org.semachina.data.accessor.DateTimeAccessor;
import org.semachina.data.accessor.DayAccessor;
import org.semachina.data.accessor.DecimalAccessor;
import org.semachina.data.accessor.DoubleAccessor;
import org.semachina.data.accessor.DurationAccessor;
import org.semachina.data.accessor.EntityAccessor;
import org.semachina.data.accessor.FloatAccessor;
import org.semachina.data.accessor.HexBinaryAccessor;
import org.semachina.data.accessor.IDRefAccessor;
import org.semachina.data.accessor.IdAccessor;
import org.semachina.data.accessor.IntegerAccessor;
import org.semachina.data.accessor.LanguageAccessor;
import org.semachina.data.accessor.LongAccessor;
import org.semachina.data.accessor.MonthAccessor;
import org.semachina.data.accessor.MonthDayAccessor;
import org.semachina.data.accessor.NCNameAccessor;
import org.semachina.data.accessor.NMTokenAccessor;
import org.semachina.data.accessor.NameAccessor;
import org.semachina.data.accessor.NegativeIntegerAccessor;
import org.semachina.data.accessor.NonNegativeIntegerAccessor;
import org.semachina.data.accessor.NonPositiveIntegerAccessor;
import org.semachina.data.accessor.NormalizedStringAccessor;
import org.semachina.data.accessor.NotationAccessor;
import org.semachina.data.accessor.PositiveIntegerAccessor;
import org.semachina.data.accessor.QNameAccessor;
import org.semachina.data.accessor.ShortAccessor;
import org.semachina.data.accessor.StringAccessor;
import org.semachina.data.accessor.TimeAccessor;
import org.semachina.data.accessor.TokenAccessor;
import org.semachina.data.accessor.URIAccessor;
import org.semachina.data.accessor.UnsignedByteAccessor;
import org.semachina.data.accessor.UnsignedIntAccessor;
import org.semachina.data.accessor.UnsignedLongAccessor;
import org.semachina.data.accessor.UnsignedShortAccessor;
import org.semachina.data.accessor.YearAccessor;
import org.semachina.data.accessor.YearMonthAccessor;

import com.hp.hpl.jena.datatypes.RDFDatatype;
import com.hp.hpl.jena.datatypes.TypeMapper;
import com.hp.hpl.jena.enhanced.BuiltinPersonalities;
import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.ModelMaker;
import com.hp.hpl.jena.vocabulary.RDFS;
import com.hp.hpl.jena.vocabulary.XSD;

public class OWLFactory {

	private static Map<String, String> PREFIX_MAP = new HashMap<String, String>();
	
	private static TypeMapper TYPE_MAPPER = TypeMapper.getInstance();
	
	public static void addNsPrefix(String prefix, String base) {
		PREFIX_MAP.put( prefix, base );
	}

	public static void addDataAccessor(DataAccessor<?> accessor) {
		if( accessor.canAs( RDFDatatype.class ) ) {
			TYPE_MAPPER.registerDatatype( accessor.as( RDFDatatype.class ) );
		}
	}
	
	public static void init() {
		//Jena config
		BuiltinPersonalities.model.add( Individual.class, OWLBean.factory );
		
		addDataAccessor( new Base64BinaryAccessor( XSD.base64Binary.getURI() ) );
		addDataAccessor( new BooleanAccessor( XSD.xboolean.getURI() ) );
	    addDataAccessor( new ByteAccessor( XSD.xbyte.getURI() ) );   
	    addDataAccessor( new DayAccessor( XSD.gDay.getURI() ) );
	    addDataAccessor( new DateAccessor( XSD.date.getURI() ) );
	    addDataAccessor( new DateTimeAccessor( XSD.dateTime.getURI() ) );
	    addDataAccessor( new DecimalAccessor( XSD.decimal.getURI() ) );
	    addDataAccessor( new DoubleAccessor( XSD.xdouble.getURI() ) );
		addDataAccessor( new DurationAccessor( XSD.duration.getURI() ) );
	    //addDataAccessor( new EntitiesAccessor( XSD.ENTITIES.getURI() ) );
		addDataAccessor( new EntityAccessor( XSD.ENTITY.getURI() ) );
		addDataAccessor( new FloatAccessor( XSD.xfloat.getURI() ) );
		addDataAccessor( new HexBinaryAccessor( XSD.hexBinary.getURI() ) );
		addDataAccessor( new IdAccessor( XSD.ID.getURI() ) );
		addDataAccessor( new IDRefAccessor( XSD.IDREF.getURI() ) );
		//OWLBean.addDataAccessor( new IDRefsAccessor( XSD.IDREFS.getURI() ) );
		addDataAccessor( new IntegerAccessor( XSD.xint.getURI() ) );
		addDataAccessor( new IntegerAccessor( XSD.integer.getURI() ) );
		addDataAccessor( new LanguageAccessor( XSD.language.getURI() ) );
		addDataAccessor( new StringAccessor( RDFS.Literal.getURI() ) );
		addDataAccessor( new LongAccessor( XSD.xlong.getURI() ) );
		addDataAccessor( new MonthAccessor( XSD.gMonth.getURI() ) );
		addDataAccessor( new MonthDayAccessor( XSD.gMonthDay.getURI() ) );
		addDataAccessor( new NameAccessor( XSD.Name.getURI() ) );
		addDataAccessor( new NCNameAccessor( XSD.NCName.getURI() ) );
		addDataAccessor( new NMTokenAccessor( XSD.NMTOKEN.getURI() ) );
		//addDataAccessor( new NMTokensAccessor( XSD.NMTOKENS.getURI() ) );				
		addDataAccessor( new TokenAccessor( XSD.token.getURI() ) );
		addDataAccessor( new NormalizedStringAccessor( XSD.normalizedString.getURI() ) );			
		addDataAccessor( new NegativeIntegerAccessor( XSD.negativeInteger.getURI() ) );
		addDataAccessor( new NonNegativeIntegerAccessor( XSD.nonNegativeInteger.getURI() ) );
		addDataAccessor( new NonPositiveIntegerAccessor( XSD.nonPositiveInteger.getURI() ) );
		addDataAccessor( new NotationAccessor( XSD.NOTATION.getURI() ) );
		addDataAccessor( new PositiveIntegerAccessor( XSD.positiveInteger.getURI() ) );
		addDataAccessor( new TimeAccessor( XSD.time.getURI() ) );
		addDataAccessor( new TokenAccessor(XSD.token.getURI() ) );			
		addDataAccessor( new ShortAccessor(XSD.xshort.getURI() ) );
		addDataAccessor( new StringAccessor(XSD.xstring.getURI() ) );
		addDataAccessor( new UnsignedByteAccessor(XSD.unsignedByte.getURI() ) );
		addDataAccessor( new UnsignedIntAccessor(XSD.unsignedInt.getURI() ) );
		addDataAccessor( new UnsignedLongAccessor(XSD.unsignedLong.getURI() ) );
		addDataAccessor( new UnsignedShortAccessor(XSD.unsignedShort.getURI() ) );
		addDataAccessor( new URIAccessor(XSD.anyURI.getURI() ) );
		addDataAccessor( new YearAccessor(XSD.gYear.getURI() ) );
		addDataAccessor( new YearMonthAccessor(XSD.gYearMonth.getURI() ) );
		addDataAccessor( new QNameAccessor(XSD.QName.getURI() ) );	
	}
	
	public static OWLModel createOWLModel() {
		return createOWLModel( null );
	}
	
	public static OWLModel createOWLModel(ModelMaker maker) {
		OntModelSpec spec = PelletReasonerFactory.THE_SPEC;
        
		if(maker == null ) {
			maker = ModelFactory.createMemModelMaker();
		}
		
		spec.setImportModelMaker( maker );
		
		Model base = maker.createDefaultModel();
		
		// this will infer subclasses (ie, if Person then also Agent)
		OWLModel m = new OWLModel( spec, base );
		m.setNsPrefixes( PREFIX_MAP );
		return m;
	}
}
