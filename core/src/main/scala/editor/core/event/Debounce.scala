package editor.core.event

import scala.scalajs.js
import scala.scalajs.js.timers.SetTimeoutHandle

class Debounce[T](val interval: Double) {

  private var handle: js.Array[SetTimeoutHandle] = js.Array()

  def wrap(task: T)(f: T => Unit): Unit = {

    handle.drop(1).foreach { h =>
      js.timers.clearTimeout(h)
    }
    handle = handle.take(1)

    handle.push(js.timers.setTimeout(interval) {
      f(task)
      handle.pop()
    })
  }

}
