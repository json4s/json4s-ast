package org.json4s.ast.safe

sealed abstract class JValue extends Product with Serializable

case object JNull extends JValue

case class JString(value: String) extends JValue

object JNumber{
  private val mc = BigDecimal.defaultMathContext
  def apply(value: Int): JNumber = JNumber(BigDecimal(value))
  def apply(value: Byte): JNumber = JNumber(BigDecimal(value))
  def apply(value: Short): JNumber = JNumber(BigDecimal(value))
  def apply(value: Long): JNumber = JNumber(BigDecimal(value))
  def apply(value: BigInt): JNumber = JNumber(BigDecimal(value))
  def apply(value: Float): JNumber = JNumber({
    // BigDecimal.decimal doesn't exist on 2.10, so this is just the 2.11 implementation
    new BigDecimal(new java.math.BigDecimal(java.lang.Float.toString(value), mc), mc)
  })
  def apply(value: Double): JNumber = JNumber(BigDecimal(value))
}

case class JNumber(value: BigDecimal) extends JValue {
  def to[B](implicit bigDecimalConverter: JNumberConverter[B]) = bigDecimalConverter(value)
}

/**
 * Implements named extractors so we can avoid boxing
 */

sealed abstract class JBoolean extends JValue {
  def isEmpty: Boolean
  def get: Boolean
}

object JBoolean {
  def apply(x: Boolean): JBoolean = if (x) JTrue else JFalse
  def unapply(x: JBoolean): Some[Boolean] = Some(x.isEmpty)
}

case object JTrue extends JBoolean {
  def isEmpty = false
  def get = true
}

case object JFalse extends JBoolean {
  def isEmpty = false
  def get = false
}

case class JObject(value: Map[String,JValue] = Map.empty) extends JValue

object JArray {
  def apply(value: JValue, values: JValue*): JArray = JArray(value +: values.to[Vector])
}

case class JArray(value: Vector[JValue] = Vector.empty) extends JValue