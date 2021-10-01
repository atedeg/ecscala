package dev.atedeg.ecscalademo.fixtures

import dev.atedeg.ecscalademo.{ EnvironmentState, PlayState }
import dev.atedeg.ecscalademo.systems.*
import org.scalatestplus.mockito.MockitoSugar.mock
import scalafx.beans.property.DoubleProperty
import org.mockito.Mockito.when

trait SystemsFixture {
  val playState = PlayState()
  val environmentState = EnvironmentState(mock[DoubleProperty], mock[DoubleProperty])
  when(environmentState.frictionCoefficient) thenReturn 0.05
  when(environmentState.wallRestitution) thenReturn 0.5
  lazy val movementSystem = new MovementSystem(playState)
  lazy val frictionSystem = new FrictionSystem(playState, environmentState)
}
