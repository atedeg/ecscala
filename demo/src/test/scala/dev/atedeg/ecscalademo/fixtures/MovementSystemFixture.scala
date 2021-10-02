package dev.atedeg.ecscalademo.fixtures

import dev.atedeg.ecscala.util.types.given
import dev.atedeg.ecscala.dsl.ECScalaDSL
import dev.atedeg.ecscalademo.systems.MovementSystem
import org.mockito.Mockito.when

trait MovementSystemFixture extends ECScalaDSL with WorldFixture with WorldStateFixture {
  val movementSystem = MovementSystem(playState)
  world hasA system(movementSystem)
  when(environmentState.frictionCoefficient) thenReturn 0.05
  when(environmentState.wallRestitution) thenReturn 0.5
}
