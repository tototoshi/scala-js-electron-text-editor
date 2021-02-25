package editor.core.model

import scala.scalajs.js

class Selection(val startPos: js.UndefOr[Int], val updatedPos: js.UndefOr[Int], val endPos: js.UndefOr[Int])
  extends js.Object {

  def start(pos: Int): Selection = {
    new Selection(
      pos,
      js.undefined,
      js.undefined
    )
  }

  def update(pos: Int): Selection = {
    startPos.toOption match {
      case Some(s) =>
        new Selection(
          s,
          pos,
          js.undefined
        )
      case None => this
    }
  }

  def end(pos: Int): Selection = {
    startPos.toOption match {
      case Some(s) =>
        new Selection(
          s,
          js.undefined,
          pos
        )
      case None => this
    }
  }

  def clear(): Selection = {
    new Selection(js.undefined, js.undefined, js.undefined)
  }

}

object Selection {

  def empty = new Selection(js.undefined, js.undefined, js.undefined)

}
