package org.json4s.ast

import scala.scalajs.js
import scala.scalajs.js.annotation.JSExportAll

sealed abstract class JValue extends Product with Serializable

@JSExportAll
case object JNull extends JValue

@JSExportAll
case class JString(value: String) extends JValue

object JNumber{
  def apply(value: Int): JNumber = JNumber(BigDecimal(value))
  def apply(value: Byte): JNumber = JNumber(BigDecimal(value))
  def apply(value: Short): JNumber = JNumber(BigDecimal(value))
  def apply(value: Long): JNumber = JNumber(BigDecimal(value))
  def apply(value: BigInt): JNumber = JNumber(BigDecimal(value))
  def apply(value: Double): JNumber = JNumber(BigDecimal(value))
  def apply(value: Float): JNumber = JNumber(BigDecimal(value.toDouble)) // In Scala.js, float has the same representation as double
}

@JSExportAll
case class JNumber(value: BigDecimal) extends JValue {
  def to[B](implicit bigDecimalConverter: JNumberConverter[B]) = bigDecimalConverter(value)

  /**
   * Javascript specification for numbers specify a `Double`, so this is the default export method to `Javascript`
   * @param value
   */
  @JSExportAll def this(value: Double) = {
    this(BigDecimal(value))
  }

  /**
   * String constructor, so its possible to construct a [[org.json4s.ast.JNumber]] with a larger precision
   * than the one defined by the IEEE 754. Note that when using it in Scala.js, it is possible for this to throw an
   * exception at runtime if you don't put in a correct number format for a [[scala.math.BigDecimal]].
   * @param value
   */
  @JSExportAll def this(value:String) = {
    this(BigDecimal(value))
  }
}

sealed abstract class JBoolean extends JValue {
  val isTrue: Boolean
}

object JBoolean {
  def apply(x: Boolean): JBoolean = if (x) JTrue else JFalse
  def unapply(x: JBoolean): Some[Boolean] = Some(x.isTrue)
}

@JSExportAll
case object JTrue extends JBoolean {
  val isTrue = true
}

@JSExportAll
case object JFalse extends JBoolean {
  val isTrue = false
}

case class JObject(value: Map[String,JValue] = Map.empty) extends JValue {

  /**
   * Construct a JObject using Javascript's object type, i.e. {} or new Object
   * @param value
   */
  @JSExportAll def this(value : js.Dictionary[JValue]) = {
    this(value.toMap)
  }
}

object JArray {
  def apply(value: JValue, values: JValue*): JArray = JArray(value +: values.to[Vector])
}

case class JArray(value: Vector[JValue] = Vector.empty) extends JValue {
  /**
   * Construct a JArray using Javascript's array type, i.e. [] or new Array
   * @param value
   */
  @JSExportAll def this(value: js.Array[JValue]) = {
    this(value.to[Vector])
  }
}