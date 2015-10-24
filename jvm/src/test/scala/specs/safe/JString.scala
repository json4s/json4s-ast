package specs.safe

import specs.Spec
import org.json4s.ast.safe._

class JString extends Spec { def is = s2"""
  The JString value should
    read a String $readStringJString
  """

  def readStringJString = prop {s: String =>
    JString(s).value must beEqualTo(s)
  }
}
