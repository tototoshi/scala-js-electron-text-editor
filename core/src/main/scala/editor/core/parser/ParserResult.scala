package editor.core.parser

import scala.scalajs.js

trait ParserResult

object ParserResult {
  case class Success(context: ParserContext, result: js.Array[Marker]) extends ParserResult
  case object Failure extends ParserResult
}
