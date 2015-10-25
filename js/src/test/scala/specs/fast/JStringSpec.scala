package specs.fast

import org.scalacheck.Properties
import org.scalacheck.Prop.forAll

object JStringSpec extends Properties("fast.JString") {

  import org.json4s.ast.fast._
  
  property("String") = forAll { (a: String) =>
    JString(a).value == a
  }

}
