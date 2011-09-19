package org.semachina.jena.sdb;

import com.hp.hpl.jena.db.DBConnection;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.query.Dataset;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.ModelMaker;
import com.hp.hpl.jena.sdb.SDBFactory;
import com.hp.hpl.jena.sdb.Store;
import com.hp.hpl.jena.sdb.StoreDesc;
import com.hp.hpl.jena.sdb.sql.SDBConnection;
import com.hp.hpl.jena.sdb.store.DatasetStore;
import org.apache.log4j.Logger;
import org.mindswap.pellet.jena.PelletReasonerFactory;

import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Connection;

/**
 */
public class SDBConnector {
    /**
     * This store already exists in the database, and has statements
     */
    public static final int STORE_HAS_STATEMENTS = -2;

    /**
     * This store already exists in the database, and it is blank
     */
    public static final int STORE_IS_BLANK = -1;

    /**
     * A store was just created in the database
     */
    public static final int STORE_CREATED = 1;

    static Logger LOG = Logger.getLogger(SDBConnector.class);
    private static Store store = null;
    private static Dataset dataset = null;
    private static boolean initialized = false;

    private static String DEFAULT_DB_LAYOUT = "layout2/index";
    private static String dbLayout = DEFAULT_DB_LAYOUT;
    private static String dbDriver = null;
    private static String dbUrl = null;
    private static String dbUser = null;
    private static String dbPassword = null;
    private static String dbType = null;
    private static boolean reload = false;
    private boolean storeIsClosed = false;
    private static Connection jdbcConnection = null;

    private SDBConnection sdbConnection = null;
    private OntModel ontModel = null;


    public SDBConnector(String dbLayout,
                        String dbUrl,
                        String dbDriver,
                        String dbType,
                        String dbUser,
                        String dbPassword,
                        boolean reload) {
        this.dbLayout = dbLayout;
        this.dbUrl = dbUrl;
        this.dbDriver = dbDriver;
        this.dbType = dbType;
        this.dbUser = dbUser;
        this.dbPassword = dbPassword;
        this.reload = reload;

        validateDriver();
    }

    public SDBConnector(String dbUrl,
                        String dbDriver,
                        String dbType,
                        String dbUser,
                        String dbPassword,
                        boolean reload) {
        this( DEFAULT_DB_LAYOUT, dbUrl, dbDriver, dbType, dbUser, dbPassword, reload );
    }

    /**
     * initialize the connection
     *
     * @return true if the connection is intialized, false if the connection is
     *         not initialized (i.e. there was a problem instantiating the
     *         Driver)
     */
    private boolean validateDriver() {
        if (initialized)
            return true;

        try {
            // Instantiate database driver
            if (dbDriver == null) {
                throw new ClassNotFoundException("Database driver is null");
            }
            Class.forName(dbDriver);
        } catch (ClassNotFoundException e) {
            LOG.info("Failed to load the driver for the database: "
                    + e.getMessage());
            return false;
        }

        // initialize the dataset
        initialized = true;
        return true;
    }

    /**
     * Retrieves a com.hp.hpl.jena.query.Dataset object, representing 1 or more
     * Jena models
     *
     * @return
     */
    public Dataset getDataset() {
        if (dataset == null) {
            synchronized (this) {
                if (dataset == null) {
                    dataset = SDBFactory.connectDataset(getStore());
                }
            }
        }
        return dataset;

    }

    /**
     * Retrieves a com.hp.hpl.jena.query.Dataset object, representing 1 or more
     * Jena models
     *
     * @return
     */
    public Store getStore() {
        if (store != null) {
            return store;
        }
        if (!initialized) {
            boolean successInitializing = validateDriver();
            if (!successInitializing)
                return null;
        }
        StoreDesc storeDesc = new StoreDesc(dbLayout, dbType);
        SDBConnection conn = null;
        try {
            conn = SDBFactory.createConnection(getJdbcConnection());
        } catch (SQLException e) {
            LOG.error(" error getting jdbc connection caused by: " + e);
            return null;
        }
        store = SDBFactory.connectStore(conn, storeDesc);

        LOG.info("retrieved store: dbUrl=" + dbUrl + "; dbLayout=" + dbLayout
                + "; dbType=" + dbType + "; dbUser=" + dbUser + "; dbPassword="
                + dbPassword);
        return store;
    }

