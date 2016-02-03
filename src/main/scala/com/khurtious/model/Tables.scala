package com.khurtious.model

import scala.slick.driver.H2Driver.simple._

object Tables {

  /**
   * @param shortUrl hash value from the long Url
   * @param longUrl long Url saved for Redirection Service
   * @param bucketInd in case of hash conflict we assign same short Url, but another bucket (Can be used to shard/distribute logically)
   */
  case class MongoEntryDocuments (
                                   shortUrl: String,
                                   longUrl: String,
                                   bucketInd: Int)

  /**
   *
   * @param shortUrl hash value from the long Url
   * @param longUrl long Url saved for Redirection Service
   * @param bucketInd in case of hash conflict we assign same short Url, but another bucket (Can be used to shard/distribute logically)
   * @param ip remote IP for tracking purposes
   * @param lang request language for demographic
   * @param userAgent request agent (browser) for demographic tracking
   * @param time request time window with hourly granularity
   * @param count number of requests hitting short Url
   */
  case class MongoTrackingEntry (
                                  shortUrl: String,
                                  longUrl: String,
                                  bucketInd: Int,
                                  ip: String,
                                  lang: String,
                                  userAgent: String,
                                  time: String,
                                  count: Int)

  /**
   * used for SQL based storage solution (either H2 or MySQL)
   */
  class Entry(tag: Tag) extends Table[(Int, String, String, Int)]( tag, "ENTRIES") {
    def id      = column[Int]("ID", O.PrimaryKey, O.AutoInc)
    def shortUrl = column[String]("SHORTURL")
    def longUrl = column[String]("LONGURL")
    def bucketIndex = column[Int]("BINDEX")
    def * = (id, shortUrl, longUrl, bucketIndex)
  }
  val entries = TableQuery[Entry]

  class TrackingInfo(tag: Tag) extends Table[(Int, String, String, String)](tag, "TRACKING") {
    def id    = column[Int]("ID", O.PrimaryKey, O.AutoInc)
    def userAgent = column[String]("USER_AGENT")
    def acceptLang = column[String]("LANG")
    def time = column[String]("TIME")
    def * = (id, userAgent, acceptLang, time)

    def entry = foreignKey("E_FK", id, entries)(_.id)
  }
  val tracking = TableQuery[TrackingInfo]

}
