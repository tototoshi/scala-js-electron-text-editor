package editor.core.util

import scala.scalajs._

object ArrayOps {

  def containsSameElements[T](a1: js.Array[T], a2: js.Array[T]): Boolean = {
    a1.length == a2.length &&
    a1.zip(a2).forall { case (x, y) => x == y }
  }

}
