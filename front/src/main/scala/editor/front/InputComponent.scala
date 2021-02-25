package editor.front

import editor.core.event.{AppEvent, EventBus}
import editor.react.ReactElementBuilder._
import editor.react.{React, ReactElement}
import org.scalajs.dom.{CompositionEvent, KeyboardEvent}

import scala.scalajs.js

trait InputComponentProps extends js.Object {
  val bufferId: Int
}

class InputComponent(override val props: InputComponentProps) extends React.Component(props) {

  private val eventBus = EventBus.get()

  private val input = React.createRef()

  private val onEditorClick: PartialFunction[AppEvent, Unit] = {
    case AppEvent.Focus(bufferId) if props.bufferId == bufferId => focus()
  }

  def componentDidMount(): Unit = {
    focus()
    eventBus.on(onEditorClick)
  }

  def componentDidUpdate(): Unit = {
    focus()
  }

  def componentWillUnmount(): Unit = {
    eventBus.removeListener(onEditorClick)
  }
  private def focus(): Unit = {
    if (input.current != null) {
      input.current.focus()
      input.current.innerHTML = ""
    }
  }

  def handleKeyDown(e: KeyboardEvent): Unit = {
    /*
      https://developer.mozilla.org/ja/docs/Web/API/Document/keydown_event
      229 は IME によって処理されたイベントに関連する keyCode の特殊な値のセットです
     */
    if (/*e.isComposing || */ e.keyCode == 229) {
      e.preventDefault()
    } else {
      resolveAppEvent(e).foreach { appEvent =>
        e.preventDefault()
        eventBus.emit(appEvent)
      }
    }
  }

  def resolveAppEvent(e: KeyboardEvent): Option[AppEvent] = {
    import KeyMatchers._

    val event = e match {
      case Control() & Key("m") | Key("Enter") => Some(AppEvent.Input(props.bufferId, "\n"))
      case Control() & Key("h") | Key("Backspace") => Some(AppEvent.DeleteBackwardChar(props.bufferId))
      case Control() & Key("d") => Some(AppEvent.DeleteForwardChar(props.bufferId))
      case Control() & Key("b") | Key("ArrowLeft") => Some(AppEvent.MoveBackward(props.bufferId))
      case Control() & Key("f") | Key("ArrowRight") => Some(AppEvent.MoveForward(props.bufferId))
      case Control() & Key("p") | Key("ArrowUp") => Some(AppEvent.MoveUp(props.bufferId))
      case Control() & Key("n") | Key("ArrowDown") => Some(AppEvent.MoveDown(props.bufferId))
      case Control() & Key("a") => Some(AppEvent.MoveStartOfLine(props.bufferId))
      case Control() & Key("e") => Some(AppEvent.MoveEndOfLine(props.bufferId))
      case Control() & Key("k") => Some(AppEvent.KillLine(props.bufferId))
      case Control() & Key("o") => Some(AppEvent.InsertLine(props.bufferId))
      case Control() & Key("i") => Some(AppEvent.Indent(props.bufferId))
      case Control() & Key(" ") => Some(AppEvent.MarkPosition(props.bufferId))
      case Control() & Key("x") => Some(AppEvent.ControlX())
      case Control() & Key("w") => Some(AppEvent.Cut(props.bufferId))
      case Control() & Key("y") => Some(AppEvent.Paste(props.bufferId))
      case Control() & Key("_") => Some(AppEvent.Undo(props.bufferId))
      case Control() & Key("g") => Some(AppEvent.Cancel(props.bufferId))
      case Meta() & Key(",") => Some(AppEvent.MoveStart(props.bufferId))
      case Meta() & Key(".") => Some(AppEvent.MoveEnd(props.bufferId))
      case Meta() | Alt() | Control() => None
      case Key(k) if k.length == 1 => Some(AppEvent.Input(props.bufferId, k))
      case _ => None
    }

    event
  }

  def handleCompositionStart(e: CompositionEvent): Unit = {
    eventBus.emit(AppEvent.StartComposition())
  }

  def handleCompositionEnd(e: CompositionEvent): Unit = {
    val data = e.data // e is SyntheticEvent
    eventBus.emit(AppEvent.EndComposition())
    eventBus.emit(AppEvent.Input(props.bufferId, data))
  }

  def render(): ReactElement = {
    span(
      "className" -> "cursor",
      "ref" -> input,
      "tabIndex" -> 0,
      "contentEditable" -> true,
      "onKeyDown" -> handleKeyDown _,
      "onCompositionStart" -> handleCompositionStart _,
      "onCompositionEnd" -> handleCompositionEnd _
    ).children()
  }

}
