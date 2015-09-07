package org.json4s.ast.fast

import org.json4s.ast.safe
import scala.scalajs.js
import scala.scalajs.js.annotation.JSExportAll

sealed abstract class JValue extends Serializable with Product {

  /**
   * Converts a [[org.json4s.ast.fast.JValue]] to a [[org.json4s.ast.safe.JValue]]. Note that
   * when converting [[org.json4s.ast.fast.JString]], this can throw runtime error if the underlying
   * string representation is not a correct number
   * @return
   */
  def toSafe: safe.JValue
}

@JSExportAll
case object JNull extends JValue {
  def toSafe: safe.JValue = safe.JNull
}

@JSExportAll
case class JString(value:String) extends JValue {
  def toSafe: safe.JValue = safe.JString(value)
}

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

  def toSafe: safe.JValue = safe.JNumber(BigDecimal(value))

  @JSExportAll def this(value: Double) = {
    this(value.toString)
  }
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

@JSExportAll
case object JTrue extends JBoolean {
  def isEmpty = false
  def get = true

  def toSafe: safe.JValue = safe.JTrue
}

@JSExportAll
case object JFalse extends JBoolean {
  def isEmpty = false
  def get = false

  def toSafe: safe.JValue = safe.JTrue
}

@JSExportAll
case class JField(field:String, value:JValue)

/**
 * JObject is internally represented as a mutable Array, to improve sequential performance
 * @param value
 */
@JSExportAll
case class JObject(value: js.Array[JField] = js.Array()) extends JValue {
  @JSExportAll def this(value: js.Dictionary[JValue]) = {
    this({
      val array:js.Array[JField] = new js.Array()
      for (key <- value.keys) {
        array.push(JField(key,value(key)))
      }
      array
    })
  }

  def toSafe: safe.JValue = {
    // Javascript array.length across all major browsers has near constant cost, so we
    // use this to build the array http://jsperf.com/length-comparisons
    val length = value.length
    
    if (length == 0) {
      safe.JObject(Map.newBuilder[String,safe.JValue].result())
    } else {
      val b = Map.newBuilder[String,safe.JValue].result()
      var index = 0
      while (index < length) {
        val v = value(index)
        b + ((v.field,v.value.toSafe))
        index = index + 1
      }
      safe.JObject(b)
    }
  }
}

/**
 * JArray is internally represented as a mutable Array, to improve sequential performance
 * @param value
 */
@JSExportAll
case class JArray(value: js.Array[JValue] = js.Array()) extends JValue {
  def toSafe: safe.JValue = {
    // Javascript array.length across all major browsers has near constant cost, so we
    // use this to build the array http://jsperf.com/length-comparisons
    val length = value.length
    if (length == 0) {
      safe.JArray(Vector.newBuilder[safe.JValue].result())
    } else {
      val b = Vector.newBuilder[safe.JValue]
      var index = 0
      while (index < length) {
        b += value(index).toSafe
        index = index + 1
      }
      safe.JArray(b.result())
    }
  }
}