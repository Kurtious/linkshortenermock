package com.khurtious

import com.khurtious.db.{MongoDataFunctions}
import com.khurtious.helpers.{RequestFunctions, UrlHasher}
import com.khurtious.tracking.Tracking
import com.mongodb.casbah.MongoCollection
import org.slf4j.LoggerFactory
import org.scalatra._
import net.liftweb.json.JsonDSL._
import net.liftweb.json._
import net.liftweb.json.Serialization.{write}

/**
 *
 * @param links storage for links and shortened links
 * @param tracking storage for analytics
 */
class LinkShortenerServlet(links: MongoCollection, tracking: MongoCollection) extends LinkShortenerAppStack with MongoDataFunctions with RequestFunctions with Tracking {
  implicit val formats = Serialization.formats(NoTypeHints)
  val logger = LoggerFactory.getLogger(getClass)
  val db = links
  val track = tracking

  /**
   * root landing with a simple instruction for usage
   */
  get("/") {
    <html>
      <body>
        <h1>Getting Started</h1>
        <p>GET /shorten?url=escaped_url => returns code</p>
        <p>GET /code => redirects url (eg /0/ercanfs123)</p>
        <p>GET /table => current entries in the table</p>
        <p>GET /tracking_table => for debug purpose, shows all records in trakcing</p>
        <p>GET /code => redirects url</p>
      </body>
    </html>
  }

  /**
   * redirection service once the short-link is hit
   */
  get("/:bucket/:code") {
    val bucket = params.getOrElse("bucket","")
    val shortened = params.getOrElse("code","")
    val entry = getLongUrl(Integer.parseInt(bucket),shortened)
    entry match {
      case None => Ok("unknown url")
      case Some(document) => {
        trackRedirect(document, request)
        redirect(document.longUrl)
      }
    }
  }

  /**
   * DEBUG PURPOSE: you way want to check the current state of the record ()
   */
  get("/table") {
    contentType = "text/html"
    table().map( document =>
      document.longUrl+" => "+
      document.bucketInd+"/"+
      document.shortUrl+" "
    ) mkString ("","<br />", "<br />")
  }

  /**
   * DEBUG PURPOSE: lists the tracking records
   */
  get("/tracking_table") {
    contentType = "text/html"
    getAllTrackingRecord.map( document =>
      document.time+"<br />"+
      document.longUrl+" => "+
      document.bucketInd+"/"+
      document.shortUrl+"<br />"+
      document.lang+"<br />"+
      document.userAgent+"<br />"+
      document.ip+"<br />"+
      " redirects="+document.count
    ) mkString ("","<br />", "<br />")
  }

  /**
   *
   */
  get("/shorten"){
    contentType = "application/json"
    params.get("url") match {
      case None => NotAcceptable("please submit URL as ?url=http://example.com")
      case Some(url) => {
        if (!validUrl(url)){
          NotAcceptable(url +" is not a valid url")
        } else {
          val shortened = UrlHasher.md5(url)
          val result = getShortUrl(url,shortened)
          val link = result.bucketInd+"/"+result.shortUrl
          val code = "link" -> link
          Ok(write(compact(render(code))))
        }
      }
    }
  }


}
