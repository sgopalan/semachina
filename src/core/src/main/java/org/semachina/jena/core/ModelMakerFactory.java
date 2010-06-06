package org.semachina.jena.core;

import com.hp.hpl.jena.rdf.model.ModelMaker;

public interface ModelMakerFactory {

	public ModelMaker createModelMaker(boolean cleanDB);
}
