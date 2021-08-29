package dev.atedeg.ecscala.fixtures

import dev.atedeg.ecscala.util.types.given
import dev.atedeg.ecscala.fixtures.*

trait ViewFixture extends ComponentsFixture with WorldFixture {
  val entity1 = world.createEntity()
  val entity2 = world.createEntity()

  entity1.addComponent(Position(1, 1))
  entity1.addComponent(Velocity(2, 2))

  entity2.addComponent(Velocity(3, 5))
  entity2.addComponent(Mass(3))

  println(world)
}
