package org.semachina.jena.core;

import java.math.BigDecimal;
import java.net.URI;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.xml.namespace.QName;

import org.mindswap.pellet.jena.PelletReasonerFactory;
import org.semachina.xml.types.Base64;
import org.semachina.xml.types.Day;
import org.semachina.xml.types.Duration;
import org.semachina.xml.types.Entity;
import org.semachina.xml.types.HexBinary;
import org.semachina.xml.types.IDRef;
import org.semachina.xml.types.Id;
import org.semachina.xml.types.Language;
import org.semachina.xml.types.Month;
import org.semachina.xml.types.MonthDay;
import org.semachina.xml.types.NCName;
import org.semachina.xml.types.NMToken;
import org.semachina.xml.types.Name;
import org.semachina.xml.types.NegativeInteger;
import org.semachina.xml.types.NonNegativeInteger;
import org.semachina.xml.types.NonPositiveInteger;
import org.semachina.xml.types.NormalizedString;
import org.semachina.xml.types.Notation;
import org.semachina.xml.types.PositiveInteger;
import org.semachina.xml.types.Time;
import org.semachina.xml.types.Token;
import org.semachina.xml.types.UnsignedByte;
import org.semachina.xml.types.UnsignedInt;
import org.semachina.xml.types.UnsignedLong;
import org.semachina.xml.types.UnsignedShort;
import org.semachina.xml.types.Year;
import org.semachina.xml.types.YearMonth;
import org.semachina.xml.utils.XMLDateUtils;

import com.hp.hpl.jena.datatypes.TypeMapper;
import com.hp.hpl.jena.enhanced.BuiltinPersonalities;
import com.hp.hpl.jena.enhanced.EnhGraph;
import com.hp.hpl.jena.enhanced.EnhNode;
import com.hp.hpl.jena.enhanced.Implementation;
import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.ontology.ConversionException;
import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.ontology.Profile;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.ModelMaker;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.vocabulary.RDFS;
import com.hp.hpl.jena.vocabulary.XSD;

public class OWLFactory {

	private static Map<String, String> PREFIX_MAP = new HashMap<String, String>();

	private static Model DEFAULT_MODEL; 
	
