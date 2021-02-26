package editor.core.parser

import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers

import scala.scalajs.js

class ParserTest extends AnyFunSuite with Matchers {

  test("Scala Parser Test") {
    val actual = Parser.exec(lang.Scala, "def").toArray
    val expected = js.Array(Marker("keyword", 0, Some("start")), Marker("keyword", 3, Some("end"))).toArray

    actual should contain theSameElementsAs expected
  }

}
