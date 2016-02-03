package com.khurtious.db
import com.khurtious.model.Tables._
import scala.slick.driver.MySQLDriver.simple._

import net.liftweb.json.Serialization.{write}
import scala.slick.jdbc.JdbcBackend.Database

/**
 * Jdbc based data functions to separate Data logic from procssing logic
 */
trait DataFunctions {
  val db: Database

  def getLongUrl( bucket: Int, code: String): Option[String] = {
    db withSession { implicit session =>
      val data = entries.filter(h => h.shortUrl === code && h.bucketIndex === bucket)
      if (data.list.isEmpty){
        None
      } else {
        val l = data.map(_.longUrl).list(session)(0)
        Some(l)
      }
    }
  }

  def getShortUrl( url: String, shortened: String): String = {
    db withSession { implicit session =>
      val exists = entries.filter(h => h.longUrl === url)
      var index = 0
      if (exists.list.isEmpty){
        val duplicate = entries.filter(h => h.shortUrl === shortened && h.longUrl === url)
        if(duplicate.list.isEmpty){
          // no duplicates => create new entry
          entries.insert(0,shortened, url, index)
        } else {
          val entry = entries.filter(_.shortUrl === shortened)
          if(!entry.list.isEmpty) {
            // hash conflict => branch to new bucket
            val max = com.khurtious.helpers.Math.max(entry.map(_.bucketIndex).list)
            entries.insert(0, shortened, url, max + 1)
            index = max + 1
          }
        }
      } else {
        // known entry return the current shortened link
        index = exists.map(_.bucketIndex).first
      }
      index+"/"+shortened
    }
  }

  def table(): String ={
    db withSession { implicit session =>
      val data = for {
        e <- entries
      } yield (e.longUrl.asColumnOf[String], e.shortUrl.asColumnOf[String])
      data.list.map { case (s1, s2) => "  " + s1 + " supplied by " + s2 } mkString "<br />"
    }
  }

}
