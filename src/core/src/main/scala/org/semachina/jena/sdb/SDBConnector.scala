package org.semachina.jena.sdb

import com.hp.hpl.jena.db.DBConnection
import com.hp.hpl.jena.ontology.OntModel
import com.hp.hpl.jena.ontology.OntModelSpec
import com.hp.hpl.jena.query.Dataset
import com.hp.hpl.jena.rdf.model.Model
import com.hp.hpl.jena.rdf.model.ModelFactory
import com.hp.hpl.jena.sdb.SDBFactory
import com.hp.hpl.jena.sdb.Store
import com.hp.hpl.jena.sdb.StoreDesc
import com.hp.hpl.jena.sdb.sql.SDBConnection
import com.hp.hpl.jena.sdb.store.DatasetStore
import org.apache.log4j.Logger

import java.sql.DriverManager
import java.sql.SQLException
import java.sql.Connection
import org.mindswap.pellet.jena.PelletReasonerFactory

class SDBConnector(
                    dbLayout: String = "layout2/index",
                    dbUrl: String,
                    dbDriver: String,
                    dbType: String,
                    dbUser: String,
                    dbPassword: String,
                    reload: Boolean) {

  /**
   * This store already exists in the database, and has statements
   */
  final val STORE_HAS_STATEMENTS: Int = -2
  /**
   * This store already exists in the database, and it is blank
   */
  final val STORE_IS_BLANK: Int = -1
  /**
   * A store was just created in the database
   */
  final val STORE_CREATED: Int = 1
  private[sdb] var LOG: Logger = Logger.getLogger(getClass)
  private var store: Store = null
  private var dataset: Dataset = null
  private var initialized: Boolean = false
  private var DEFAULT_DB_LAYOUT: String = "layout2/index"
  private var jdbcConnection: Connection = null

  validateDriver

  /**
   * initialize the connection
   *
   * @return true if the connection is intialized, false if the connection is
   *         not initialized (i.e. there was a problem instantiating the
   *         Driver)
   */
  private def validateDriver: Boolean = {
    if (initialized) return true
    try {
      if (dbDriver == null) {
        throw new ClassNotFoundException("Database driver is null")
      }
      Class.forName(dbDriver)
    }
    catch {
      case e: ClassNotFoundException => {
        LOG.info("Failed to load the driver for the database: " + e.getMessage)
        return false
      }
    }
    initialized = true
    return true
  }

  /**
   * Retrieves a com.hp.hpl.jena.query.Dataset object, representing 1 or more
   * Jena models
   *
   * @return
   */
  def getDataset: Dataset = {
    if (dataset == null) {
      this synchronized {
        if (dataset == null) {
          dataset = SDBFactory.connectDataset(getStore)
        }
      }
    }
    return dataset
  }

  /**
   * Retrieves a com.hp.hpl.jena.query.Dataset object, representing 1 or more
   * Jena models
   *
   * @return
   */
  def getStore: Store = {
    if (store != null) {
      return store
    }
    if (!initialized) {
      var successInitializing: Boolean = validateDriver
      if (!successInitializing) return null
    }
    var storeDesc: StoreDesc = new StoreDesc(dbLayout, dbType)
    var conn: SDBConnection = null
    try {
      conn = SDBFactory.createConnection(getJdbcConnection)
    }
    catch {
      case e: SQLException => {
        LOG.error(" error getting jdbc connection caused by: " + e)
        return null
      }
    }
    store = SDBFactory.connectStore(conn, storeDesc)
    LOG.info("retrieved store: dbUrl=" + dbUrl + "; dbLayout=" + dbLayout + "; dbType=" + dbType + "; dbUser=" + dbUser + "; dbPassword=" + dbPassword)
    return store
  }

  /**
   * Retrieves a Jena Model. Ensure that the SDB Store is created first, such
   * that the database is an SDB one instead of an ARQ one.
   *
   * @param modelName the name of the named graph to retrieve. If null or blank,
   *                  retrieve the default graph
   * @return the Model object
   */
  def getModel(modelName: String = null): Model = {
    var store: Store = getStore
    var model: Model = null
    if (modelName == null || modelName.length == 0) {
      model = SDBFactory.connectDefaultModel(store)
    }
    else {
      model = SDBFactory.connectNamedModel(store, modelName)
    }
    return model
  }

  def close: Unit = {
    if (!storeIsClosed) {
      this synchronized {
        if (!storeIsClosed) {
          LOG.debug("Closing store")
          try {
            store.close
            storeIsClosed = true
          }
          catch {
            case e: Exception => {
              LOG.error("Error closing store caused by : ", e)
            }
          }
        }
      }
    }
  }

  def closeConnection: Unit = {
    LOG.info("Closing Connection to DB")
    this synchronized {
      try {
        store.getConnection.close
        store.close
      }
      catch {
        case e: Exception => {
          LOG.error("Error closing connection caused by : ", e)
        }
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
  def getJdbcConnection: Connection = {
    this synchronized {
      if (!initialized) {
        var successInitializing: Boolean = validateDriver
        if (!successInitializing) return null
      }
      if (jdbcConnection == null) {
        jdbcConnection = DriverManager.getConnection(dbUrl, dbUser, dbPassword)
      }
      return jdbcConnection
    }
  }

  /**
   * Get a Jena com.hp.hpl.jena.db.DBConnection object, for Jena ARQ queries
   *
   * @return
   */
  def getJenaConnection: DBConnection = {
    if (!initialized) {
      var successInitializing: Boolean = validateDriver
      if (!successInitializing) return null
    }
    return new DBConnection(dbUrl, dbUser, dbPassword, dbType)
  }

  /**
   * @return true if able to get a JDBC connection
   */
  def testConnection: Boolean = {
    try {
      var store: Store = getStore
      LOG.info("Testing store: retrieved store " + store)
      if (store == null) return false
      return true
    }
    catch {
      case e: Exception => {
        LOG.info("Testing store: fails.", e)
        return false
      }
    }
  }

  /**
   * This creates a new SDB store. ** CAUTION: This deletes the store if it
   * already exists! **
   */
  def createSDBStore: Unit = {
    LOG.info("createSDBStore()...")
    try {
      getStore.getTableFormatter.create
    }
    catch {
      case e: Exception => {
        LOG.error("Unable to createSDBStore()", e)
      }
    }
  }

  /**
   * Create a new SDB store only if it does not yet exist
   *
   * @return DBConnector.status
   */
  def tryToCreateSDBStore: Int = {
    LOG.info("Trying to create store...")
    var status: Int = 0
    var store: Store = getStore
    try {
      var storeSize: Long = store.getSize
      LOG.info("Store already has size " + storeSize)
      if (storeSize > 0) {
        status = STORE_HAS_STATEMENTS
      }
      else {
        status = STORE_IS_BLANK
      }
    }
    catch {
      case e: Exception => {
        LOG.error("Unable to connect to store " + dbUser + "@" + dbUrl + "\nCreating this store...")
        createSDBStore
        status = STORE_CREATED
      }
    }
    return status
  }

  /**
   * @return true if successful; false if an error occurred
   */
  def deleteSDBStore: Boolean = {
    var store: Store = getStore
    try {
      store.getTableFormatter.truncate
      return true
    }
    catch {
      case e: Exception => {
        LOG.error("Unable to delete store", e)
        return false
      }
    }
  }

  def getOntModel(modelName: String): OntModel = {
    this synchronized {
      if (reload) {
        LOG.info("Reloading SDB Store...")
        createSDBStore
      }
      else {
        tryToCreateSDBStore
      }
      var baseModel: Model = getModel(modelName)
      var modelSpec: OntModelSpec = new OntModelSpec(PelletReasonerFactory.THE_SPEC)
      var ontModel: OntModel = ModelFactory.createOntologyModel(modelSpec, baseModel)
      ontModel.prepare
      return ontModel
    }
  }

  def getSDBStore: Store = {
    this synchronized {
      if (storeIsClosed) {
        store = reconnect
        return store
      }
      if (store != null) {
        return store
      }
      var storeDesc: StoreDesc = new StoreDesc(dbLayout, dbType)
      store = SDBFactory.connectStore(getSDBConnection, storeDesc)
      return store
    }
  }

  /**
   * This creates a new SDB store. ** CAUTION: This deletes the store if it
   * already exists! **
   */
  def createStore: Unit = {
    LOG.info("createSDBStore()...")
    try {
      getSDBStore.getTableFormatter.create
    }
    catch {
      case e: Exception => {
        LOG.error("Unable to createSDBStore()", e)
      }
    }
  }

  /**
   * Create a new SDB store only if it does not yet exist
   *
   * @return DBConnector.status
   */
  def tryToCreateStore: Int = {
    LOG.info("Trying to create store...")
    var status: Int = 0
    var store: Store = getSDBStore
    try {
      var storeSize: Long = store.getSize
      LOG.info("Store already has size " + storeSize)
      if (storeSize > 0) {
        status = STORE_HAS_STATEMENTS
      }
      else {
        status = STORE_IS_BLANK
      }
    }
    catch {
      case e: Exception => {
        LOG.error("Unable to connect to store")
        createStore
        status = STORE_CREATED
      }
    }
    return status
  }

  /**
   * Retrieves a Jena Model. Ensure that the SDB Store is created first, such
   * that the database is an SDB one instead of an ARQ one.
   *
   * @param modelName the name of the named graph to retrieve. If null or blank,
   *                  retrieve the default graph
   * @return the Model object
   */
  def getJenaModel(modelName: String = null): Model = {
    var store: Store = getSDBStore
    var model: Model = null
    if (modelName == null || modelName.length == 0) {
      model = SDBFactory.connectDefaultModel(store)
    }
    else {
      model = SDBFactory.connectNamedModel(store, modelName)
    }
    return model
  }

  def getJenaOntModel(modelName: String): OntModel = {
    if (ontModel == null) {
      this synchronized {
        if (ontModel == null) {
          if (reload) {
            LOG.debug("Reloading SDB Store...")
            createStore
          }
          else {
            tryToCreateStore
          }
          var baseModel: Model = getJenaModel(modelName)
          var modelSpec: OntModelSpec = new OntModelSpec(PelletReasonerFactory.THE_SPEC)
          ontModel = ModelFactory.createOntologyModel(modelSpec, baseModel)
          ontModel.prepare
        }
      }
    }
    else if (storeIsClosed) {
      this synchronized {
        if (storeIsClosed) ontModel = ModelFactory.createOntologyModel(new OntModelSpec(PelletReasonerFactory.THE_SPEC), DatasetStore.create(getSDBStore).getNamedModel(modelName))
      }
    }
    return ontModel
  }

  def reconnect: Store = {
    if (storeIsClosed) {
      this synchronized {
        if (storeIsClosed) {
          LOG.debug("Store is Closed, Reconnecting store")
          var storeDesc: StoreDesc = new StoreDesc(dbLayout, dbType)
          store = SDBFactory.connectStore(getJdbcConnection, storeDesc)
          storeIsClosed = false
        }
      }
    }
    return store
  }

  private def getSDBConnection: SDBConnection = {
    if (sdbConnection == null) {
      this synchronized {
        if (sdbConnection == null) {
          LOG.debug("Creating SDB Database Connection")
          try {
            sdbConnection = SDBFactory.createConnection(getJdbcConnection)
          }
          catch {
            case e: SQLException => {
              LOG.error(" error getting datasource connection caused by: " + e)
              return null
            }
          }
        }
      }
    }
    return sdbConnection
  }

  private var storeIsClosed: Boolean = false
  private var sdbConnection: SDBConnection = null
  private var ontModel: OntModel = null
}