	static {
		TypeMapper typeMapper = TypeMapper.getInstance();
		try {
			init( typeMapper );
		} 
		catch (Exception e) {
			throw new RuntimeException( e.getMessage(), e );
		}
		
		DEFAULT_MODEL = ModelFactory.createDefaultModel();
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
	
	protected static OWLBean objectify(RDFNode rdf) {
		if( rdf != null && rdf.canAs( Individual.class ) ) {
			Individual indiv = (Individual) rdf.as( Individual.class );
			if( indiv instanceof OWLBean ) {
				return (OWLBean) indiv;
			}
			else if( indiv instanceof EnhNode ) {
				return new OWLBean( (EnhNode) indiv );
			}
			else {
				return new OWLBean( indiv.asNode(), null );
			}
		}
		return null;	
	}
	
	public static void addNsPrefix(String prefix, String base) {
		PREFIX_MAP.put( prefix, base );
	}

	
	public static Literal createLiteral(String literalString) {
		return DEFAULT_MODEL.createLiteral( literalString );
	}
	
	public static Literal createLiteral(String literalString, String uri) {
		return DEFAULT_MODEL.createLiteral( literalString, uri );
	}
	
	public static Literal parseLiteral(String literalString) {
		String[] literalParts = literalString.split( "\\^\\^" );
		if( literalParts.length == 2 ) {
			
			if( TypeMapper.getInstance().getTypeByName( literalParts[1] ) != null ) {
				return DEFAULT_MODEL.createTypedLiteral( literalParts[0], literalParts[1] );
			}
		}

		return DEFAULT_MODEL.createLiteral( literalString );
	}
	
	private static void init(TypeMapper typeMapper) throws Exception {
		
		//Jena config - This replaces default IndividualImpl.class with OWLBean.class
		BuiltinPersonalities.model.add( Individual.class, new Implementation() {

			public EnhNode wrap( Node n, EnhGraph eg ) {
	            if (canWrap( n, eg )) {
	                return new OWLBean( n, eg );
	            }
	            else {
	                throw new ConversionException( "Cannot convert node " + n.toString() + " to Individual");
	            }
	        }

	        public boolean canWrap( Node node, EnhGraph eg ) {
	            // node will support being an Individual facet if it is a URI node or bNode
	            Profile profile = (eg instanceof OntModel) ? ((OntModel) eg).getProfile() : null;
	            return (profile != null)  &&  profile.isSupported( node, eg, Individual.class );
	        }
		});
		
		//Base64Binary
		typeMapper.registerDatatype( 
				new DataAccessor<Base64>( XSD.base64Binary.getURI(), 
										  Base64.class, 
										  new DataTypeFactory<Base64>() {

			public Base64 parseLexicalForm(String lexicalForm) {
				 return new Base64( lexicalForm );
			}
			

			public String toLexicalForm(Base64 value) {
				return value.toString();
			}		
		}) );
		
		//Boolean
		typeMapper.registerDatatype( 
				new DataAccessor<Boolean>( XSD.xboolean.getURI(), 
										   Boolean.class, 
										   new DataTypeFactory<Boolean>() {

			public Boolean parseLexicalForm(String lexicalForm) {
				return Boolean.valueOf( lexicalForm );
			}
			

			public String toLexicalForm(Boolean value) {
				return value.toString();
			}		
		}) );

		//Byte
		typeMapper.registerDatatype( 
				new DataAccessor<Byte>( XSD.xbyte.getURI() , 
										Byte.class, 
										new DataTypeFactory<Byte>() {
					
			public Byte parseLexicalForm(String lexicalForm) {
				return Byte.valueOf( lexicalForm );
			}
			
			public String toLexicalForm(Byte value) {
				return value.toString();
			}		
		}) );		

		//date
		typeMapper.registerDatatype( 
				new DataAccessor<Date>( XSD.date.getURI() , 
										Date.class, 
									    new DataTypeFactory<Date>() {
					
			public Date parseLexicalForm(String lexicalForm) {		        
				return XMLDateUtils.parseDate( lexicalForm );
			}

			public String toLexicalForm(Date value) {
				return XMLDateUtils.toLexicalDate( value );
			}		
		}) );			
		
		//datetime
		typeMapper.registerDatatype( 
				new DataAccessor<Date>( XSD.dateTime.getURI(), 
										Date.class, 
										new DataTypeFactory<Date>() {
			public Date parseLexicalForm(String lexicalForm) {
				return XMLDateUtils.parseDateTime( lexicalForm );
			}

			public String toLexicalForm(Date value) {
		    	return XMLDateUtils.toLexicalDateTime( value );
			}	
		}) );	
		
		//gDay
		typeMapper.registerDatatype( 
				new DataAccessor<Day>( XSD.gDay.getURI() , 
									   Day.class, 
									   new DataTypeFactory<Day>() {

			public Day parseLexicalForm(String lexicalForm) {
				return new Day( lexicalForm );
			}
			
			public String toLexicalForm(Day value) {
				return value.toString();
			}		
		}) );	
  		
		//decimal
		typeMapper.registerDatatype( 
				new DataAccessor<BigDecimal>( XSD.decimal.getURI(), 
										      BigDecimal.class, 
										      new DataTypeFactory<BigDecimal>() {

			public BigDecimal parseLexicalForm(String lexicalForm) {
				return new BigDecimal( lexicalForm );
			}
			
			public String toLexicalForm(BigDecimal value) {
				return value.toString();
			}		
		}) );
		
		//double
		typeMapper.registerDatatype( 
				new DataAccessor<Double>( XSD.xdouble.getURI(), 
										  Double.class, 
										  new DataTypeFactory<Double>() {

			public Double parseLexicalForm(String lexicalForm) {
				return Double.valueOf( lexicalForm );
			}
			
			public String toLexicalForm(Double value) {
				return value.toString();
			}		
		}) );		
		
		//duration
		typeMapper.registerDatatype( 
				new DataAccessor<Duration>( XSD.duration.getURI(), 
										    Duration.class, 
										    new DataTypeFactory<Duration>() {

			public Duration parseLexicalForm(String lexicalForm) {
				return new Duration( lexicalForm );
			}
			
			public String toLexicalForm(Duration value) {
				return value.toString();
			}		
		}) );			

		//entities
//		typeMapper.registerDatatype( 
//				new DataAccessor<Entities>( XSD.ENTITIES.getURI(), 
//											Entities.class, 
//											new DataTypeFactory<Entities>() {
//
//			public Entities parseLexicalForm(String lexicalForm) {
//				return new Entities( lexicalForm );
//			}
//
//			public String toLexicalForm(Entities value) {
//				return value.toString();
//			}	
//		}) );
		
		//entity
		typeMapper.registerDatatype( 
				new DataAccessor<Entity>( XSD.ENTITY.getURI(), 
										  Entity.class, 
										  new DataTypeFactory<Entity>() {

			public Entity parseLexicalForm(String lexicalForm) {
				return new Entity( lexicalForm );
			}

			public String toLexicalForm(Entity value) {
				return value.toString();
			}	
		}) );	
		
		
		//float
		typeMapper.registerDatatype( 
				new DataAccessor<Float>( XSD.xfloat.getURI(), 
										 Float.class, 
										 new DataTypeFactory<Float>() {

			public Float parseLexicalForm(String lexicalForm) {
				return Float.valueOf( lexicalForm );
			}
			
			public String toLexicalForm(Float value) {
				return value.toString();
			}		
		}) );	
		
		//hexBinary
		typeMapper.registerDatatype( 
				new DataAccessor<HexBinary>( XSD.hexBinary.getURI(), 
											 HexBinary.class, 
											 new DataTypeFactory<HexBinary>() {

			public HexBinary parseLexicalForm(String lexicalForm) {
				return new HexBinary( lexicalForm );
			}
			
			public String toLexicalForm(HexBinary value) {
				return value.toString();
			}		
		}) );	
		
		//ID
		typeMapper.registerDatatype( 
				new DataAccessor<Id>( XSD.ID.getURI(), 
									  Id.class, 
									  new DataTypeFactory<Id>() {

			public Id parseLexicalForm(String lexicalForm) {
				return new Id( lexicalForm );
			}
			
			public String toLexicalForm(Id value) {
				return value.toString();
			}		
		}) );
		
		//IDREF
		typeMapper.registerDatatype( 
				new DataAccessor<IDRef>( XSD.IDREF.getURI(), 
									     IDRef.class, 
									     new DataTypeFactory<IDRef>() {

			public IDRef parseLexicalForm(String lexicalForm) {
				return new IDRef( lexicalForm );
			}
			
			public String toLexicalForm(IDRef value) {
				return value.toString();
			}		
		}) );		
		
		//IDREFS
//		typeMapper.registerDatatype( 
//				new DataAccessor<IDRefs>( XSD.IDREFS.getURI(), 
//									     IDRefs.class, 
//									     new DataTypeFactory<IDRefs>() {
//
//			public IDRefs parseLexicalForm(String lexicalForm) {
//				return new IDRefs( lexicalForm );
//			}
//			
//			public String toLexicalForm(IDRefs value) {
//				return value.toString();
//			}		
//		}) );	
		
		//xint
		typeMapper.registerDatatype( 
				new DataAccessor<Integer>( XSD.xint.getURI(), 
										   Integer.class, 
										   new DataTypeFactory<Integer>() {

			@Override
			public Integer parseLexicalForm(String lexicalForm) {
				return Integer.valueOf( lexicalForm );
			}
			
			@Override
			public String toLexicalForm(Integer value) {
				return value.toString();
			}
			
		}) );

		//integer
		typeMapper.registerDatatype( 
				new DataAccessor<Integer>( XSD.integer.getURI(), 
										   Integer.class, 
										   new DataTypeFactory<Integer>() {

			@Override
			public Integer parseLexicalForm(String lexicalForm) {
				return Integer.valueOf( lexicalForm );
			}
			
			@Override
			public String toLexicalForm(Integer value) {
				return value.toString();
			}
			
		}) );

		//language
		typeMapper.registerDatatype( 
				new DataAccessor<Language>( XSD.language.getURI(), 
										    Language.class, 
										    new DataTypeFactory<Language>() {

			@Override
			public Language parseLexicalForm(String lexicalForm) {
				return new Language( lexicalForm );
			}
			
			@Override
			public String toLexicalForm(Language value) {
				return value.toString();
			}
			
		}) );

		//literal
		typeMapper.registerDatatype( 
				new DataAccessor<String>( RDFS.Literal.getURI(), 
										  String.class, 
										  new DataTypeFactory<String>() {

			@Override
			public String parseLexicalForm(String lexicalForm) {
				return String.valueOf( lexicalForm );
			}
			
			@Override
			public String toLexicalForm(String value) {
				return value.toString();
			}
			
		}) );
		
		//xlong
		typeMapper.registerDatatype( 
				new DataAccessor<Long>( XSD.xlong.getURI(), 
									    Long.class, 
										new DataTypeFactory<Long>() {

			@Override
			public Long parseLexicalForm(String lexicalForm) {
				return Long.valueOf( lexicalForm );
			}
			
			@Override
			public String toLexicalForm(Long value) {
				return value.toString();
			}
			
		}) );
			
		//gMonth
		typeMapper.registerDatatype( 
				new DataAccessor<Month>( XSD.gMonth.getURI(), 
									    Month.class, 
										new DataTypeFactory<Month>() {

			@Override
			public Month parseLexicalForm(String lexicalForm) {
				return new Month( lexicalForm );
			}
			
			@Override
			public String toLexicalForm(Month value) {
				return value.toString();
			}
			
		}) );
		
		//gMonthDay
		typeMapper.registerDatatype( 
				new DataAccessor<MonthDay>( XSD.gMonthDay.getURI(), 
											MonthDay.class, 
										    new DataTypeFactory<MonthDay>() {

			@Override
			public MonthDay parseLexicalForm(String lexicalForm) {
				return new MonthDay( lexicalForm );
			}
			
			@Override
			public String toLexicalForm(MonthDay value) {
				return value.toString();
			}
			
		}) );
		
		//name
		typeMapper.registerDatatype( 
				new DataAccessor<Name>( XSD.Name.getURI(), 
										Name.class, 
										new DataTypeFactory<Name>() {

			@Override
			public Name parseLexicalForm(String lexicalForm) {
				return new Name( lexicalForm );
			}
			
			@Override
			public String toLexicalForm(Name value) {
				return value.toString();
			}
			
		}) );
		
		//NCName
		typeMapper.registerDatatype( 
				new DataAccessor<NCName>( XSD.NCName.getURI(), 
										  NCName.class, 
										  new DataTypeFactory<NCName>() {

			@Override
			public NCName parseLexicalForm(String lexicalForm) {
				return new NCName( lexicalForm );
			}
			
			@Override
			public String toLexicalForm(NCName value) {
				return value.toString();
			}
			
		}) );

		
		//negativeInteger
		typeMapper.registerDatatype( 
				new DataAccessor<NegativeInteger>( XSD.negativeInteger.getURI(), 
												   NegativeInteger.class, 
												   new DataTypeFactory<NegativeInteger>() {

			@Override
			public NegativeInteger parseLexicalForm(String lexicalForm) {
				return new NegativeInteger( lexicalForm );
			}
			
			@Override
			public String toLexicalForm(NegativeInteger value) {
				return value.toString();
			}
			
		}) );	
		
		//NMTOKEN
		typeMapper.registerDatatype( 
				new DataAccessor<NMToken>( XSD.NMTOKEN.getURI(), 
										  NMToken.class, 
										  new DataTypeFactory<NMToken>() {

			@Override
			public NMToken parseLexicalForm(String lexicalForm) {
				return new NMToken( lexicalForm );
			}
			
			@Override
			public String toLexicalForm(NMToken value) {
				return value.toString();
			}
			
		}) );
			
		//NMTOKENS
//		typeMapper.registerDatatype( 
//				new DataAccessor<NMTokens>( XSD.NMTOKENS.getURI(), 
//										  NMTokens.class, 
//										  new DataTypeFactory<NMTokens>() {
//
//			@Override
//			public NMTokens parseLexicalForm(String lexicalForm) {
//				return new NMTokens( lexicalForm );
//			}
//			
//			@Override
//			public String toLexicalForm(NMTokens value) {
//				return value.toString();
//			}
//			
//		}) );

		//nonNegativeInteger
		typeMapper.registerDatatype( 
				new DataAccessor<NonNegativeInteger>( XSD.nonNegativeInteger.getURI(), 
												      NonNegativeInteger.class, 
												      new DataTypeFactory<NonNegativeInteger>() {

			@Override
			public NonNegativeInteger parseLexicalForm(String lexicalForm) {
				return new NonNegativeInteger( lexicalForm );
			}
			
			@Override
			public String toLexicalForm(NonNegativeInteger value) {
				return value.toString();
			}
			
		}) );	
		
		//nonPositiveInteger
		typeMapper.registerDatatype( 
				new DataAccessor<NonPositiveInteger>( XSD.nonPositiveInteger.getURI(), 
													  NonPositiveInteger.class, 
												      new DataTypeFactory<NonPositiveInteger>() {

			@Override
			public NonPositiveInteger parseLexicalForm(String lexicalForm) {
				return new NonPositiveInteger( lexicalForm );
			}
			
			@Override
			public String toLexicalForm(NonPositiveInteger value) {
				return value.toString();
			}
			
		}) );
	
		//normalizedString
		typeMapper.registerDatatype( 
				new DataAccessor<NormalizedString>( XSD.normalizedString.getURI(), 
													NormalizedString.class, 
													new DataTypeFactory<NormalizedString>() {

			@Override
			public NormalizedString parseLexicalForm(String lexicalForm) {
				return new NormalizedString( lexicalForm );
			}
			
			@Override
			public String toLexicalForm(NormalizedString value) {
				return value.toString();
			}
			
		}) );

		//NOTATION
		typeMapper.registerDatatype( 
				new DataAccessor<Notation>( XSD.NOTATION.getURI(), 
											Notation.class, 
											new DataTypeFactory<Notation>() {

			@Override
			public Notation parseLexicalForm(String lexicalForm) {
				throw new UnsupportedOperationException();
			}
			
			@Override
			public String toLexicalForm(Notation value) {
				return value.toString();
			}
			
		}) );
		
		//positiveInteger
		typeMapper.registerDatatype( 
				new DataAccessor<PositiveInteger>( XSD.positiveInteger.getURI(), 
													  PositiveInteger.class, 
												      new DataTypeFactory<PositiveInteger>() {

			@Override
			public PositiveInteger parseLexicalForm(String lexicalForm) {
				return new PositiveInteger( lexicalForm );
			}
			
			@Override
			public String toLexicalForm(PositiveInteger value) {
				return value.toString();
			}
			
		}) );		

		
		//QName
		typeMapper.registerDatatype( 
				new DataAccessor<QName>( XSD.QName.getURI(), 
										 QName.class, 
									     new DataTypeFactory<QName>() {

			@Override
			public QName parseLexicalForm(String lexicalForm) {
				return QName.valueOf( lexicalForm );
			}
			
			@Override
			public String toLexicalForm(QName value) {
				return value.toString();
			}
			
		}) );
		
		//xshort
		typeMapper.registerDatatype( 
				new DataAccessor<Short>( XSD.xshort.getURI(), 
										 Short.class, 
										 new DataTypeFactory<Short>() {

			@Override
			public Short parseLexicalForm(String lexicalForm) {
				return Short.valueOf( lexicalForm );
			}
			
			@Override
			public String toLexicalForm(Short value) {
				return value.toString();
			}
			
		}) );
		
		//xstring
		typeMapper.registerDatatype( 
				new DataAccessor<String>( XSD.xstring.getURI(), 
										 String.class, 
										 new DataTypeFactory<String>() {

			@Override
			public String parseLexicalForm(String lexicalForm) {
				return String.valueOf( lexicalForm );
			}
			
			@Override
			public String toLexicalForm(String value) {
				return value.toString();
			}
			
		}) );

		//time
		typeMapper.registerDatatype( 
				new DataAccessor<Time>( XSD.time.getURI(), 
										Time.class, 
										new DataTypeFactory<Time>() {

			@Override
			public Time parseLexicalForm(String lexicalForm) {
				return new Time( lexicalForm );
			}
			
			@Override
			public String toLexicalForm(Time value) {
				return value.toString();
			}
			
		}) );
		
		//token
		typeMapper.registerDatatype( 
				new DataAccessor<Token>( XSD.token.getURI(), 
										 Token.class, 
										 new DataTypeFactory<Token>() {

			@Override
			public Token parseLexicalForm(String lexicalForm) {
				return new Token( lexicalForm );
			}
			
			@Override
			public String toLexicalForm(Token value) {
				return value.toString();
			}
			
		}) );

		//unsignedByte
		typeMapper.registerDatatype( 
				new DataAccessor<UnsignedByte>( XSD.unsignedByte.getURI(), 
												UnsignedByte.class, 
												new DataTypeFactory<UnsignedByte>() {

			@Override
			public UnsignedByte parseLexicalForm(String lexicalForm) {
				return new UnsignedByte( lexicalForm );
			}
			
			@Override
			public String toLexicalForm(UnsignedByte value) {
				return value.toString();
			}
			
		}) );
		
		//unsignedInt
		typeMapper.registerDatatype( 
				new DataAccessor<UnsignedInt>( XSD.unsignedInt.getURI(), 
												UnsignedInt.class, 
												new DataTypeFactory<UnsignedInt>() {

			@Override
			public UnsignedInt parseLexicalForm(String lexicalForm) {
				return new UnsignedInt( lexicalForm );
			}
			
			@Override
			public String toLexicalForm(UnsignedInt value) {
				return value.toString();
			}
			
		}) );
		
		//unsignedLong
		typeMapper.registerDatatype( 
				new DataAccessor<UnsignedLong>( XSD.unsignedInt.getURI(), 
												UnsignedLong.class, 
												new DataTypeFactory<UnsignedLong>() {

			@Override
			public UnsignedLong parseLexicalForm(String lexicalForm) {
				return new UnsignedLong( lexicalForm );
			}
			
			@Override
			public String toLexicalForm(UnsignedLong value) {
				return value.toString();
			}
			
		}) );
		
		//unsignedShort
		typeMapper.registerDatatype( 
				new DataAccessor<UnsignedShort>( XSD.unsignedShort.getURI(), 
												 UnsignedShort.class, 
												 new DataTypeFactory<UnsignedShort>() {

			@Override
			public UnsignedShort parseLexicalForm(String lexicalForm) {
				return new UnsignedShort( lexicalForm );
			}
			
			@Override
			public String toLexicalForm(UnsignedShort value) {
				return value.toString();
			}
			
		}) );
		
		//anyURI
		typeMapper.registerDatatype( 
				new DataAccessor<URI>( XSD.anyURI.getURI(), 
						               URI.class, 
									   new DataTypeFactory<URI>() {

			@Override
			public URI parseLexicalForm(String lexicalForm) {
				return URI.create( lexicalForm );
			}
			
			@Override
			public String toLexicalForm(URI value) {
				return value.toString();
			}
			
		}) );
		
		//gYear
		typeMapper.registerDatatype( 
				new DataAccessor<Year>( XSD.gYear.getURI(), 
						               Year.class, 
									   new DataTypeFactory<Year>() {

			@Override
			public Year parseLexicalForm(String lexicalForm) {
				return new Year( lexicalForm );
			}
			
			@Override
			public String toLexicalForm(Year value) {
				return value.toString();
			}
			
		}) );
		
		//gYearMonth
		typeMapper.registerDatatype( 
				new DataAccessor<YearMonth>( XSD.gYearMonth.getURI(), 
										     YearMonth.class, 
										     new DataTypeFactory<YearMonth>() {

			@Override
			public YearMonth parseLexicalForm(String lexicalForm) {
				return new YearMonth( lexicalForm );
			}
			
			@Override
			public String toLexicalForm(YearMonth value) {
				return value.toString();
			}
			
		}) );
	}
}
