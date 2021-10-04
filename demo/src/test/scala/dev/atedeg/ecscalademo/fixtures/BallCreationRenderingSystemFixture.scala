package dev.atedeg.ecscalademo.fixtures

import dev.atedeg.ecscala.util.types.given
import dev.atedeg.ecscala.World
import dev.atedeg.ecscala.dsl.ECScalaDSL
import dev.atedeg.ecscalademo.{ ECSCanvas, MouseState, PlayState, StartingState }
import dev.atedeg.ecscalademo.systems.BallCreationRenderingSystem
import org.scalatestplus.mockito.MockitoSugar.mock

trait BallCreationRenderingSystemFixture extends ECScalaDSL with WorldFixture with WorldStateFixture {
  val ballCreationRenderingSystem = BallCreationRenderingSystem(playState, mouseState, startingState, canvas)
  world hasA system(ballCreationRenderingSystem)
}
