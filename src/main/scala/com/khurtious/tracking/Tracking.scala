package com.khurtious.tracking

import javax.servlet.http.HttpServletRequest

import com.khurtious.model.Tables.{MongoTrackingEntry, MongoEntryDocuments}
import com.mongodb.casbah.{ MongoCollection}
import com.mongodb.casbah.commons.{ MongoDBObject}
import com.mongodb.casbah.Imports._

import org.joda.time.DateTime

/**
 * adds tracking capability
 */
trait Tracking {
  val track: MongoCollection

  def trackRedirect( entry: MongoEntryDocuments, request: HttpServletRequest ): Unit = {
      val query =
        MongoDBObject("shortUrl" ->  entry.shortUrl) ++
        MongoDBObject("longUrl" -> entry.longUrl) ++
        MongoDBObject("bucketIndex" -> entry.bucketInd) ++
        MongoDBObject("ip" -> request.getRemoteAddr) ++
        MongoDBObject("lang" -> request.getHeader("accept-language")) ++
        MongoDBObject("userAgent" -> request.getHeader("User-Agent"))
        MongoDBObject("time" -> DateTime.now.formatted("YYYY-mm-dd HH"))
      val update =
        MongoDBObject("$inc" -> MongoDBObject("count" -> 1))

      track findAndModify(query = query, update = update) match {
        case None => track += query ++ MongoDBObject("count" -> 1)
        case Some(_) =>
      }
  }

  def getAllTrackingRecord: List[MongoTrackingEntry] ={
    track.find().map( e => MongoTrackingEntry(
        e.getAs[String]("shortUrl").getOrElse(""),
        e.getAs[String]("longUrl").getOrElse(""),
        e.getAs[Int]("bucketIndex").getOrElse(0),
        e.getAs[String]("ip").getOrElse(""),
        e.getAs[String]("lang").getOrElse(""),
        e.getAs[String]("userAgent").getOrElse(""),
        e.getAs[String]("time").getOrElse(""),
        e.getAs[Int]("count").getOrElse(0)
      )
    ).toList
  }
}
