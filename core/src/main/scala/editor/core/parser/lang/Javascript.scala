package editor.core.parser.lang

import editor.core.parser._

object Javascript extends Parser {

  override def parse(context: ParserContext): ParserResult = {
    new BetweenParser("comment", "/*", "*/", null, true)
      .or(new BetweenParser("quote", "\"", "\"", "\\", false))
      .or(new BetweenParser("quote", "`", "`", "\\", true))
      .or(new BetweenParser("quote", "'", "'", "\\", false))
      .or(new RegexParser("comment", """(//.*)\n""".r))
      .or(
        new KeywordParser(
          "keyword",
          Seq(
            "function",
            "import",
            "from",
            "class",
            "constructor",
            "this",
            "new",
            "return",
            "if",
            "for",
            "async",
            "await",
            "var",
            "let",
            "const",
            "true",
            "false",
          ))
      )
      .or(new RegexParser("default", """(\w+)""".r))
      .or(new RegexParser("default", """(\s+)""".r))
      .or(new DefaultParser())
      .parse(context)
  }

}
