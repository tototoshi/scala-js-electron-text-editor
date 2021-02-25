package editor.core.model

import editor.core.parser.{Marker, MarkerOption}
import editor.core.util.Text

import scala.annotation.tailrec
import scala.scalajs.js

class View(val lines: js.Array[ViewLine]) extends js.Object {

  private val lineLength = lines.length

  private def insertNoNewLine(x: Int, y: Int, s: String): View = {

    @tailrec
    def go(offsetY: Int, result: js.Array[ViewLine]): js.Array[ViewLine] = {
      if (offsetY > lineLength) {
        result
      } else if (offsetY >= y) {
        val splitted = lines(offsetY).split(x)
        val newLine = splitted(0).addElement(new ViewElement(js.Array(), s)).merge(splitted(1))
        result.concat(js.Array(newLine), lines.drop(offsetY + 1))
      } else {
        go(offsetY + 1, result :+ lines(offsetY))
      }
    }

    new View(go(0, js.Array()))
  }

  private def insertNewLine(x: Int, y: Int): View = {

    @tailrec
    def go(offsetY: Int, result: js.Array[ViewLine]): js.Array[ViewLine] = {
      if (offsetY > lineLength) {
        result
      } else if (offsetY >= y) {
        val splitted = lines(offsetY).split(x)
        result.concat(splitted).concat(lines.drop(offsetY + 1))
      } else {
        go(offsetY + 1, result :+ lines(offsetY))
      }
    }

    new View(go(0, js.Array()))

  }

  def remove(x1: Int, y1: Int, x2: Int, y2: Int): View = {
    new View(
      lines.take(y1) :+
        lines(y1).split(x1)(0).merge(lines(y2).split(x2)(1)) :++
        lines.drop(y2 + 1))
  }

  def insert(x: Int, y: Int, s: String): View = {
    @tailrec
    def go(ss: Seq[String], result: View): View = {
      ss.headOption match {
        case Some(s) if s.indexOf('\n') == -1 =>
          go(ss.tail, result.insertNoNewLine(x, y, s))
        case Some(s) if s.indexOf('\n') == 0 =>
          go(ss.tail, result.insertNewLine(x, y))
        case Some(s) =>
          val i = s.indexOf('\n')
          go(ss.tail, result.insertNewLine(x, y).insertNoNewLine(x, y, s.substring(0, i)))
        case None => result
      }
    }

    val ss = Text.toLines(s).reverse
    go(ss.toSeq, this)
  }

}

object View {

  def create(text: String, option: MarkerOption): View = {

    @tailrec
    def go(
        marker: js.Array[Marker],
        offset: Int,
        types: js.Array[String],
        viewLine: ViewLine,
        viewLines: js.Array[ViewLine]
      ): js.Array[ViewLine] = {
      marker.headOption match {
        case Some(m) if m.action.contains("start") =>
          val newViewLine =
            if (m.pos > offset)
              viewLine.addElement(new ViewElement(types, text.substring(offset, m.pos)))
            else
              viewLine
          go(
            marker.tail,
            m.pos,
            (types :+ m.`type`).distinct,
            newViewLine,
            viewLines
          )
        case Some(m) if m.action.contains("end") =>
          val newViewLine =
            if (m.pos > offset)
              viewLine.addElement(new ViewElement(types, text.substring(offset, m.pos)))
            else
              viewLine
          go(
            marker.tail,
            m.pos,
            types.filterNot(_ == m.`type`),
            newViewLine,
            viewLines
          )
        case Some(m) if m.`type` == "newline" =>
          val newViewLine =
            if (m.pos > offset)
              viewLine.addElement(new ViewElement(types, text.substring(offset, m.pos)))
            else
              viewLine
          go(
            marker.tail,
            m.pos + 1,
            types,
            ViewLine.empty(),
            viewLines.concat(js.Array(newViewLine))
          )
        case Some(m) =>
          sys.error("error: unknown marker")
        case None =>
          val newViewLine =
            if (text.length > offset)
              viewLine.addElement(new ViewElement(types, text.substring(offset)))
            else
              viewLine
          viewLines.concat(js.Array(newViewLine))
      }
    }

    val marker = Marker.mark(text, option)
    val viewLines = go(marker, 0, js.Array(), ViewLine.empty(), js.Array())
    new View(viewLines)
  }

}
