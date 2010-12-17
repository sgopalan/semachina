/**
 *
 * ------------------------------------------------------------
 *
 * @project solr-plugins
 *
 * Copyright (C) 2007,
 * @author Renaud Delbru [ 13 févr. 08 ]
 * @link http://renaud.delbru.fr/
 * All rights reserved.
 */
package org.semachina.siren;

import java_cup.runtime.Symbol;
import org.apache.commons.io.FileUtils;
import org.apache.lucene.analysis.TokenStream;
import org.junit.Test;
import org.sindice.siren.qparser.analysis.CupScannerWrapper;
import org.sindice.siren.qparser.analysis.NTripleQueryStandardAnalyzer;
import org.sindice.siren.qparser.ntriple.NTripleQParserImpl;
import org.sindice.siren.qparser.ntriple.query.model.EmptyQuery;
import org.sindice.siren.qparser.ntriple.query.model.NTripleQuery;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * @author renaud
 *
 */
public class NTripleQParserTest {

  //private final String _dsDir = "src/test/resources/dataset/ntriple-parser/";

  private final String _dsDir = "/dataset/ntriple-parser/";

  /**
   * Test method for {@link org.sindice.siren.qparser.ntriple.NTripleQParserImpl#parse()}.
   * @throws Exception
   */
  @Test
  public void testParseEmpty() throws Exception {
    final String query = "";
    final NTripleQueryStandardAnalyzer analyzer = new NTripleQueryStandardAnalyzer();
    final TokenStream stream = analyzer.tokenStream(null, new StringReader(query));
    final NTripleQParserImpl parser = new NTripleQParserImpl(new CupScannerWrapper(stream));
    final Symbol sym = parser.parse();
    final NTripleQuery q = (NTripleQuery) sym.value;
    assertTrue(q instanceof EmptyQuery);
  }

  /**
   * Test method for {@link org.sindice.siren.qparser.ntriple.NTripleQParserImpl#parse()}.
   * @throws Exception
   */
  @Test
  public void testParseSimpleURI1() throws Exception {
    final String query = "* * <s>";
    final NTripleQueryStandardAnalyzer analyzer = new NTripleQueryStandardAnalyzer();
    final TokenStream stream = analyzer.tokenStream(null, new StringReader(query));
    final NTripleQParserImpl parser = new NTripleQParserImpl(new CupScannerWrapper(stream));
    final Symbol sym = parser.parse();
    final NTripleQuery q = (NTripleQuery) sym.value;
    assertEquals(this.readFile(_dsDir + "SimpleURI1.txt"), q.toString());
  }

  /**
   * Test method for {@link org.sindice.siren.qparser.ntriple.NTripleQParserImpl#parse()}.
   * @throws Exception
   */
  @Test
  public void testParseSimpleURI2() throws Exception {
    final String query = "* <http://ns/#s> <http://ns/p>";
    final NTripleQueryStandardAnalyzer analyzer = new NTripleQueryStandardAnalyzer();
    final TokenStream stream = analyzer.tokenStream(null, new StringReader(query));
    final NTripleQParserImpl parser = new NTripleQParserImpl(new CupScannerWrapper(stream));
    final Symbol sym = parser.parse();
    final NTripleQuery q = (NTripleQuery) sym.value;
    assertEquals(this.readFile(_dsDir + "SimpleURI2.txt"), q.toString());
  }

  /**
   * Test method for {@link org.sindice.siren.qparser.ntriple.NTripleQParserImpl#parse()}.
   * @throws Exception
   */
  @Test
  public void testParseSimpleLiteral2() throws Exception {
    final String query = "* * \"A literal ...\"";
    final NTripleQueryStandardAnalyzer analyzer = new NTripleQueryStandardAnalyzer();
    final TokenStream stream = analyzer.tokenStream(null, new StringReader(query));
    final NTripleQParserImpl parser = new NTripleQParserImpl(new CupScannerWrapper(stream));
    final Symbol sym = parser.parse();
    final NTripleQuery q = (NTripleQuery) sym.value;
    assertEquals(this.readFile(_dsDir + "SimpleLiteral.txt"), q.toString());
  }

