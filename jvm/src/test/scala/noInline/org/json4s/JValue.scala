package noInline.org.json4s

sealed trait JValue

case object JNull extends JValue

case class JString(value: String) extends JValue

object JNumber{
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
  def to[B](implicit bigDecimalConverter: BigDecimalConverter[B]) = bigDecimalConverter(value)
}

sealed abstract class JBoolean extends JValue {
  val value: Boolean
}

object JBoolean {
  def apply(x: Boolean): JBoolean = if (x) JTrue else JFalse
  def unapply(x: JBoolean): Option[Boolean] = Some(x.value)
}
case object JTrue extends JBoolean {
  val value = true
}
case object JFalse extends JBoolean {
  val value = false
}

case class JObject(value: Map[String,JValue] = Map.empty) extends JValue

object JArray {
  def apply(value: JValue, values: JValue*): JArray = JArray(value +: values.to[collection.immutable.Seq])
  def apply(value:Seq[JValue]): JArray = JArray(value.to[collection.immutable.Seq])
}
case class JArray(value: collection.immutable.Seq[JValue] = Nil) extends JValue