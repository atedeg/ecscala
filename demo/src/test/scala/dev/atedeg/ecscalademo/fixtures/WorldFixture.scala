package dev.atedeg.ecscalademo.fixtures

import dev.atedeg.ecscala.World

trait WorldFixture {
  lazy val world: World = World()
}