  /**
   * Test method for {@link org.sindice.siren.qparser.ntriple.NTripleQParserImpl#parse()}.
   * @throws Exception
   * @throws Exception
   */
  @Test
  public void testParseLiteralWithWildcard() throws Exception {
    final String query = "* * \"pao*\"";

    final NTripleQueryStandardAnalyzer analyzer = new NTripleQueryStandardAnalyzer();
    final TokenStream stream = analyzer.tokenStream(null, new StringReader(query));
    final NTripleQParserImpl parser = new NTripleQParserImpl(new CupScannerWrapper(stream));
    final Symbol sym = parser.parse();
    final NTripleQuery q = (NTripleQuery) sym.value;
    assertEquals(this.readFile(_dsDir + "LiteralWildcard.txt"), q.toString());
  }

  /**
   * Test method for {@link org.sindice.siren.qparser.ntriple.NTripleQParserImpl#parse()}.
   * @throws Exception
   */
  @Test
  public void testParseSimpleTriple1() throws Exception {
    final String query = "<http://ns/#s> <http://ns/p> <http://ns/obj>";
    final NTripleQueryStandardAnalyzer analyzer = new NTripleQueryStandardAnalyzer();
    final TokenStream stream = analyzer.tokenStream(null, new StringReader(query));
    final NTripleQParserImpl parser = new NTripleQParserImpl(new CupScannerWrapper(stream));
    final Symbol sym = parser.parse();
    final NTripleQuery q = (NTripleQuery) sym.value;
    assertEquals(this.readFile(_dsDir + "SimpleTriple1.txt"), q.toString());
  }

  /**
   * Test method for {@link org.sindice.siren.qparser.ntriple.NTripleQParserImpl#parse()}.
   * @throws Exception
   */
  @Test
  public void testParseSimpleTriple2() throws Exception {
    final String query = "<http://ns/#s> <http://ns/p> \"A Literal ...\"";
    final NTripleQueryStandardAnalyzer analyzer = new NTripleQueryStandardAnalyzer();
    final TokenStream stream = analyzer.tokenStream(null, new StringReader(query));
    final NTripleQParserImpl parser = new NTripleQParserImpl(new CupScannerWrapper(stream));
    final Symbol sym = parser.parse();
    final NTripleQuery q = (NTripleQuery) sym.value;
    assertEquals(this.readFile(_dsDir + "SimpleTriple2.txt"), q.toString());
  }

  /**
   * Test method for {@link org.sindice.siren.qparser.ntriple.NTripleQParserImpl#parse()}.
   * @throws Exception
   */
  @Test
  public void testParseSimpleTriple3() throws Exception {
    final String query = "* <http://purl.org/dc/terms/license> \"http://creativecommons.org/licenses/by-nc-sa/3.0/us\"";
    final NTripleQueryStandardAnalyzer analyzer = new NTripleQueryStandardAnalyzer();
    final TokenStream stream = analyzer.tokenStream(null, new StringReader(query));
    final NTripleQParserImpl parser = new NTripleQParserImpl(new CupScannerWrapper(stream));
    final Symbol sym = parser.parse();
    final NTripleQuery q = (NTripleQuery) sym.value;
    assertEquals(this.readFile(_dsDir + "SimpleTriple3.txt"), q.toString());
  }

  /**
   * Test method for {@link org.sindice.siren.qparser.ntriple.NTripleQParserImpl#parse()}.
   * @throws Exception
   */
  @Test
  public void testParseBinaryClauseAnd() throws Exception {
    String query = "<http://ns/#s> <http://ns/p> \"A Literal ...\" AND ";
    query += "* <http://#s> \"A \\\"second Literal\\\"\"";

    final NTripleQueryStandardAnalyzer analyzer = new NTripleQueryStandardAnalyzer();
    final TokenStream stream = analyzer.tokenStream(null, new StringReader(query));
    final NTripleQParserImpl parser = new NTripleQParserImpl(new CupScannerWrapper(stream));
    final Symbol sym = parser.parse();
    final NTripleQuery q = (NTripleQuery) sym.value;
    assertEquals(this.readFile(_dsDir + "BinaryClauseAnd.txt"), q.toString());
  }

