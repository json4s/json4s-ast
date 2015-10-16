package org.json4s.ast.safe

import org.json4s.ast.fast

sealed abstract class JValue extends Product with Serializable {

  /**
   * Converts a [[org.json4s.ast.safe.JValue]] to a [[org.json4s.ast.fast.JValue]]. Note that
   * when converting [[org.json4s.ast.fast.JObject]], this can produce [[org.json4s.ast.fast.JArray]] of
   * unknown ordering, since ordering on a [[scala.collection.Map]] isn't defined.
   * @return
   */

  def toFast: fast.JValue
}

case object JNull extends JValue {
  def toFast: fast.JValue = fast.JNull
}

case class JString(value: String) extends JValue {
  def toFast: fast.JValue = fast.JString(value)
}

object JNumber {
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

  def toFast: fast.JValue = fast.JNumber(value)
}

/**
 * Implements named extractors so we can avoid boxing
 */

sealed abstract class JBoolean extends JValue {
  def get: Boolean
}

object JBoolean {
  def apply(x: Boolean): JBoolean = if (x) JTrue else JFalse

  def unapply(x: JBoolean): Some[Boolean] = Some(x.get)
}

case object JTrue extends JBoolean {
  def get = true

  def toFast: fast.JValue = fast.JTrue
}

case object JFalse extends JBoolean {
  def get = false

  def toFast: fast.JValue = fast.JFalse
}

case class JObject(value: Map[String, JValue] = Map.empty) extends JValue {
  def toFast: fast.JValue = {
    if (value.isEmpty) {
      fast.JArray(Array.ofDim[fast.JValue](0))
    } else {
      val array = Array.ofDim[fast.JField](value.size)
      var index = 0
      for ((k, v) <- value) {
        array(index) = fast.JField(k, v.toFast)
        index = index + 1
      }
      fast.JObject(array)
    }
  }
}

object JArray {
  def apply(value: JValue, values: JValue*): JArray = JArray(value +: values.to[Vector])
}

case class JArray(value: Vector[JValue] = Vector.empty) extends JValue {
  def toFast: fast.JValue = {
    val length = value.length
    if (length == 0) {
      fast.JArray(Array.ofDim[fast.JValue](0))
    } else {
      val array = Array.ofDim[fast.JValue](length)
      val iterator = value.iterator
      var index = 0
      while (iterator.hasNext) {
        array(index) = iterator.next().toFast
        index = index + 1
      }
      fast.JArray(array)
    }
  }
}