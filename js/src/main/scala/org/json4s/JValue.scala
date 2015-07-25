package org.json4s

import scala.scalajs.js
import scala.scalajs.js.annotation.JSExport

sealed trait JValue

@JSExport
case object JNull extends JValue

@JSExport
case class JString(value: String) extends JValue

@JSExport
object JNumber{
  private val mc = BigDecimal.defaultMathContext
  @inline def apply(value: Int): JNumber = JNumber(BigDecimal(value))
  @inline def apply(value: Byte): JNumber = JNumber(BigDecimal(value))
  @inline def apply(value: Short): JNumber = JNumber(BigDecimal(value))
  @inline def apply(value: Long): JNumber = JNumber(BigDecimal(value))
  @inline def apply(value: BigInt): JNumber = JNumber(BigDecimal(value))
  @inline def apply(value: Float): JNumber = JNumber({
    // BigDecimal.decimal doesn't exist on 2.10, so this is just the 2.11 implementation
    new BigDecimal(new java.math.BigDecimal(value.toString, mc), mc) // Need to check that this will work on Scala.js
  })
  @inline def apply(value: Double): JNumber = JNumber(BigDecimal(value))
}

@JSExport
case class JNumber(value: BigDecimal) extends JValue {
  @inline def to[B](implicit bigDecimalConverter: BigDecimalConverter[B]) = bigDecimalConverter(value)
}

sealed abstract class JBoolean extends JValue {
  val value: Boolean
}

@JSExport
object JBoolean {
  @inline def apply(x: Boolean): JBoolean = if (x) JTrue else JFalse
  @inline def unapply(x: JBoolean): Option[Boolean] = Some(x.value)
}

@JSExport
case object JTrue extends JBoolean {
  val value = true
}

@JSExport
case object JFalse extends JBoolean {
  val value = false
}

@JSExport
case class JObject(value: Map[String,JValue] = Map.empty) extends JValue

@JSExport
object JArray {
  @inline def apply(value: JValue, values: JValue*): JArray = JArray(value +: values.to[collection.immutable.Seq])
  @inline def apply(value:Seq[JValue]): JArray = JArray(value.to[collection.immutable.Seq])
  @inline def apply(value:js.Array[JValue]): JArray = JArray(value.to[collection.immutable.Seq])
}

@JSExport
case class JArray(value: collection.immutable.Seq[JValue] = Nil) extends JValue