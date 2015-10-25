package specs.fast

import org.scalacheck.Properties
import org.scalacheck.Prop.forAll

object JNumberSpec extends Properties("fast.JNumber") {

  import org.json4s.ast.fast._

  property("Int") = forAll { (i: Int) =>
    JNumber(i).value == i.toString
  }

  property("Long") = forAll { (l: Long) =>
    JNumber(l).value == l.toString
  }

  property("BigDecimal") = forAll { (b: BigDecimal) =>
    JNumber(b).value == b.toString
  }

  property("BigInt") = forAll { (b: BigInt) =>
    JNumber(b).value == b.toString
  }

  property("Byte") = forAll { (b: Byte) =>
    JNumber(b).value == b.toInt.toString
  }

  property("Double") = forAll { (d: Double) =>
    JNumber(d).value == d.toString
  }

  property("Float") = forAll { (f: Float) =>
    JNumber(f).value == f.toString
  }

  property("Short") = forAll { (s: Short) =>
    JNumber(s).value == s.toString
  }

}
