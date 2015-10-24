package specs.safe

import org.scalacheck.{Arbitrary, Gen}
import scala.util.Random
import org.json4s.ast.safe._

object Generators {
  def jIntGenerator = Arbitrary.arbitrary[BigInt].map(JNumber.apply)

  def jStringGenerator = Arbitrary.arbitrary[String].map(JString.apply)

  def jBooleanGenerator = Arbitrary.arbitrary[Boolean].map(JBoolean.apply)

  def jArrayGenerator: Gen[JArray] =
    Gen.containerOf[Vector,JValue](jValueGenerator).map(JArray.apply)

  private def jObjectTypeGenerator: Gen[(String, JValue)] = for {
    string <- Arbitrary.arbitrary[String]
    jValue <- jValueGenerator
  } yield (string, jValue)

  def jObjectGenerator: Gen[JObject] =
    Gen.containerOf[List,(String,JValue)](jObjectTypeGenerator).map(data => JObject.apply(data.toMap))

  def jValueGenerator: Gen[JValue] = for {
    jInt <- jIntGenerator
    jString <- jStringGenerator
    jBoolean <- jBooleanGenerator
    jArray <- jArrayGenerator
    jObject <- jObjectGenerator
  } yield {
    val ran = Seq(jInt, jString, jBoolean, jArray, jObject)
    ran(Random.nextInt(ran.size))
  }

}
