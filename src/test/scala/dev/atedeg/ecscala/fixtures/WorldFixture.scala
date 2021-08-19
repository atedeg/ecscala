package dev.atedeg.ecscala.fixtures

import dev.atedeg.ecscala.World

trait WorldFixture {
  lazy val world: World = World()
}
