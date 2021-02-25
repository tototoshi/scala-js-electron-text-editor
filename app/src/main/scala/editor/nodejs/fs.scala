package editor.nodejs

import scala.scalajs.js
import scala.scalajs.js.annotation.JSImport

@JSImport("fs", JSImport.Namespace)
@js.native
object fs extends js.Any {

  @js.native
  object promises extends js.Any {
    def readFile(path: String, encoding: String): js.Promise[String] = js.native
  }

}
