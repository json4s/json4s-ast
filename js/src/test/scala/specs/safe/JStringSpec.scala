package specs.safe

import org.scalacheck.Properties
import org.scalacheck.Prop.forAll

object JStringSpec extends Properties("safe.JString") {

  import org.json4s.ast.safe._

  property("String") = forAll { (a: String) =>
    JString(a).value == a
  }

}