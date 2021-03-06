package dev.atedeg.ecscalademo.fixtures

import dev.atedeg.ecscala.given
import dev.atedeg.ecscala.dsl.ECScalaDSL
import dev.atedeg.ecscalademo.systems.BallCreationSystem

trait BallCreationSystemFixture extends ECScalaDSL with WorldFixture with WorldStateFixture {
  val entity1 = world hasAn entity
  val ballCreationSystem = BallCreationSystem(playState, mouseState, startingState)
  world hasA system(ballCreationSystem)
}
