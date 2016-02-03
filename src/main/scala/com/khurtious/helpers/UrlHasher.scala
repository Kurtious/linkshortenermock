package com.khurtious.helpers
import scala.util.hashing.MurmurHash3;

object UrlHasher {
  import java.security.MessageDigest

  def md5(s: String) = {
    val code = MurmurHash3.stringHash(s)
    Integer.toHexString(code)
  }

}
