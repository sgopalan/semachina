package org.semachina.jena.features.pellet;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.*;
import com.hp.hpl.jena.util.iterator.ExtendedIterator;
import com.hp.hpl.jena.vocabulary.RDF;
import org.mindswap.pellet.PelletOptions;
import org.mindswap.pellet.jena.BuiltinTerm;
import org.mindswap.pellet.jena.PelletInfGraph;
import org.mindswap.pellet.utils.progress.ProgressMonitor;
import org.semachina.jena.SemachinaOntModel;
import org.semachina.jena.features.Feature;

public class PelletFeature implements Feature {

    public static String KEY = "pellet-feature";
	
	protected SemachinaOntModel pelletModel;

	protected PelletInfGraph pelletGraph;

    @Override
    public String getKey() {
        return KEY;
    }

    @Override
    public void init(SemachinaOntModel ontModel, org.semachina.jena.SemachinaFactory factory) throws Exception {
        this.pelletModel = ontModel;

        PelletOptions.USE_TRACING = true;
		PelletOptions.USE_ANNOTATION_SUPPORT = true;
		PelletOptions.SAMPLING_RATIO = 0;

		pelletGraph = (PelletInfGraph) pelletModel.getGraph();
    }

    @Override
    public void close() throws Exception {
    }

	public void classify(ProgressMonitor progressBar) {

        checkAndResolveInconsistency(progressBar);
		
		if( progressBar != null ) {
			pelletGraph.getKB().getTaxonomyBuilder().setProgressMonitor( progressBar );
		}
		
		pelletGraph.classify();
	}
	
	private void checkAndResolveInconsistency(ProgressMonitor progressBar) {
		if( progressBar != null ) {
			progressBar.setProgressMessage("Consistency checking");
		}
		// continue until all inconsistencies are resolved
		while( !pelletGraph.isConsistent() ) {
			// get the explanation for current inconsistency
			Model explanation = pelletGraph.explainInconsistency();
			
			System.out.println( "Data is inconsistent:" );
			explanation.setNsPrefixes( pelletModel );
			explanation.write( System.out, "TTL" );
			
			// iterate over the axioms in the explanation
			for( Statement stmt : explanation.listStatements().toList() ) {
				// remove any individual assertion that contributes
				// to the inconsistency (assumption: all the axioms
				// in the schema are believed to be correct and
				// should not be removed)
				if( isIndividualAssertion( stmt ) ) {
					System.out.println( "Remove statement: "
							+ stmt.asTriple().toString( pelletModel ) );
					ExtendedIterator<OntModel> subModels = pelletModel.listSubModels();
					while( subModels.hasNext() ) {
						subModels.next().remove( stmt );
					}
				}
			}
			
			// The following statement is needed to workaround
			pelletGraph.getLoader().setKB( pelletGraph.getKB() );
			
			pelletModel.rebind();
		}
		
		if( progressBar != null ) {
			progressBar.taskFinished();
		}
	}

	private boolean isIndividualAssertion(Statement stmt) {
		Property p = stmt.getPredicate();
		RDFNode o = stmt.getResource();

		return !isBuiltinTerm( p )
			|| !isBuiltinTerm( o )  && p.equals( RDF.type );
	}
	
	private boolean isBuiltinTerm(RDFNode r) {
		return BuiltinTerm.find(r.asNode()) != null;
	}
}
