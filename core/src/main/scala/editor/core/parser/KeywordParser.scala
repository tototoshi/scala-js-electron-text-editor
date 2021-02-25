package editor.core.parser

class KeywordParser(val `type`: String, val keywords: Seq[String]) extends Parser {

  private def test(text: String, keyword: String): Boolean = {
    text.startsWith(keyword) &&
    (if (text.length > keyword.length) """[^a-zA-Z0-9_]""".r.matches(text.charAt(keyword.length).toString)
     else true)
  }

  override def parse(context: ParserContext): ParserResult = {
    val text = context.rest

    keywords
      .find(k => test(text, k))
      .map { k =>
        ParserResult.Success(context.forward(k.length), Marker.between(`type`, context.pos, k.length))
      }
      .getOrElse(ParserResult.Failure)
  }

}
