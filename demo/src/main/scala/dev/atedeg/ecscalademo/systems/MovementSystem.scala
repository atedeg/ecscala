package dev.atedeg.ecscalademo.systems

import dev.atedeg.ecscala.*
import dev.atedeg.ecscala.util.types.given
import dev.atedeg.ecscala.CNil
import dev.atedeg.ecscalademo.{ PlayState, Position, Velocity }

class MovementSystem extends System[Position &: Velocity &: CNil] {
  override def shouldRun: Boolean = PlayState.playing

  override def update(entity: Entity, components: Position &: Velocity &: CNil)(
      deltaTime: DeltaTime,
      world: World,
      view: View[Position &: Velocity &: CNil],
  ): Deletable[Position &: Velocity &: CNil] = {
    val Position(point) &: Velocity(vector) &: CNil = components
    val newPosition = point + (vector * deltaTime)
    Position(newPosition) &: Velocity(vector) &: CNil
  }
}
