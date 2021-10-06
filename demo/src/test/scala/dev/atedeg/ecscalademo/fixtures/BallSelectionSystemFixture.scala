package dev.atedeg.ecscalademo.fixtures

import org.scalatestplus.mockito.MockitoSugar.mock
import dev.atedeg.ecscala.given
import dev.atedeg.ecscala.dsl.ECScalaDSL
import dev.atedeg.ecscalademo.{ ECSCanvas, MouseState, PlayState, StartingState }
import dev.atedeg.ecscalademo.systems.BallSelectionSystem

trait BallSelectionSystemFixture extends ECScalaDSL with WorldFixture with WorldStateFixture {
  val ballSelectionSystem = BallSelectionSystem(playState, mouseState)
  world hasA system(ballSelectionSystem)
}
