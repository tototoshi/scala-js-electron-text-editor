package editor.app

import editor.electron.Electron
import editor.nodejs.{fs, process}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.scalajs.js
import scala.scalajs.js.JSConverters._
import scala.scalajs.js.Promise

object Main {

  def main(args: Array[String]): Unit = {

    for {
      _ <- Electron.app.whenReady().toFuture
    } {
      createWindow()
    }
  }

  private def createWindow(): Unit = {
    val option = js.Dynamic.literal(
      "webPreferences" -> js.Dynamic.literal(
        "nodeIntegration" -> true,
        "contextIsolation" -> false
      )
    )
    val window = new Electron.BrowserWindow(option)

    Electron.ipcMain.handle("content", (e, args) => readFile())
    window.loadFile("index.html")
  }

  def readFile(): Promise[String] = {
    process.argv.lift(2) match {
      case Some(f) => fs.promises.readFile(f, "utf-8")
      case None => Future.successful("").toJSPromise
    }
  }

}
