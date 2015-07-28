package org.json4s.basic.ast

sealed abstract class JValue extends Serializable with Product

case object JNull extends JValue

case class JString(value:String) extends JValue

object JNumber {
  def apply(value: Int): JNumber = JNumber(value.toString)
  def apply(value: Byte): JNumber = JNumber(value.toString)
  def apply(value: Short): JNumber = JNumber(value.toString)
  def apply(value: Long): JNumber = JNumber(value.toString)
  def apply(value: BigInt): JNumber = JNumber(value.toString)
  def apply(value: BigDecimal): JNumber = JNumber(value.toString)
  def apply(value: Float): JNumber = JNumber(value.toString)
  def apply(value: Double): JNumber = JNumber(value.toString)
}

/**
 * JNumber is internally represented as a string, to improve performance
 * @param value
 */
case class JNumber(value: String) extends JValue {
  def to[B](implicit jNumberConverter: JNumberConverter[B]) = jNumberConverter(value)
}

sealed abstract class JBoolean extends JValue {
  val isTrue: Boolean
}

object JBoolean {
  def apply(x: Boolean): JBoolean = if (x) JTrue else JFalse
  def unapply(x: JBoolean): Some[Boolean] = Some(x.isTrue)
}
case object JTrue extends JBoolean {
  val isTrue = true
}

case object JFalse extends JBoolean {
  val isTrue = false
}

case class JField(field:String, value:JValue)

/**
 * JObject is internally represented as a mutable Array, to improve sequential performance
 * @param value
 */
case class JObject(value: Array[JField] = Array.empty) extends JValue

/**
 * JArray is internally represented as a mutable Array, to improve sequential performance
 * @param value
 */
case class JArray(value: Array[JValue] = Array.empty) extends JValue