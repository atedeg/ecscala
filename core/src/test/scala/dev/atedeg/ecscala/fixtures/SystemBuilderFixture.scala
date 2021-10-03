package dev.atedeg.ecscala.fixtures

import dev.atedeg.ecscala.World
import dev.atedeg.ecscala.util.types.given

trait SystemBuilderFixture {
  val world = World()
  val entity = world.createEntity()
  entity setComponent Position(1, 1)
  entity setComponent Velocity(1, 1)
}