  /**
   * Test method for {@link org.sindice.siren.qparser.ntriple.NTripleQParserImpl#parse()}.
   * @throws Exception
   */
  @Test
  public void testParseBinaryClauseOr() throws Exception {
    String query = "<http://ns/#s> <http://ns/p> \"A Literal ...\" OR ";
    query += "* <http://#s> \"A \\\"second Literal\\\"\"";

    final NTripleQueryStandardAnalyzer analyzer = new NTripleQueryStandardAnalyzer();
    final TokenStream stream = analyzer.tokenStream(null, new StringReader(query));
    final NTripleQParserImpl parser = new NTripleQParserImpl(new CupScannerWrapper(stream));
    final Symbol sym = parser.parse();
    final NTripleQuery q = (NTripleQuery) sym.value;
    assertEquals(this.readFile(_dsDir + "BinaryClauseOr.txt"), q.toString());
  }

  /**
   * Test method for {@link org.sindice.siren.qparser.ntriple.NTripleQParserImpl#parse()}.
   * @throws Exception
   */
  @Test
  public void testParseBinaryClauseMinus() throws Exception {
    String query = "<http://ns/#s> <http://ns/p> \"A Literal ...\" - ";
    query += "* <http://#s> \"A \\\"second Literal\\\"\"";

    final NTripleQueryStandardAnalyzer analyzer = new NTripleQueryStandardAnalyzer();
    final TokenStream stream = analyzer.tokenStream(null, new StringReader(query));
    final NTripleQParserImpl parser = new NTripleQParserImpl(new CupScannerWrapper(stream));
    final Symbol sym = parser.parse();
    final NTripleQuery q = (NTripleQuery) sym.value;
    assertEquals(this.readFile(_dsDir + "BinaryClauseMinus.txt"), q.toString());
  }

  /**
   * Test method for {@link org.sindice.siren.qparser.ntriple.NTripleQParserImpl#parse()}.
   * @throws Exception
   */
  @Test
  public void testParseNestedClause1() throws Exception {
    final String query = "<s> <p> <obj> AND (<s> <p2> <o2> OR <s> <p3> \"A literal\")";

    final NTripleQueryStandardAnalyzer analyzer = new NTripleQueryStandardAnalyzer();
    final TokenStream stream = analyzer.tokenStream(null, new StringReader(query));
    final NTripleQParserImpl parser = new NTripleQParserImpl(new CupScannerWrapper(stream));
    final Symbol sym = parser.parse();
    final NTripleQuery q = (NTripleQuery) sym.value;
    assertEquals(this.readFile(_dsDir + "NestedClause1.txt"), q.toString());
  }

  /**
   * Test method for {@link org.sindice.siren.qparser.ntriple.NTripleQParserImpl#parse()}.
   * @throws Exception
   */
  @Test
  public void testParseNestedClause2() throws Exception {
    final String query = "(<s> <p> <obj> AND <s> <p2> <o2>) OR <s> <p3> \"A literal\"";

    final NTripleQueryStandardAnalyzer analyzer = new NTripleQueryStandardAnalyzer();
    final TokenStream stream = analyzer.tokenStream(null, new StringReader(query));
    final NTripleQParserImpl parser = new NTripleQParserImpl(new CupScannerWrapper(stream));
    final Symbol sym = parser.parse();
    final NTripleQuery q = (NTripleQuery) sym.value;
    assertEquals(this.readFile(_dsDir + "NestedClause2.txt"), q.toString());
  }

