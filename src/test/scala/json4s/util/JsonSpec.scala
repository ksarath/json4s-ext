package json4s.util

package nl.ing.api.contacting.util

import java.time.{Instant, LocalDate, LocalDateTime, LocalTime, ZonedDateTime}

import org.json4s.CustomSerializer
import org.json4s.JsonAST.JString
import org.scalatest.{BeforeAndAfter, BeforeAndAfterEach, FlatSpec, Matchers}

case class ChildClass(v1: Long, v2: String, v3: Option[Int])
case class ParentClass(v1: BigInt, v2: Map[String, Any], v3: Seq[ChildClass])
case class VariousJavaTimeTypes(locatDate: Option[LocalDate], instant: Instant, localTime: LocalTime, localDateTime: Option[LocalDateTime], zonedDateTime: ZonedDateTime)

sealed abstract class CustomEnumType {
  lazy val name: String = {
    val n = getClass.getName.stripSuffix("$")
    n.split("\\.").last.split("\\$").last
  }

  override def toString: String = name
}

case object EnumValue1 extends CustomEnumType
case object EnumValue2 extends CustomEnumType
case object EnumValue3 extends CustomEnumType

object CustomEnumType {
  val customEnumTypeSerializer = new CustomSerializer[CustomEnumType](
    _ => (
      {case JString(s) => CustomEnumType.withName(s)},
      {case t: CustomEnumType => JString(t.name)}
    )
  )

  def apply(name: String): CustomEnumType = {
    //import scala.reflect.runtime.universe._
    //val symbol = typeOf[CustomEnumType].typeSymbol.asClass.knownDirectSubclasses.find(_.name.decodedName.toString == s).get
    //val module = reflect.runtime.currentMirror.staticModule(symbol.fullName)
    //reflect.runtime.currentMirror.reflectModule(module).instance.asInstanceOf[CustomEnumType]

    name match {
      case EnumValue1.name => EnumValue1
      case EnumValue2.name => EnumValue2
      case EnumValue3.name => EnumValue3
      case _ => throw new UnsupportedOperationException(s"Unknown audit type: $name")
    }
  }


  def withName(s: String): CustomEnumType = {
    apply(s)
  }
}

case class ClassWithCustomEnum(id: String, `type`: CustomEnumType)

class JsonSpec extends FlatSpec with BeforeAndAfterEach with BeforeAndAfter with Matchers {
  private val attributes: Map[String, Any] = Map(
    "v1" -> 1,
    "v2" -> "something",
    "v3" -> 1
  )

  private val children = Seq(
    ChildClass(1, "someString", Option(1)),
    ChildClass(2, null, Option(2)),
    ChildClass(3, "someString", None)
  )

  private val parents = Map(
    "1" -> ParentClass(1, attributes, children),
    "2" -> ParentClass(2, attributes, Nil)
  )

  it should "convert to json string" in {
    Json.toJsonString(attributes) shouldBe """{"v1":1,"v2":"something","v3":1}"""
    Json.toJsonString(children) shouldBe """[{"v1":1,"v2":"someString","v3":1},{"v1":2,"v2":null,"v3":2},{"v1":3,"v2":"someString"}]"""
    Json.toJsonString(parents) shouldBe """{"1":{"v1":1,"v2":{"v1":1,"v2":"something","v3":1},"v3":[{"v1":1,"v2":"someString","v3":1},{"v1":2,"v2":null,"v3":2},{"v1":3,"v2":"someString"}]},"2":{"v1":2,"v2":{"v1":1,"v2":"something","v3":1},"v3":[]}}"""
  }

  it should "convert json string to values" in {
    Json.as[Map[String, Any]](Json.toJsonString(attributes)) shouldBe attributes
    Json.as[Seq[ChildClass]](Json.toJsonString(children)) shouldBe children
    Json.as[Map[String, ParentClass]](Json.toJsonString(parents)) shouldBe parents
  }

  it should "convert a normal json to a Map" in {
    Json.asMap(Json.toJsonString(attributes)) shouldBe attributes
    Json.asMap(Json.toJsonString(parents)) shouldBe parents.map { case(k, v) => (k, Json.asMap(Json.toJsonString(v))) }
  }

  it should "convert a json array to a List" in {
    Json.asList[ChildClass](Json.toJsonString(children)) shouldBe children.toList
  }

  it should "be able to compare 2 values" in {
    Json.compare(parents("1"), parents("2")) shouldBe false
    Json.compare(parents("1"), parents("1")) shouldBe true
  }

  it should "be able to serialize and deserialize java time instances" in {
    val dateSamples = VariousJavaTimeTypes(Option(LocalDate.now), Instant.now, LocalTime.now, Option(LocalDateTime.now), ZonedDateTime.now)
    Json.as[VariousJavaTimeTypes](Json.toJsonString(dateSamples)) shouldBe dateSamples

    val dateSamplesWithoutValues = VariousJavaTimeTypes(None, Instant.now, LocalTime.now, None, ZonedDateTime.now)
    Json.as[VariousJavaTimeTypes](Json.toJsonString(dateSamplesWithoutValues)) shouldBe dateSamplesWithoutValues
  }

  it should "be able to handle custom serialized objects" in {
    val customEnum1 = ClassWithCustomEnum("1", EnumValue1)
    val customEnum2 = ClassWithCustomEnum("2", EnumValue2)
    val customEnum3 = ClassWithCustomEnum("3", EnumValue3)
    val enum1Json = Json.toJsonString(customEnum1, CustomEnumType.customEnumTypeSerializer)
    val enum2Json = Json.toJsonString(customEnum2, CustomEnumType.customEnumTypeSerializer)
    val enum3Json = Json.toJsonString(customEnum3, CustomEnumType.customEnumTypeSerializer)
    Json.as[ClassWithCustomEnum](enum1Json, CustomEnumType.customEnumTypeSerializer) shouldBe customEnum1
    Json.as[ClassWithCustomEnum](enum2Json, CustomEnumType.customEnumTypeSerializer) shouldBe customEnum2
    Json.as[ClassWithCustomEnum](enum3Json, CustomEnumType.customEnumTypeSerializer) shouldBe customEnum3
  }
}
