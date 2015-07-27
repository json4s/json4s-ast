package org.json4s

package object ast {
  trait JNumberConverter[T]{  def apply(b: BigDecimal): T }

  object JNumberConverter {
    implicit val JNumberToByte = new JNumberConverter[Byte] {
      def apply(b: BigDecimal): Byte = b.byteValue()
    }

    implicit val JNumberToInt = new JNumberConverter[Int] {
      def apply(b: BigDecimal): Int = b.intValue()
    }

    implicit val JNumberToShort = new JNumberConverter[Short] {
      def apply(b: BigDecimal): Short = b.shortValue()
    }

    implicit val JNumberToLong = new JNumberConverter[Long] {
      def apply(b: BigDecimal): Long = b.longValue()
    }

    implicit val JNumberToBigInt = new JNumberConverter[BigInt] {
      def apply(b: BigDecimal): BigInt = b.toBigInt()
    }

    implicit val JNumberToFloat = new JNumberConverter[Float] {
      def apply(b: BigDecimal): Float = b.floatValue()
    }

    implicit val JNumberToDouble = new JNumberConverter[Double] {
      def apply(b: BigDecimal): Double = b.doubleValue()
    }

    implicit val JNumberToBigDecimal = new JNumberConverter[BigDecimal] {
      def apply(b: BigDecimal): BigDecimal = b
    }

  }
}
