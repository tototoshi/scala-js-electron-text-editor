package editor.core.parser

import scala.scalajs.js

trait ParserEvent

object ParserEvent {
  case class Result(marks: js.Array[Marker]) extends ParserEvent
  case object Done extends ParserEvent
}
