package dev.atedeg.ecscala.fixtures

import dev.atedeg.ecscala.{ World, given }

trait SystemBuilderFixture {
  val world = World()
  val entity = world.createEntity()
  entity setComponent Position(1, 1)
  entity setComponent Velocity(1, 1)
}
