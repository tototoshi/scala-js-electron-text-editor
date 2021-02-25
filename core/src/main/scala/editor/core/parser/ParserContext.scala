package editor.core.parser

class ParserContext(val rest: String, val pos: Int) {

  def forward(length: Int): ParserContext = {
    new ParserContext(rest.substring(length), pos + length)
  }

}

object ParserContext {

  def init(text: String): ParserContext =
    new ParserContext(text, 0)

}
