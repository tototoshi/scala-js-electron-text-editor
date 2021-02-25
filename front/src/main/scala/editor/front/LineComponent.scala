package editor.front

import editor.core.event.{AppEvent, Debounce, EventBus}
import editor.core.model.{Coordinates, ViewElement, ViewLine}
import editor.react.ReactElementBuilder._
import editor.react.{React, ReactElement, SyntheticEvent}
import org.scalajs.dom.raw.{HTMLElement, MouseEvent}

import scala.scalajs.js

trait LineComponentProps extends js.Object {
  val id: Int
  val bufferId: Int
  val cursor: Coordinates
  val elements: js.Array[ViewElement]
}

object LineComponent {
  val debouncer = new Debounce[Int](200)
}

class LineComponent(override val props: LineComponentProps) extends React.Component(props) {

  private val eventBus = EventBus.get()

  def shouldComponentUpdate(props: LineComponentProps): Boolean = {
    props.id != this.props.id ||
    props.cursor != null ||
    this.props.cursor != null
  }

  private def handleLineClick(e: MouseEvent): Unit = {
    e.preventDefault()
    e.stopPropagation()
    eventBus.emit(AppEvent.ClearSelection(props.bufferId))
  }

  private def handleLineMouseUp(e: MouseEvent): Unit = {
    e.preventDefault()
    e.stopPropagation()
    eventBus.emit(AppEvent.EndSelection(props.bufferId, None, getY(e)))
  }

  private def handleLineMouseDown(e: MouseEvent): Unit = {
    e.preventDefault()
    e.stopPropagation()
    eventBus.emit(AppEvent.StartSelection(props.bufferId, None, getY(e)))
  }

  private def handleLineMouseEnter(ev: SyntheticEvent): Unit = {
    ev.persist()
    val e = ev.asInstanceOf[MouseEvent]
    e.preventDefault()
    e.stopPropagation()
    val y = getY(e)
    LineComponent.debouncer.wrap(y) { y =>
      eventBus.emit(AppEvent.UpdateSelection(props.bufferId, None, getY(e)))
    }
  }

  private def getY(e: MouseEvent): Int = {
    val target = e.target.asInstanceOf[HTMLElement]
    (target.offsetTop / 18).toInt
  }

  def render(): ReactElement = {
    val cursor = props.cursor

    val elements = if (cursor != null) {
      val splitted = new ViewLine(props.elements)
        .split(cursor.x)
        .map(_.elements)
      js.Array().concat(splitted(0), js.Array(null), splitted(1))
    } else {
      props.elements
    }

    val style = if (cursor != null) {
      js.Array("line", "cursor")
    } else {
      js.Array("line")
    }

    div(
      "className" -> style.join(" "),
      "onClick" -> handleLineClick _,
      "onMouseDown" -> handleLineMouseDown _,
      "onMouseUp" -> handleLineMouseUp _,
      "onMouseEnter" -> handleLineMouseEnter _
    ).children(
      span("className" -> "line-content").children(elements.zipWithIndex.map {
        case (e, i) =>
          if (e == null) component[InputComponent]("key" -> "cursor", "bufferId" -> props.bufferId).children()
          else
            component[ElementComponent](
              "key" -> i,
              "bufferId" -> props.bufferId,
              "className" -> e.types.join(" "),
              "text" -> e.text).children()
      })
    )
  }

}
