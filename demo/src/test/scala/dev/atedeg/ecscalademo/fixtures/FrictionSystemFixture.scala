package dev.atedeg.ecscalademo.fixtures

import dev.atedeg.ecscalademo.{ EnvironmentState, PlayState }
import dev.atedeg.ecscalademo.systems.FrictionSystem
import org.mockito.Mockito.when
import org.scalatestplus.mockito.MockitoSugar.mock

trait FrictionSystemFixture extends WorldFixture {
  val playState = PlayState()
  val environmentState = mock[EnvironmentState]
  lazy val frictionSystem = new FrictionSystem(playState, environmentState)
  when(environmentState.frictionCoefficient) thenReturn 1.0
  when(environmentState.wallRestitution) thenReturn 0.5
  when(environmentState.gravity) thenReturn 9.81
}
