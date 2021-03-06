package dev.atedeg.ecscalademo.fixtures

import dev.atedeg.ecscala.given
import dev.atedeg.ecscala.dsl.ECScalaDSL
import dev.atedeg.ecscalademo.systems.BallCreationRenderingSystem

trait BallCreationRenderingSystemFixture extends ECScalaDSL with WorldFixture with WorldStateFixture {
  val ballCreationRenderingSystem = BallCreationRenderingSystem(playState, mouseState, startingState, canvas)
  world hasA system(ballCreationRenderingSystem)
}
