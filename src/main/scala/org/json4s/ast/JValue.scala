package org.json4s.ast

/**
 * Data type for JSON AST.
 */
sealed abstract class JValue  {
  type Values


  /**
   * Return unboxed values from JSON
   * <p>
   * Example:<pre>
   * JObject(JField("name", JString("joe")) :: Nil).values == Map("name" -> "joe")
   * </pre>
   */
  def values: Values

  /**
   * Return direct child elements.
   * <p>
   * Example:<pre>
   * JArray(JInt(1) :: JInt(2) :: Nil).children == List(JInt(1), JInt(2))
   * </pre>
   */
  def children: List[JValue] = this match {
    case JObject(l) ⇒ l map (_._2)
    case JArray(l) ⇒ l
    case _ ⇒ Nil
  }


  /**
   * Return nth element from JSON.
   * Meaningful only to JArray, JObject and JField. Returns JNothing for other types.
   * <p>
   * Example:<pre>
   * JArray(JInt(1) :: JInt(2) :: Nil)(1) == JInt(2)
   * </pre>
   */
  def apply(i: Int): JValue = JNothing


  /**
   * Concatenate with another JSON.
   * This is a concatenation monoid: (JValue, ++, JNothing)
   * <p>
   * Example:<pre>
   * JArray(JInt(1) :: JInt(2) :: Nil) ++ JArray(JInt(3) :: Nil) ==
   * JArray(List(JInt(1), JInt(2), JInt(3)))
   * </pre>
   */
  def ++(other: JValue) = {
    def append(value1: JValue, value2: JValue): JValue = (value1, value2) match {
      case (JNothing, x) ⇒ x
      case (x, JNothing) ⇒ x
      case (JArray(xs), JArray(ys)) ⇒ JArray(xs ::: ys)
      case (JArray(xs), v: JValue) ⇒ JArray(xs ::: List(v))
      case (v: JValue, JArray(xs)) ⇒ JArray(v :: xs)
      case (x, y) ⇒ JArray(x :: y :: Nil)
    }
    append(this, other)
  }
}

case object JNothing extends JValue {
  type Values = None.type
  def values = None
}
case object JNull extends JValue {
  type Values = Null
  def values = null
}
case class JString(s: String) extends JValue {
  type Values = String
  def values = s
}
trait JNumber
case class JDouble(num: Double) extends JValue with JNumber {
  type Values = Double
  def values = num
}
case class JDecimal(num: BigDecimal) extends JValue with JNumber {
  type Values = BigDecimal
  def values = num
}
case class JInt(num: BigInt) extends JValue with JNumber {
  type Values = BigInt
  def values = num
}
case class JBool(value: Boolean) extends JValue {
  type Values = Boolean
  def values = value
}

case class JObject(obj: List[JField]) extends JValue {
  type Values = Map[String, Any]
  def values = (obj.map { case (n, v) ⇒ (n, v.values) }).toMap

  override def equals(that: Any): Boolean = that match {
    case o: JObject ⇒ obj.toSet == o.obj.toSet
    case _ ⇒ false
  }

  override def hashCode = obj.toSet[JField].hashCode
}
case object JObject {
  def apply(fs: JField*): JObject = JObject(fs.toList)
}

case class JArray(arr: List[JValue]) extends JValue {
  type Values = List[Any]
  def values = arr.map(_.values)
  override def apply(i: Int): JValue = arr(i)
}

object JField {
  def apply(name: String, value: JValue) = (name, value)
  def unapply(f: JField): Option[(String, JValue)] = Some(f)
}