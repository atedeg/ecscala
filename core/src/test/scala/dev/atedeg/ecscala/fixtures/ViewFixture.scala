package dev.atedeg.ecscala.fixtures

import dev.atedeg.ecscala.util.types.given
import dev.atedeg.ecscala.fixtures.*

trait ViewFixture extends ComponentsFixture with WorldFixture {
  val entity1 = world.createEntity()
  val entity2 = world.createEntity()
  val entity3 = world.createEntity()
  val entity4 = world.createEntity()
  val entity5 = world.createEntity()

  entity1.addComponent(Position(1, 1))
  entity1.addComponent(Velocity(1, 1))

  entity2.addComponent(Mass(1))

  entity3.addComponent(Position(1, 1))
  entity3.addComponent(Velocity(1, 1))
  entity3.addComponent(Mass(1))

  entity4.addComponent(Position(1, 1))
  entity4.addComponent(Velocity(1, 1))

  entity5.addComponent(Position(1, 1))
  entity5.addComponent(Mass(1))
}
