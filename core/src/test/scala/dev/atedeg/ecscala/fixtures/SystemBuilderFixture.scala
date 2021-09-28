package dev.atedeg.ecscala.fixtures

import dev.atedeg.ecscala.World
import dev.atedeg.ecscala.util.types.given

trait SystemBuilderFixture {
  val world = World()
  val entity = world.createEntity()
  entity.addComponent(Position(1, 1))
  entity.addComponent(Velocity(1, 1))
}
