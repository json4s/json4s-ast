package org.json4s.ast.safe

import org.json4s.ast.fast
import scala.scalajs.js
import scala.scalajs.js.annotation.JSExportAll

sealed abstract class JValue extends Product with Serializable {

  /**
   * Converts a [[org.json4s.ast.safe.JValue]] to a [[org.json4s.ast.fast.JValue]]. Note that
   * when converting [[org.json4s.ast.fast.JObject]], this can produce [[org.json4s.ast.fast.JArray]] of
   * unknown ordering, since ordering on a [[scala.collection.Map]] isn't defined.
   * @return
   */

  def toFast: org.json4s.ast.fast.JValue

  /**
   * Converts a [[org.json4s.ast.safe.JValue]] to a Javascript object/value that can be used within
   * Javascript
   * @return
   */
  
  def toJsAny: js.Any
}

@JSExportAll
case object JNull extends JValue {
  def toFast: fast.JValue = fast.JNull
  
  def toJsAny: js.Any = null
}

@JSExportAll
case class JString(value: String) extends JValue {
  def toFast: fast.JValue = fast.JString(value)
  
  def toJsAny: js.Any = value
}

object JNumber {
  def apply(value: Int): JNumber = JNumber(BigDecimal(value))
  
  def apply(value: Integer): JNumber = JNumber(BigDecimal(value))

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
   * String constructor, so its possible to construct a [[JNumber]] with a larger precision
   * than the one defined by the IEEE 754. Note that when using it in Scala.js, it is possible for this to throw an
   * exception at runtime if you don't put in a correct number format for a [[scala.math.BigDecimal]].
   * @param value
   */
  @JSExportAll def this(value: String) = {
    this(BigDecimal(value))
  }

  def toFast: fast.JValue = fast.JNumber(value)
  
  def toJsAny: js.Any = value.toInt
}

/**
 * Implements named extractors so we can avoid boxing
 */

sealed abstract class JBoolean extends JValue {
  def get: Boolean
  
  def toJsAny: js.Any = get
}

object JBoolean {
  def apply(x: Boolean): JBoolean = if (x) JTrue else JFalse

  def unapply(x: JBoolean): Some[Boolean] = Some(x.get)
}

@JSExportAll
case object JTrue extends JBoolean {
  def get = true

  def toFast: fast.JValue = fast.JTrue
}

@JSExportAll
case object JFalse extends JBoolean {
  def get = false

  def toFast: fast.JValue = fast.JFalse
}

case class JObject(value: Map[String, JValue] = Map.empty) extends JValue {

  /**
   * Construct a JObject using Javascript's object type, i.e. {} or new Object
   * @param value
   */
  @JSExportAll def this(value: js.Dictionary[JValue]) = {
    this(value.toMap)
  }

  def toFast: fast.JValue = {
    if (value.isEmpty) {
      fast.JObject(js.Array[fast.JField]())
    } else {
      val iterator = value.iterator
      val array = js.Array[fast.JField]()
      while (iterator.hasNext) {
        val (k, v) = iterator.next()
        array.push(fast.JField(k, v.toFast))
      }
      fast.JObject(array)
    }
  }
  
  def toJsAny: js.Any = {
    if (value.isEmpty) {
      js.Dictionary[js.Any]().empty
    } else {
      val iterator = value.iterator
      val dict = js.Dictionary[js.Any]()
      while (iterator.hasNext) {
        val (k, v) = iterator.next()
        dict(k) = v.toJsAny
      }
      dict
    }
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

  def toFast: fast.JValue = {
    if (value.isEmpty) {
      fast.JArray(js.Array[fast.JValue]())
    } else {
      val iterator = value.iterator
      val array = js.Array[fast.JValue]()
      while (iterator.hasNext) {
        array.push(iterator.next().toFast)
      }
      fast.JArray(array)
    }
  }
  
  def toJsAny: js.Any = {
    if (value.isEmpty) {
      js.Array[fast.JValue]()
    } else {
      val iterator = value.iterator
      val array = js.Array[fast.JValue]()
      while (iterator.hasNext) {
        array.push(iterator.next().toJsAny)
      }
      array
    }
  }
}