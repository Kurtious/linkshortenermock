package com.khurtious.db

import com.khurtious.model.Tables._
import com.mongodb.casbah.{ MongoCollection}
import com.mongodb.casbah.commons.{MongoDBObject}
import com.mongodb.casbah.Imports._

/**
 * MongoDB based data functions to separate Data logic from processing logic
 */
trait MongoDataFunctions {
  val db: MongoCollection

  /**
   *
   * @param bucket
   * @param code
   * @return returns the long url is entry exists
   */
  def getLongUrl( bucket: Int, code: String): Option[MongoEntryDocuments] = {

    val q = MongoDBObject("bucketIndex" -> bucket)++ MongoDBObject("shortUrl" -> code )
    val entry = for(
      e <- db.findOne(q)
    ) yield MongoEntryDocuments(
        e.getAs[String]("shortUrl").get,
        e.getAs[String]("longUrl").get,
        e.getAs[Int]("bucketIndex").get
      )
    entry
  }

  /**
   *
   * @param url
   * @param shortened
   * @return short Url as convenient Document case class
   */
  def getShortUrl( url: String, shortened: String): MongoEntryDocuments = {
    val query = MongoDBObject("longUrl" -> s"$url") ++ MongoDBObject("shortUrl" -> s"$shortened")
    val res = db.find(query)
    if (res.size > 0){
      val data = res.next()
      val short = data.getAs[String]("shortUrl").get
      val index = data.getAs[Int]("bucketIndex").get
      MongoEntryDocuments(short, url, index)
    } else {
      val hashed = MongoDBObject("shortUrl" -> shortened)
      val conflicting = db.find(hashed).sort(MongoDBObject("bucketIndex" -> -1)).limit(1)
      val res1 = for(d <- conflicting) yield d

      if(res1.nonEmpty){
        val d = res1.next()
        val index = d.getAs[Int]("bucketIndex").get
        MongoEntryDocuments(shortened, url, index)
      }else {
        db += (query ++ MongoDBObject("bucketIndex" -> 0))
        MongoEntryDocuments(shortened, url, 0)
      }
    }
  }

  /**
   *
   * @return all documents stored in the current data store
   */
  def table(): List[MongoEntryDocuments] ={
    db.find().map(d => MongoEntryDocuments(
      d.getAs[String]("shortUrl").get,
      d.getAs[String]("longUrl").get,
      d.getAs[Int]("bucketIndex").get)
    ).toList
  }
}
