package dev.atedeg.ecscalademo.fixtures

import dev.atedeg.ecscalademo.systems.*

trait SystemsFixture {
  lazy val movementSystem = new MovementSystem()
  lazy val frictionSystem = new FrictionSystem()
}
