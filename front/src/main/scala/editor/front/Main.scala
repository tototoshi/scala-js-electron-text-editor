package editor.front

import editor.core.model.EditorModel
import editor.react.ReactDom
import editor.react.ReactElementBuilder._

object Main {

  def main(args: Array[String]): Unit = {

    new EditorModel()

    ReactDom.render(
      component[EditorComponent]("className" -> "editor").children(),
      org.scalajs.dom.document.getElementById("root")
    )
  }

}
