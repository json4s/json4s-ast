package specs.safe

import specs.Spec
import org.json4s.ast.safe._

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
  """

  private final val mc = BigDecimal.defaultMathContext
  
  def readByteJNumber = prop {b: Byte =>
    JNumber(b).value must beEqualTo(BigDecimal(b))
  }

  def readLongJNumber = prop {l: Long =>
    JNumber(l).value must beEqualTo(BigDecimal(l))
  }

  def readBigDecimalJNumber = prop {b: BigDecimal =>
    JNumber(b).value must beEqualTo(b)
  }

  def readBigIntJNumber = prop {b: BigInt =>
    JNumber(b).value must beEqualTo(BigDecimal(b))
  }

  def readIntJNumber = prop {i: Int =>
    JNumber(i).value must beEqualTo(BigDecimal(i))
  }

  def readDoubleJNumber = prop {d: Double =>
    JNumber(d).value must beEqualTo(BigDecimal(d))
  }

  def readFloatJNumber = prop {f: Float =>
    JNumber(f).value must beEqualTo(new BigDecimal(new java.math.BigDecimal(java.lang.Float.toString(f), mc), mc))
  }

  def readShortJNumber = prop {s: Short =>
    JNumber(s).value must beEqualTo(BigDecimal(s))
  }
}
