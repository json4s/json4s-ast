import org.json4s.ast.safe
import org.scalameter.api._

case class JBoolean(boolean: Boolean)

object JBooleanVariants extends Bench.ForkedTime {
  val sizes: Gen[Int] = Gen.range("size")(300000, 1500000, 300000)

  val bools: Gen[Boolean] = for {
    size <- sizes
  } yield {
    if (size % 2 == 0)
      true
    else
      false
  }

  performance of "CaseObject" in {
    measure method "construct" in {
      using(bools) in {
        b => safe.JBoolean(b)
      }
    }
  }

  performance of "Constructor" in {
    measure method "construct" in {
      using(bools) in {
        b => JBoolean(b)
      }
    }
  }
}
