package org.json4s

package object bigDecimalConverter {
  trait BigDecimalConverter[T]{  def apply(b: BigDecimal): T }

  object BigDecimalConverter {
    implicit val bigDecimalToByte = new BigDecimalConverter[Byte] {
      @inline def apply(b: BigDecimal): Byte = b.byteValue()
    }

    implicit val bigDecimalToInt = new BigDecimalConverter[Int] {
      @inline def apply(b: BigDecimal): Int = b.intValue()
    }

    implicit val bigDecimalToShort = new BigDecimalConverter[Short] {
      @inline def apply(b: BigDecimal): Short = b.shortValue()
    }

    implicit val bigDecimalToLong = new BigDecimalConverter[Long] {
      @inline def apply(b: BigDecimal): Long = b.longValue()
    }

    implicit val bigDecimalToBigInt = new BigDecimalConverter[BigInt] {
      @inline def apply(b: BigDecimal): BigInt = b.toBigInt()
    }

    implicit val bigDecimalToFloat = new BigDecimalConverter[Float] {
      @inline def apply(b: BigDecimal): Float = b.floatValue()
    }

    implicit val bigDecimalToDouble = new BigDecimalConverter[Double] {
      @inline def apply(b: BigDecimal): Double = b.doubleValue()
    }

    implicit val bigDecimalToBigDecimal = new BigDecimalConverter[BigDecimal] {
      @inline def apply(b: BigDecimal): BigDecimal = b
    }

  }
}
