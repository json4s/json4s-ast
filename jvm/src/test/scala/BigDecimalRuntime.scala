import org.scalameter.PerformanceTest.Microbenchmark
import org.scalameter.api._

object BigDecimalRuntime extends Microbenchmark {

  val sizes: Gen[Int] = Gen.range("size")(300000, 1500000, 300000)
  
  // Lets assume our input is an Array of characters, likely to be similar to what you would receive in JSON
  val chars: Gen[Array[Char]] = for {
    size <- sizes
  } yield size.toString.toCharArray

  performance of "BigDecimal" in {
    import org.json4s.ast.JNumber
    measure method "construct" in {
      using(chars) in {
        c => JNumber(BigDecimal(c.mkString))
      }
    }
  }
  
  performance of "String" in {
    case class JNumber(value: String)
    measure method "construct" in {
      using(chars) in {
        c => JNumber(c.mkString)
      }
    }
  }

}
