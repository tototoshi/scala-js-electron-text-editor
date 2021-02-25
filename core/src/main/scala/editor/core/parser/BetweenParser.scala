package editor.core.parser

import scala.annotation.tailrec

class BetweenParser(val `type`: String, val start: String, val end: String, val escape: String, val multiline: Boolean)
  extends Parser {

  override def parse(context: ParserContext): ParserResult = {
    val text = context.rest

    if (text.startsWith(start)) {
      findEnd(text.substring(start.length)) match {
        case Some(endIndex) =>
          ParserResult.Success(
            context.forward(endIndex + end.length),
            Marker.between(`type`, context.pos, endIndex + end.length))
        case None =>
          ParserResult.Failure
      }
    } else {
      ParserResult.Failure
    }

  }

  private def findEnd(text: String): Option[Int] = {

    @tailrec
    def go(text: String, result: Int): Option[Int] = {
      if (text.isEmpty || (text.head == '\n' && !multiline)) {
        None
      } else if (Option(escape).nonEmpty && text.startsWith(escape)) {
        go(text.substring(escape.length + 1), result + escape.length + 1)
      } else if (text.startsWith(end)) {
        Some(result + end.length)
      } else {
        go(text.substring(1), result + 1)
      }
    }

    go(text, 0)
  }

}
