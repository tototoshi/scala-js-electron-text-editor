package editor.front

import editor.core.event.{AppEvent, Debounce, EventBus}
import editor.react.ReactElementBuilder._
import editor.react.{React, ReactElement, SyntheticEvent}
import org.scalajs.dom.raw.{HTMLElement, MouseEvent}

import scala.scalajs.js

trait ElementComponentProps extends js.Object {
  val bufferId: Int
  val className: String
  val text: String
}

object ElementComponent {
  private val debounce = new Debounce[(Int, Int)](200)
}

class ElementComponent(override val props: ElementComponentProps) extends React.Component(props) {

  private val eventBus = EventBus.get()

  private def handleElementClick(e: MouseEvent): Unit = {
    e.preventDefault()
    e.stopPropagation()
    eventBus.emit(AppEvent.ClearSelection(props.bufferId))
  }

  private def handleElementMouseUp(e: MouseEvent): Unit = {
    e.preventDefault()
    e.stopPropagation()
    eventBus.emit(AppEvent.EndSelection(props.bufferId, Some(getX(e)), getY(e)))
  }

  private def handleElementMouseDown(e: MouseEvent): Unit = {
    e.preventDefault()
    e.stopPropagation()
    eventBus.emit(AppEvent.StartSelection(props.bufferId, Some(getX(e)), getY(e)))
  }

  private def handleElementMouseEnterAndMove(ev: SyntheticEvent): Unit = {
    ev.persist()
    val e = ev.asInstanceOf[MouseEvent]
    e.preventDefault()
    e.stopPropagation()
    val x = getX(e)
    val y = getY(e)

    ElementComponent.debounce.wrap((x, y)) {
      case (x, y) =>
        eventBus.emit(AppEvent.UpdateSelection(props.bufferId, Some(x), y))
    }
  }

  private def getX(e: MouseEvent): Int = {
    val target = e.target.asInstanceOf[HTMLElement]
    val charWidth =
      target.getBoundingClientRect().width / props.text.length
    val offsetLeft = target.parentElement.getBoundingClientRect().left
    ((e.pageX - offsetLeft) / charWidth).round.toInt
  }

  private def getY(e: MouseEvent): Int = {
    val target = e.target.asInstanceOf[HTMLElement]
    (target.offsetTop / 18).toInt
  }

  def render(): ReactElement = {
    span(
      "className" -> props.className,
      "onClick" -> handleElementClick _,
      "onMouseDown" -> handleElementMouseDown _,
      "onMouseUp" -> handleElementMouseUp _,
      "onMouseMove" -> handleElementMouseEnterAndMove _,
      "onMouseEnter" -> handleElementMouseEnterAndMove _
    ).children(props.text)
  }

}
