package editor.core.event

import editor.core.model.EditorState

sealed trait AppEvent

object AppEvent {

  type BufferId = Int

  case class Init(id: BufferId) extends AppEvent
  case class Focus(id: BufferId) extends AppEvent

  case class EditorStateUpdated(editorState: EditorState) extends AppEvent

  // KeyEvent
  case class NewLine(id: BufferId) extends AppEvent
  case class DeleteBackwardChar(id: BufferId) extends AppEvent
  case class DeleteForwardChar(id: BufferId) extends AppEvent
  case class MoveBackward(id: BufferId) extends AppEvent
  case class MoveForward(id: BufferId) extends AppEvent
  case class MoveUp(id: BufferId) extends AppEvent
  case class MoveDown(id: BufferId) extends AppEvent
  case class MoveStartOfLine(id: BufferId) extends AppEvent
  case class MoveEndOfLine(id: BufferId) extends AppEvent
  case class MoveStart(id: BufferId) extends AppEvent
  case class MoveEnd(id: BufferId) extends AppEvent
  case class KillLine(id: BufferId) extends AppEvent
  case class InsertLine(id: BufferId) extends AppEvent
  case class Indent(id: BufferId) extends AppEvent
  case class SelectAll(id: BufferId) extends AppEvent
  case class MarkPosition(id: BufferId) extends AppEvent
  case class Cut(id: BufferId) extends AppEvent
  case class Paste(id: BufferId) extends AppEvent
  case class Undo(id: BufferId) extends AppEvent
  case class Cancel(id: BufferId) extends AppEvent
  case class Input(id: BufferId, s: String) extends AppEvent
  case class StartComposition() extends AppEvent
  case class EndComposition() extends AppEvent
  case class ControlX() extends AppEvent

  // Selection
  case class StartSelection(id: BufferId, x: Option[Int], y: Int) extends AppEvent
  case class EndSelection(id: BufferId, x: Option[Int], y: Int) extends AppEvent
  case class UpdateSelection(id: BufferId, x: Option[Int], y: Int) extends AppEvent
  case class ClearSelection(id: BufferId) extends AppEvent
}