  /**
   * Test method for {@link org.sindice.siren.qparser.ntriple.NTripleQParserImpl#parse()}.
   * @throws Exception
   */
  @Test
  public void testParseNestedClause3() throws Exception {
    final String query = "(<s> <p> <obj> AND <s> <p2> <o2>)";

    final NTripleQueryStandardAnalyzer analyzer = new NTripleQueryStandardAnalyzer();
    final TokenStream stream = analyzer.tokenStream(null, new StringReader(query));
    final NTripleQParserImpl parser = new NTripleQParserImpl(new CupScannerWrapper(stream));
    final Symbol sym = parser.parse();
    final NTripleQuery q = (NTripleQuery) sym.value;
    assertEquals(this.readFile(_dsDir + "NestedClause3.txt"), q.toString());
  }

  /**
   * Test method for {@link org.sindice.siren.qparser.ntriple.NTripleQParserImpl#parse()}.
   * @throws Exception
   */
  @Test
  public void testParseNestedClauseEmpty() throws Exception {
    final String query = "<s> <p> <obj> AND ()";

    final NTripleQueryStandardAnalyzer analyzer = new NTripleQueryStandardAnalyzer();
    final TokenStream stream = analyzer.tokenStream(null, new StringReader(query));
    final NTripleQParserImpl parser = new NTripleQParserImpl(new CupScannerWrapper(stream));
    final Symbol sym = parser.parse();
    final NTripleQuery q = (NTripleQuery) sym.value;
    assertEquals(this.readFile(_dsDir + "NestedClauseEmpty.txt"), q.toString());
  }

  /**
   * Test method for {@link org.sindice.siren.qparser.ntriple.NTripleQParserImpl#parse()}.
   * @throws Exception
   * @throws Exception
   */
  @Test(expected=IOException.class)
  public void testParseInvalid1() throws Exception {
    final String query = "   s  ";

    final NTripleQueryStandardAnalyzer analyzer = new NTripleQueryStandardAnalyzer();
    final TokenStream stream = analyzer.tokenStream(null, new StringReader(query));
    final NTripleQParserImpl parser = new NTripleQParserImpl(new CupScannerWrapper(stream));
    parser.parse();
  }

  /**
   * Test method for {@link org.sindice.siren.qparser.ntriple.NTripleQParserImpl#parse()}.
   * @throws Exception
   * @throws Exception
   */
  @Test(expected=IOException.class)
  public void testParseInvalid2() throws Exception {
    final String query = " <s> <p> AND (<p> OR <obj>  ";

    final NTripleQueryStandardAnalyzer analyzer = new NTripleQueryStandardAnalyzer();
    final TokenStream stream = analyzer.tokenStream(null, new StringReader(query));
    final NTripleQParserImpl parser = new NTripleQParserImpl(new CupScannerWrapper(stream));
    parser.parse();
  }

  /**
   * Test method for {@link org.sindice.siren.qparser.ntriple.NTripleQParserImpl#parse()}.
   * @throws Exception
   * @throws Exception
   */
  @Test(expected=IOException.class)
  public void testParseInvalid3() throws Exception {
    final String query = " <s> <p> <p> <obj>  ";

    final NTripleQueryStandardAnalyzer analyzer = new NTripleQueryStandardAnalyzer();
    final TokenStream stream = analyzer.tokenStream(null, new StringReader(query));
    final NTripleQParserImpl parser = new NTripleQParserImpl(new CupScannerWrapper(stream));
    parser.parse();
  }

  /**
   * Test method for {@link org.sindice.siren.qparser.ntriple.NTripleQParserImpl#parse()}.
   * @throws Exception
   * @throws Exception
   */
  @Test(expected=IOException.class)
  public void testParseInvalid4() throws Exception {
    final String query = " <s> <p> \"Literal ...  ";

    final NTripleQueryStandardAnalyzer analyzer = new NTripleQueryStandardAnalyzer();
    final TokenStream stream = analyzer.tokenStream(null, new StringReader(query));
    final NTripleQParserImpl parser = new NTripleQParserImpl(new CupScannerWrapper(stream));
    parser.parse();
  }

  private String readFile(final String filename) throws IOException {

    String path = getClass().getResource( filename ).getPath();

    //final FileReader reader = new FileReader(filename);
    final String content = FileUtils.readFileToString(new File(path));
    //reader.close();
    return content.trim();
  }

}