package org.semachina.core.db;

import org.semachina.core.ModelMakerFactory;

import com.hp.hpl.jena.db.DBConnection;
import com.hp.hpl.jena.db.IDBConnection;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.ModelMaker;

public class DBModelMakerFactory implements ModelMakerFactory {

    // database connection parameters, with defaults
    private String _dbURL;
    private String _dbUser;
    private String _dbPw;
    private String _dbType;
	
    public DBModelMakerFactory(String dbURL, 
					    		 String dbUser, 
					    		 String dbPw, 
					    		 String dbType) {
    	_dbURL = dbURL;
    	_dbUser = dbUser;
    	_dbPw = dbPw;
    	_dbType = dbType;
    }
    
    public ModelMaker createModelMaker(boolean cleanDB) {
        try {
            // Create database connection
            IDBConnection conn  = new DBConnection( _dbURL, _dbUser, _dbPw, _dbType );

            // do we need to clean the database?
            if (cleanDB) {
                conn.cleanDB();
            }

            // Create a model maker object
            return ModelFactory.createModelRDBMaker( conn );
        }
        catch (Exception e) {
            throw new RuntimeException( e.getMessage(), e );
        }
    }
}
