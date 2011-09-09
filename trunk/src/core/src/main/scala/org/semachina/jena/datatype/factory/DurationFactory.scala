package org.semachina.jena.datatype.factory

import com.hp.hpl.jena.vocabulary.XSD
import org.joda.time.Period
import org.joda.time.format.ISOPeriodFormat
import org.semachina.jena.datatype.SimpleRDFDatatype

class DurationFactory extends SimpleRDFDatatype[Period] (
  XSD.duration.getURI,
  { lexicalForm: String => ISOPeriodFormat.standard.parsePeriod(lexicalForm) },
  { cast: Period => ISOPeriodFormat.standard.print(cast) } )