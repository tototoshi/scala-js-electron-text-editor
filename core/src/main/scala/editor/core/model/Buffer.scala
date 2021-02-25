package editor.core.model

import editor.core.util.Text

import scala.annotation.tailrec
import scala.scalajs.js

class Buffer(
    var id: Int,
    val content: String,
    val pos: Int,
    val mark: js.UndefOr[Int],
    val revision: Int,
    val view: View,
    val cursor: Coordinates,
    val selection: Selection)
  extends js.Object {

  def deleteBackwardChar(): Buffer = {
    val newPos = Math.max(0, pos - 1)
    val co = Text.coordinates(content, newPos)
    val updated = content.substring(0, newPos) + content.substring(pos)
    copy(
      content = updated,
      pos = newPos,
      view = view.remove(co.x, co.y, cursor.x, cursor.y)
    ).updateCursor()
  }

  def deleteForwardChar(): Buffer = {
    if (pos == content.length) {
      this
    } else {
      val co = Text.coordinates(content, pos + 1)
      val updated = content.substring(0, pos) + content.substring(pos + 1)
      copy(
        content = updated,
        view = view.remove(cursor.x, cursor.y, co.x, co.y)
      )
    }
  }

  def enter(): Buffer = {
    val updated = content.substring(0, pos) + "\n" + content.substring(pos)
    copy(
      content = updated,
      pos = pos + 1,
      view = view.insert(cursor.x, cursor.y, "\n")
    ).updateCursor()
  }

  def moveBackward(): Buffer = {
    copy(pos = Seq(0, pos - 1).max).updateCursor()
  }

  def moveForward(): Buffer = {
    copy(pos = Seq(content.length, pos + 1).min).updateCursor()
  }

  def moveUp(): Buffer = {
    copy(pos = Text.position(content, cursor.x, cursor.y - 1)).updateCursor()
  }

  def moveDown(): Buffer = {
    copy(pos = Text.position(content, cursor.x, cursor.y + 1)).updateCursor()
  }

  def moveStartOfLine(): Buffer = {
    copy(pos = Text.position(content, 0, cursor.y)).updateCursor()
  }

  def moveEndOfLine(): Buffer = {
    val i = content.indexOf('\n', pos)
    val newPos =
      if (i == -1) content.length
      else i
    copy(pos = newPos).updateCursor()
  }

  def moveStart(): Buffer = {
    copy(pos = 0).updateCursor()
  }

  def moveEnd(): Buffer = {
    copy(pos = content.length).updateCursor()
  }

  def killLine(): (Buffer, String) = {
    val i = content.indexOf('\n', pos)
    val selectionEnd =
      if (i == -1) content.length
      else if (i == pos) pos + 1
      else i

    val co1 = cursor
    val co2 = Text.coordinates(content, selectionEnd)

    val killedText = content.substring(pos, selectionEnd)
    val newBuf = copy(
      content = content.substring(0, pos) + content.substring(selectionEnd),
      view = view.remove(co1.x, co1.y, co2.x, co2.y)
    )
    (newBuf, killedText)
  }

  def insertLine(): Buffer = {
    val updated = content.substring(0, pos) + "\n" + content.substring(pos)
    copy(
      content = updated,
      view = view.insert(cursor.x, cursor.y, "\n"),
    )
  }

  def insert(s: String): Buffer = {
    val updated = content.substring(0, pos) + s + content.substring(pos)
    copy(
      content = updated,
      pos = pos + s.length,
      view = view.insert(cursor.x, cursor.y, s),
    ).updateCursor()
  }

  def indent(): Buffer = {
    @tailrec
    def searchNonWhitespaceChar(offset: Int): Int = {
      if (offset >= content.length) offset
      if (content.charAt(offset) == ' ') searchNonWhitespaceChar(offset + 1)
      else offset
    }

    val i = searchNonWhitespaceChar(pos)
    copy(pos = i).updateCursor()
  }

  def selectAll(): Buffer = {
    copy(selection = selection.start(0).end(content.length))
  }

  def markPosition(): Buffer = {
    copy(mark = pos)
  }

  def startSelection(x: Option[Int], y: Int): Buffer = {
    val pos = x match {
      case Some(x_) => Text.position(content, x_, y)
      case None => Text.position(content, 0, y + 1) - 1
    }
    copy(selection = selection.start(pos), pos = pos).updateCursor()
  }

  def updateSelection(x: Option[Int], y: Int): Buffer = {
    if (selection.endPos.isDefined)
      this
    else {
      val pos = x match {
        case Some(x_) => Text.position(content, x_, y)
        case None => Text.position(content, 0, y + 1) - 1
      }
      copy(selection = selection.update(pos)).updateCursor()
    }
  }

  def endSelection(x: Option[Int], y: Int): Buffer = {
    val pos = x match {
      case Some(x_) => Text.position(content, x_, y)
      case None => Text.position(content, 0, y + 1) - 1
    }
    copy(selection = selection.end(pos), pos = pos).updateCursor()
  }

  def clearSelection(): Buffer = {
    copy(selection = selection.clear())
  }

  def paste(clipboardText: String): Buffer = {
    val updated =
      content.substring(0, pos) + clipboardText + content.substring(pos)
    copy(
      content = updated,
      pos = pos + clipboardText.length,
      view = view.insert(cursor.x, cursor.y, clipboardText),
    ).updateCursor()
  }

  def cut(): (Buffer, Option[String]) = {
    def cutSelection(start: Int, end: Int): (Buffer, Option[String]) = {
      val co1 = Text.coordinates(content, start)
      val co2 = Text.coordinates(content, end)
      val updated = content.substring(0, start) + content.substring(end)
      val clipboardText = content.substring(start, end)
      val newBuf = copy(
        pos = start,
        content = updated,
        view = view.remove(co1.x, co1.y, co2.x, co2.y),
        selection = selection.clear()
      ).updateCursor()
      (newBuf, Some(clipboardText))
    }

    if (selection.startPos != js.undefined && selection.endPos != js.undefined) {
      val selectionStart = selection.startPos
      val selectionEnd = selection.endPos
      cutSelection(selectionStart.get, selectionEnd.get)
    } else if (mark.isDefined) {
      val selectionStart = Seq(pos, mark.get).min
      val selectionEnd = Seq(Seq(pos, mark.get).max, content.length).min
      cutSelection(selectionStart, selectionEnd)
    } else {
      (this, None)
    }
  }

  def updateView(view: View): Buffer = {
    copy(view = view)
  }

  def undo(): Buffer = this

  def cancel(): Buffer = copy(selection = Selection.empty)

  private def updateCursor(): Buffer = {
    copy(cursor = Text.coordinates(content, pos))
  }

  private def copy(
      content: String = content,
      pos: Int = pos,
      mark: js.UndefOr[Int] = mark,
      revision: Int = revision,
      view: View = view,
      cursor: Coordinates = cursor,
      selection: Selection = selection
    ): Buffer = new Buffer(
    id,
    content,
    pos,
    mark,
    revision,
    view,
    cursor,
    selection
  )

}

object Buffer {

  private var id = 0;

  def nextId(): Int = {
    id += 1
    id
  }

  def empty(id: Int): Buffer = new Buffer(
    id = id,
    content = "",
    pos = 0,
    mark = js.undefined,
    revision = 0,
    view = new View(js.Array(new ViewLine(js.Array()))),
    cursor = new Coordinates(0, 0),
    selection = Selection.empty
  )

}
