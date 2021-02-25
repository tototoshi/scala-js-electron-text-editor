package editor.core.worker

import scala.scalajs.js
import scala.scalajs.js.UndefOr

trait WorkerRequest extends js.Object {
  val id: Int
  val content: String
  val selection: WorkerSelectionRequest
}

trait WorkerSelectionRequest extends js.Object {
  val startPos: UndefOr[Int]
  val updatedPos: UndefOr[Int]
  val endPos: UndefOr[Int]
}
