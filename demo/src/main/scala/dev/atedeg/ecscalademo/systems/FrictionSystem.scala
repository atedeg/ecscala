package dev.atedeg.ecscalademo.systems

import dev.atedeg.ecscala.*
import dev.atedeg.ecscala.util.types.given
import dev.atedeg.ecscalademo.{ PlayState, Velocity }
import dev.atedeg.ecscalademo.Vector
import dev.atedeg.ecscalademo.StartingState.*
import dev.atedeg.ecscalademo.EnvironmentState.*
import dev.atedeg.ecscalademo.*

/**
 * The [[System]] that applies the friction to the balls that have a Velocity.
 */
class FrictionSystem(private val playState: PlayState, private val environmentState: EnvironmentState)
    extends System[Velocity &: CNil] {

  override def shouldRun: Boolean = playState.gameState == State.Play

  override def update(
      entity: Entity,
      components: Velocity &: CNil,
  )(deltaTime: DeltaTime, world: World, view: View[Velocity &: CNil]): Deletable[Velocity &: CNil] = {
    val Velocity(velocity) &: CNil = components
    if (velocity.norm > 0) {
      val frictionDirection = -1 * velocity.normalized
      val friction = (environmentState.frictionCoefficient * environmentState.gravity) * frictionDirection
      val newVelocity = velocity + friction
      if (velocity dot newVelocity) < 0 then Velocity(0, 0) &: CNil else Velocity(newVelocity) &: CNil
    } else {
      components
    }
  }
}
