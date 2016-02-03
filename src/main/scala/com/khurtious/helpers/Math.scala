package com.khurtious.helpers

object Math {

  def max(xs: List[Int]): Int = {
    if (xs.isEmpty) 0
    else {
      if( xs.head >= max(xs.tail) ) xs.head
      else max(xs.tail)
    }
  }

}
