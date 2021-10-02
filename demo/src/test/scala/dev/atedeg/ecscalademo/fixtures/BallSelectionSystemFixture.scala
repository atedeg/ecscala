package dev.atedeg.ecscalademo.fixtures

import dev.atedeg.ecscala.util.types.given
import dev.atedeg.ecscala.World
import dev.atedeg.ecscala.dsl.ECScalaDSL
import dev.atedeg.ecscalademo.systems.BallSelectionSystem
import dev.atedeg.ecscalademo.{ ECSCanvas, MouseState, PlayState, StartingState }
import org.scalatestplus.mockito.MockitoSugar.mock

trait BallSelectionSystemFixture extends ECScalaDSL with WorldFixture with WorldStateFixture {
  val ballSelectionSystem = BallSelectionSystem(playState, mouseState)
  world hasA system(ballSelectionSystem)
}
