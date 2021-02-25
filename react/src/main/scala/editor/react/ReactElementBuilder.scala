package editor.react

import scala.scalajs.js
import scala.scalajs.js.{ConstructorTag, constructorTag, |}

object ReactElementBuilder {

  def div(fields: (String, js.Any)*): ReactElementNode = tag("div", fields)

  def input(fields: (String, js.Any)*): ReactElementNode = tag("input", fields)

  def span(fields: (String, js.Any)*): ReactElementNode = tag("span", fields)

  def component[T <: js.Any: ConstructorTag](fields: (String, js.Any)*): ReactElementNode =
    new ReactElementNode(constructorTag[T].constructor, fields)

  private def tag(tagName: String, fields: Seq[(String, js.Any)]): ReactElementNode =
    new ReactElementNode(tagName, fields)

  class ReactElementNode(tag: String | js.Dynamic, fields: Seq[(String, js.Any)]) {

    def children(children: (ReactElement | String)*): ReactElement =
      React.createElement(tag, js.Dynamic.literal.applyDynamic("apply")(fields: _*), children: _*)

    def children(children: js.Array[ReactElement]): ReactElement =
      React.createElement(tag, js.Dynamic.literal.applyDynamic("apply")(fields: _*), children)

  }

}
