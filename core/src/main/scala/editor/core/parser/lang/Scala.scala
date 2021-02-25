package editor.core.parser.lang

import editor.core.parser._

object Scala extends Parser {

  override def parse(context: ParserContext): ParserResult = {
    new BetweenParser("comment", "/*", "*/", null, true)
      .or(new BetweenParser("quote", "\"\"\"", "\"\"\"", "\\", true))
      .or(new BetweenParser("quote", "\"", "\"", "\\", false))
      .or(new BetweenParser("quote", "'", "'", "\\", false))
      .or(new BetweenParser("comment", "//", "\n", null, false))
      .or(new RegexParser("comment", """(//.*)\n""".r))
      .or(
        new KeywordParser(
          "keyword",
          Seq(
            "abstract",
            "case",
            "catch",
            "class",
            "def",
            "do",
            "else",
            "extends",
            "false",
            "final",
            "finally",
            "for",
            "forSome",
            "if",
            "implicit",
            "import",
            "lazy",
            "match",
            "new",
            "null",
            "object",
            "override",
            "package",
            "private",
            "protected",
            "return",
            "sealed",
            "super",
            "this",
            "throw",
            "trait",
            "try",
            "true",
            "type",
            "val",
            "var",
            "while",
            "with",
            "yield",
          )
        )
      )
      .or(new RegexParser("keyword", """(@\w+)""".r))
      .or(new RegexParser("default", """(\w+)""".r))
      .or(new RegexParser("default", """(\s+)""".r))
      .or(new DefaultParser())
      .parse(context)
  }
}
