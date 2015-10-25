package specs.safe

import org.scalacheck.Properties
import org.scalacheck.Prop.forAll

object JNumberSpec extends Properties("safe.JNumber") {

  import org.json4s.ast.safe._

  private final val mc = BigDecimal.defaultMathContext

  property("Int") = forAll { (i: Int) =>
    JNumber(i).value == BigDecimal(i)
  }

  property("Long") = forAll { (l: Long) =>
    JNumber(l).value == BigDecimal(l)
  }

  property("BigDecimal") = forAll { (b: BigDecimal) =>
    JNumber(b).value == b
  }

  property("BigInt") = forAll { (b: BigInt) =>
    JNumber(b).value == BigDecimal(b)
  }

  property("Byte") = forAll { (b: Byte) =>
    JNumber(b).value == BigDecimal(b)
  }

  property("Double") = forAll { (d: Double) =>
    JNumber(d).value == BigDecimal(d)
  }

  property("Float") = forAll { (f: Float) =>
    JNumber(f).value == new BigDecimal(new java.math.BigDecimal(java.lang.Float.toString(f), mc), mc)
  }

  property("Short") = forAll { (s: Short) =>
    JNumber(s).value == BigDecimal(s)
  }

}

