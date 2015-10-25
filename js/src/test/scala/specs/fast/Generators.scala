package specs.fast

import org.scalacheck.{Arbitrary, Gen}
import scala.util.Random
import org.json4s.ast.fast._
import scalajs.js

object Generators {
  def jIntGenerator = Arbitrary.arbitrary[BigInt].map(JNumber.apply)

  def jStringGenerator = Arbitrary.arbitrary[String].map(JString.apply)

  def jBooleanGenerator = Arbitrary.arbitrary[Boolean].map(JBoolean.apply)

  def jFieldGenerator: Gen[JField] = for {
    string <- Arbitrary.arbitrary[String]
    jValue <- jValueGenerator
  } yield JField(string, jValue)

  def jArrayGenerator: Gen[JArray] =
    Gen.containerOf[js.Array,JValue](jValueGenerator).map(JArray.apply)

  def jObjectGenerator: Gen[JObject] =
    Gen.containerOf[js.Array,JField](jFieldGenerator).map(JObject.apply)

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
