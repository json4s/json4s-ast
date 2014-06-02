package org.json4s.ast

/**
 * Data type for JSON AST.
 */
sealed trait JValue

case object JUndefined extends JValue

case object JNull extends JValue

case class JString(s: String) extends JValue

case class JNumber(num: BigDecimal) extends JValue {
  def toByte = num.byteValue()
  def toShort = num.shortValue()
  def toInt = num.intValue()
  def toLong = num.longValue()
  def toBigInt = num.toBigInt()
  def toFloat = num.floatValue()
  def toDouble = num.doubleValue()
  def toBigDecimal = num
}

case class JBool(value: Boolean) extends JValue

case class JObject(obj: JField*) extends JValue {
  override def equals(that: Any): Boolean = that match {
    case o: JObject ⇒ obj.toSet == o.obj.toSet
    case _ ⇒ false
  }
  override def hashCode = obj.toSet[JField].hashCode
}

case class JArray(arr: JValue*) extends JValue

object JField {
  def apply(name: String, value: JValue) = (name, value)
  def unapply(f: JField): Option[(String, JValue)] = Some(f)
}