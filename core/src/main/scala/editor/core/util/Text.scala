package editor.core.util

import editor.core.model.Coordinates

import scala.annotation.tailrec
import scala.collection.mutable.ArrayBuffer
import scala.scalajs._
import scala.scalajs.js.JSConverters._

object Text {

  private val newLine = '\n'

  def coordinates(content: String, pos: Int): Coordinates = {
    @tailrec
    def go(content: String, pos: Int, x: Int, y: Int): Coordinates = {
      if (pos == 0 || content.isEmpty) {
        new Coordinates(x, y)
      } else {
        val c = content.charAt(0)
        if (c == newLine) {
          go(content.drop(1), pos - 1, 0, y + 1)
        } else {
          go(content.drop(1), pos - 1, x + 1, y)
        }
      }
    }

    go(content, pos, 0, 0)
  }

  def position(content: String, x: Int, y: Int): Int = {

    val contentLength = content.length

    @tailrec
    def go(x: Int, y: Int, offset: Int): Int = {
      val i = content.indexOf(newLine, offset)
      if (offset > contentLength) {
        contentLength
      } else if (y <= 0) {
        if (i == -1) {
          Seq(offset + x, contentLength).min
        } else {
          Seq(offset + x, i).min
        }
      } else {
        if (i == -1) {
          contentLength
        } else {
          go(x, y - 1, i + 1)
        }
      }
    }
    go(x, y, 0)
  }

  def insert(content: String, pos: Int, input: String): String =
    content.substring(0, pos) + input + content.substring(pos)

  def toLines(s: String): js.Array[String] = {

    @tailrec
    def go(offset: Int, result: ArrayBuffer[String]): ArrayBuffer[String] = {
      val i = s.indexOf(newLine, offset)
      if (i == -1) {
        result += s.substring(offset)
      } else {
        result += s.substring(offset, i + 1)
        go(i + 1, result)
      }
    }

    go(0, ArrayBuffer.empty[String]).toJSArray
  }

}
