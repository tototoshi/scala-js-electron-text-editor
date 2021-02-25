package editor.react

import org.scalajs.dom

import scala.scalajs.js
import scala.scalajs.js.annotation.JSImport
import scala.scalajs.js.|

@JSImport("react-dom", JSImport.Namespace)
@js.native
object ReactDom extends js.Object {

  def render(reactElement: ReactElement, domElement: dom.Element): Unit = js.native

}

@JSImport("react", JSImport.Namespace)
@js.native
object React extends js.Object {

  @js.native
  class Component[P <: js.Object](val props: P = js.Object()) extends js.Object {
    def setState(state: js.Object): Unit = js.native
  }

  def createElement(tag: String | js.Dynamic, props: js.Object, children: (ReactElement | String)*): ReactElement =
    js.native

  def createElement(tag: String | js.Dynamic, props: js.Object, children: js.Array[ReactElement]): ReactElement =
    js.native

  def createRef(): ReactRef = js.native

}

@js.native
trait ReactElement extends js.Object

@js.native
trait ReactRef extends js.Object {

  def current: dom.html.Element = js.native

}

@js.native
trait SyntheticEvent extends js.Object {
  def persist(): Unit = js.native
}
