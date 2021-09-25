package dev.atedeg.ecscalademo.systems

import dev.atedeg.ecscala.*
import dev.atedeg.ecscala.util.types.given
import dev.atedeg.ecscalademo.{ PlayState, Velocity }
import dev.atedeg.ecscalademo.Vector
import dev.atedeg.ecscalademo.StartingState.*
import dev.atedeg.ecscalademo.*

class FrictionSystem() extends System[Velocity &: CNil] {

  override def shouldRun: Boolean = PlayState.playing

  override def update(
      entity: Entity,
      components: Velocity &: CNil,
  )(deltaTime: DeltaTime, world: World, view: View[Velocity &: CNil]): Deletable[Velocity &: CNil] = {
    val Velocity(velocity) &: CNil = components
    val frictionDirection = -1 * velocity.normalized
    val friction = (frictionCoefficient * gravity) * frictionDirection
    val newVelocity = velocity + friction
    if ((velocity dot newVelocity) < 0) Velocity(Vector(0, 0)) &: CNil else Velocity(newVelocity) &: CNil
  }
}