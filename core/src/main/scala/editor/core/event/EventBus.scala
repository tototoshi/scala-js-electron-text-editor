package editor.core.event

import scala.scalajs.js

class EventBus {

  type Callback = PartialFunction[AppEvent, Unit]

  private var subscriber: js.Array[Callback] = js.Array()

  def on(callback: Callback): Unit = {
    subscriber.push(callback)
  }

  def removeListener(callback: Callback): Unit = {
    subscriber = subscriber.filterNot(_ == callback)
  }

  def emit(data: AppEvent): Unit = {
    println(data.toString)
    subscriber.foreach { callback =>
      val default: AppEvent => Unit = _ => ()
      callback.applyOrElse(data, default)
    }
  }

}

object EventBus {

  private val instance = new EventBus()

  def get(): EventBus = instance

}
