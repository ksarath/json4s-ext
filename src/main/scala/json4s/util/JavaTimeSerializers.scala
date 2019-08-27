package json4s.util


import java.time.format.DateTimeFormatter
import java.time.temporal.{TemporalAccessor, TemporalQuery}
import java.time.{Instant, LocalDate, LocalDateTime, LocalTime, ZonedDateTime}

import org.json4s.CustomSerializer
import org.json4s.JsonAST.JString

/** `java.time` support for json4s. */
private[util] object JavaTimeSerializers {

  val defaults = List(LocalDateSerializer, InstantSerializer, LocalTimeSerializer, LocalDateTimeSerializer, ZonedDateTimeSerializer)

  /** The default `LocalTimeSerializer` for ISO-8601 strings. */
  object LocalDateSerializer extends CustomSerializerT(DateTimeFormatter.ISO_LOCAL_DATE, LocalDate.from)

  /** The default `InstantSerializer` for ISO-8601 strings. */
  object InstantSerializer extends CustomSerializerT(DateTimeFormatter.ISO_INSTANT, Instant.from)

  /** The default `LocalTimeSerializer` for ISO-8601 strings. */
  object LocalTimeSerializer extends CustomSerializerT(DateTimeFormatter.ISO_LOCAL_TIME, LocalTime.from)

  /** The default `LocalDateTimeSerializer` for ISO-8601 strings. */
  object LocalDateTimeSerializer extends CustomSerializerT(DateTimeFormatter.ISO_LOCAL_DATE_TIME, LocalDateTime.from)

  /** The default `ZonedDateTimeSerializer` for ISO-8601 strings. */
  object ZonedDateTimeSerializer extends CustomSerializerT(DateTimeFormatter.ISO_ZONED_DATE_TIME, ZonedDateTime.from)

  /** A `CustomSerializer` for `T`. */
  class CustomSerializerT[T <: TemporalAccessor](val format: DateTimeFormatter, f: TemporalAccessor => T)(implicit mf: Manifest[T]) extends CustomSerializer[T](
    _ => (
      {case JString(s) => format.parse(s, asQuery(f))},
      {case t: T => JString(format.format(t))}
    )
  )

  private def asQuery[A](f: TemporalAccessor => A): TemporalQuery[A] =
    (temporal: TemporalAccessor) => f(temporal)
}