package editor.core.parser.lang

import editor.core.parser._

object HTML extends Parser {
  override def parse(context: ParserContext): ParserResult = {
    new BetweenParser("quote", "\"", "\"", "\\", false)
      .or(new BetweenParser("quote", "'", "'", "\\", false))
      .or(new RegexParser("keyword", """(\w+)=""".r))
      .or(new RegexParser("keyword", """(<\s*\w+)""".r))
      .or(new RegexParser("keyword", """(/?\s*?>)""".r))
      .or(new RegexParser("keyword", """(</\s*\w+\s*?>)""".r))
      .parse(context)
  }
}