    /**
     * Retrieves a Jena Model. Ensure that the SDB Store is created first, such
     * that the database is an SDB one instead of an ARQ one.
     *
     * @param modelName the name of the named graph to retrieve. If null or blank,
     *                  retrieve the default graph
     * @return the Model object
     */
    public Model getModel(String modelName) {
        //synchronized(this){
        Store store = getStore();
        // initialize SDB's connection to the dataset
        // getDataset();
        Model model = null;
        if (modelName == null || modelName.length() == 0) {
            model = SDBFactory.connectDefaultModel(store);
        } else {
            model = SDBFactory.connectNamedModel(store, modelName);
        }
        return model;
        //}
    }

    public void close() {
        //synchronized(this) {
        if (!storeIsClosed) {
            synchronized (this) {
                if (!storeIsClosed) {
                    LOG.debug("Closing store");
                    try {
                        store.close();
                        storeIsClosed = true;
                    } catch (Exception e) {
                        LOG.error("Error closing store caused by : ", e);
                    }
                }
            }
        }
        //}
    }

    public void closeConnection() {
        LOG.info("Closing Connection to DB");
        synchronized (this) {
            try {
                store.getConnection().close();
                store.close();
            } catch (Exception e) {
                LOG.error("Error closing connection caused by : ", e);
            }
        }
    }

    /**
     * Get a java.sql.Connection object, for Jena SDB queries (and anything else
     * that might need JDBC connection).
     *
     * @return a java.sql.Connection to the database
     * @throws SQLException
     */
    public java.sql.Connection getJdbcConnection() throws SQLException {
        synchronized (this) {
            if (!initialized) {
                boolean successInitializing = validateDriver();
                if (!successInitializing)
                    return null;
            }
            if (jdbcConnection == null) {
                jdbcConnection = DriverManager.getConnection(dbUrl, dbUser, dbPassword);
            }
            return jdbcConnection;
        }
    }

    /**
     * Get a Jena com.hp.hpl.jena.db.DBConnection object, for Jena ARQ queries
     *
     * @return
     */
    public DBConnection getJenaConnection() {
        if (!initialized) {
            boolean successInitializing = validateDriver();
            if (!successInitializing)
                return null;
        }
        return new DBConnection(dbUrl, dbUser, dbPassword, dbType);
    }

    /**
     * @return true if able to get a JDBC connection
     */
    public boolean testConnection() {
        try {
            // DBConnection testConn = getJenaConnection();
            // ExtendedIterator allModelNames = testConn.getAllModelNames();
            // log.info("^^^Got Jena DBConnection:" + testConn + "\n" +
            // allModelNames);
            Store store = getStore();
            LOG.info("Testing store: retrieved store " + store);
            if (store == null)
                return false;
            // When store has not yet had statements inserted, SDB API seems to
            // return a store but this store throws an error on getSize()
            // method.
            // long storeSize = store.getSize();
            // log.info("Testing store: has size " + storeSize);
            return true;
        } catch (Exception e) {
            LOG.info("Testing store: fails.", e);
            return false;
        }
    }

    /**
     * This creates a new SDB store. ** CAUTION: This deletes the store if it
     * already exists! **
     */
    public void createSDBStore() {
        LOG.info("createSDBStore()...");
        try {
            getStore().getTableFormatter().create();
        } catch (Exception e) {
            LOG.error("Unable to createSDBStore()", e);
        }
    }

    /**
     * Create a new SDB store only if it does not yet exist
     *
     * @return DBConnector.status
     */
    public int tryToCreateSDBStore() {
        LOG.info("Trying to create store...");
        int status = 0;
        Store store = getStore();

        try {
            // When store has not yet been created, SDB API will return a store
            // but this store throws an error on getSize() method.
            long storeSize = store.getSize();
            LOG.info("Store already has size " + storeSize);
            if (storeSize > 0) {
                status = STORE_HAS_STATEMENTS;
            } else {
                status = STORE_IS_BLANK;
            }
        } catch (Exception e) {
            LOG.error("Unable to connect to store "
                    + dbUser + "@"
                    + dbUrl + "\nCreating this store...");
            createSDBStore();
            status = STORE_CREATED;
        }

        return status;
    }

    /**
     * @return true if successful; false if an error occurred
     */
    public boolean deleteSDBStore() {
        Store store = getStore();
        try {
            store.getTableFormatter().truncate();
            return true;
        } catch (Exception e) {
            LOG.error("Unable to delete store", e);
            return false;
        }
    }

