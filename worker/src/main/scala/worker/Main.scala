package worker

import editor.core.worker.WorkerRequest
import org.scalajs.dom.MessageEvent

import scala.scalajs.js

object Main {

  private val workerProcess = new WorkerProcess()

  def main(args: Array[String]): Unit = {
    js.Dynamic.global.onmessage = (e: MessageEvent) => {
      val state = e.data.asInstanceOf[WorkerRequest]
      workerProcess.register(state)
    }
    workerProcess.processTask()
  }

}
