package dev.atedeg.ecscalademo.fixtures

import dev.atedeg.ecscala.util.types.given
import dev.atedeg.ecscala.World
import dev.atedeg.ecscala.dsl.ECScalaDSL
import dev.atedeg.ecscalademo
import dev.atedeg.ecscalademo.{ MouseState, PlayState, Point, Position, Vector, Velocity }
import dev.atedeg.ecscalademo.systems.VelocityEditingSystem

trait VelocityFixture extends ECScalaDSL {
  val world = World()
  val playState = PlayState()
  val mouseState = MouseState()
  val system = VelocityEditingSystem(playState, mouseState)
  val entity1 = world hasAn entity withComponents { Position(0, 0) &: Velocity(0, 0) }
  world hasA system(system)
}
