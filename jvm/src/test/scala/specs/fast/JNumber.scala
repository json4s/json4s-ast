package specs.fast

import specs.Spec
import org.json4s.ast.fast._

class JNumber extends Spec { def is = s2"""
  The JNumber value should
    read a Long $readLongJNumber
    read a Byte $readByteJNumber
    read a BigDecimal $readBigDecimalJNumber
    read a BigInt $readBigIntJNumber
    read an Int $readIntJNumber
    read a Double $readDoubleJNumber
    read a Float $readFloatJNumber
    read a Short $readShortJNumber
    read a String and not fail $readStringJNumber
    read a String and detect non numeric numbers $readStringJNumberDetect
  """
  
  def readByteJNumber = prop {b: Byte =>
    JNumber(b).value must beEqualTo(b.toInt.toString)
  }

  def readLongJNumber = prop {l: Long =>
    JNumber(l).value must beEqualTo(l.toString)
  }
  
  def readBigDecimalJNumber = prop {b: BigDecimal =>
    JNumber(b).value must beEqualTo(b.toString())
  }

  def readBigIntJNumber = prop {b: BigInt =>
    JNumber(b).value must beEqualTo(b.toString())
  }

  def readIntJNumber = prop {i: Int =>
    JNumber(i).value must beEqualTo(i.toString)
  }
  
  def readDoubleJNumber = prop{d: Double =>
    JNumber(d).value must beEqualTo(d.toString)
  }

  def readFloatJNumber = prop{f: Float =>
    JNumber(f).value must beEqualTo(f.toString)
  }

  def readShortJNumber = prop{s: Short =>
    JNumber(s).value must beEqualTo(s.toString)
  }
  
  def readStringJNumber = prop{s: String =>
    JNumber(s).value must beEqualTo(s.toString)
  }
  
  def readStringJNumberDetect = prop{s: String =>
    {scala.util.Try{BigDecimal(s)}.toOption.isEmpty} ==> {
      scala.util.Try(BigDecimal(JNumber(s).value)).toOption.isEmpty must beTrue
    }
  }
}
