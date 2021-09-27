package dev.atedeg.ecscalademo.fixtures

import scala.language.implicitConversions
import dev.atedeg.ecscala.util.types.given
import dev.atedeg.ecscala.World
import dev.atedeg.ecscala.dsl.ECScalaDSL
import dev.atedeg.ecscalademo.given
import dev.atedeg.ecscalademo.{ Point, Position, Vector, Velocity }
import dev.atedeg.ecscalademo.systems.VelocityEditingSystem

trait VelocityFixture extends ECScalaDSL {
  val world = World()
  val system = VelocityEditingSystem()
  val entity1 = world hasAn entity withComponents { Position(0, 0) &: Velocity(0, 0) }
  world hasA system(system)
}
