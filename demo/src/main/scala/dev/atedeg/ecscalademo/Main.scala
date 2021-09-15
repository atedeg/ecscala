package dev.atedeg.ecscalademo

import dev.atedeg.ecscala.util.types
import dev.atedeg.ecscala.*
import org.scalajs.dom
import org.scalajs.dom.{document, html}

import org.scalajs.dom
import dom.html
import scala.scalajs.js.annotation.JSExport
import scalatags.JsDom.all._

case class Position(x: Int, y: Int) extends Component

//@main def main: Unit = {
//  document.addEventListener("DOMContentLoaded", { (e: dom.Event) =>
//    setupUI()
//  })
//  val world = World()
//  world.addSystem[Position &: CNil]((e, c, dt) => {
//    ???
//  })
//
//  println(world)
//}

@JSExport
object HelloWorld1 extends App {
  @JSExport
  def main(target: html.Div) = {
    val (animalA, animalB) = ("fox", "dog")
    target.appendChild(
      div(
        h1("Hello World!"),
        p(
          "The quick brown ", b(animalA),
          " jumps over the lazy ",
          i(animalB), "."
        )
      ).render
    )
  }
}