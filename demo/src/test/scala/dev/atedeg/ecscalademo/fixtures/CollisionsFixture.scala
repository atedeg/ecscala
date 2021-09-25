package dev.atedeg.ecscalademo.fixtures

import scala.language.implicitConversions
import dev.atedeg.ecscala.World
import dev.atedeg.ecscala.util.types.given
import dev.atedeg.ecscalademo.{ Circle, Color, Mass, Position, Velocity }
import dev.atedeg.ecscalademo.given

trait CollisionsFixture {
  val world = World()

  val entities = for {
    x <- Seq(-1.0, 50.0, 101.0)
    y <- Seq(-1.0, 50.0, 101.0)
  } yield {
    val entity = world.createEntity()
    entity
      .addComponent(Position(x, y))
      .addComponent(Velocity(1.0, 1.0))
      .addComponent(Circle(10, Color(0, 0, 0)))
      .addComponent(Mass(1))
  }
}
