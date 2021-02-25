package editor.front

import org.scalajs.dom.KeyboardEvent

object KeyMatchers {

  object Alt {

    def unapply(e: KeyboardEvent): Boolean = {
      e.getModifierState("Alt")
    }

  }

  object Control {

    def unapply(e: KeyboardEvent): Boolean = {
      e.getModifierState("Control")
    }

  }

  object Meta {

    def unapply(e: KeyboardEvent): Boolean = {
      e.getModifierState("Meta")
    }

  }

  object Key {

    def unapply(e: KeyboardEvent): Option[String] = {
      Some(e.key)
    }

  }

  object & {
    def unapply[A](o: A): Option[(A, A)] = Some((o, o))
  }

}
