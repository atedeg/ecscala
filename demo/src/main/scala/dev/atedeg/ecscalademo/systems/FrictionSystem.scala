package dev.atedeg.ecscalademo.systems

import dev.atedeg.ecscala.given
import dev.atedeg.ecscala.{ &:, CNil, Deletable, DeltaTime, Entity, IteratingSystem, View, World }
import dev.atedeg.ecscalademo.{ EnvironmentState, PlayState, State, Velocity }

/**
 * The [[System]] that applies the friction to the balls that have a Velocity.
 */
class FrictionSystem(private val playState: PlayState, private val environmentState: EnvironmentState)
    extends IteratingSystem[Velocity &: CNil] {

  override def shouldRun: Boolean = playState.gameState == State.Play

  override def update(
      entity: Entity,
      components: Velocity &: CNil,
  )(deltaTime: DeltaTime, world: World, view: View[Velocity &: CNil]): Deletable[Velocity &: CNil] = {
    val Velocity(velocity) &: CNil = components
    if (velocity.norm > 0) {
      val frictionDirection = velocity.normalized * -1
      val friction = frictionDirection * (environmentState.frictionCoefficient * environmentState.gravity)
      val newVelocity = velocity + friction
      if (velocity dot newVelocity) < 0 then Velocity(0, 0) &: CNil else Velocity(newVelocity) &: CNil
    } else {
      components
    }
  }
}
