package dev.atedeg.ecscalademo.fixtures

import org.scalatestplus.mockito.MockitoSugar.mock
import dev.atedeg.ecscala.World
import dev.atedeg.ecscala.dsl.ECScalaDSL
import dev.atedeg.ecscala.util.types.given
import dev.atedeg.ecscalademo.{ ECSCanvas, MouseState, PlayState, StartingState }
import dev.atedeg.ecscalademo.systems.BallCreationRenderingSystem

trait BallCreationRenderingSystemFixture extends ECScalaDSL with WorldFixture with WorldStateFixture {
  val ballCreationRenderingSystem = BallCreationRenderingSystem(playState, mouseState, startingState, canvas)
  world hasA system(ballCreationRenderingSystem)
}
