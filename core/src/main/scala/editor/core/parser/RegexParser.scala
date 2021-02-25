package editor.core.parser

import scala.util.matching.Regex

class RegexParser(val `type`: String, val regex: Regex) extends Parser {

  override def parse(context: ParserContext): ParserResult = {
    val text = context.rest

    regex
      .findPrefixMatchOf(text)
      .map { m =>
        val s = m.group(1)
        ParserResult.Success(context.forward(s.length), Marker.between(`type`, context.pos, s.length))
      }
      .getOrElse(ParserResult.Failure)
  }

}
