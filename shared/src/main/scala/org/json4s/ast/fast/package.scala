package org.json4s.ast

package object fast {
  trait JNumberConverter[T]{  def apply(s: String): T }

  object JNumberConverter {
    implicit val JNumberString2BigDecimal = new JNumberConverter[BigDecimal] {
      def apply(s: String): BigDecimal = BigDecimal(s)
    }

    implicit val JNumberString2Long = new JNumberConverter[Long] {
      def apply(s: String): Long = s.toLong
    }

    implicit val JNumberString2Int = new JNumberConverter[Int] {
      def apply(s: String): Int = s.toInt
    }

    implicit val JNumberString2Double = new JNumberConverter[Double] {
      def apply(s: String): Double = s.toDouble
    }

    implicit val JNumberString2Float = new JNumberConverter[Float] {
      def apply(s: String): Float = s.toFloat
    }

    implicit val JNumberString2Byte = new JNumberConverter[Byte] {
      def apply(s: String): Byte = s.toByte
    }

    implicit val JNumberString2BigInt = new JNumberConverter[BigInt] {
      def apply(s: String): BigInt = BigInt(s)
    }

    implicit val JNumberString2Short = new JNumberConverter[Short] {
      def apply(s: String): Short = s.toShort
    }

    implicit val JNumberString2String = new JNumberConverter[String] {
      def apply(s: String): String = s
    }
  }
}