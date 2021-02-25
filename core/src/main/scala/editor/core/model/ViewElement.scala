package editor.core.model

import editor.core.util.ArrayOps

import scala.scalajs.js

object ViewElement {

  private var id: Int = 0

  def nextId(): Int = {
    id = id + 1
    id
  }

}

class ViewElement(val types: js.Array[String], val text: String) extends js.Object {

  val id: Int = ViewElement.nextId()

  def elementEquals(o: ViewElement): Boolean = ArrayOps.containsSameElements(types, o.types) && text == o.text

  def split(i: Int): js.Array[ViewElement] = {
    js.Array(
      new ViewElement(types, text.substring(0, i)),
      new ViewElement(types, text.substring(i))
    )
  }

}
