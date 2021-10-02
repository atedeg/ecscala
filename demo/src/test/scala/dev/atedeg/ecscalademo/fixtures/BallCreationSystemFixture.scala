package dev.atedeg.ecscalademo.fixtures

import dev.atedeg.ecscala.util.types.given
import dev.atedeg.ecscala.World
import dev.atedeg.ecscala.dsl.ECScalaDSL
import dev.atedeg.ecscalademo.{ ECSCanvas, MouseState, PlayState, StartingState }
import dev.atedeg.ecscalademo.systems.BallCreationSystem
import org.scalatestplus.mockito.MockitoSugar.mock

trait BallCreationSystemFixture extends ECScalaDSL with WorldFixture with WorldStateFixture {
  val entity1 = world hasAn entity
  val ballCreationSystem = new BallCreationSystem(playState, mouseState, startingState)
  world hasA system(ballCreationSystem)
}
