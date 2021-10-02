package dev.atedeg.ecscala.fixtures

import dev.atedeg.ecscala.World

trait WorldFixture {
  val world: World = World()
}
