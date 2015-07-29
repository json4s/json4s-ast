package org.json4s.basic.ast

import scala.scalajs.js
import scala.scalajs.js.annotation.JSExportAll

sealed abstract class JValue extends Serializable with Product

@JSExportAll
case object JNull extends JValue

@JSExportAll
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

@JSExportAll
case class JNumber(value: String) extends JValue {
  def to[B](implicit jNumberConverter: JNumberConverter[B]) = jNumberConverter(value)

  @JSExportAll def this(value: Double) = {
    this(value.toString)
  }
}

sealed abstract class JBoolean extends JValue {
  def isEmpty: Boolean
  def get: Boolean
}

object JBoolean {
  def apply(x: Boolean): JBoolean = if (x) JTrue else JFalse
  def unapply(x: JBoolean): Some[Boolean] = Some(x.isEmpty)
}

@JSExportAll
case object JTrue extends JBoolean {
  def isEmpty = false
  def get = true
}

@JSExportAll
case object JFalse extends JBoolean {
  def isEmpty = false
  def get = false
}

@JSExportAll
case class JField(field:String, value:JValue)

/**
 * JObject is internally represented as a mutable Array, to improve sequential performance
 * @param value
 */
@JSExportAll
case class JObject(private val value: js.Array[JField] = js.Array()) extends JValue {
  @JSExportAll def this(value: js.Dictionary[JValue]) = {
    this({
      val array:js.Array[JField] = new js.Array()
      var index = 0
      value.foreach{case(key,value) =>
        array(index) = JField(key,value)
        index = index + 1
      }
      array
    })
  }
}

/**
 * JArray is internally represented as a mutable Array, to improve sequential performance
 * @param value
 */
@JSExportAll
case class JArray(private val value: js.Array[JValue] = js.Array()) extends JValue