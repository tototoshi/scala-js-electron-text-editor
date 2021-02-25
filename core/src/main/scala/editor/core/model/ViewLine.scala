package editor.core.model

import scala.annotation.tailrec
import scala.scalajs.js

class ViewLine(val elements: js.Array[ViewElement]) extends js.Object {

  val id: Int = ViewLine.nextId()

  def viewLineEquals(o: ViewLine): Boolean = {
    elements.length == o.elements.length &&
    elements.zip(o.elements).forall {
      case (e1, e2) =>
        e1.elementEquals(e2)
    }
  }

  def split(x: Int): js.Array[ViewLine] = {

    @tailrec
    def go(es: js.Array[ViewElement], offset: Int, v1: ViewLine, v2: ViewLine): js.Array[ViewLine] = {
      es.headOption match {
        case Some(e) if offset > x =>
          go(es.tail, offset + e.text.length, v1, new ViewLine(v2.elements :+ e))
        case Some(e) if offset + e.text.length > x =>
          val splitted = e.split(x - offset)
          go(
            es.tail,
            offset + e.text.length,
            new ViewLine(v1.elements :+ splitted(0)),
            new ViewLine(v2.elements :+ splitted(1)))
        case Some(e) =>
          go(es.tail, offset + e.text.length, new ViewLine(v1.elements :+ e), v2)
        case None => js.Array(v1, v2)
      }
    }

    go(elements, 0, ViewLine.empty(), ViewLine.empty())
  }

  def addElement(element: ViewElement): ViewLine = {
    new ViewLine(elements = elements :+ element)
  }

  def merge(viewLine: ViewLine): ViewLine = {
    new ViewLine(elements ++ viewLine.elements)
  }

}

object ViewLine {

  private var id = 0

  private def nextId(): Int = {
    id += 1
    id
  }

  def empty(): ViewLine = new ViewLine(js.Array())

}
