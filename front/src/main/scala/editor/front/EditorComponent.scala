package editor.front

import editor.core.event.{AppEvent, EventBus}
import editor.core.model.{Buffer, EditorState}
import editor.react.ReactElementBuilder._
import editor.react.{React, ReactElement}
import org.scalajs.dom.MouseEvent

import scala.scalajs.js

trait EditorComponentState extends js.Object {
  val bufferId: Int
  val editorState: EditorState
}

class DefaultEditorComponentState extends EditorComponentState {
  override val bufferId: Int = Buffer.nextId()
  override val editorState: EditorState = EditorState.empty(bufferId)

}

class EditorComponent extends React.Component {

  private val eventBus = EventBus.get()

  // must be public
  var state: EditorComponentState = new DefaultEditorComponentState

  private val handleStateUpdated: PartialFunction[AppEvent, Unit] = {
    case AppEvent.EditorStateUpdated(state) =>
      setState(js.Dynamic.literal("editorState" -> state))
  }

  def componentDidMount(): Unit = {
    eventBus.on(handleStateUpdated)
    eventBus.emit(AppEvent.Init(state.bufferId))
  }

  def componentWillUnmount(): Unit = {
    eventBus.removeListener(handleStateUpdated)
  }

  def handleClick(e: MouseEvent): Unit = {
    eventBus.emit(AppEvent.Focus(state.bufferId))
  }

  def render(): ReactElement = {
    val buffer = state.editorState.buffers.find(_.id == this.state.bufferId).getOrElse(sys.error("illegal state"))
    val view = buffer.view

    val cursor = buffer.cursor

    div("className" -> "app").children(
      div("className" -> "editor-wrapper", "onClick" -> handleClick _).children(
        div("className" -> "line-number").children(
          view.lines.zipWithIndex.map {
            case (_, i) =>
              div("key" -> i, "className" -> "line-number-text").children(
                span().children((i + 1).toString)
              )
          }
        ),
        div("className" -> "editor").children(
          div("className" -> "editor-content").children(
            view.lines.zipWithIndex.map {
              case (line, i) =>
                component[LineComponent](
                  "key" -> line.id,
                  "bufferId" -> state.bufferId,
                  "id" -> line.id,
                  "elements" -> line.elements,
                  "cursor" -> (if (cursor.y == i) cursor else null)).children()
            }
          )
        )
      ),
      div("className" -> "status-bar").children(
        input("type" -> "text").children()
      )
    )

  }

}
