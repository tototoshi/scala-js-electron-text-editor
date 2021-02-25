package editor.nodejs

import scala.scalajs.js
import scala.scalajs.js.annotation.JSGlobal

@JSGlobal
@js.native
object process extends js.Any {
  val argv: js.Array[String] = js.native
}
