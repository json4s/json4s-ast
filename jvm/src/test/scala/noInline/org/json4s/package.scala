package noInline.org

package object json4s {
  trait BigDecimalConverter[T]{  def apply(b: BigDecimal): T }

  object BigDecimalConverter {
    implicit val bigDecimalToByte = new BigDecimalConverter[Byte] {
      def apply(b: BigDecimal): Byte = b.byteValue()
    }

    implicit val bigDecimalToInt = new BigDecimalConverter[Int] {
      def apply(b: BigDecimal): Int = b.intValue()
    }

    implicit val bigDecimalToShort = new BigDecimalConverter[Short] {
      def apply(b: BigDecimal): Short = b.shortValue()
    }

    implicit val bigDecimalToLong = new BigDecimalConverter[Long] {
      def apply(b: BigDecimal): Long = b.longValue()
    }

    implicit val bigDecimalToBigInt = new BigDecimalConverter[BigInt] {
      def apply(b: BigDecimal): BigInt = b.toBigInt()
    }

    implicit val bigDecimalToFloat = new BigDecimalConverter[Float] {
      def apply(b: BigDecimal): Float = b.floatValue()
    }

    implicit val bigDecimalToDouble = new BigDecimalConverter[Double] {
      def apply(b: BigDecimal): Double = b.doubleValue()
    }

    implicit val bigDecimalToBigDecimal = new BigDecimalConverter[BigDecimal] {
      def apply(b: BigDecimal): BigDecimal = b
    }

  }
}
