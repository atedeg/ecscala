package dev.atedeg.ecscala.fixtures

import dev.atedeg.ecscala.util.types.given
import dev.atedeg.ecscala.fixtures.*

trait ViewFixture extends ComponentsFixture with WorldFixture {
  val entity1 = world.createEntity()
  val entity2 = world.createEntity()
  val entity3 = world.createEntity()
  val entity4 = world.createEntity()
  val entity5 = world.createEntity()

  entity1 setComponent Position(1, 1)
  entity1 setComponent Velocity(1, 1)

  entity2 setComponent Mass(1)

  entity3 setComponent Position(1, 1)
  entity3 setComponent Velocity(1, 1)
  entity3 setComponent Mass(1)

  entity4 setComponent Position(1, 1)
  entity4 setComponent Velocity(1, 1)

  entity5 setComponent Position(1, 1)
  entity5 setComponent Mass(1)
}
