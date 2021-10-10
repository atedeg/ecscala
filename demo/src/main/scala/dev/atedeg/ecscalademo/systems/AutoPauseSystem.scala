package dev.atedeg.ecscalademo.systems

import dev.atedeg.ecscala.given
import dev.atedeg.ecscala.{ &:, CNil, Deletable, DeltaTime, Entity, IteratingSystem, View, World }
import dev.atedeg.ecscalademo.given
import dev.atedeg.ecscalademo.{ PlayState, State, Vector, Velocity }

/**
 * Pause the simulation when the system's energy is zero (all the ball has velocity = 0).
 */
class AutoPauseSystem(private val playState: PlayState) extends IteratingSystem[Velocity &: CNil] {

  override def shouldRun: Boolean = playState.gameState == State.Play

  override def update(
      entity: Entity,
      components: Velocity &: CNil,
  )(deltaTime: DeltaTime, world: World, view: View[Velocity &: CNil]): Deletable[Velocity &: CNil] = {
    val systemEnergy = view.map(_._2.h).foldLeft(0.0)(_ + _.velocity.squaredNorm)
    if (systemEnergy == 0.0) {
      playState.gameState = State.Pause
    }
    components
  }
}
