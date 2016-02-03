import com.khurtious.LinkShortenerServlet
import com.khurtious.db.DataInit
import com.mchange.v2.c3p0._
import com.mongodb.casbah.MongoClient
import com.typesafe.config.ConfigFactory

import org.scalatra._
import javax.servlet.ServletContext

import org.slf4j.LoggerFactory

class ScalatraBootstrap extends LifeCycle with DataInit {
  val logger = LoggerFactory.getLogger(getClass)
  val pool = new ComboPooledDataSource

  override def init(context: ServletContext) {
    val mongoClient =  MongoClient()
    val config = ConfigFactory.load()
    val mongoDb = config.getString("mongo.database.name")
    val linkdb = config.getString("mongo.database.linkdb")
    val trackingDb = config.getString("mongo.database.trackingdb")
    val links = mongoClient(mongoDb)(linkdb)
    val tracking = mongoClient(mongoDb)(trackingDb)

    context.mount(new LinkShortenerServlet(links, tracking), "/*")
  }

  override def destroy(context: ServletContext) {
    super.destroy(context)
    closeDbConnection
  }

  private def closeDbConnection() {
    logger.info("closing connection pool")
    pool.close
  }

}
