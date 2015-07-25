import org.scalameter.api._

object InlineTest extends PerformanceTest {

  def warmer = Warmer.Zero
  def aggregator = Aggregator.min
  def measurer = new Measurer.IgnoringGC with Measurer.PeriodicReinstantiation {
    override val defaultFrequency = 12
    override val defaultFullGC = true
  }
  def executor = SeparateJvmsExecutor(warmer, aggregator, measurer)
  def reporter = new LoggingReporter
  def persistor = Persistor.None
  
  val sizes: Gen[Int] = Gen.range("size")(300000, 1500000, 300000)

  val ints: Gen[Int] = for {
    size <- sizes
  } yield size

  val strings: Gen[String] = for {
    size <- sizes
  } yield size.toString

  val bigDecimals: Gen[BigDecimal] = for {
    size <- sizes
  } yield BigDecimal(size)

  val floats: Gen[Float] = for {
    size <- sizes
  } yield size.toFloat

  val maps: Gen[Map[String,Int]] = for {
    size <- sizes
  } yield Map(size.toString -> size)

  performance of "Inline" in {
    import org.json4s._

    measure method "A" in {
      using(strings) in {
        s => JArray(JString(s))
      }
    }

    measure method "B" in {
      using(bigDecimals) in {
        b => JArray(JNumber(b))
      }
    }

    measure method "C" in {
      using(bigDecimals) in {
        b => JArray(JNumber(b)).value.map{
          case v @ JNumber(_) => v.to[Int]
        }
      }
    }


    measure method "D" in {
      using(floats) in {
        f => JArray(JNumber(f))
      }
    }

    measure method "E" in {
      using(maps) in {
        map => JObject(map.map{case (k,v) => (k,JNumber(v))})
      }
    }
  }
  
  performance of "NoInline" in {
    import noInline.org.json4s._
    measure method "A" in {
      using(strings) in {
        s => JArray(JString(s))
      }
    }
    
    measure method "B" in {
      using(bigDecimals) in {
        b => JArray(JNumber(b))
      }
    }

    measure method "C" in {
      using(bigDecimals) in {
        b => JArray(JNumber(b)).value.map{
          case v @ JNumber(_) => v.to[Int]
        }
      }
    }

    measure method "D" in {
      using(floats) in {
        f => JArray(JNumber(f))
      }
    }

    measure method "E" in {
      using(maps) in {
        map => JObject(map.map{case (k,v) => (k,JNumber(v))})
      }
    }
  }
}
