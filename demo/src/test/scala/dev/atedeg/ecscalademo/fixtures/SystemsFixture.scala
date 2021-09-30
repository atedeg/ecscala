package dev.atedeg.ecscalademo.fixtures

import dev.atedeg.ecscalademo.{ EnvironmentState, PlayState }
import dev.atedeg.ecscalademo.systems.*

trait SystemsFixture {
  val playState = PlayState()
  val environmentState = EnvironmentState()
  lazy val movementSystem = new MovementSystem(playState)
  lazy val frictionSystem = new FrictionSystem(playState, environmentState)
}
