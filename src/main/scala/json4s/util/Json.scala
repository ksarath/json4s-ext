package json4s.util

import org.json4s.{CustomSerializer, DefaultFormats}
import org.json4s.jackson.Serialization.{read, write}

object Json {
  val formats = DefaultFormats ++ JavaTimeSerializers.defaults

  def as[T](jsonString: String, customSerializers: CustomSerializer[_]*)(implicit manifest: Manifest[T]): T =
    read[T](jsonString)(formats ++ customSerializers, manifest)

  def toJsonString[T <: AnyRef](value: T, customSerializers: CustomSerializer[_]*): String =
    write[T](value)(formats ++ customSerializers)

  def asMap(jsonString: String, customSerializers: CustomSerializer[_]*): Map[String, AnyRef] =
    as[Map[String, AnyRef]](jsonString, customSerializers: _*)

  def asList[T](jsonString: String, customSerializers: CustomSerializer[_]*)(implicit manifest: Manifest[T]): List[T] =
    as[List[T]](jsonString, customSerializers: _*)

  def compare[A <: AnyRef, B <: AnyRef](a: A, b: B): Boolean =
    toJsonString(a) == toJsonString(b)
}