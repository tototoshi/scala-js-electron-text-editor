package editor.core.parser

import scala.annotation.tailrec
import scala.scalajs.js

trait Parser { self =>

  def parse(context: ParserContext): ParserResult

  def or(parser: Parser): Parser = { (context: ParserContext) =>
    self.parse(context) match {
      case ParserResult.Failure => parser.parse(context)
      case success => success
    }
  }

}

object Parser {

  def exec(parser: Parser, text: String): js.Array[Marker] = {
    @tailrec
    def go(context: ParserContext, marks: js.Array[Marker]): js.Array[Marker] = {
      if (context.rest.nonEmpty) {
        parser.parse(context) match {
          case ParserResult.Success(ctx, result) =>
            go(ctx, marks.concat(result.filterNot(_.`type` == "default")))
          case ParserResult.Failure =>
            go(context.forward(1), marks)
        }
      } else {
        marks
      }
    }
    go(ParserContext.init(text), js.Array())
  }

}
