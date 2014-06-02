package org.json4s.ast

/**
 * Data type for JSON AST.
 */
sealed trait JValue

case object JUndefined extends JValue

case object JNull extends JValue

case class JString(value: String) extends JValue

case class JNumber(value: BigDecimal) extends JValue {
  def toByte = value.byteValue()
  def toShort = value.shortValue()
  def toInt = value.intValue()
  def toLong = value.longValue()
  def toBigInt = value.toBigInt()
  def toFloat = value.floatValue()
  def toDouble = value.doubleValue()
  def toBigDecimal = value
}

case class JBool(value: Boolean) extends JValue

object JObject {
  def apply(field: JField, fields: JField*): JObject = new JObject(field +: fields)
}
case class JObject(value: Seq[JField] = Seq.empty) extends JValue {
  override def equals(that: Any): Boolean = that match {
    case o: JObject ⇒ value.toSet == o.value.toSet
    case _ ⇒ false
  }
  override def hashCode = value.toSet[JField].hashCode
}

object JArray {
  def apply(jvalue: JValue, jvalues: JValue*): JArray = new JArray(jvalue +: jvalues)
}
case class JArray(value: Seq[JValue] = Seq.empty) extends JValue

object JField {
  def apply(name: String, value: JValue) = (name, value)
  def unapply(f: JField): Option[(String, JValue)] = Some(f)
}