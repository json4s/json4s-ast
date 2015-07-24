package org.json4s.ast

import org.json4s.bigDecimalConverter.BigDecimalConverter

sealed trait JValue

case object JUndefined extends JValue

case object JNull extends JValue

case class JString(value: String) extends JValue

object JNumber{
  @inline def apply(value: Int):JNumber = JNumber(BigDecimal(value))
  @inline def apply(value: Byte):JNumber = JNumber(BigDecimal(value))
  @inline def apply(value: Short):JNumber = JNumber(BigDecimal(value))
  @inline def apply(value: Long):JNumber = JNumber(BigDecimal(value))
  @inline def apply(value: BigInt):JNumber = JNumber(BigDecimal(value))
  @inline def apply(value: Float):JNumber = JNumber(BigDecimal.decimal(value))
  @inline def apply(value: Double):JNumber = JNumber(BigDecimal(value))
}

case class JNumber(value: BigDecimal) extends JValue {
  @inline def to[B](implicit bigDecimalConverter: BigDecimalConverter[B]) = bigDecimalConverter(value)
}

sealed abstract class JBoolean extends JValue {
  val value: Boolean
}

object JBoolean {
  @inline def apply(x: Boolean): JBoolean = if (x) JTrue else JFalse
  @inline def unapply(x: JBoolean): Boolean = x.value
}
case object JTrue extends JBoolean {
  val value = true
}
case object JFalse extends JBoolean {
  val value = false
}

case class JObject(value: Map[String,JValue] = Map.empty) extends JValue

object JArray {
  @inline def apply(value: JValue, values: JValue*): JArray = JArray(value +: values.to[collection.immutable.Seq])
  @inline def apply(value:Seq[JValue]): JArray = JArray(value.to[collection.immutable.Seq])
}
case class JArray(value: collection.immutable.Seq[JValue] = Nil) extends JValue