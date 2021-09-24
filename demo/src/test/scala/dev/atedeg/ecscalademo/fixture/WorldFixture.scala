package dev.atedeg.ecscalademo.fixture

import dev.atedeg.ecscala.{ Entity, World }

trait WorldFixture {
  lazy val world: World = World()
  lazy val entity: Entity = world.createEntity()
}
