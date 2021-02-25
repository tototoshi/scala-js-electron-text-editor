package editor.core.model

import editor.core.event.AppEvent.BufferId
import editor.core.event.{AppEvent, EventBus}
import editor.core.parser.MarkerOption
import editor.core.worker.{WorkerResponse, WorkerResponseView}
import editor.electron.Electron.ipcRenderer
import org.scalajs.dom.webworkers.Worker

import scala.concurrent.ExecutionContext.Implicits.global

class EditorModel {

  private val eventBus = EventBus.get()

  private var state: EditorState = EditorState.empty(0)

  private val worker = new Worker("dist/worker.js")

  addListener()

  private def addListener(): Unit = {
    worker.onmessage = (e) => {
      val prevState = state
      val newBuffer = e.data.asInstanceOf[WorkerResponse]
      for {
        prevBuffer <- prevState.buffers.find(_.id == newBuffer.id)
      } {
        if (!prevState.composition && prevBuffer.content == newBuffer.content) {
          val currentLines = prevBuffer.view.lines
          val lines = convertWorkerResponseToView(newBuffer.view).lines

          0.until(Seq(lines.length, currentLines.length).min).foreach { i =>
            if (lines(i).viewLineEquals(currentLines(i))) {
              lines(i) = currentLines(i)
            }
          }

          val newView = new View(lines)
          state = prevState.updateView(newBuffer.id, newView)
          eventBus.emit(AppEvent.EditorStateUpdated(state))
        }

      }
    }

    eventBus.on {
      case AppEvent.Cancel(bufferId) => update(bufferId, state.cancel(bufferId))
      case _ if state.controlX =>
        println("not implemented")
      case AppEvent.ControlX() => update(state.enableContextualCommandEnabled())
      case AppEvent.Init(bufferId) => init(bufferId)
      case AppEvent.StartSelection(bufferId, x, y) => update(bufferId, state.startSelection(bufferId, x, y))
      case AppEvent.EndSelection(bufferId, x, y) => update(bufferId, state.endSelection(bufferId, x, y))
      case AppEvent.UpdateSelection(bufferId, x, y) => update(bufferId, state.updateSelection(bufferId, x, y))
      case AppEvent.ClearSelection(bufferId) => update(bufferId, state.clearSelection(bufferId))
      case AppEvent.DeleteBackwardChar(bufferId) => update(bufferId, state.deleteBackwardChar(bufferId))
      case AppEvent.DeleteForwardChar(bufferId) => update(bufferId, state.deleteForwardChar(bufferId))
      case AppEvent.MoveForward(bufferId) => update(bufferId, state.moveForward(bufferId))
      case AppEvent.MoveBackward(bufferId) => update(bufferId, state.moveBackward(bufferId))
      case AppEvent.MoveUp(bufferId) => update(bufferId, state.moveUp(bufferId))
      case AppEvent.MoveDown(bufferId) => update(bufferId, state.moveDown(bufferId))
      case AppEvent.MoveStart(bufferId) => update(bufferId, state.moveStart(bufferId))
      case AppEvent.MoveEnd(bufferId) => update(bufferId, state.moveEnd(bufferId))
      case AppEvent.MoveStartOfLine(bufferId) => update(bufferId, state.moveStartOfLine(bufferId))
      case AppEvent.MoveEndOfLine(bufferId) => update(bufferId, state.moveEndOfLine(bufferId))
      case AppEvent.KillLine(bufferId) => update(bufferId, state.killLine(bufferId))
      case AppEvent.InsertLine(bufferId) => update(bufferId, state.insertLine(bufferId))
      case AppEvent.Indent(bufferId) => update(bufferId, state.indent(bufferId))
      case AppEvent.SelectAll(bufferId) => update(bufferId, state.selectAll(bufferId))
      case AppEvent.MarkPosition(bufferId) => update(bufferId, state.markPosition(bufferId))
      case AppEvent.Cut(bufferId) => update(bufferId, state.cut(bufferId))
      case AppEvent.Paste(bufferId) => update(bufferId, state.paste(bufferId))
      case AppEvent.Undo(bufferId) => update(bufferId, state.undo(bufferId))
      case AppEvent.Input(bufferId, s) => update(bufferId, state.insert(bufferId, s))
      case AppEvent.StartComposition() => update(state.startComposition())
      case AppEvent.EndComposition() => update(state.endComposition())
    }
  }

  private def init(bufferId: BufferId): Unit = {
    for {
      content <- ipcRenderer.invoke("content").toFuture
      _ = println("ipcRenderer.invoke: content")
    } {
      load(bufferId, content.asInstanceOf[String], 0)
    }
  }

  private def load(bufferId: BufferId, content: String, pos: Int): Unit = {
    state = EditorState
      .empty(bufferId)
      .insert(bufferId, content)
      .moveStart(bufferId)
      .updateView(bufferId, View.create(content, new MarkerOption(false, Selection.empty)))
    eventBus.emit(AppEvent.EditorStateUpdated(state))
    state = state.updateView(bufferId, View.create(content, new MarkerOption(true, Selection.empty)))
    eventBus.emit(AppEvent.EditorStateUpdated(state))
  }

  private def convertWorkerResponseToView(view: WorkerResponseView): View = {
    val viewLines = view.lines.map(line => {
      new ViewLine(
        line.elements.map((e) => new ViewElement(e.types, e.text))
      )
    })
    new View(viewLines)
  }

  def update(newState: EditorState): Unit = {
    state = newState
    eventBus.emit(AppEvent.EditorStateUpdated(state))
  }

  def update(id: BufferId, newState: EditorState): Unit = {
    val prevState = state
    state = newState
    eventBus.emit(AppEvent.EditorStateUpdated(state))

    for {
      prevBuf <- prevState.buffers.find(_.id == id)
      newBuf <- newState.buffers.find(_.id == id)
    } {
      if (prevBuf.content != newBuf.content ||
          prevBuf.selection != newBuf.selection) {
        worker.postMessage(newBuf)
      }
    }
  }

}
