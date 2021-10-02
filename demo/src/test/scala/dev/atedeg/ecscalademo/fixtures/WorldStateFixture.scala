package dev.atedeg.ecscalademo.fixtures

import dev.atedeg.ecscalademo.{ EnvironmentState, MouseState, PlayState }
import org.scalatestplus.mockito.MockitoSugar.mock

trait WorldStateFixture {
  val playState = PlayState()
  val mouseState = MouseState()
  val environmentState = mock[EnvironmentState]
}
