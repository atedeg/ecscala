package dev.atedeg.ecscalademo.fixtures

import dev.atedeg.ecscala.util.types.given
import dev.atedeg.ecscala.dsl.ECScalaDSL
import dev.atedeg.ecscalademo.{ Position, Velocity }
import dev.atedeg.ecscalademo.systems.MovementSystem
import org.mockito.Mockito.when

trait MovementSystemFixture extends ECScalaDSL with WorldFixture with WorldStateFixture {
  val ball = world hasAn entity withComponents { Position(0, 0) &: Velocity(300, 0) }
  val movementSystem = MovementSystem(playState)
  world hasA system(movementSystem)
  when(environmentState.frictionCoefficient) thenReturn 0.05
  when(environmentState.wallRestitution) thenReturn 0.5
}
