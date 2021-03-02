
package worker

import editor.core.model.{Selection, View}
import editor.core.parser.MarkerOption
import editor.core.worker.WorkerRequest

import scala.scalajs.js
import scala.scalajs.js.Date

class WorkerProcess {

  private val tasks: js.Array[WorkerRequest] = js.Array()

  def register(request: WorkerRequest): Unit = {
    tasks.push(request)
  }

  def processTask(): Unit = {
    if (tasks.length == 0) {
      next(100)
    } else {
      val request = tasks.pop()
      tasks.length = 0
      val start = Date.now()
      println("worker process start")

      val selection = new Selection(
        request.selection.startPos,
        request.selection.updatedPos,
        request.selection.endPos,
      )

      val v = View.create(request.content, new MarkerOption(true, selection))

      val payload = scalajs.js.Dynamic.literal(
        "id" -> request.id,
        "content" -> request.content,
        "view" -> v
      )

      scalajs.js.Dynamic.global.postMessage(payload)
      val end = Date.now()

      println(s"worker process: end ${end - start}ms")

      next(0)
    }
  }

  private def next(interval: Int): Unit = {
    js.timers.setTimeout(interval) {
      processTask()
    }
  }

}
