package dev.atedeg.ecscalademo.fixtures

import org.mockito.Mockito.when
import dev.atedeg.ecscala.given
import dev.atedeg.ecscala.dsl.ECScalaDSL
import dev.atedeg.ecscalademo.Velocity
import dev.atedeg.ecscalademo.systems.FrictionSystem

trait FrictionSystemFixture extends ECScalaDSL with WorldFixture with WorldStateFixture {
  val initialVelocity = Velocity(300, 0)
  val ball = world hasAn entity withComponent initialVelocity
  val frictionSystem = FrictionSystem(playState, environmentState)
  world hasA system(frictionSystem)
  when(environmentState.frictionCoefficient) thenReturn 1.0
  when(environmentState.wallRestitution) thenReturn 0.5
  when(environmentState.gravity) thenReturn 9.81
}
