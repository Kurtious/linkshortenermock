package com.khurtious

import org.scalatra._
import scalate.ScalateSupport

trait LinkShortenerAppStack extends ScalatraServlet with ScalateSupport {

  notFound {
    contentType = null
    findTemplate(requestPath) map { path =>
      contentType = "text/html"
      layoutTemplate(path)
    } orElse serveStaticResource() getOrElse resourceNotFound()
  }

}
