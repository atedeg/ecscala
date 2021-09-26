package dev.atedeg.ecscalademo.fixtures

import scala.language.implicitConversions
import dev.atedeg.ecscala.World
import dev.atedeg.ecscala.dsl.ECScalaDSL
import dev.atedeg.ecscala.util.types.given
import dev.atedeg.ecscalademo.{ Circle, Color, Mass, Position, Velocity }
import dev.atedeg.ecscalademo

trait WallCollisionsFixture extends ECScalaDSL {
  val world = World()

  val entities = for {
    x <- Seq(-1.0, 50.0, 101.0)
    y <- Seq(-1.0, 50.0, 101.0)
  } yield {
    world hasAn entity withComponents {
      Position(x, y) &: Velocity(1, 1) &: Circle(10, Color(0, 0, 0)) &: Mass(1)
    }
  }
}