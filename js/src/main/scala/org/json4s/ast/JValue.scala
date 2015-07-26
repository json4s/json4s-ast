package org.json4s.ast

import scala.scalajs.js
import scala.scalajs.js.annotation.{JSExportAll, JSExport}

sealed abstract class JValue extends Product with Serializable

@JSExportAll
case object JNull extends JValue

@JSExportAll
case class JString(value: String) extends JValue

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

@JSExportAll
case class JNumber(value: BigDecimal) extends JValue {
  @inline def to[B](implicit bigDecimalConverter: BigDecimalConverter[B]) = bigDecimalConverter(value)

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
  val value: Boolean
}

object JBoolean {
  @inline def apply(x: Boolean): JBoolean = if (x) JTrue else JFalse
  @inline def unapply(x: JBoolean): Option[Boolean] = Some(x.value)
}

@JSExportAll
case object JTrue extends JBoolean {
  val value = true
}

@JSExportAll
case object JFalse extends JBoolean {
  val value = false
}

case class JObject(value: Map[String,JValue] = Map.empty) extends JValue {

  /**
   * Construct a JObject using Javascript's object type, i.e. {} or new Object
   * @param value
   */
  @JSExport def this(value : js.Dictionary[JValue]) = {
    this(value.toMap)
  }
}

object JArray {
  @inline def apply(value: JValue, values: JValue*): JArray = JArray(value +: values.to[collection.immutable.Seq])
  @inline def apply(value: Seq[JValue]): JArray = JArray(value.to[collection.immutable.Seq])
}

case class JArray(value: collection.immutable.Seq[JValue] = Nil) extends JValue {
  /**
   * Construct a JArray using Javascript's array type, i.e. [] or new Array
   * @param value
   */
  @JSExportAll def this(value: js.Array[JValue]) = {
    this(value.to[collection.immutable.Seq])
  }
}