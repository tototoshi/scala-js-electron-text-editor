package editor.core.model

import scala.scalajs.js
import scala.scalajs.js.JSConverters._

object EditorState {

  def empty(id: Int): EditorState = new EditorState(
    buffers = js.Array(Buffer.empty(id)),
    clipboardText = js.undefined,
    composition = false,
    controlX = false
  )

}

class EditorState(
    val buffers: js.Array[Buffer],
    val clipboardText: js.UndefOr[String],
    val composition: Boolean,
    val controlX: Boolean)
  extends js.Object {

  type BufferId = Int

  def deleteBackwardChar(id: BufferId): EditorState =
    updateBuffer(id, _.deleteBackwardChar())

  def deleteForwardChar(id: BufferId): EditorState =
    updateBuffer(id, _.deleteForwardChar())

  def enter(id: BufferId): EditorState =
    updateBuffer(id, _.enter())

  def moveBackward(id: BufferId): EditorState =
    updateBuffer(id, _.moveBackward())

  def moveForward(id: BufferId): EditorState =
    updateBuffer(id, _.moveForward())

  def moveUp(id: BufferId): EditorState =
    updateBuffer(id, _.moveUp())

  def moveDown(id: BufferId): EditorState =
    updateBuffer(id, _.moveDown())

  def moveStartOfLine(id: BufferId): EditorState = {
    updateBuffer(id, _.moveStartOfLine())
  }

  def moveEndOfLine(id: BufferId): EditorState = {
    updateBuffer(id, _.moveEndOfLine())
  }

  def moveStart(id: BufferId): EditorState = {
    updateBuffer(id, _.moveStart())
  }

  def moveEnd(id: BufferId): EditorState = {
    updateBuffer(id, _.moveEnd())
  }

  def killLine(id: BufferId): EditorState =
    findBuffer(id) match {
      case Some(buffer) =>
        val (newBuf, killedText) = buffer.killLine()
        val clipboardText = if (killedText.isEmpty || killedText == "\n") {
          this.clipboardText
        } else {
          killedText: js.UndefOr[String]
        }
        updateBuffer(id, _ => newBuf)
          .copy(clipboardText = clipboardText)
      case None => this
    }

  def insertLine(id: BufferId): EditorState =
    updateBuffer(id, _.insertLine())

  def insert(id: BufferId, s: String): EditorState =
    updateBuffer(id, _.insert(s))

  def startComposition(): EditorState =
    copy(composition = true)

  def endComposition(): EditorState =
    copy(composition = false)

  def indent(id: BufferId): EditorState =
    updateBuffer(id, _.indent())

  def selectAll(id: BufferId): EditorState =
    updateBuffer(id, _.selectAll())

  def markPosition(id: BufferId): EditorState =
    updateBuffer(id, _.markPosition())

  def paste(id: BufferId): EditorState = {
    clipboardText.toOption match {
      case Some(ct) => updateBuffer(id, _.paste(ct))
      case None => this
    }
  }

  def cut(id: BufferId): EditorState =
    findBuffer(id) match {
      case Some(buffer) =>
        val (newBuf, maybeClipboardText) = buffer.cut()
        updateBuffer(id, _ => newBuf)
          .copy(clipboardText = maybeClipboardText.orElse(clipboardText.toOption).orUndefined)
      case None => this
    }

  def startSelection(id: BufferId, x: Option[Int], y: Int): EditorState =
    updateBuffer(id, _.startSelection(x, y))

  def updateSelection(id: BufferId, x: Option[Int], y: Int): EditorState =
    updateBuffer(id, _.updateSelection(x, y))

  def endSelection(id: BufferId, x: Option[Int], y: Int): EditorState =
    updateBuffer(id, _.endSelection(x, y))

  def clearSelection(id: BufferId): EditorState =
    updateBuffer(id, _.clearSelection())

  def updateView(id: BufferId, view: View): EditorState =
    updateBuffer(id, _.updateView(view))

  def undo(id: BufferId): EditorState = this

  def cancel(id: BufferId): EditorState = updateBuffer(id, _.cancel()).disableContextualCommandEnabled()

  def enableContextualCommandEnabled(): EditorState = copy(controlX = true)

  def disableContextualCommandEnabled(): EditorState = copy(controlX = false)

  private def findBuffer(id: BufferId): Option[Buffer] = buffers.find(_.id == id)

  private def updateBuffer(id: BufferId, f: Buffer => Buffer): EditorState =
    copy(buffers = findBuffer(id).map(f).toJSArray.concat(buffers.filterNot(_.id == id)))

  private def copy(
      buffers: js.Array[Buffer] = buffers,
      clipboardText: js.UndefOr[String] = clipboardText,
      composition: Boolean = composition,
      controlX: Boolean = controlX
    ): EditorState = new EditorState(
    buffers = buffers,
    clipboardText = clipboardText,
    composition = composition,
    controlX = controlX
  )

}
