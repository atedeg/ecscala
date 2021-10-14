package dev.atedeg.ecscalademo.fixtures

import org.scalatestplus.mockito.MockitoSugar.mock
import dev.atedeg.ecscalademo.{ ECSCanvas, EnvironmentState, MouseState, PlayState, StartingState }

trait WorldStateFixture {
  val playState = PlayState()
  val mouseState = MouseState()
  val environmentState = mock[EnvironmentState]
  val canvas = mock[ECSCanvas]
  val startingState = StartingState(canvas)
}
