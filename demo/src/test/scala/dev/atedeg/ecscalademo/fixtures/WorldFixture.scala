package dev.atedeg.ecscalademo.fixtures

import dev.atedeg.ecscala.{ Entity, World }

trait WorldFixture {
  lazy val world: World = World()
  lazy val entity: Entity = world.createEntity()
}
