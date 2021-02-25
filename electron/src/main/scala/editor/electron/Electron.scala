package editor.electron

import scala.scalajs.js
import scala.scalajs.js.annotation.JSImport

@JSImport("electron", JSImport.Namespace)
@js.native
object Electron extends js.Any {

  @js.native
  object app extends js.Any {
    def whenReady(): js.Promise[Unit] = js.native
  }

  @js.native
  class BrowserWindow(val options: js.Object) extends js.Any {
    def loadFile(file: String): Unit = js.native
    val webContents: WebContents = js.native
  }

  @js.native
  object ipcMain extends js.Any {
    def on(channel: String, listener: js.Function2[ipcRendererEvent, js.Array[Any], Unit]): Unit = js.native

    def handle(channel: String, listener: js.Function2[ipcMainInvokeEvent, js.Array[Any], js.Promise[Any]]): Unit =
      js.native
  }

  @js.native
  object ipcRenderer extends js.Any {
    def on(channel: String, listener: js.Function2[ipcRendererEvent, js.Array[Any], Unit]): Unit = js.native
    def invoke(channel: String, args: Any*): js.Promise[Any] = js.native
  }

  @js.native
  class ipcMainInvokeEvent extends js.Any

  @js.native
  class ipcRendererEvent extends js.Any

  @js.native
  class WebContents extends js.Any {
    def send(channel: String, args: Any*): Unit = js.native
  }

}
