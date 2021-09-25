package dev.atedeg.ecscalademo.fixtures

import dev.atedeg.ecscalademo.systems.MovementSystem

trait SystemsFixture {
  lazy val movementSystem = new MovementSystem
}
