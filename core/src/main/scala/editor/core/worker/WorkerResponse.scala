package editor.core.worker

import scala.scalajs.js

trait WorkerResponseElement extends js.Object {
  val types: js.Array[String]
  val text: String
}

trait WorkerResponseViewLine extends js.Object {
  val elements: js.Array[WorkerResponseElement]
}

trait WorkerResponseView extends js.Object {
  val lines: js.Array[WorkerResponseViewLine]
}

trait WorkerResponse extends js.Object {
  val id: Int
  val content: String
  val view: WorkerResponseView
}
