package dev.atedeg.ecscalademo.fixtures

import dev.atedeg.ecscala.given
import dev.atedeg.ecscalademo.systems.AutoPauseSystem
import dev.atedeg.ecscala.dsl.ECScalaDSL

trait AutoPauseSystemFixture extends ECScalaDSL with WorldFixture with WorldStateFixture {
  val autoPauseSystem = AutoPauseSystem(playState)
  world hasA system(autoPauseSystem)
}