    public OntModel getOntModel(String modelName) {
        synchronized (this) {
            if (reload) {
                LOG.info("Reloading SDB Store...");
                createSDBStore();
            } else {
                tryToCreateSDBStore();
            }
            Model baseModel = getModel(modelName);

            OntModelSpec modelSpec = new OntModelSpec(PelletReasonerFactory.THE_SPEC);
            //OntModelSpec modelSpec =  OntModelSpec.OWL_MEM_MINI_RULE_INF;
            // modelSpec.setImportModelMaker( getModelMaker() );

            OntModel ontModel = ModelFactory.createOntologyModel(modelSpec,
                    baseModel);
            ontModel.prepare();
            return ontModel;
        }
    }


    public Store getSDBStore() throws SQLException {
        synchronized (this) {
            if (storeIsClosed) {
                store = reconnect();
                return store;
            }

            if (store != null) {
                return store;
            }

            StoreDesc storeDesc = new StoreDesc(dbLayout, dbType);
            store = SDBFactory.connectStore(getSDBConnection(), storeDesc);
            return store;
        }
    }

    /**
     * This creates a new SDB store. ** CAUTION: This deletes the store if it
     * already exists! **
     */
    public void createStore() {
        LOG.info("createSDBStore()...");
        try {
            getSDBStore().getTableFormatter().create();
        } catch (Exception e) {
            LOG.error("Unable to createSDBStore()", e);
        }
    }

    /**
     * Create a new SDB store only if it does not yet exist
     *
     * @return DBConnector.status
     */
    public int tryToCreateStore()  throws SQLException {
        LOG.info("Trying to create store...");
        int status = 0;
        Store store = getSDBStore();

        try {
            // When store has not yet been created, SDB API will return a store
            // but this store throws an error on getSize() method.
            long storeSize = store.getSize();
            LOG.info("Store already has size " + storeSize);
            if (storeSize > 0) {
                status = STORE_HAS_STATEMENTS;
            } else {
                status = STORE_IS_BLANK;
            }
        } catch (Exception e) {
            LOG.error("Unable to connect to store");
            createStore();
            status = STORE_CREATED;
        }

        return status;
    }

    /**
     * Retrieves a Jena Model. Ensure that the SDB Store is created first, such
     * that the database is an SDB one instead of an ARQ one.
     *
     * @param modelName the name of the named graph to retrieve. If null or blank,
     *                  retrieve the default graph
     * @return the Model object
     */
    public Model getJenaModel(String modelName) throws SQLException {
        Store store = getSDBStore();

        Model model = null;
        if (modelName == null || modelName.length() == 0) {
            model = SDBFactory.connectDefaultModel(store);
        } else {
            model = SDBFactory.connectNamedModel(store, modelName);
        }
        return model;
    }

    public OntModel getJenaOntModel(String modelName) throws SQLException {
        if (ontModel == null) {
            synchronized (this) {
                if (ontModel == null) {

                    if (reload) {
                        LOG.debug("Reloading SDB Store...");
                        createStore();
                    } else {
                        tryToCreateStore();
                    }
                    Model baseModel = getJenaModel(modelName);

                    OntModelSpec modelSpec = new OntModelSpec(PelletReasonerFactory.THE_SPEC);
                    //OntModelSpec modelSpec =  OntModelSpec.OWL_MEM_MINI_RULE_INF;
                    // modelSpec.setImportModelMaker( getModelMaker() );

                    ontModel = ModelFactory.createOntologyModel(modelSpec,
                            baseModel);
                    ontModel.prepare();
                }
            }
        } else if (storeIsClosed) {
            synchronized (this) {
                if (storeIsClosed)
                    ontModel = ModelFactory.createOntologyModel(new OntModelSpec(PelletReasonerFactory.THE_SPEC),
                            DatasetStore.create(getSDBStore()).getNamedModel(modelName));
            }
        }
        return ontModel;
    }


    public Store reconnect() throws SQLException {
        if (storeIsClosed) {
            synchronized (this) {
                if (storeIsClosed) {
                    LOG.debug("Store is Closed, Reconnecting store");
                    StoreDesc storeDesc = new StoreDesc(dbLayout, dbType);
                    store = SDBFactory.connectStore(getJdbcConnection(), storeDesc);
                    storeIsClosed = false;
                }
            }
        }

        return store;


    }

    private SDBConnection getSDBConnection() {
        if (sdbConnection == null) {
            synchronized (this) {
                if (sdbConnection == null) {
                    LOG.debug("Creating SDB Database Connection");
                    try {
                        sdbConnection = SDBFactory.createConnection(getJdbcConnection());
                    } catch (SQLException e) {
                        LOG.error(" error getting datasource connection caused by: " + e);
                        return null;
                    }
                }
            }
        }
        return sdbConnection;

    }

}
