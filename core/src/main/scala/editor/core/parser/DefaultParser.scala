package editor.core.parser

import scala.scalajs.js

class DefaultParser extends Parser {

  override def parse(context: ParserContext): ParserResult = {
    if (context.rest.isEmpty) ParserResult.Failure
    else ParserResult.Success(context.forward(1), js.Array())
  }

}
