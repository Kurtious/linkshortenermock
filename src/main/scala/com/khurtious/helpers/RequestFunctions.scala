package com.khurtious.helpers

import javax.servlet.http.HttpServletRequest
import java.net.URL

import scala.util.Try

/**
 * helper trait to enable request processing
 */
trait RequestFunctions {

  def getClientIpAddr(request: HttpServletRequest ): String  =  {
    var ip = request.getHeader("X-Forwarded-For")
    if (ip == null || ip.length == 0 || "unknown".equalsIgnoreCase(ip)) {
      ip = request.getHeader("Proxy-Client-IP")
    }
    if (ip == null || ip.length == 0 || "unknown".equalsIgnoreCase(ip)) {
      ip = request.getHeader("WL-Proxy-Client-IP")
    }
    if (ip == null || ip.length == 0 || "unknown".equalsIgnoreCase(ip)) {
      ip = request.getHeader("HTTP_CLIENT_IP")
    }
    if (ip == null || ip.length == 0 || "unknown".equalsIgnoreCase(ip)) {
      ip = request.getHeader("HTTP_X_FORWARDED_FOR")
    }
    if (ip == null || ip.length == 0 || "unknown".equalsIgnoreCase(ip)) {
      ip = request.getRemoteAddr
    }
    return ip
  }

  def validUrl(url: String): Boolean = {
      Try { new URL(url) }.toOption match {
        case Some(r) => true
        case None => false
      }
  }

}
