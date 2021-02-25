package editor.core.parser

import editor.core.model.Selection

import scala.annotation.tailrec
import scala.collection.mutable.ArrayBuffer
import scala.scalajs._
import scala.scalajs.js.JSConverters._

case class Marker(val `type`: String, val pos: Int, val action: Option[String])

object Marker {

  def between(`type`: String, pos: Int, length: Int): js.Array[Marker] = {
    js.Array(Marker(`type`, pos, Some("start")), Marker(`type`, pos + length, Some("end")))
  }

  def mark(text: String, option: MarkerOption): js.Array[Marker] = {
    val highlightMarkers =
      if (option.highlight) Parser.exec(lang.Scala, text)
      else js.Array()

    val selectionMarker = (
      option.selection.startPos.toOption,
      option.selection.endPos.toOption.orElse(option.selection.updatedPos.toOption)) match {
      case (Some(start), Some(end)) =>
        markSelection(Seq(start, end).min, Seq(start, end).max)
      case _ => js.Array()
    }
    val newLineMarker = markNewLine(text)
    (highlightMarkers.concat(selectionMarker).concat(newLineMarker)).sortBy(_.pos)
  }

  def markNewLine(text: String): js.Array[Marker] = {
    @tailrec
    def go(offset: Int, result: ArrayBuffer[Marker]): ArrayBuffer[Marker] = {
      val i = text.indexOf('\n', offset)
      if (i == -1) {
        result
      } else {
        result += Marker("newline", i, None)
        go(i + 1, result)
      }
    }

    go(0, ArrayBuffer.empty[Marker]).toJSArray
  }

  def markSelection(start: Int, end: Int): js.Array[Marker] =
    js.Array(Marker("selection", start, Some("start")), Marker("selection", end, Some("end")))

}

class MarkerOption(val highlight: Boolean, val selection: Selection)
